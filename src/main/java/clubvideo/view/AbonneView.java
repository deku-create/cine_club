package clubvideo.view;

import clubvideo.dao.AbonneDAO;
import clubvideo.dao.LocationDAO;
import clubvideo.model.Abonne;
import clubvideo.model.Location;
import clubvideo.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.List;

public class AbonneView {

    private final BorderPane root;
    private final AbonneDAO dao = new AbonneDAO();
    private TableView<Abonne> table;
    private ObservableList<Abonne> data;
    private TextField tfSearch;

    public AbonneView() {
        root = new BorderPane();
        root.getStyleClass().add("view-root");
        root.setPadding(new Insets(34, 40, 34, 40));

        // Header
        VBox titles = new VBox(3);
        Label titre = new Label("👤  Abonnés");
        titre.getStyleClass().add("page-title");
        Label sub = new Label("Gestion des membres du club");
        sub.getStyleClass().add("page-subtitle");
        titles.getChildren().addAll(titre, sub);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        tfSearch = new TextField();
        tfSearch.setPromptText("🔍  Rechercher par nom ou adresse…");
        tfSearch.getStyleClass().add("search-field");
        tfSearch.setPrefWidth(260);
        tfSearch.setOnKeyReleased(e -> charger(tfSearch.getText().trim()));

        Button btnAjouter = new Button("＋  Ajouter");
        btnAjouter.getStyleClass().addAll("btn", "btn-primary-small");
        btnAjouter.setOnAction(e -> ouvrirFormulaire(null));

        HBox header = new HBox(12, titles, spacer, tfSearch, btnAjouter);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 18, 0));

        // Table
        table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Label placeholder = new Label("Aucun abonné trouvé.");
        placeholder.getStyleClass().add("card-text");
        table.setPlaceholder(placeholder);

        TableColumn<Abonne, Integer> cId     = col("N°",          "noAbonne",        55);
        TableColumn<Abonne, String>  cNom    = col("Nom",         "nomAbonne",      170);
        TableColumn<Abonne, String>  cAdr    = col("Adresse",     "adresseAbonne",  200);
        TableColumn<Abonne, Object>  cAbo    = col("Abonnement",  "dateAbonnement", 110);
        TableColumn<Abonne, Object>  cEnt    = col("Entrée",      "dateEntree",     110);

        // Badge locations
        TableColumn<Abonne, Integer> cLoc = new TableColumn<>("Locations");
        cLoc.setPrefWidth(90);
        cLoc.setCellValueFactory(new PropertyValueFactory<>("nombreLocation"));
        cLoc.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Integer v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setGraphic(null); setText(null); return; }
                Label lbl = new Label(v + " / 3");
                lbl.getStyleClass().add(v >= 3 ? "badge-danger" : v >= 2 ? "badge-warning" : "badge-success");
                setGraphic(lbl); setText(null);
            }
        });

        // Actions
        TableColumn<Abonne, Void> cActions = new TableColumn<>("Actions");
        cActions.setPrefWidth(240); cActions.setResizable(false);
        cActions.setCellFactory(c -> new TableCell<>() {
            final Button btnE = btn("✏  Modifier",    "btn-info");
            final Button btnD = btn("🗑  Suppr.",      "btn-danger");
            final Button btnH = btn("📋  Historique", "btn-accent");
            { btnE.setOnAction(e -> ouvrirFormulaire(getTableView().getItems().get(getIndex())));
              btnD.setOnAction(e -> supprimer(getTableView().getItems().get(getIndex())));
              btnH.setOnAction(e -> afficherHistorique(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                HBox b = new HBox(5, btnE, btnD, btnH); b.setAlignment(Pos.CENTER);
                setGraphic(b);
            }
        });

        table.getColumns().addAll(cId, cNom, cAdr, cAbo, cEnt, cLoc, cActions);
        data = FXCollections.observableArrayList();
        table.setItems(data);
        charger("");

        VBox layout = new VBox(0, header, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(layout);
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<Abonne, T> col(String name, String prop, double w) {
        TableColumn<Abonne, T> c = new TableColumn<>(name);
        c.setCellValueFactory(new PropertyValueFactory<>(prop)); c.setPrefWidth(w); return c;
    }

    private Button btn(String label, String style) {
        Button b = new Button(label); b.getStyleClass().addAll("btn-sm", style); return b;
    }

    private void charger(String kw) {
        data.setAll(kw.isEmpty() ? dao.listerTout() : dao.rechercher(kw));
    }

    private void supprimer(Abonne a) {
        if (!AlertHelper.confirmer("Suppression",
            "Supprimer l'abonné « " + a.getNomAbonne() + " » ?\nSes locations seront également supprimées.")) return;
        if (dao.supprimer(a.getNoAbonne())) charger(tfSearch.getText().trim());
        else AlertHelper.erreur("Erreur", "La suppression a échoué.");
    }

    private void afficherHistorique(Abonne a) {
        List<Location> locs;
        try { locs = new LocationDAO().listerParAbonne(a.getNoAbonne()); }
        catch (Exception e) { AlertHelper.erreur("Erreur", "Impossible de charger l'historique."); return; }
        StringBuilder sb = new StringBuilder();
        if (locs.isEmpty()) {
            sb.append("Aucune location enregistrée pour cet abonné.");
        } else {
            for (Location l : locs) {
                sb.append("📼  ").append(l.getTitreCassette() != null ? l.getTitreCassette() : "—");
                sb.append("\n     Loué le : ").append(l.getDateLocation() != null ? l.getDateLocation() : "—");
                if (l.getDateRetour() != null)
                    sb.append("  |  Retourné le : ").append(l.getDateRetour());
                else sb.append("  |  ⏳ EN COURS");
                sb.append("\n\n");
            }
        }
        AlertHelper.info("Historique — " + a.getNomAbonne(), sb.toString().trim());
    }

    private void ouvrirFormulaire(Abonne abonne) {
        boolean isNew = abonne == null;
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle(isNew ? "Ajouter un abonné" : "Modifier l'abonné");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okBtn = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setText(isNew ? "Ajouter" : "Enregistrer");
        okBtn.getStyleClass().add("btn-primary-small");

        TextField  tfNom = field(abonne != null ? abonne.getNomAbonne() : "");
        TextField  tfAdr = field(abonne != null && abonne.getAdresseAbonne() != null ? abonne.getAdresseAbonne() : "");
        DatePicker dpAbo = dp(abonne != null && abonne.getDateAbonnement() != null ? abonne.getDateAbonnement() : LocalDate.now());
        DatePicker dpEnt = dp(abonne != null && abonne.getDateEntree()     != null ? abonne.getDateEntree()     : LocalDate.now());

        GridPane grid = grid();
        row(grid, 0, "Nom *",             tfNom);
        row(grid, 1, "Adresse",           tfAdr);
        row(grid, 2, "Date abonnement *", dpAbo);
        row(grid, 3, "Date d'entrée *",   dpEnt);
        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().setPrefWidth(460);

        okBtn.addEventFilter(ActionEvent.ACTION, ev -> {
            if (tfNom.getText().trim().isEmpty())        { AlertHelper.erreur("Saisie", "Le nom est obligatoire."); ev.consume(); return; }
            if (dpAbo.getValue() == null || dpEnt.getValue() == null) { AlertHelper.erreur("Saisie", "Les dates sont obligatoires."); ev.consume(); }
        });

        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            Abonne ab = isNew ? new Abonne() : abonne;
            ab.setNomAbonne     (tfNom.getText().trim());
            ab.setAdresseAbonne (tfAdr.getText().trim().isEmpty() ? null : tfAdr.getText().trim());
            ab.setDateAbonnement(dpAbo.getValue());
            ab.setDateEntree    (dpEnt.getValue());
            if (isNew) ab.setNombreLocation(0);
            boolean ok = isNew ? dao.ajouter(ab) : dao.modifier(ab);
            if (ok) charger(tfSearch.getText().trim());
            else    AlertHelper.erreur("Erreur", "L'opération a échoué.");
        });
    }

    private TextField field(String v) {
        TextField tf = new TextField(v); tf.getStyleClass().add("field-input"); tf.setMaxWidth(Double.MAX_VALUE); return tf;
    }
    private DatePicker dp(LocalDate d) {
        DatePicker p = new DatePicker(d); p.getStyleClass().add("date-picker"); p.setMaxWidth(Double.MAX_VALUE); return p;
    }
    private GridPane grid() {
        GridPane g = new GridPane(); g.setHgap(14); g.setVgap(10); g.setPadding(new Insets(20, 24, 12, 24));
        ColumnConstraints c1 = new ColumnConstraints(155), c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS); g.getColumnConstraints().addAll(c1, c2); return g;
    }
    private void row(GridPane g, int r, String text, javafx.scene.Node n) {
        Label l = new Label(text); l.getStyleClass().add("field-label"); g.add(l, 0, r); g.add(n, 1, r);
    }

    public BorderPane getRoot() { return root; }
}
