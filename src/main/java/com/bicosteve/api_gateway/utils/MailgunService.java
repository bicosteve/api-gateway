package com.bicosteve.api_gateway.utils;

import com.bicosteve.api_gateway.config.MailgunConfig;
import com.bicosteve.api_gateway.dto.requests.MailRequest;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class MailgunService{
    private final MailgunConfig mailgunConfig;

    @Async("asyncMailSender")
    public void sendEmail(MailRequest mail){
        String url = "%s/%s/messages".formatted(
                this.mailgunConfig.getBaseUrl(),
                this.mailgunConfig.getSandbox()
        );

        log.info("MailgunService::sending mail to {}",mail.getTo());
        log.info("MailgunService::Sending request to url {}",url);

        try{
            HttpResponse<String> response = Unirest
                    .post(url)
                    .basicAuth("api", this.mailgunConfig.getApiKey())
                    .field("from",this.mailgunConfig.getFrom())
                    .field("to",mail.getTo())
                    .field("subject",mail.getSubject())
                    .field("text",mail.getBody())
                    .field("purpose", mail.getPurpose())
                    .asString();

            if(response.getStatus() == 200){
                log.info(
                        "MailgunService::email sent successfully to {}",
                        mail.getTo()
                );

            } else {

                log.error(
                        "MailgunService::failed to send mail to {} body {}",
                        response.getStatus(),
                        response.getBody()
                        );

            }
        }catch(Exception e){
            log.error(
                    "MailgunService::error sending email to {} with {}",
                    mail.getTo(),
                    e.getMessage()
                    );
            throw new RuntimeException(e);
        }
    }
}
