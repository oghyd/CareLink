package src.model;

import java.util.Date;

public class PieceJustificative {
    private int id;
    private String nomFichier;
    private String chemin;
    private Date dateAjout;

    public PieceJustificative(int id, String nomFichier, String chemin, Date dateAjout) {
        this.id = id;
        this.nomFichier = nomFichier;
        this.chemin = chemin;
        this.dateAjout = dateAjout;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNomFichier() { return nomFichier; }
    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }
    public String getChemin() { return chemin; }
    public void setChemin(String chemin) { this.chemin = chemin; }
    public Date getDateAjout() { return dateAjout; }
    public void setDateAjout(Date dateAjout) { this.dateAjout = dateAjout; }

    public boolean ajouter() { return false; }
    public boolean supprimer() { return false; }
    public String consulter() { return null; }
}
