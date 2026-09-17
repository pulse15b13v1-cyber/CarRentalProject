package security;

import Model.Utilisateur;

public final class AuthorizationUtils {
    private static final ThreadLocal<Utilisateur> CURRENT_USER = new ThreadLocal<>();

    private AuthorizationUtils() {
    }

    public static void setCurrentUser(Utilisateur utilisateur) {
        CURRENT_USER.set(utilisateur);
    }

    public static void clearCurrentUser() {
        CURRENT_USER.remove();
    }

    public static Utilisateur currentUser() {
        return CURRENT_USER.get();
    }

    public static boolean isAdmin(Utilisateur utilisateur) {
        return utilisateur != null && "admin".equalsIgnoreCase(utilisateur.getTypeUtilisateur());
    }

    public static boolean isClient(Utilisateur utilisateur) {
        return utilisateur != null && "client".equalsIgnoreCase(utilisateur.getTypeUtilisateur());
    }

    public static void requireAdmin(Utilisateur utilisateur) {
        if (!isAdmin(utilisateur)) {
            throw new SecurityException("Accès administrateur requis.");
        }
    }

    public static void requireAuthenticated(Utilisateur utilisateur) {
        if (!isAdmin(utilisateur) && !isClient(utilisateur)) {
            throw new SecurityException("Utilisateur authentifié requis.");
        }
    }
}