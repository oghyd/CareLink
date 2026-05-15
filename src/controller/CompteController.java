package src.controller;

import src.dao.AdminDAO;
import src.dao.EtudiantDAO;
import src.dao.UserDAO;
import src.model.Admin;
import src.model.Etudiant;
import src.model.User;
import src.utils.HashUtil;
import src.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Gère le cycle de vie des comptes utilisateurs.
 *
 * Règle métier (issue des UC) :
 *  - Compte créé par un Étudiant (auto-inscription) → actif = FALSE, en attente de validation admin.
 *  - Compte créé directement par un Admin → actif = TRUE immédiatement.
 *
 * Tous les mots de passe sont hashés en SHA-256 avant d'être stockés.
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

    // Auto-inscription d'un étudiant (depuis la page Inscription)
    public boolean sInscrire(Etudiant etudiant) {
        if (etudiant == null) return false;
        etudiant.setMotDePasse(HashUtil.sha256(etudiant.getMotDePasse()));
        return etudiantDAO.create(etudiant);
    }

    // Création d'un compte étudiant par un admin : on crée puis on active.
    public boolean creerCompteEtudiantParAdmin(Etudiant etudiant) {
        if (!SessionManager.isAdmin()) return false;
        if (etudiant == null) return false;

        etudiant.setMotDePasse(HashUtil.sha256(etudiant.getMotDePasse()));
        boolean created = etudiantDAO.create(etudiant);
        if (!created) return false;

        Etudiant saved = etudiantDAO.findByMatricule(etudiant.getMatricule());
        if (saved == null) return false;
        return userDAO.setActif(saved.getId(), true);
    }

    // Création d'un compte admin
    public boolean creerCompteAdmin(Admin admin) {
        if (!SessionManager.isAdmin()) return false;
        if (admin == null) return false;
        admin.setMotDePasse(HashUtil.sha256(admin.getMotDePasse()));
        return adminDAO.create(admin);
    }

    // Active un compte
    public boolean activerCompte(int userId) {
        if (!SessionManager.isAdmin()) return false;
        return userDAO.setActif(userId, true);
    }

    // Désactive un compte
    public boolean desactiverCompte(int userId) {
        if (!SessionManager.isAdmin()) return false;
        return userDAO.setActif(userId, false);
    }

    // Supprime un compte
    public boolean supprimerCompte(int userId) {
        if (!SessionManager.isAdmin()) return false;
        return userDAO.delete(userId);
    }

    // Liste tous les comptes
    public List<User> listerComptes() {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return userDAO.findAll();
    }

    // Liste uniquement les étudiants
    public List<Etudiant> listerEtudiants() {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return etudiantDAO.findAll();
    }

    // Liste uniquement les admins
    public List<Admin> listerAdmins() {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        return adminDAO.findAll();
    }

    // Liste les comptes en attente d'activation
    public List<Etudiant> listerComptesEnAttente() {
        if (!SessionManager.isAdmin()) return new ArrayList<>();
        List<Etudiant> enAttente = new ArrayList<>();
        for (Etudiant e : etudiantDAO.findAll()) {
            if (!e.isActif()) enAttente.add(e);
        }
        return enAttente;
    }

    // Modifier son propre compte
    public boolean modifierMonCompte(User user) {
        if (!SessionManager.isLoggedIn()) return false;
        if (user == null) return false;
        if (user.getId() != SessionManager.getCurrentUserId()) return false;

        if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
            user.setMotDePasse(HashUtil.sha256(user.getMotDePasse()));
        }

        if (user instanceof Etudiant) {
            return etudiantDAO.update((Etudiant) user);
        } else if (user instanceof Admin) {
            return adminDAO.update((Admin) user);
        }
        return false;
    }

    // ============================================================
    // MÉTHODES AJOUTÉES PAR IDRISS POUR GestionComptesController
    // ============================================================

    /**
     * Crée un compte étudiant (actif = false, en attente d'activation)
     * Appelé depuis l'interface admin quand il ajoute un étudiant
     */
    public boolean creerCompteEtudiant(String nom, String prenom, String email, String motDePasse,
                                        String matricule, String typeHandicap, String telephone) {
        Etudiant etudiant = new Etudiant(0, nom, prenom, email, motDePasse, false,
                                          matricule, typeHandicap, telephone);
        etudiant.setMotDePasse(HashUtil.sha256(motDePasse));
        return etudiantDAO.create(etudiant);
    }

    /**
     * Crée un compte admin (actif = true directement)
     * Appelé depuis l'interface admin quand il ajoute un admin
     */
    public boolean creerCompteAdmin(String nom, String prenom, String email, String motDePasse,
                                     String fonction) {
        Admin admin = new Admin(0, nom, prenom, email, motDePasse, true, fonction);
        admin.setMotDePasse(HashUtil.sha256(motDePasse));
        return adminDAO.create(admin);
    }
}