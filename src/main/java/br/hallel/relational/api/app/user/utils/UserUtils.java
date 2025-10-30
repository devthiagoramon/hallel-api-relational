package br.hallel.relational.api.app.user.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.Date;

public class UserUtils {
    public static String[] splitFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) return new String[]{"", ""};
        String[] parts = fullName.split(" ", 2);
        return new String[]{parts[0], parts.length > 1 ? parts[1] : ""};
    }

    public static Integer getAge(Date birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate.toInstant()
                .atOffset(ZoneOffset.UTC)
                .toLocalDate(),
                LocalDate.now())
                .getYears();
    }
}
