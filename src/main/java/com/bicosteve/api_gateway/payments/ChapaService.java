package com.bicosteve.api_gateway.payments;

import com.bicosteve.api_gateway.config.ChapaConfig;
import com.bicosteve.api_gateway.dto.response.DepositResponse;
import com.bicosteve.api_gateway.enums.DepositStatus;
import com.bicosteve.api_gateway.dto.requests.DepositRequest;
import com.bicosteve.api_gateway.enums.TransactionStatus;
import com.bicosteve.api_gateway.enums.TransactionType;
import com.bicosteve.api_gateway.mappers.dtomappers.DepositDtoMapper;
import com.bicosteve.api_gateway.models.Deposit;
import com.bicosteve.api_gateway.models.Transaction;
import com.bicosteve.api_gateway.repository.DepositRepository;
import com.bicosteve.api_gateway.repository.TransactionRepository;
import com.bicosteve.api_gateway.repository.WalletRepository;
import com.bicosteve.api_gateway.security.CustomUserDetails;
import com.bicosteve.api_gateway.utils.TrxRefGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
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
    private final DepositRepository depositRepository;
    private final TransactionRepository transactionRepository;
    private final TrxRefGenerator trxRefGenerator;
    private final DepositDtoMapper depositDtoMapper;


    // STEP 01: verify payment with Chapa
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

            Map<String,Object> data = (Map<String, Object>) body.get("data");
            if(data == null) return false;

            String status = (String) data.get("status");
            log.info("Chapa payment trxRef={} has status {}",trxRef, status);

            return "success".equalsIgnoreCase(status);

        }catch (Exception e){
            log.error("Verification for ref={} failed with {} ",trxRef,e.getMessage());
            return false;
        }
    }

    // STEP 03: Verify the webhook sent from chapa is valid
    // Chapa & I share secret only the two of us know
    // Chapa receives payment and takes the raw requestBody
    // hash it using shared secretKey
    // attach the hash to the request header as chapa-signature
    // chapa sends the webhook to the server.
    // server takes the webhook with the same rawBody,
    // compute SHA256 using the shared secret
    // compares the hash vs chapa signature in the header
    // match -> request is genuine
    // !match -> fake request reject
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


    public DepositResponse initiateDeposit(Authentication auth, DepositRequest request) {
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        Long profileId = customUserDetails.getProfileId();

        log.info("Initiating deposit request for profileId={} and amount={}",
                profileId, request.getAmount());

        String trxRef = this.trxRefGenerator.generateTrxRef(profileId);

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

        Map<String, Object> data = (Map<String,Object>) responseBody.get("data");
        String checkoutUrl = (String) data.get("checkout_url");

        // Save Deposit
        Deposit deposit = Deposit.builder()
                .profileId(profileId)
                .trxRef(trxRef)
                .amount(request.getAmount())
                .currency(this.chapaConfig.getCurrency())
                .checkoutUrl(checkoutUrl)
                .status(DepositStatus.PENDING.getStatus())
                .build();

        this.depositRepository.insertDeposit(deposit);



        log.info(
                "deposit initialized for profileId={} trxRef={} and checkoutUrl={}",
                profileId,
                trxRef,
                checkoutUrl
        );

//        DepositResponse.builder()
//                .trxRef(trxRef)
//                .checkoutUrl(checkoutUrl)
//                .amount(request.getAmount())
//                .currency(this.chapaConfig.getCurrency())
//                .build()


        return this.depositDtoMapper.toDto(deposit);
    }

    @Transactional
    public void handleWebhook(String trxRef, String chapaSignature, String rawBody){
        log.info("webhook received trxRef={}",trxRef);

        // STEP 01:: Verify if the request is from Chapa
        if(!this.verifyWebhookSignature(rawBody, chapaSignature)){
            log.warn("Invalid webhook received trxRef={}",trxRef);
            throw new SecurityException("Invalid webhook received trxRef="+trxRef);
        }

        // STEP 02:: Check if the deposit with ref exists
         Deposit deposit = this.depositRepository.findByTrxRef(trxRef);
         if(deposit == null){
             log.warn("Deposit not found for provided trxRef={}",trxRef);
             throw new IllegalArgumentException("Deposit not found for trxRef="+trxRef);
         }


         // STEP 03:: Check for idempotency
         if(deposit.getStatus().equals(DepositStatus.SUCCESS.getStatus())){
             log.info("Deposit already exists for trxRef={}",trxRef);
             return;
         }

        // STEP 04:: Verify payment with Chapa
        boolean isSuccess = this.verifyPayment(trxRef);
        if(!isSuccess){
            log.warn("Payment verification failed for trxRef={}",trxRef);
            this.depositRepository.updateDepositStatus(trxRef,DepositStatus.FAILED.getStatus());
            return;
        }


        // STEP 05:: Credit wallet
        this.walletRepository.creditWalletBalance(deposit.getProfileId(),deposit.getAmount());

        // STEP 06:: Update deposit status
        this.depositRepository.updateDepositStatus(trxRef, DepositStatus.SUCCESS.getStatus());

        // STEP 07:: Insert into transactions table as a record
        Transaction trx = Transaction.builder()
                .profileId(deposit.getProfileId())
                .amount(deposit.getAmount())
                .reference(deposit.getTrxRef())
                .type(TransactionType.CREDIT.getStatus())
                .status(TransactionStatus.SUCCESS.getStatus())
                .createdBy("CHAPA_SERVICE")
                .build();

        this.transactionRepository.addTransaction(trx);

        log.info("Wallet credited profileId={} amount={} trxRef={}",deposit.getProfileId(), deposit.getAmount(), trxRef);
    }



}
