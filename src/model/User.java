package src.model;

public abstract class User {
    protected int id;
    protected String nom;
    protected String prenom;
    protected String email;
    protected String motDePasse;
    protected boolean actif;

    public User(int id, String nom, String prenom, String email, String motDePasse, boolean actif) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.actif = actif;
    }

    public int getId() { 
        return id; }
    public void setId(int id) { 
        this.id = id; }
    public String getNom() {
         return nom; }
    public void setNom(String nom) {
         this.nom = nom; }
    public String getPrenom() {
         return prenom; }
    public void setPrenom(String prenom) { 
        this.prenom = prenom; }
    public String getEmail() {
         return email; }
    public void setEmail(String email) {
         this.email = email; }
    public String getMotDePasse() { 
        return motDePasse; }
    public void setMotDePasse(String motDePasse) { 
        this.motDePasse = motDePasse; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) {
         this.actif = actif; }

    public abstract boolean seConnecter(String email, String motDePasse);
    public abstract void seDeconnecter();
    public abstract boolean modifierCompte(String nom, String prenom, String email, String motDePasse);
}
