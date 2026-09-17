package security;

import at.favre.lib.crypto.bcrypt.BCrypt;

public final class PasswordUtils {
    private static final int COST = 12;

    private PasswordUtils() {
    }

    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(COST, password.toCharArray());
    }

    public static boolean verifyPassword(String password, String passwordHash) {
        return BCrypt.verifyer().verify(password.toCharArray(), passwordHash).verified;
    }
}