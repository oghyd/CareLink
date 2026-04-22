package src.model;

import java.util.List;

public class Admin extends User {
    private String fonction;

    public Admin(int id, String nom, String prenom, String email, String motDePasse, boolean actif, String fonction) {
        super(id, nom, prenom, email, motDePasse, actif);
        this.fonction = fonction;
    }

    public String getFonction() { return fonction; }
    public void setFonction(String fonction) { this.fonction = fonction; }

    @Override
    public boolean seConnecter(String email, String motDePasse) { return false; }
    @Override
    public void seDeconnecter() { }
    @Override
    public boolean modifierCompte(String nom, String prenom, String email, String motDePasse) { return false; }

    public List<Demande> listerDemandes() { return null; }
    public boolean traiterDemande(int idDemande, String nouveauStatut) { return false; }
    public void consulterTableauDeBord() { }
    public List<?> filtrerStatistiques(java.util.Date date, String type) { return null; }
}
