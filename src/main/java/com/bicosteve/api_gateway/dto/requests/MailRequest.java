package com.bicosteve.api_gateway.dto.requests;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MailRequest{
    private String purpose;
    private String to;
    private String from;
    private String subject;
    private String body;
    private List<String> cc;
}
