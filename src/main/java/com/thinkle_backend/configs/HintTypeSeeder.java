package com.thinkle_backend.configs;

import com.thinkle_backend.models.HintType;
import com.thinkle_backend.repositories.HintTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HintTypeSeeder {

    @Bean
    public CommandLineRunner seedHintTypes(HintTypeRepository hintTypeRepository) {
        return args -> {
            insertIfNotExists(hintTypeRepository, "FUN_FACT", "Fun Fact");
            insertIfNotExists(hintTypeRepository, "DEFINITION", "Definition");
            insertIfNotExists(hintTypeRepository, "SYNONYM", "Synonym");
        };
    }

    private void insertIfNotExists(HintTypeRepository repo, String type, String displayName) {
        if (repo.existsByHintTypeIgnoreCase(type).isPresent() && !repo.existsByHintTypeIgnoreCase(type).get()) {
            HintType hintType = new HintType();
            hintType.setHintType(type);
            hintType.setDisplayName(displayName);
            repo.save(hintType);
        }
    }
}
