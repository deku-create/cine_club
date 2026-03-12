package clubvideo.view;

import clubvideo.dao.AbonneDAO;
import clubvideo.dao.CassetteDAO;
import clubvideo.dao.LocationDAO;
import clubvideo.model.Abonne;
import clubvideo.model.Cassette;
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
import java.util.List;

public class LocationView {

    private final BorderPane root;
    private final LocationDAO locDao  = new LocationDAO();
    private final AbonneDAO   aboDao  = new AbonneDAO();
    private final CassetteDAO cassDao = new CassetteDAO();
    private TableView<Location> table;
    private ObservableList<Location> data;
    private ToggleGroup filterGroup;

    public LocationView() {
        root = new BorderPane();
        root.getStyleClass().add("view-root");
        root.setPadding(new Insets(34, 40, 34, 40));

        // Header
        VBox titles = new VBox(3);
        Label titre = new Label("🔄  Locations");
        titre.getStyleClass().add("page-title");
        Label sub = new Label("Enregistrement des locations et retours de cassettes");
        sub.getStyleClass().add("page-subtitle");
        titles.getChildren().addAll(titre, sub);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        filterGroup = new ToggleGroup();
        RadioButton rbAll  = radio("Toutes",     "all");
        RadioButton rbCour = radio("En cours",   "encours");
        RadioButton rbRet  = radio("Retournées", "retournees");
        rbAll.setSelected(true);
        rbAll .setOnAction(e -> charger("all"));
        rbCour.setOnAction(e -> charger("encours"));
        rbRet .setOnAction(e -> charger("retournees"));
        HBox filters = new HBox(10, rbAll, rbCour, rbRet);
        filters.setAlignment(Pos.CENTER);

        Button btnLouer = new Button("📼  Nouvelle location");
        btnLouer.getStyleClass().addAll("btn", "btn-primary-small");
        btnLouer.setOnAction(e -> ouvrirDialogLocation());

        HBox header = new HBox(12, titles, spacer, filters, btnLouer);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 18, 0));

        // Table
        table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Label placeholder = new Label("Aucune location trouvée.");
        placeholder.getStyleClass().add("card-text");
        table.setPlaceholder(placeholder);

        TableColumn<Location, Integer> cAbo  = col("N° Ab.",     "noAbonne",      60);
        TableColumn<Location, String>  cNom  = col("Abonné",     "nomAbonne",    145);
        TableColumn<Location, Integer> cCass = col("N° Cass.",   "noCassette",    65);
        TableColumn<Location, String>  cTit  = col("Cassette",   "titreCassette",175);
        TableColumn<Location, Object>  cDate = col("Date loc.",  "dateLocation", 105);
        TableColumn<Location, Object>  cRet  = col("Retour",     "dateRetour",   105);

        // Statut badge
        TableColumn<Location, Void> cStat = new TableColumn<>("Statut");
        cStat.setPrefWidth(105);
        cStat.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) { setGraphic(null); return; }
                Location l = getTableView().getItems().get(getIndex());
                Label badge = new Label(l.isRetournee() ? "✅  Retourné" : "⏳  En cours");
                badge.getStyleClass().add(l.isRetournee() ? "badge-success" : "badge-warning");
                setGraphic(badge); setText(null);
            }
        });

        // Actions
        TableColumn<Location, Void> cActions = new TableColumn<>("Actions");
        cActions.setPrefWidth(160); cActions.setResizable(false);
        cActions.setCellFactory(c -> new TableCell<>() {
            final Button btnR = btn("↩  Retour", "btn-success");
            final Button btnD = btn("🗑  Suppr.", "btn-danger");
            {
                btnR.setOnAction(e -> {
                    int i = getIndex();
                    if (i >= 0 && i < getTableView().getItems().size()) {
                        Location l = getTableView().getItems().get(i);
                        if (!l.isRetournee()) enregistrerRetour(l);
                    }
                });
                btnD.setOnAction(e -> {
                    int i = getIndex();
                    if (i >= 0 && i < getTableView().getItems().size())
                        supprimerLocation(getTableView().getItems().get(i));
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) { setGraphic(null); return; }
                Location l = getTableView().getItems().get(getIndex());
                btnR.setDisable(l.isRetournee());
                HBox b = new HBox(6, btnR, btnD); b.setAlignment(Pos.CENTER);
                setGraphic(b);
            }
        });

        table.getColumns().addAll(cAbo, cNom, cCass, cTit, cDate, cRet, cStat, cActions);
        data = FXCollections.observableArrayList();
        table.setItems(data);
        charger("all");

        VBox layout = new VBox(0, header, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(layout);
    }

    private RadioButton radio(String text, String tag) {
        RadioButton rb = new RadioButton(text);
        rb.setToggleGroup(filterGroup); rb.getStyleClass().add("radio-filter"); rb.setUserData(tag);
        return rb;
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<Location, T> col(String name, String prop, double w) {
        TableColumn<Location, T> c = new TableColumn<>(name);
        c.setCellValueFactory(new PropertyValueFactory<>(prop)); c.setPrefWidth(w); return c;
    }

    private Button btn(String label, String style) {
        Button b = new Button(label); b.getStyleClass().addAll("btn-sm", style); return b;
    }

    private void charger(String filtre) {
        List<Location> list = switch (filtre) {
            case "encours"    -> locDao.listerEnCours();
            case "retournees" -> locDao.listerRetournees();
            default           -> locDao.listerTout();
        };
        data.setAll(list);
    }

    private String getFiltre() {
        Toggle t = filterGroup.getSelectedToggle();
        return t != null ? (String) t.getUserData() : "all";
    }

    private void enregistrerRetour(Location l) {
        if (!AlertHelper.confirmer("Enregistrer le retour",
            "Confirmer le retour de :\n📼  " + l.getTitreCassette() + "\npar :  👤  " + l.getNomAbonne() + " ?")) return;
        if (locDao.retourner(l.getNoAbonne(), l.getNoCassette())) charger(getFiltre());
        else AlertHelper.erreur("Erreur", "Le retour n'a pas pu être enregistré.");
    }

    private void supprimerLocation(Location l) {
        if (!AlertHelper.confirmer("Suppression", "Supprimer cet enregistrement de location ?")) return;
        // Si en cours, décrémenter le compteur de l'abonné
        if (!l.isRetournee()) new AbonneDAO().decrementerLocation(l.getNoAbonne());
        if (locDao.supprimer(l.getNoAbonne(), l.getNoCassette())) charger(getFiltre());
    }

    private void ouvrirDialogLocation() {
        List<Abonne>   abonnes   = aboDao.listerTout();
        List<Cassette> cassettes = cassDao.listerTout();

        if (abonnes.isEmpty())   { AlertHelper.erreur("Aucun abonné",   "Ajoutez d'abord un abonné.");   return; }
        if (cassettes.isEmpty()) { AlertHelper.erreur("Aucune cassette","Ajoutez d'abord une cassette."); return; }

        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Nouvelle location");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okBtn = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setText("Enregistrer la location");
        okBtn.getStyleClass().add("btn-primary-small");

        ComboBox<Abonne>   cbAbo  = new ComboBox<>(FXCollections.observableArrayList(abonnes));
        ComboBox<Cassette> cbCass = new ComboBox<>(FXCollections.observableArrayList(cassettes));
        cbAbo .getStyleClass().add("combo-box"); cbAbo .setMaxWidth(Double.MAX_VALUE); cbAbo .setPromptText("Sélectionner un abonné");
        cbCass.getStyleClass().add("combo-box"); cbCass.setMaxWidth(Double.MAX_VALUE); cbCass.setPromptText("Sélectionner une cassette");

        Label infoLoc = new Label(" ");
        infoLoc.getStyleClass().add("hint-label");
        infoLoc.setWrapText(true);

        cbAbo.setOnAction(e -> {
            Abonne sel = cbAbo.getValue();
            if (sel == null) return;
            int nb = sel.getNombreLocation();
            infoLoc.setText("Cet abonné a " + nb + "/3 cassette(s) en cours." + (nb >= 3 ? "  ⛔ Limite atteinte !" : ""));
            infoLoc.setStyle(nb >= 3 ? "-fx-text-fill:#f85149;" : "-fx-text-fill:#3fb950;");
        });

        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(12); grid.setPadding(new Insets(20, 24, 12, 24)); grid.setMinWidth(440);
        ColumnConstraints c1 = new ColumnConstraints(130), c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS); grid.getColumnConstraints().addAll(c1, c2);

        Label l1 = lbl("Abonné *"); Label l2 = lbl("Cassette *"); Label l3 = lbl("Info");
        Label regle = new Label("⚠  Un abonné ne peut avoir plus de 3 cassettes simultanément.");
        regle.getStyleClass().add("hint-label"); regle.setWrapText(true);

        grid.add(l1, 0, 0); grid.add(cbAbo,   1, 0);
        grid.add(l2, 0, 1); grid.add(cbCass,  1, 1);
        grid.add(l3, 0, 2); grid.add(infoLoc, 1, 2);
        grid.add(regle, 0, 3, 2, 1);
        dlg.getDialogPane().setContent(grid);

        okBtn.addEventFilter(ActionEvent.ACTION, ev -> {
            Abonne   abo  = cbAbo.getValue();
            Cassette cass = cbCass.getValue();
            if (abo == null || cass == null) {
                AlertHelper.erreur("Champs manquants", "Sélectionnez un abonné et une cassette."); ev.consume();
            } else if (abo.getNombreLocation() >= 3) {
                AlertHelper.erreur("Limite atteinte",
                    abo.getNomAbonne() + " a déjà 3 cassettes en location.\nEnregistrez un retour avant de continuer.");
                ev.consume();
            }
        });

        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            Abonne   abo  = cbAbo.getValue();
            Cassette cass = cbCass.getValue();
            if (locDao.louer(abo.getNoAbonne(), cass.getNoCassette())) charger(getFiltre());
            else AlertHelper.erreur("Erreur",
                "La location n'a pas pu être enregistrée.\nCette cassette est peut-être déjà en location par cet abonné.");
        });
    }

    private Label lbl(String t) { Label l = new Label(t); l.getStyleClass().add("field-label"); return l; }

    public BorderPane getRoot() { return root; }
}
