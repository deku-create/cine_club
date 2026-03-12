package clubvideo.view;

import clubvideo.dao.CassetteDAO;
import clubvideo.dao.CategorieDAO;
import clubvideo.model.Cassette;
import clubvideo.model.Categorie;
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

public class CassetteView {

    private final BorderPane root;
    private final CassetteDAO  dao    = new CassetteDAO();
    private final CategorieDAO catDao = new CategorieDAO();
    private TableView<Cassette> table;
    private ObservableList<Cassette> data;
    private TextField tfSearch;

    public CassetteView() {
        root = new BorderPane();
        root.getStyleClass().add("view-root");
        root.setPadding(new Insets(34, 40, 34, 40));

        // Header
        VBox titles = new VBox(3);
        Label titre = new Label("📼  Cassettes");
        titre.getStyleClass().add("page-title");
        Label sub = new Label("Gestion du catalogue de cassettes vidéo");
        sub.getStyleClass().add("page-subtitle");
        titles.getChildren().addAll(titre, sub);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        tfSearch = new TextField();
        tfSearch.setPromptText("🔍  Rechercher par titre ou auteur…");
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
        Label placeholder = new Label("Aucune cassette trouvée.");
        placeholder.getStyleClass().add("card-text");
        table.setPlaceholder(placeholder);

        TableColumn<Cassette, Integer> cId     = col("N°",          "noCassette",       50);
        TableColumn<Cassette, String>  cTitre  = col("Titre",       "titre",           185);
        TableColumn<Cassette, String>  cAuteur = col("Auteur",      "auteur",          140);
        TableColumn<Cassette, Integer> cDuree  = col("Durée (min)", "duree",            90);
        TableColumn<Cassette, Double>  cPrix   = col("Prix (FCFA)", "prix",            105);
        TableColumn<Cassette, String>  cCat    = col("Catégorie",   "libelleCategorie",120);
        TableColumn<Cassette, Object>  cDate   = col("Date achat",  "dateAchat",       105);

        TableColumn<Cassette, Void> cActions = new TableColumn<>("Actions");
        cActions.setPrefWidth(155); cActions.setResizable(false);
        cActions.setCellFactory(c -> new TableCell<>() {
            final Button btnE = btn("✏  Modifier", "btn-info");
            final Button btnD = btn("🗑  Suppr.",  "btn-danger");
            { btnE.setOnAction(e -> ouvrirFormulaire(getTableView().getItems().get(getIndex())));
              btnD.setOnAction(e -> supprimer(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                HBox b = new HBox(6, btnE, btnD); b.setAlignment(Pos.CENTER);
                setGraphic(b);
            }
        });

        table.getColumns().addAll(cId, cTitre, cAuteur, cDuree, cPrix, cCat, cDate, cActions);
        data = FXCollections.observableArrayList();
        table.setItems(data);
        charger("");

        VBox layout = new VBox(0, header, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(layout);
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<Cassette, T> col(String name, String prop, double w) {
        TableColumn<Cassette, T> c = new TableColumn<>(name);
        c.setCellValueFactory(new PropertyValueFactory<>(prop)); c.setPrefWidth(w);
        return c;
    }

    private Button btn(String label, String style) {
        Button b = new Button(label); b.getStyleClass().addAll("btn-sm", style); return b;
    }

    private void charger(String kw) {
        data.setAll(kw.isEmpty() ? dao.listerTout() : dao.rechercher(kw));
    }

    private void supprimer(Cassette c) {
        if (!AlertHelper.confirmer("Suppression",
            "Supprimer la cassette « " + c.getTitre() + " » ?\n" +
            "Impossible si des locations y sont liées.")) return;
        if (dao.supprimer(c.getNoCassette()))
            charger(tfSearch.getText().trim());
        else
            AlertHelper.erreur("Suppression impossible",
                "Cette cassette est liée à une ou plusieurs locations.\nSupprimez d'abord les locations concernées.");
    }

    private void ouvrirFormulaire(Cassette cassette) {
        boolean isNew = cassette == null;
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle(isNew ? "Ajouter une cassette" : "Modifier la cassette");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okBtn = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setText(isNew ? "Ajouter" : "Enregistrer");
        okBtn.getStyleClass().add("btn-primary-small");

        TextField   tfTitre  = field(cassette != null ? cassette.getTitre()              : "");
        TextField   tfAuteur = field(cassette != null ? cassette.getAuteur() != null ? cassette.getAuteur() : "" : "");
        TextField   tfDuree  = field(cassette != null ? String.valueOf(cassette.getDuree()) : "90");
        TextField   tfPrix   = field(cassette != null ? String.valueOf(cassette.getPrix())  : "0");
        DatePicker  dpDate   = dp(cassette != null ? cassette.getDateAchat() : LocalDate.now());

        List<Categorie> cats = catDao.listerTout();
        ComboBox<Categorie> cbCat = new ComboBox<>(FXCollections.observableArrayList(cats));
        cbCat.getStyleClass().add("combo-box"); cbCat.setMaxWidth(Double.MAX_VALUE);
        cbCat.setPromptText("Sélectionner une catégorie");
        if (cassette != null) cats.stream()
            .filter(c -> c.getIdCategorie() == cassette.getIdCategorie())
            .findFirst().ifPresent(cbCat::setValue);

        GridPane grid = grid();
        row(grid, 0, "Titre *",        tfTitre);
        row(grid, 1, "Auteur",         tfAuteur);
        row(grid, 2, "Durée (min) *",  tfDuree);
        row(grid, 3, "Prix (FCFA) *",  tfPrix);
        row(grid, 4, "Date d'achat *", dpDate);
        row(grid, 5, "Catégorie *",    cbCat);
        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().setPrefWidth(480);

        okBtn.addEventFilter(ActionEvent.ACTION, ev -> {
            if (tfTitre.getText().trim().isEmpty())  { AlertHelper.erreur("Saisie", "Le titre est obligatoire.");  ev.consume(); return; }
            if (dpDate.getValue() == null)           { AlertHelper.erreur("Saisie", "La date est obligatoire.");   ev.consume(); return; }
            if (cbCat.getValue()  == null)           { AlertHelper.erreur("Saisie", "La catégorie est obligatoire."); ev.consume(); return; }
            try { Integer.parseInt(tfDuree.getText().trim()); } catch (NumberFormatException e) {
                AlertHelper.erreur("Saisie", "La durée doit être un entier."); ev.consume(); return; }
            try { Double.parseDouble(tfPrix.getText().trim().replace(",", ".")); } catch (NumberFormatException e) {
                AlertHelper.erreur("Saisie", "Le prix doit être un nombre."); ev.consume(); }
        });

        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            try {
                Cassette c = isNew ? new Cassette() : cassette;
                c.setTitre      (tfTitre.getText().trim());
                c.setAuteur     (tfAuteur.getText().trim().isEmpty() ? null : tfAuteur.getText().trim());
                c.setDuree      (Integer.parseInt(tfDuree.getText().trim()));
                c.setPrix       (Double.parseDouble(tfPrix.getText().trim().replace(",", ".")));
                c.setDateAchat  (dpDate.getValue());
                c.setIdCategorie(cbCat.getValue().getIdCategorie());
                boolean ok = isNew ? dao.ajouter(c) : dao.modifier(c);
                if (ok) charger(tfSearch.getText().trim());
                else    AlertHelper.erreur("Erreur", "L'opération a échoué.");
            } catch (NumberFormatException ex) {
                AlertHelper.erreur("Erreur", "Valeurs numériques invalides.");
            }
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
        ColumnConstraints c1 = new ColumnConstraints(135), c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS); g.getColumnConstraints().addAll(c1, c2); return g;
    }
    private void row(GridPane g, int r, String text, javafx.scene.Node n) {
        Label l = new Label(text); l.getStyleClass().add("field-label"); g.add(l, 0, r); g.add(n, 1, r);
    }

    public BorderPane getRoot() { return root; }
}
