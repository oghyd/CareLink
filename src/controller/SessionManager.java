package src.utils;

import src.model.Admin;
import src.model.Etudiant;
import src.model.User;

/**
 * Gère l'utilisateur connecté au niveau de l'application.
 * Singleton très simple : les controllers appellent getCurrentUser() pour savoir
 * qui est connecté et appliquer les règles métier (admin vs étudiant).
 */
public class SessionManager {

    private static User currentUser = null;

    // Empêche l'instanciation (classe statique)
    private SessionManager() {}

    // Enregistre l'utilisateur comme connecté — appelé par AuthController.login()
    public static void login(User user) {
        currentUser = user;
    }

    // Déconnecte l'utilisateur courant
    public static void logout() {
        currentUser = null;
    }

    // Retourne l'utilisateur courant, ou null si personne n'est connecté
    public static User getCurrentUser() {
        return currentUser;
    }

    // Vérifie qu'un utilisateur est connecté
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    // Vérifie que l'utilisateur connecté est un admin
    public static boolean isAdmin() {
        return currentUser instanceof Admin;
    }

    // Vérifie que l'utilisateur connecté est un étudiant
    public static boolean isEtudiant() {
        return currentUser instanceof Etudiant;
    }

    // Raccourci pratique : retourne l'id de l'utilisateur connecté, -1 si personne
    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
}
