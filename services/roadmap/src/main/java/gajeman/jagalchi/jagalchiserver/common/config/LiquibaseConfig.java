package gajeman.jagalchi.jagalchiserver.common.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Bean
    public SpringLiquibase liquibase(
            DataSource dataSource,
            @Value("${spring.liquibase.change-log}") String changeLog,
            @Value("${spring.liquibase.contexts:}") String contexts,
            @Value("${spring.liquibase.default-schema:}") String defaultSchema,
            @Value("${spring.liquibase.drop-first:false}") boolean dropFirst,
            @Value("${spring.liquibase.enabled:true}") boolean enabled) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        if (!contexts.isBlank()) {
            liquibase.setContexts(contexts);
        }
        if (!defaultSchema.isBlank()) {
            liquibase.setDefaultSchema(defaultSchema);
        }
        liquibase.setDropFirst(dropFirst);
        liquibase.setShouldRun(enabled);
        return liquibase;
    }
}
