package com.fudgeq.api.audit;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class AuditConfig {
    // This activates @CreatedDate and @LastModifiedDate annotations
}
