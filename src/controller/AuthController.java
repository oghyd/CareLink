package src.controller;

import src.dao.UserDAO;
import src.model.User;
import src.utils.SessionManager;

/**
 * Gère la connexion et la déconnexion des utilisateurs.
 * Délègue la vérification email + mot de passe au UserDAO,
 * puis enregistre l'utilisateur dans SessionManager.
 */
public class AuthController {

    private UserDAO userDAO;

    public AuthController() {
        this.userDAO = new UserDAO();
    }

    // Authentifie un utilisateur par email + mot de passe.
    // Retourne le User si succès (et enregistre la session), null sinon.
    // Le DAO filtre déjà les comptes inactifs (actif = TRUE dans la requête).
    public User login(String email, String motDePasse) {
        if (email == null || email.isEmpty() || motDePasse == null || motDePasse.isEmpty()) {
            return null;
        }

        User user = userDAO.authenticate(email, motDePasse);
        if (user != null) {
            SessionManager.login(user);
        }
        return user;
    }

    // Déconnecte l'utilisateur courant
    public void logout() {
        SessionManager.logout();
    }

    // Retourne l'utilisateur actuellement connecté (null si personne)
    public User getCurrentUser() {
        return SessionManager.getCurrentUser();
    }
}
