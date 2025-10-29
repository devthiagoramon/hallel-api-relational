package br.hallel.relational.api.app.user.utils;

public class UserUtils {
    public static String[] splitFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) return new String[]{"", ""};
        String[] parts = fullName.split(" ", 2);
        return new String[]{parts[0], parts.length > 1 ? parts[1] : ""};
    }
}
