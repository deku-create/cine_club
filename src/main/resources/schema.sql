-- ============================================================
--  CLUB VIDÉO — Script SQL pour XAMPP / phpMyAdmin
--  Auteur   : HIT-T
--  Usage    : Copiez ce fichier dans phpMyAdmin > onglet SQL
--             ou importez-le directement (Importer > club_video.sql)
-- ============================================================

-- 1. Création de la base
DROP DATABASE IF EXISTS club_video;
CREATE DATABASE club_video
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE club_video;

-- ── CATEGORIE ──────────────────────────────────────────────
CREATE TABLE CATEGORIE (
    id_categorie   INT          NOT NULL AUTO_INCREMENT,
    libelle        VARCHAR(60)  NOT NULL,
    CONSTRAINT pk_categorie PRIMARY KEY (id_categorie),
    CONSTRAINT uq_categorie_libelle UNIQUE (libelle)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── CASSETTE ───────────────────────────────────────────────
CREATE TABLE CASSETTE (
    no_cassette    INT            NOT NULL AUTO_INCREMENT,
    date_achat     DATE           NOT NULL,
    titre          VARCHAR(150)   NOT NULL,
    auteur         VARCHAR(120)   DEFAULT NULL,
    duree          INT            DEFAULT 0 COMMENT 'Durée en minutes',
    prix           DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    id_categorie   INT            NOT NULL,
    CONSTRAINT pk_cassette    PRIMARY KEY (no_cassette),
    CONSTRAINT fk_cass_cat    FOREIGN KEY (id_categorie)
        REFERENCES CATEGORIE(id_categorie)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── ABONNE ─────────────────────────────────────────────────
CREATE TABLE ABONNE (
    no_abonne          INT          NOT NULL AUTO_INCREMENT,
    nom_abonne         VARCHAR(100) NOT NULL,
    adresse_abonne     VARCHAR(255) DEFAULT NULL,
    date_abonnement    DATE         NOT NULL,
    date_entree        DATE         NOT NULL,
    nombre_location    INT          NOT NULL DEFAULT 0,
    CONSTRAINT pk_abonne         PRIMARY KEY (no_abonne),
    CONSTRAINT chk_nb_location   CHECK (nombre_location BETWEEN 0 AND 3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── LOCATION ───────────────────────────────────────────────
-- Règle : on ne garde que la DERNIÈRE date de location
-- pour un couple (abonné, cassette) → PK composite
CREATE TABLE LOCATION (
    no_abonne      INT  NOT NULL,
    no_cassette    INT  NOT NULL,
    date_location  DATE NOT NULL,
    date_retour    DATE DEFAULT NULL,
    CONSTRAINT pk_location   PRIMARY KEY (no_abonne, no_cassette),
    CONSTRAINT fk_loc_abo    FOREIGN KEY (no_abonne)
        REFERENCES ABONNE(no_abonne)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_loc_cass   FOREIGN KEY (no_cassette)
        REFERENCES CASSETTE(no_cassette)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── UTILISATEUR (authentification) ─────────────────────────
CREATE TABLE UTILISATEUR (
    id_user    INT          NOT NULL AUTO_INCREMENT,
    login      VARCHAR(50)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    role       ENUM('ADMIN','EMPLOYE') NOT NULL DEFAULT 'EMPLOYE',
    CONSTRAINT pk_utilisateur  PRIMARY KEY (id_user),
    CONSTRAINT uq_login        UNIQUE (login)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
--  DONNÉES INITIALES
-- ============================================================

-- Comptes (admin / admin123  et  employe / employe123)
INSERT INTO UTILISATEUR (login, password, role) VALUES
    ('admin',   'admin123',   'ADMIN'),
    ('employe', 'employe123', 'EMPLOYE');

-- Catégories
INSERT INTO CATEGORIE (libelle) VALUES
    ('Action'),
    ('Comédie'),
    ('Drame'),
    ('Science-Fiction'),
    ('Horreur'),
    ('Documentaire'),
    ('Animation'),
    ('Thriller'),
    ('Romance'),
    ('Aventure');

-- Cassettes de démonstration
INSERT INTO CASSETTE (date_achat, titre, auteur, duree, prix, id_categorie) VALUES
    ('2022-03-10', 'Terminator 2',    'James Cameron',     137, 2500.00, 1),
    ('2022-05-20', 'Le Parrain',      'Francis Coppola',   175, 3000.00, 3),
    ('2023-01-15', 'Inception',       'Christopher Nolan', 148, 2800.00, 4),
    ('2023-02-08', 'Intouchables',    'Olivier Nakache',   112, 2200.00, 2),
    ('2023-04-18', 'Alien',           'Ridley Scott',      117, 2600.00, 5),
    ('2023-06-01', 'The Dark Knight', 'Christopher Nolan', 152, 3200.00, 8),
    ('2023-07-22', 'Le Roi Lion',     'Rob Minkoff',        88, 1800.00, 7),
    ('2023-09-10', 'Interstellar',    'Christopher Nolan', 169, 3500.00, 4);

-- Abonnés de démonstration
INSERT INTO ABONNE (nom_abonne, adresse_abonne, date_abonnement, date_entree, nombre_location) VALUES
    ('Kofi Mensah',   'Rue des Cocotiers, Lomé',       '2023-01-10', '2023-01-10', 0),
    ('Ama Adjoua',    'Quartier Hédzranawoé, Lomé',     '2023-03-15', '2023-03-15', 1),
    ('Koffi Agbeko',  'Bld du 13 Janvier, Lomé',       '2023-05-20', '2023-05-20', 2),
    ('Akosua Dossou', 'Rue Nkafu, Lomé',                '2023-08-01', '2023-08-01', 0),
    ('Yao Amétépé',   'Quartier Bè, Lomé',              '2024-01-05', '2024-01-05', 0);

-- Locations de démonstration
INSERT INTO LOCATION (no_abonne, no_cassette, date_location, date_retour) VALUES
    (2, 1, '2025-03-01', NULL),          -- Ama a Terminator 2 (en cours)
    (3, 3, '2025-02-20', NULL),          -- Koffi a Inception (en cours)
    (3, 5, '2025-03-05', NULL),          -- Koffi a Alien (en cours)
    (1, 2, '2025-01-10', '2025-01-17'); -- Kofi avait Le Parrain (retourné)
