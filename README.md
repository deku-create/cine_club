# 🎬 CinéClub — Gestion de Club Vidéo
### Application JavaFX + MySQL (XAMPP)

---

## ⚡ DÉMARRAGE RAPIDE (3 étapes)

### Étape 1 — Préparer XAMPP
1. Ouvrir **XAMPP Control Panel**
2. Cliquer **Start** sur **MySQL** (et Apache optionnel)
3. Vérifier que MySQL est bien **vert / Running**

### Étape 2 — Importer la base de données
1. Ouvrir **phpMyAdmin** : http://localhost/phpmyadmin
2. Cliquer **Importer** dans la barre du haut
3. Sélectionner le fichier : `club_video.sql`
4. Cliquer **Exécuter**  ✅

### Étape 3 — Lancer l'application
```bash
mvn javafx:run
```

---

## 🔑 Comptes de démonstration

| Login    | Mot de passe | Rôle    | Accès |
|----------|-------------|---------|-------|
| `admin`  | `admin123`  | ADMIN   | Tout  |
| `employe`| `employe123`| EMPLOYE | Tout sauf gestion utilisateurs |

---

## 📁 Structure du projet

```
ClubVideo/
├── club_video.sql               ← Script SQL à importer dans XAMPP
├── pom.xml                      ← Dépendances Maven (JavaFX + MySQL)
├── README.md
└── src/main/
    ├── java/clubvideo/
    │   ├── MainApp.java                  ← Point d'entrée JavaFX
    │   ├── database/
    │   │   └── DatabaseConnection.java   ← Connexion MySQL XAMPP
    │   ├── model/
    │   │   ├── Cassette.java
    │   │   ├── Abonne.java
    │   │   ├── Location.java
    │   │   ├── Categorie.java
    │   │   └── Utilisateur.java
    │   ├── dao/                          ← Accès base de données (SQL)
    │   │   ├── CassetteDAO.java
    │   │   ├── AbonneDAO.java
    │   │   ├── LocationDAO.java
    │   │   ├── CategorieDAO.java
    │   │   └── UtilisateurDAO.java
    │   ├── view/                         ← Interfaces JavaFX
    │   │   ├── LoginView.java            ← Authentification
    │   │   ├── MainView.java             ← Fenêtre principale + sidebar
    │   │   ├── AccueilView.java          ← Page d'accueil
    │   │   ├── DashboardView.java        ← Tableau de bord
    │   │   ├── CassetteView.java         ← CRUD cassettes
    │   │   ├── AbonneView.java           ← CRUD abonnés
    │   │   ├── LocationView.java         ← Locations & retours
    │   │   ├── CategorieView.java        ← CRUD catégories
    │   │   └── UtilisateurView.java      ← CRUD utilisateurs (admin)
    │   └── util/
    │       ├── Session.java              ← Utilisateur connecté
    │       └── AlertHelper.java          ← Boîtes de dialogue
    └── resources/
        ├── css/style.css                 ← Thème sombre
        └── schema.sql                    ← Référence SQL
```

---

## ⚙️ Configuration MySQL

Si votre XAMPP utilise un mot de passe MySQL, modifiez :
```java
// src/main/java/clubvideo/database/DatabaseConnection.java
private static final String PASSWORD = "votre_mot_de_passe";
```

Si MySQL est sur un autre port :
```java
private static final int PORT = 3307; // changer si besoin
```

---

## 🛠️ Prérequis

| Outil | Version minimale |
|-------|-----------------|
| Java JDK | 17 ou supérieur |
| Maven | 3.8+ |
| XAMPP | 8.x (MySQL 8.0+) |
| JavaFX SDK | 21 (inclus via Maven) |

---

## 📋 Fonctionnalités complètes

| Page | Description |
|------|-------------|
| 🔐 Authentification | Login/MDP, gestion rôles ADMIN/EMPLOYE |
| 🏠 Accueil | Présentation de l'application |
| 📊 Tableau de bord | Statistiques, locations en cours |
| 📼 Cassettes | Ajouter, modifier, supprimer, rechercher |
| 👤 Abonnés | Ajouter, modifier, supprimer, historique |
| 🔄 Locations | Nouvelle location, retour, filtres |
| 📂 Catégories | Ajouter, modifier, supprimer |
| 👥 Utilisateurs | Créer comptes, changer MDP (ADMIN) |

---

## 🗄️ Modèle de données

```sql
CATEGORIE(id_categorie PK, libelle)
CASSETTE(no_cassette PK, date_achat, titre, auteur, duree, prix, id_categorie FK)
ABONNE(no_abonne PK, nom_abonne, adresse_abonne, date_abonnement, date_entree, nombre_location)
LOCATION(no_abonne FK, no_cassette FK, date_location, date_retour)  -- PK composite
UTILISATEUR(id_user PK, login UNIQUE, password, role)
```

---

## ✅ Règles métier implémentées

- Un abonné ne peut pas avoir plus de **3 cassettes simultanément**
- Seule la **dernière date** de location est conservée par couple (abonné, cassette)
- Le retour d'une cassette **décrémente automatiquement** le compteur de l'abonné
- La location **incrémente automatiquement** le compteur de l'abonné
- Suppression protégée par les clés étrangères (CASCADE/RESTRICT)
