package clubvideo.view;

import clubvideo.util.Session;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AccueilView {

    private final ScrollPane root;

    public AccueilView() {
        VBox content = new VBox(28);
        content.setPadding(new Insets(40, 44, 40, 44));

        // ── Header ────────────────────────────────────────────────────────
        String dateStr = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH));
        Label titre = new Label("Bienvenue, " + Session.getUtilisateur().getLogin() + " 👋");
        titre.getStyleClass().add("page-title");
        Label dateLbl = new Label(dateStr.substring(0, 1).toUpperCase() + dateStr.substring(1));
        dateLbl.getStyleClass().add("page-subtitle");

        // ── Description ───────────────────────────────────────────────────
        VBox descCard = card("🎬  À propos de CinéClub",
            "CinéClub est un système de gestion complet pour votre club de location de cassettes vidéo.\n\n" +
            "Cette application vous permet de gérer l'ensemble de votre activité : catalogue de cassettes, " +
            "catégories, abonnés, locations et retours — tout en temps réel avec MySQL.");

        // ── Fonctionnalités ───────────────────────────────────────────────
        HBox features = new HBox(16);
        features.getChildren().addAll(
            featureCard("📼", "Cassettes",  "Gérez l'intégralité de votre catalogue avec prix, durée et catégorie."),
            featureCard("👤", "Abonnés",    "Suivez vos membres, leurs coordonnées et l'historique de leurs locations."),
            featureCard("🔄", "Locations",  "Enregistrez facilement les départs et les retours de cassettes."),
            featureCard("📂", "Catégories", "Organisez votre catalogue : Action, Comédie, Drame, Science-Fiction…")
        );

        // ── Règles métier ─────────────────────────────────────────────────
        VBox rules = card("📋  Règles de gestion",
            "• Un abonné ne peut avoir plus de 3 cassettes simultanément en location.\n" +
            "• Chaque cassette appartient à une seule catégorie.\n" +
            "• L'enregistrement d'un retour libère automatiquement un emplacement.\n" +
            "• La suppression d'un abonné entraîne la suppression de ses locations.\n" +
            "• Une cassette ayant des locations actives ne peut pas être supprimée.");

        // ── DB Info ───────────────────────────────────────────────────────
        VBox dbInfo = card("🗄️  Base de données — MySQL XAMPP",
            "Connexion active sur MySQL (XAMPP).\n" +
            "Hôte : localhost:3306  |  Base : club_video  |  Utilisateur : root\n\n" +
            "Pour modifier les paramètres :\nsrc/main/java/clubvideo/database/DatabaseConnection.java");

        content.getChildren().addAll(titre, dateLbl, new Separator(), descCard, features, rules, dbInfo);

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.getStyleClass().add("scroll-pane");
    }

    private VBox card(String titre, String texte) {
        VBox box = new VBox(10);
        box.getStyleClass().add("info-card");
        box.setPadding(new Insets(20, 22, 20, 22));
        Label t = new Label(titre); t.getStyleClass().add("card-title");
        Label d = new Label(texte); d.getStyleClass().add("card-text"); d.setWrapText(true);
        box.getChildren().addAll(t, d);
        return box;
    }

    private VBox featureCard(String icon, String titre, String desc) {
        VBox box = new VBox(8);
        box.getStyleClass().add("feature-card");
        box.setPadding(new Insets(20, 18, 20, 18));
        HBox.setHgrow(box, Priority.ALWAYS);
        Label ic = new Label(icon); ic.setStyle("-fx-font-size:26px;");
        Label t  = new Label(titre); t.getStyleClass().add("feature-title");
        Label d  = new Label(desc);  d.getStyleClass().add("feature-desc"); d.setWrapText(true);
        box.getChildren().addAll(ic, t, d);
        return box;
    }

    public ScrollPane getRoot() { return root; }
}
