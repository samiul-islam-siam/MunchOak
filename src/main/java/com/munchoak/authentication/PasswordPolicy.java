package com.munchoak.authentication;

public final class PasswordPolicy {
    private PasswordPolicy() {}

    // One consistent special set across the whole app
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:'\",.<>/?";

    public static boolean isValid(String password) {
        if (password == null) return false;
        if (password.length() < 8) return false;

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> SPECIAL.indexOf(ch) >= 0);

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public static String strengthWord(String password) {
        if (password == null || password.isEmpty()) return "";

        int score = 0;
        if (password.length() >= 8) score++;
        if (password.chars().anyMatch(Character::isUpperCase)) score++;
        if (password.chars().anyMatch(Character::isLowerCase)) score++;
        if (password.chars().anyMatch(Character::isDigit)) score++;
        if (password.chars().anyMatch(ch -> SPECIAL.indexOf(ch) >= 0)) score++;

        if (score <= 2) return "Weak";
        if (score <= 4) return "Normal";
        return "Strong";
    }

    public static String rulesText() {
        return "Password must be at least 8 characters long,\n" +
                "and include uppercase, lowercase, numbers, and special characters.";
    }
}