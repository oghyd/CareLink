package src.controller;

import src.dao.UserDAO;
import src.model.User;
import src.utils.HashUtil;
import src.utils.SessionManager;

/**
 * Gère la connexion et la déconnexion des utilisateurs.
 * Hash le mot de passe avant de l'envoyer au DAO (les mots de passe sont stockés
 * en SHA-256 dans la BDD, cf. schema.sql).
 */
public class AuthController {

    private UserDAO userDAO;

    public AuthController() {
        this.userDAO = new UserDAO();
    }

    // Authentifie un utilisateur par email + mot de passe.
    // Le mot de passe est hashé ici avant comparaison avec celui stocké en BDD.
    public User login(String email, String motDePasse) {
        if (email == null || email.isEmpty() || motDePasse == null || motDePasse.isEmpty()) {
            return null;
        }
        String hash = HashUtil.sha256(motDePasse);
        User user = userDAO.authenticate(email, hash);
        if (user != null) {
            SessionManager.login(user);
        }
        return user;
    }

    public void logout() {
        SessionManager.logout();
    }

    public User getCurrentUser() {
        return SessionManager.getCurrentUser();
    }
}