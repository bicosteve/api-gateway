package com.bicosteve.api_gateway.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailRequest{
    private String purpose;
    private String to;
    private String from;
    private String subject;
    private String body;
    private List<String> cc;
}
