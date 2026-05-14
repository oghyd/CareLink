package src.model;

public class Etudiant extends User {
    private String matricule;
    private String typeHandicap;
    private String telephone;

    public Etudiant(int id, String nom, String prenom, String email, String motDePasse, boolean actif,
                    String matricule, String typeHandicap, String telephone) {
        super(id, nom, prenom, email, motDePasse, actif);
        this.matricule = matricule;
        this.typeHandicap = typeHandicap;
        this.telephone = telephone;
    }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) {
         this.matricule = matricule; }
    public String getTypeHandicap() { return typeHandicap; }
    public void setTypeHandicap(String typeHandicap) {
         this.typeHandicap = typeHandicap; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { 
        this.telephone = telephone; }

    @Override
    public boolean seConnecter(String email, String motDePasse) { 
        return false; }
    @Override
    public void seDeconnecter() { }
    @Override
    public boolean modifierCompte(String nom, String prenom, String email, String motDePasse) { return false; }

    public boolean creerDemande(String titre, String description, TypeDemande type) { return false; }
    public boolean suivreDemande(int idDemande) { return false; }
    public boolean creerReclamation(String objet, String description) { return false; }
}
