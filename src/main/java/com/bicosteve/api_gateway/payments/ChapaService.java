package com.bicosteve.api_gateway.payments;

import com.bicosteve.api_gateway.config.ChapaConfig;
import com.bicosteve.api_gateway.dto.response.DepositResponse;
import com.bicosteve.api_gateway.enums.DepositStatus;
import com.bicosteve.api_gateway.dto.requests.DepositRequest;
import com.bicosteve.api_gateway.models.Deposit;
import com.bicosteve.api_gateway.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChapaService {
    private final ChapaConfig chapaConfig;
    private final RestTemplate restTemplate;
    private final WalletRepository walletRepository;


    // STEP 01: Generate a unique trxRef
    private String generateTrxRef(Long profileId) {
        return "DEPOSIT-%s-%s-%s".formatted(
                profileId,
                System.currentTimeMillis(),
                UUID.randomUUID().toString().substring(0,8).toLowerCase());
    }

    // STEP 02: verify payment with Chapa
    private boolean verifyPayment(String trxRef){
        log.info("Verifying chapa payment with trx ref {}", trxRef);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.chapaConfig.getSecretKey());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try{
            String url = "%s/transaction/verify/%s".formatted(this.chapaConfig.getBaseUrl(),trxRef);
            ResponseEntity<Map> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            Map<String, Object> body = response.getBody();
            if(body == null) return false;

            Map<String,Object> data = (Map<String, Object>) body.get("enums");
            if(data == null) return false;

            String status = (String) data.get("status");
            log.info("Chapa payment trxRef={} has status {}",trxRef, status);

            return "success".equalsIgnoreCase(status);

        }catch (Exception e){
            log.error("Verification for ref={} failed with {} ",trxRef,e.getMessage());
            return false;
        }
    }

    private boolean verifyWebhookSignature(String rawBody, String chapaSignature){
        try{
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    this.chapaConfig.getWebhookSecret().getBytes("UTF-8"),
                    "HmacSHA256"
            );
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(rawBody.getBytes("UTF-8"));
            String expectedSignature = HexFormat.of().formatHex(hash);
            return expectedSignature.equalsIgnoreCase(chapaSignature);
        } catch(Exception e) {
            log.error("signature verification error={}",e.getMessage());
            return false;
        }

    }


    public DepositResponse initiateDeposit(Long profileId, DepositRequest request) {
        log.info("Initiating deposit request for profileId={} and amount={}", profileId, request.getAmount());
        String trxRef = this.generateTrxRef(profileId);

        Map<String,Object> chapaPayload = new HashMap<>();
        chapaPayload.put("amount", request.getAmount());
        chapaPayload.put("currency", chapaConfig.getCurrency());
        chapaPayload.put("email", request.getEmail());
        chapaPayload.put("first_name", request.getFirstName());
        chapaPayload.put("last_name", request.getLastName());
        chapaPayload.put("phone_number", request.getPhoneNumber());
        chapaPayload.put("tx_ref", trxRef);
        chapaPayload.put("callback_url", chapaConfig.getCallbackUrl());
        chapaPayload.put("return_url", chapaConfig.getReturnUrl());
        chapaPayload.put("customization[title]", "CustomerDeposit");
        chapaPayload.put("customization[description]", "Deposit to sportsbook wallet");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.chapaConfig.getSecretKey());
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(chapaPayload, headers);

        ResponseEntity<Map> response;

        try{
            String url = "%s/transaction/initialize".formatted(this.chapaConfig.getBaseUrl());
            response = this.restTemplate.postForEntity(url, entity, Map.class );
        } catch(Exception e) {
            log.error("Deposit request failed with error={}",e.getMessage());
            throw new RuntimeException("Deposit request failed with error= ",e);
        }

        Map<String,Object> responseBody = response.getBody();
        if(responseBody == null || !"success".equals(responseBody.get("status"))){
            log.error("Deposit request for profileId={} failed with error={}",profileId,responseBody);
            throw new RuntimeException("Payment initialization failed with error= "+responseBody.get("error"));
        };

        Map<String, Object> data = (Map<String,Object>) responseBody.get("enums");
        String checkoutUrl = (String) data.get("checkout_url");

        // Save Deposit
        Deposit.builder()
                .profileId(profileId)
                .trxRef(trxRef)
                .amount(request.getAmount())
                .currency(this.chapaConfig.getCurrency())
                .checkoutUrl(checkoutUrl)
                .status(DepositStatus.PENDING)
                .build();

        log.info(
                "deposit initialized for profileId={} trxRef={} and checkoutUrl={}",
                profileId,
                trxRef,
                checkoutUrl
        );


        return DepositResponse.builder()
                .trxRef(trxRef)
                .checkoutUrl(checkoutUrl)
                .amount(request.getAmount())
                .currency(this.chapaConfig.getCurrency())
                .build();
    }

    @Transactional
    public void handleWebhook(String trxRef, String chapaSignature, String rawBody){
        log.info("webhook received trxRef={}",trxRef);

        // STEP 01:: Verify webhook signature
        if(!this.verifyWebhookSignature(rawBody, chapaSignature)){
            log.warn("Invalid webhook received trxRef={}",trxRef);
            throw new SecurityException("Invalid webhook received trxRef="+trxRef);
        }

        // STEP 02:: Check for idempotency
        // Deposit deposit = this.depositRepositor.findByTrXRef(trxRef);
        // if null return;

        // STEP 03:: Verify payment with Chapa
        boolean isSuccess = this.verifyPayment(trxRef);
        if(!isSuccess){
            log.warn("Payment verification failed for trxRef={}",trxRef);
            // this.depositRepository.updateStatus(trxRef,DepositStatus.FAILED)
            return;
        }


        // STEP 04:: Credit wallet
        // this.walletRepository.crediBalanace(deposit.getProfileId(), deposit.getAmount();

        // STEP 05:: Update deposit status
        // this.depositRepository.updateStatus(trxRef, DepositStatus.SUCCESS);

        // STEP 06:: Insert into transactions table as a record
        // this.transactionRepository.insert(Transaction.builder().build());

        // log.info("Wallet credited profileId={} amount={} trxRef={}",deposit.getProfileId(), deposit.getAmount(), trxRef);
    }



}
