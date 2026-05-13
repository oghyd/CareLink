-- ============================================================
--  CareLink — Complete Database Schema
--  
--  Run:  mysql -u root -p < schema.sql
--
--  Verified against every SQL statement in:
--    UserDAO, AdminDAO, EtudiantDAO,
--    DemandeDAO, ReclamationDAO, PieceJustificativeDAO
-- ============================================================

DROP DATABASE IF EXISTS carelink;

CREATE DATABASE carelink
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE carelink;

-- ──────────────────────────────────────────────────────────────
--  1. USERS — single-table inheritance (role = etudiant | admin)
--
--  Queried by:
--    UserDAO.authenticate()   → WHERE email = ? AND mot_de_passe = ? AND actif = TRUE
--    UserDAO.findAll()        → SELECT * FROM users
--    UserDAO.findById()       → WHERE id = ?
--    UserDAO.findByEmail()    → WHERE email = ?
--    UserDAO.setActif()       → UPDATE ... SET actif = ? WHERE id = ?
--    UserDAO.delete()         → DELETE ... WHERE id = ?
--    AdminDAO.*               → WHERE role = 'admin'
--    EtudiantDAO.*            → WHERE role = 'etudiant'
--    EtudiantDAO.findByMatricule() → WHERE matricule = ?
-- ──────────────────────────────────────────────────────────────

CREATE TABLE users (
    id             INT           AUTO_INCREMENT PRIMARY KEY,
    nom            VARCHAR(100)  NOT NULL,
    prenom         VARCHAR(100)  NOT NULL,
    email          VARCHAR(255)  NOT NULL UNIQUE,
    mot_de_passe   VARCHAR(255)  NOT NULL,
    actif          BOOLEAN       NOT NULL DEFAULT FALSE,
    role           ENUM('etudiant', 'admin') NOT NULL,

    -- Étudiant-specific columns (NULL when role = 'admin')
    matricule      VARCHAR(50)   NULL UNIQUE,
    type_handicap  VARCHAR(255)  NULL,
    telephone      VARCHAR(20)   NULL,

    -- Admin-specific column (NULL when role = 'etudiant')
    fonction       VARCHAR(255)  NULL,

    INDEX idx_users_role       (role),
    INDEX idx_users_actif      (actif),
    INDEX idx_users_email      (email)
) ENGINE=InnoDB;

-- ──────────────────────────────────────────────────────────────
--  2. DEMANDES
--
--  Queried by:
--    DemandeDAO.create()        → INSERT ... (titre, description, type, date_creation, statut, etudiant_id)
--    DemandeDAO.findById()      → WHERE id = ?
--    DemandeDAO.findAll()       → ORDER BY date_creation DESC
--    DemandeDAO.findByEtudiant()→ WHERE etudiant_id = ? ORDER BY date_creation DESC
--    DemandeDAO.findByStatut()  → WHERE statut = ?
--    DemandeDAO.findByType()    → WHERE type = ?
--    DemandeDAO.findByAnnee()   → WHERE YEAR(date_creation) = ?
--    DemandeDAO.countByStatut() → SELECT COUNT(*) ... WHERE statut = ?
--    DemandeDAO.update()        → UPDATE ... SET titre=?, description=?, type=? WHERE id=?
--    DemandeDAO.changerStatut() → UPDATE ... SET statut=? WHERE id=?
--    DemandeDAO.delete()        → DELETE ... WHERE id=?
-- ──────────────────────────────────────────────────────────────

