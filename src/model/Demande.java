package src.model;

import java.util.Date;
import src.dao.DemandeDAO;

public class Demande {
    private int id;
    private String titre;
    private String description;
    private TypeDemande type;
    private Date dateCreation;
    private StatutDemande statut;
    private int etudiantId;
    
    public Demande(int id, String titre, String description, TypeDemande type, Date dateCreation, StatutDemande statut, int etudiantId) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.type = type;
        this.dateCreation = dateCreation;
        this.statut = statut;
        this.etudiantId = etudiantId;
    }
    
    public Demande(int id, String titre, String description, TypeDemande type,
                   Date dateCreation, StatutDemande statut) {
        this(id, titre, description, type, dateCreation, statut, 0);
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
    public int getEtudiantId() { return etudiantId; }
    public void setEtudiantId(int etudiantId) { this.etudiantId = etudiantId; }
    
    public boolean ajouter() {
        return new DemandeDAO().create(this, this.etudiantId);
    }

    public boolean modifier() {
        return new DemandeDAO().update(this);
    }

    public boolean changerStatut(StatutDemande nouveauStatut) {
        boolean ok = new DemandeDAO().changerStatut(this.id, nouveauStatut);
        if (ok) this.statut = nouveauStatut;
        return ok;
    }
}
