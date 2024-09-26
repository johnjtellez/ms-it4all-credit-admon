package com.it4all.credit.admon.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.data.jpa.repository.JpaRepository;

@Configuration
public class CsvServiceConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public <T> CsvService<T> csvService(JpaRepository<T, Long> repository, Class<T> type) {
        return new CsvService<>(repository, type);
    }
}
