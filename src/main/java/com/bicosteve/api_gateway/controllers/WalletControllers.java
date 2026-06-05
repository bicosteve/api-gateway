package com.bicosteve.api_gateway.controllers;


import com.bicosteve.api_gateway.dto.requests.DepositRequest;
import com.bicosteve.api_gateway.dto.response.BadRequestResponse;
import com.bicosteve.api_gateway.dto.response.DepositResponse;
import com.bicosteve.api_gateway.dto.response.ServerErrorResponse;
import com.bicosteve.api_gateway.payments.ChapaService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name="bearerAuth")
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Slf4j
@Tag(name="Payment Controller", description = "Payment management endpoint")
public class WalletControllers {
    private final ChapaService chapaService;


    @PostMapping("/deposit-chapa")
    @Operation(
            summary = "Make Chapa deposits",
            description = "Used to receive deposits from Chapa"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Make deposits",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DepositResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BadRequestResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServerErrorResponse.class)
                    )),

    })
    public ResponseEntity<DepositResponse> depositChapa(
            @Valid @RequestBody DepositRequest request,
            Authentication auth
    ) {
        DepositResponse deposit = this.chapaService.initiateDeposit(auth,request);
        return ResponseEntity.status(HttpStatus.CREATED).body(deposit);
    }

    // Chapa will call this endpoint after payment not user
    @Hidden // hidden from swagger ui
    @PostMapping("/webhook/chapa")
    public ResponseEntity<Void> chapaWebhook(
            @RequestHeader(value="chapa-signature", required = false)
            String chapaSignature,
            @RequestHeader(value = "x-chapa-signature", required = false)
            String xChapaSignature,
            @RequestParam(required = false) String trx_ref,
            @RequestBody String rawBody
    ){
        log.info("Chapa webhook received trxRef={}: ", trx_ref);
        String signature = chapaSignature != null ? chapaSignature : xChapaSignature;

        try{
            // handleWebhook is called here
            // triggered by Chapa not by user
            chapaService.handleWebhook(trx_ref,signature,rawBody);
            return ResponseEntity.ok().build();
        }catch(SecurityException ex){
            log.warn("Invalid signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch(Exception e) {
            log.warn("Webhook error={}", e.getMessage());
            return  ResponseEntity.ok().build();
        }


    }


}