CREATE TABLE demandes (
    id             INT           AUTO_INCREMENT PRIMARY KEY,
    titre          VARCHAR(255)  NOT NULL,
    description    TEXT          NULL,
    type           ENUM('MATERIEL', 'LOGICIEL', 'ACCESSIBILITE', 'AUTRE') NOT NULL,
    date_creation  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statut         ENUM('CREE', 'EN_COURS_DE_TRAITEMENT', 'TRAITEE', 'REJETE') NOT NULL DEFAULT 'CREE',
    etudiant_id    INT           NOT NULL,

    INDEX idx_demandes_statut     (statut),
    INDEX idx_demandes_type       (type),
    INDEX idx_demandes_etudiant   (etudiant_id),
    INDEX idx_demandes_date       (date_creation),

    CONSTRAINT fk_demande_etudiant
        FOREIGN KEY (etudiant_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ──────────────────────────────────────────────────────────────
--  3. RECLAMATIONS
--
--  Queried by:
--    ReclamationDAO.create()        → INSERT ... (objet, description, date_creation, statut, etudiant_id)
--    ReclamationDAO.findById()      → WHERE id = ?
--    ReclamationDAO.findAll()       → ORDER BY date_creation DESC
--    ReclamationDAO.findByEtudiant()→ WHERE etudiant_id = ? ORDER BY date_creation DESC
--    ReclamationDAO.findByStatut()  → WHERE statut = ?
--    ReclamationDAO.findByAnnee()   → WHERE YEAR(date_creation) = ?
--    ReclamationDAO.countByStatut() → SELECT COUNT(*) ... WHERE statut = ?
--    ReclamationDAO.update()        → UPDATE ... SET objet=?, description=? WHERE id=?
--    ReclamationDAO.changerStatut() → UPDATE ... SET statut=? WHERE id=?
--    ReclamationDAO.delete()        → DELETE ... WHERE id=?
-- ──────────────────────────────────────────────────────────────

CREATE TABLE reclamations (
    id             INT           AUTO_INCREMENT PRIMARY KEY,
    objet          VARCHAR(255)  NOT NULL,
    description    TEXT          NULL,
    date_creation  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statut         ENUM('CREE', 'EN_COURS_DE_TRAITEMENT', 'TRAITEE', 'REJETE') NOT NULL DEFAULT 'CREE',
    etudiant_id    INT           NOT NULL,

    INDEX idx_reclamations_statut     (statut),
    INDEX idx_reclamations_etudiant   (etudiant_id),
    INDEX idx_reclamations_date       (date_creation),

    CONSTRAINT fk_reclamation_etudiant
        FOREIGN KEY (etudiant_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ──────────────────────────────────────────────────────────────
--  4. PIECES JUSTIFICATIVES
--
--  Queried by:
--    PieceJustificativeDAO.create()         → INSERT ... (nom_fichier, chemin, date_ajout, demande_id)
--    PieceJustificativeDAO.findById()       → WHERE id = ?
--    PieceJustificativeDAO.findByDemande()  → WHERE demande_id = ?
--    PieceJustificativeDAO.delete()         → DELETE ... WHERE id = ?
--    PieceJustificativeDAO.deleteByDemande()→ DELETE ... WHERE demande_id = ?
-- ──────────────────────────────────────────────────────────────

CREATE TABLE pieces_justificatives (
    id             INT           AUTO_INCREMENT PRIMARY KEY,
    nom_fichier    VARCHAR(255)  NOT NULL,
    chemin         VARCHAR(500)  NOT NULL,
    date_ajout     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    demande_id     INT           NOT NULL,

    INDEX idx_pieces_demande (demande_id),

    CONSTRAINT fk_piece_demande
        FOREIGN KEY (demande_id) REFERENCES demandes(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;


-- ============================================================
--  SEED DATA — covers every role, statut, type, and edge case
-- ============================================================

-- ── Admin (id=1) ─────────────────────────────────────────────
--    Login: admin@carelink.ma / admin123

INSERT INTO users (nom, prenom, email, mot_de_passe, actif, role, fonction)
VALUES ('Admin', 'CareLink', 'admin@carelink.ma', 'admin123', TRUE, 'admin', 'Responsable inclusion');

-- ── Active students (id=2,3,4) — can log in immediately ─────
--    Login: etud1@carelink.ma / test123  (etc.)

INSERT INTO users (nom, prenom, email, mot_de_passe, actif, role, matricule, type_handicap, telephone)
VALUES
    ('Etudiant1', 'etud1', 'etud1@carelink.ma', 'test123', TRUE, 'etudiant', 'MAT-2025-001', 'Moteur',  '0611111111'),
    ('Etudiant2', 'etud2', 'etud2@carelink.ma', 'test123', TRUE, 'etudiant', 'MAT-2025-002', 'Visuel',  '0622222222'),
    ('Etudiant3', 'etud3', 'etud3@carelink.ma', 'test123', TRUE, 'etudiant', 'MAT-2025-003', 'Visuel',  '0622222222'),
    ('Testeur', 'Etudiant', 'etudiant@carelink.ma',  'etudiant123', TRUE, 'etudiant', 'MAT-2025-004', 'Auditif', '0600000000');

-- ── Inactive student (id=5) — tests activation workflow ──────
--    Login blocked until admin activates

INSERT INTO users (nom, prenom, email, mot_de_passe, actif, role, matricule, type_handicap, telephone)
VALUES ('EnAttente', 'Compte', 'attente@carelink.ma', 'test123', FALSE, 'etudiant', 'MAT-2025-099', 'Moteur', '0699999999');

-- ── Demandes — one per statut, spread across types and students ──

INSERT INTO demandes (titre, description, type, date_creation, statut, etudiant_id) VALUES
    ('Aménagement examen S1', 'Besoin de temps supplémentaire pour les examens du S1.', 'AUTRE', '2025-09-15 10:00:00', 'CREE', 2),
    ('Rampe accès bâtiment B', 'Le bâtiment B n''a pas de rampe d''accès au 2e étage.', 'ACCESSIBILITE', '2025-10-01 14:30:00', 'EN_COURS_DE_TRAITEMENT', 2),
    ('Logiciel lecteur écran', 'Demande d''installation de JAWS sur les postes de la bibliothèque.', 'LOGICIEL', '2025-10-10 09:00:00', 'TRAITEE', 3),
    ('Matériel adapté salle TP', 'Clavier et souris ergonomiques pour les séances de TP.', 'MATERIEL', '2025-11-05 11:00:00', 'REJETE', 4),
    ('Accompagnement cours maths', 'Besoin d''un preneur de notes pour le module Analyse.', 'AUTRE', '2026-01-20 08:30:00', 'CREE', 3),
    ('Accessibilité parking', 'Aucune place handicapée au parking du campus nord.', 'ACCESSIBILITE', '2026-02-12 16:00:00', 'EN_COURS_DE_TRAITEMENT', 4);

-- ── Pièces justificatives — at least one per demande for testing ──

INSERT INTO pieces_justificatives (nom_fichier, chemin, date_ajout, demande_id) VALUES
    ('certificat_medical.pdf', '/uploads/pj/certificat_medical.pdf', '2025-09-15 10:05:00', 1),
    ('carte_handicap.pdf', '/uploads/pj/carte_handicap.pdf', '2025-09-15 10:06:00', 1),
    ('photo_batiment_b.jpg', '/uploads/pj/photo_batiment_b.jpg', '2025-10-01 14:35:00', 2),
    ('devis_jaws.pdf', '/uploads/pj/devis_jaws.pdf', '2025-10-10 09:10:00', 3),
    ('ordonnance.pdf', '/uploads/pj/ordonnance.pdf', '2025-11-05 11:05:00', 4),
    ('attestation_handicap.pdf', '/uploads/pj/attestation_handicap.pdf', '2026-01-20 08:35:00', 5);

-- ── Réclamations — one per statut ────────────────────────────

INSERT INTO reclamations (objet, description, date_creation, statut, etudiant_id) VALUES
    ('Délai de réponse trop long', 'Ma demande #1 est sans réponse depuis 3 semaines.', '2025-10-05 09:00:00', 'CREE', 2),
    ('Rampe toujours absente', 'La demande #2 a été acceptée mais rien n''a changé.', '2025-11-15 13:00:00', 'EN_COURS_DE_TRAITEMENT', 2),
    ('Logiciel non installé', 'JAWS est marqué traité mais les postes n''ont toujours rien.', '2025-12-01 10:00:00', 'TRAITEE', 3),
    ('Refus injustifié', 'Mon dossier matériel adapté a été refusé sans explication.', '2025-12-20 15:00:00', 'REJETE', 4);


-- ============================================================
--  VERIFICATION
-- ============================================================

SELECT 'users' AS tbl, COUNT(*) AS total FROM users
UNION ALL
SELECT 'demandes', COUNT(*) FROM demandes
UNION ALL
SELECT 'reclamations', COUNT(*) FROM reclamations
UNION ALL
SELECT 'pieces_justificatives', COUNT(*) FROM pieces_justificatives;
