package com.vitzemtsov.flowmanager.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class ObjectNameGenerator {

    public String generate(UUID fileId, String originalFileName) {
        LocalDate now = LocalDate.now();

        return "%d/%02d/%02d/%s/%s".formatted(
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth(),
                fileId,
                sanitize(originalFileName)
        );
    }

    private String sanitize(String name) {
        if (name == null || name.isBlank()) {
            return "file";
        }
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
