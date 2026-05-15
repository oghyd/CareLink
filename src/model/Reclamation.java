package src.model;

import java.util.Date;
import src.dao.ReclamationDAO;

public class Reclamation {
    private int id;
    private String objet;
    private String description;
    private Date dateCreation;
    private StatutDemande statut;
    private int etudiantId;

    public Reclamation(int id, String objet, String description, Date dateCreation, StatutDemande statut, int etudiantId) {
        this.id = id;
        this.objet = objet;
        this.description = description;
        this.dateCreation = dateCreation;
        this.statut = statut;
        this.etudiantId = etudiantId;
    }
    
    public Reclamation(int id, String objet, String description, Date dateCreation, StatutDemande statut) {
        this(id, objet, description, dateCreation, statut, 0);
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
    public int getEtudiantId() { return etudiantId; }
    public void setEtudiantId(int etudiantId) { this.etudiantId = etudiantId; }

    public boolean ajouter() {
        return new ReclamationDAO().create(this, this.etudiantId);
    }

    public boolean modifier() {
        return new ReclamationDAO().update(this);
    }

    public boolean changerStatut(StatutDemande nouveauStatut) {
        boolean ok = new ReclamationDAO().changerStatut(this.id, nouveauStatut);
        if (ok) this.statut = nouveauStatut;
        return ok;
    }
}
