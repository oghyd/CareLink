package src.controller;

import src.dao.AdminDAO;
import src.dao.EtudiantDAO;
import src.dao.UserDAO;
import src.model.Admin;
import src.model.Etudiant;
import src.model.User;
import src.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Gère le cycle de vie des comptes utilisateurs.
 *
 * Règle métier (issue des UC) :
 *  - Compte créé par un Étudiant (auto-inscription) → actif = FALSE, en attente de validation admin.
 *    C'est déjà géré dans EtudiantDAO.create() qui insère avec actif=FALSE.
 *  - Compte créé directement par un Admin → actif = TRUE immédiatement.
 *    C'est déjà géré dans AdminDAO.create() et on force l'activation pour l'étudiant dans creerCompteEtudiantParAdmin().
 */
public class CompteController {

    private UserDAO userDAO;
    private EtudiantDAO etudiantDAO;
    private AdminDAO adminDAO;

    public CompteController() {
        this.userDAO = new UserDAO();
        this.etudiantDAO = new EtudiantDAO();
        this.adminDAO = new AdminDAO();
    }

    // Auto-inscription d'un étudiant (depuis la page Inscription).
    // Le compte est créé INACTIF, un admin doit l'activer ensuite.
    public boolean sInscrire(Etudiant etudiant) {
        if (etudiant == null) return false;
        // EtudiantDAO.create() force actif=FALSE côté SQL
        return etudiantDAO.create(etudiant);
    }

    // Création d'un compte étudiant par un admin : on crée puis on active dans la foulée.
    public boolean creerCompteEtudiantParAdmin(Etudiant etudiant) {
        if (!SessionManager.isAdmin()) return false; // admin only
        if (etudiant == null) return false;

        boolean created = etudiantDAO.create(etudiant);
        if (!created) return false;

        // Récupère l'id fraîchement créé par matricule, puis active le compte
        Etudiant saved = etudiantDAO.findByMatricule(etudiant.getMatricule());
        if (saved == null) return false;
        return userDAO.setActif(saved.getId(), true);
    }

    // Création d'un compte admin — admin only, compte actif par défaut
    public boolean creerCompteAdmin(Admin admin) {
        if (!SessionManager.isAdmin()) return false;
        if (admin == null) return false;
        // AdminDAO.create() force actif=TRUE côté SQL
        return adminDAO.create(admin);
    }

    // Active un compte (typiquement un compte étudiant en attente) — admin only
    public boolean activerCompte(int userId) {
        if (!SessionManager.isAdmin()) return false;
        return userDAO.setActif(userId, true);
    }

    // Désactive un compte — admin only
    public boolean desactiverCompte(int userId) {
        if (!SessionManager.isAdmin()) return false;
        return userDAO.setActif(userId, false);
    }

    // Supprime un compte — admin only
    public boolean supprimerCompte(int userId) {
        if (!SessionManager.isAdmin()) return false;
        return userDAO.delete(userId);
    }

    // Liste tous les comptes (admins + étudiants) — admin only
    public List<User> listerComptes() {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return userDAO.findAll();
    }

    // Liste uniquement les étudiants — admin only
    public List<Etudiant> listerEtudiants() {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return etudiantDAO.findAll();
    }

    // Liste uniquement les admins — admin only
    public List<Admin> listerAdmins() {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return adminDAO.findAll();
    }

    // Liste les comptes en attente d'activation (étudiants inactifs) — admin only
    public List<Etudiant> listerComptesEnAttente() {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        List<Etudiant> enAttente = new ArrayList<>();
        for (Etudiant e : etudiantDAO.findAll()) {
            if (!e.isActif()) enAttente.add(e);
        }
        return enAttente;
    }

    // Modifier son propre compte (étudiant ou admin)
    public boolean modifierMonCompte(User user) {
        if (!SessionManager.isLoggedIn()) return false;
        if (user == null) return false;
        // On n'autorise la modification que sur son propre compte
        if (user.getId() != SessionManager.getCurrentUserId()) return false;

        if (user instanceof Etudiant) {
            return etudiantDAO.update((Etudiant) user);
        } else if (user instanceof Admin) {
            return adminDAO.update((Admin) user);
        }
        return false;
    }
}
