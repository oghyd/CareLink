package src.utils;

import src.model.User;

public class SessionManager {
    private static User currentUser = null;

    private SessionManager() {}

    public static boolean login(String email, String motDePasse) {
        return false;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}
