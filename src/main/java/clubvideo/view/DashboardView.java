package clubvideo.view;

import clubvideo.dao.*;
import clubvideo.model.Location;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class DashboardView {

    private final ScrollPane root;

    public DashboardView() {
        VBox content = new VBox(28);
        content.setPadding(new Insets(40, 44, 40, 44));

        Label titre = new Label("📊  Tableau de Bord");
        titre.getStyleClass().add("page-title");
        Label sub = new Label("Vue d'ensemble du club en temps réel");
        sub.getStyleClass().add("page-subtitle");
        content.getChildren().addAll(titre, sub, new Separator());

        // ── Stats ─────────────────────────────────────────────────────────
        int nbCassettes  = safeCount(() -> new CassetteDAO().listerTout().size());
        int nbAbonnes    = safeCount(() -> new AbonneDAO().listerTout().size());
        int nbLocTotal   = safeCount(() -> new LocationDAO().listerTout().size());
        int nbEnCours    = safeCount(() -> new LocationDAO().listerEnCours().size());
        int nbCategories = safeCount(() -> new CategorieDAO().listerTout().size());

        HBox statsRow = new HBox(14);
        statsRow.getChildren().addAll(
            statCard("📼", String.valueOf(nbCassettes),  "Cassettes",   "en catalogue"),
            statCard("👤", String.valueOf(nbAbonnes),    "Abonnés",     "inscrits"),
            statCard("🔄", String.valueOf(nbLocTotal),   "Locations",   "au total"),
            statCard("⏳", String.valueOf(nbEnCours),    "En cours",    "non retournées"),
            statCard("📂", String.valueOf(nbCategories), "Catégories",  "disponibles")
        );
        content.getChildren().add(statsRow);

        // ── Locations en cours ────────────────────────────────────────────
        Label secTitre = new Label("⏳  Locations en cours");
        secTitre.getStyleClass().add("section-title");
        content.getChildren().add(secTitre);

        List<Location> encours = new ArrayList<>();
        try { encours = new LocationDAO().listerEnCours(); }
        catch (Exception e) { System.err.println("[Dashboard] listerEnCours: " + e.getMessage()); }

        VBox locBox = new VBox(4);
        locBox.getStyleClass().add("info-card");
        locBox.setPadding(new Insets(14, 16, 14, 16));

        if (encours.isEmpty()) {
            Label vide = new Label("Aucune location en cours actuellement.");
            vide.getStyleClass().add("card-text");
            locBox.getChildren().add(vide);
        } else {
            for (Location loc : encours) {
                HBox row = new HBox(12);
                row.getStyleClass().add("list-row");
                row.setPadding(new Insets(9, 14, 9, 14));
                row.setAlignment(Pos.CENTER_LEFT);

                Label lAbo  = new Label("👤  " + (loc.getNomAbonne()     != null ? loc.getNomAbonne()     : "—"));
                Label lCass = new Label("📼  " + (loc.getTitreCassette() != null ? loc.getTitreCassette() : "—"));
                lAbo .getStyleClass().add("row-primary");
                lCass.getStyleClass().add("row-secondary");
                lAbo.setMinWidth(200);

                Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

                Label lDate = new Label("📅  " + (loc.getDateLocation() != null ? loc.getDateLocation().toString() : "—"));
                lDate.getStyleClass().add("row-date");

                row.getChildren().addAll(lAbo, lCass, sp, lDate);
                locBox.getChildren().add(row);
            }
        }
        content.getChildren().add(locBox);

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.getStyleClass().add("scroll-pane");
    }

    private int safeCount(java.util.function.IntSupplier s) {
        try { return s.getAsInt(); }
        catch (Exception e) { System.err.println("[Dashboard] stat error: " + e.getMessage()); return 0; }
    }

    private VBox statCard(String icon, String value, String label, String desc) {
        VBox box = new VBox(5);
        box.getStyleClass().add("stat-card");
        box.setPadding(new Insets(20, 22, 20, 22));
        box.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(box, Priority.ALWAYS);
        Label ic  = new Label(icon);  ic .setStyle("-fx-font-size:26px;");
        Label val = new Label(value); val.getStyleClass().add("stat-value");
        Label lbl = new Label(label); lbl.getStyleClass().add("stat-label");
        Label d   = new Label(desc);  d  .getStyleClass().add("stat-desc");
        box.getChildren().addAll(ic, val, lbl, d);
        return box;
    }

    public ScrollPane getRoot() { return root; }
}
