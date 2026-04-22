package src.model;

import java.util.Date;

public class Demande {
    private int id;
    private String titre;
    private String description;
    private TypeDemande type;
    private Date dateCreation;
    private StatutDemande statut;

    public Demande(int id, String titre, String description, TypeDemande type, Date dateCreation, StatutDemande statut) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.type = type;
        this.dateCreation = dateCreation;
        this.statut = statut;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TypeDemande getType() { return type; }
    public void setType(TypeDemande type) { this.type = type; }
    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
    public StatutDemande getStatut() { return statut; }
    public void setStatut(StatutDemande statut) { this.statut = statut; }

    public boolean ajouter() { return false; }
    public boolean modifier() { return false; }
    public boolean changerStatut(StatutDemande nouveauStatut) { return false; }
}
