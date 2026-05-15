-- ============================================================
--  CareLink — Complete Database Schema
-- ============================================================

DROP DATABASE IF EXISTS carelink;

CREATE DATABASE carelink
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE carelink;

-- ──────────────────────────────────────────────────────────────
--  1. USERS — single-table inheritance (role = etudiant | admin)
-- ──────────────────────────────────────────────────────────────

CREATE TABLE users (
    id             INT           AUTO_INCREMENT PRIMARY KEY,
    nom            VARCHAR(100)  NOT NULL,
    prenom         VARCHAR(100)  NOT NULL,
    email          VARCHAR(255)  NOT NULL UNIQUE,
    mot_de_passe   VARCHAR(255)  NOT NULL,
    actif          BOOLEAN       NOT NULL DEFAULT FALSE,
    role           ENUM('etudiant', 'admin') NOT NULL,

    matricule      VARCHAR(50)   NULL UNIQUE,
    type_handicap  VARCHAR(255)  NULL,
    telephone      VARCHAR(20)   NULL,

    fonction       VARCHAR(255)  NULL,

    INDEX idx_users_role       (role),
    INDEX idx_users_actif      (actif),
    INDEX idx_users_email      (email)
) ENGINE=InnoDB;

-- ──────────────────────────────────────────────────────────────
--  2. DEMANDES
-- ──────────────────────────────────────────────────────────────

CREATE TABLE demandes (
    id             INT           AUTO_INCREMENT PRIMARY KEY,
    titre          VARCHAR(255)  NOT NULL,
    description    TEXT          NULL,
    type           ENUM('AMENAGEMENT_EXAMEN', 'ACCESSIBILITE', 'ACCOMPAGNEMENT', 'AUTRE') NOT NULL,
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
--  SEED DATA
-- ============================================================

-- ── Admin (id=1) — Login: admin@carelink.ma / admin123

INSERT INTO users (nom, prenom, email, mot_de_passe, actif, role, fonction)
VALUES ('Admin', 'CareLink', 'admin@carelink.ma', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', TRUE, 'admin', 'Responsable inclusion');

-- ── Active students (id=2,3,4,5)

INSERT INTO users (nom, prenom, email, mot_de_passe, actif, role, matricule, type_handicap, telephone)
VALUES
    ('Etudiant1', 'etud1', 'etud1@carelink.ma', 'ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae', TRUE, 'etudiant', 'MAT-2025-001', 'Moteur',  '0611111111'),
    ('Etudiant2', 'etud2', 'etud2@carelink.ma', 'ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae', TRUE, 'etudiant', 'MAT-2025-002', 'Visuel',  '0622222222'),
    ('Etudiant3', 'etud3', 'etud3@carelink.ma', 'ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae', TRUE, 'etudiant', 'MAT-2025-003', 'Visuel',  '0622222222'),
    ('Testeur', 'Etudiant', 'etudiant@carelink.ma',  '36432aa0a54a06c13ca2ff16cef78ca66e1cd5fa869f36791a79bc4f4c5d8120', TRUE, 'etudiant', 'MAT-2025-004', 'Auditif', '0600000000');

-- ── Inactive student (id=6)

INSERT INTO users (nom, prenom, email, mot_de_passe, actif, role, matricule, type_handicap, telephone)
VALUES ('EnAttente', 'Compte', 'attente@carelink.ma', 'test123', FALSE, 'etudiant', 'MAT-2025-099', 'Moteur', '0699999999');

-- ── Demandes — one per statut, spread across types and students

INSERT INTO demandes (titre, description, type, date_creation, statut, etudiant_id) VALUES
    ('Aménagement examen S1',      'Besoin de temps supplémentaire pour les examens du S1.',              'AMENAGEMENT_EXAMEN', '2025-09-15 10:00:00', 'CREE',                   2),
    ('Rampe accès bâtiment B',     'Le bâtiment B n''a pas de rampe d''accès au 2e étage.',              'ACCESSIBILITE',      '2025-10-01 14:30:00', 'EN_COURS_DE_TRAITEMENT', 2),
    ('Preneur de notes TD',        'Besoin d''un accompagnant pour les TD de physique.',                  'ACCOMPAGNEMENT',     '2025-10-10 09:00:00', 'TRAITEE',                3),
    ('Temps supplémentaire partiel','Demande de tiers-temps pour les contrôles continus.',                'AMENAGEMENT_EXAMEN', '2025-11-05 11:00:00', 'REJETE',                 4),
    ('Accompagnement cours maths', 'Besoin d''un preneur de notes pour le module Analyse.',               'ACCOMPAGNEMENT',     '2026-01-20 08:30:00', 'CREE',                   3),
    ('Accessibilité parking',      'Aucune place handicapée au parking du campus nord.',                  'ACCESSIBILITE',      '2026-02-12 16:00:00', 'EN_COURS_DE_TRAITEMENT', 4);

-- ── Pièces justificatives

INSERT INTO pieces_justificatives (nom_fichier, chemin, date_ajout, demande_id) VALUES
    ('certificat_medical.pdf',   '/uploads/pj/certificat_medical.pdf',   '2025-09-15 10:05:00', 1),
    ('carte_handicap.pdf',       '/uploads/pj/carte_handicap.pdf',       '2025-09-15 10:06:00', 1),
    ('photo_batiment_b.jpg',     '/uploads/pj/photo_batiment_b.jpg',     '2025-10-01 14:35:00', 2),
    ('attestation_accompagnement.pdf', '/uploads/pj/attestation_accompagnement.pdf', '2025-10-10 09:10:00', 3),
    ('ordonnance.pdf',           '/uploads/pj/ordonnance.pdf',           '2025-11-05 11:05:00', 4),
    ('attestation_handicap.pdf', '/uploads/pj/attestation_handicap.pdf', '2026-01-20 08:35:00', 5);

-- ── Réclamations — one per statut

INSERT INTO reclamations (objet, description, date_creation, statut, etudiant_id) VALUES
    ('Délai de réponse trop long', 'Ma demande #1 est sans réponse depuis 3 semaines.',             '2025-10-05 09:00:00', 'CREE',                   2),
    ('Rampe toujours absente',     'La demande #2 a été acceptée mais rien n''a changé.',           '2025-11-15 13:00:00', 'EN_COURS_DE_TRAITEMENT', 2),
    ('Accompagnant non affecté',   'La demande #3 est marquée traitée mais personne ne m''a contacté.', '2025-12-01 10:00:00', 'TRAITEE',           3),
    ('Refus injustifié',           'Mon dossier tiers-temps a été refusé sans explication.',         '2025-12-20 15:00:00', 'REJETE',                 4);


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
