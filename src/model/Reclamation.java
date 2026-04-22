package src.model;

import java.util.Date;

public class Reclamation {
    private int id;
    private String objet;
    private String description;
    private Date dateCreation;
    private StatutDemande statut;

    public Reclamation(int id, String objet, String description, Date dateCreation, StatutDemande statut) {
        this.id = id;
        this.objet = objet;
        this.description = description;
        this.dateCreation = dateCreation;
        this.statut = statut;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getObjet() { return objet; }
    public void setObjet(String objet) { this.objet = objet; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
    public StatutDemande getStatut() { return statut; }
    public void setStatut(StatutDemande statut) { this.statut = statut; }

    public boolean ajouter() { return false; }
    public boolean modifier() { return false; }
    public boolean changerStatut(StatutDemande nouveauStatut) { return false; }
}
