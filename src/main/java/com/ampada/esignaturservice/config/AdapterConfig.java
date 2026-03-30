package com.ampada.esignaturservice.config;

import com.ampada.esignaturservice.adapter.demo.DemoAdapter;
import com.ampada.esignaturservice.adapter.demo.SignatureProviderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdapterConfig {

    @Bean
    public SignatureProviderAdapter signatureProviderAdapter() {
        return new DemoAdapter();
    }
}
