package clubvideo.view;

import clubvideo.dao.CategorieDAO;
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

public class CategorieView {

    private final BorderPane root;
    private final CategorieDAO dao = new CategorieDAO();
    private TableView<Categorie> table;
    private ObservableList<Categorie> data;

    public CategorieView() {
        root = new BorderPane();
        root.getStyleClass().add("view-root");
        root.setPadding(new Insets(34, 40, 34, 40));

        // Header
        VBox titles = new VBox(3);
        Label titre = new Label("📂  Catégories");
        titre.getStyleClass().add("page-title");
        Label sub = new Label("Gestion des catégories de cassettes vidéo");
        sub.getStyleClass().add("page-subtitle");
        titles.getChildren().addAll(titre, sub);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAjouter = new Button("＋  Ajouter");
        btnAjouter.getStyleClass().addAll("btn", "btn-primary-small");
        btnAjouter.setOnAction(e -> ouvrirDialogue(null));

        HBox header = new HBox(12, titles, spacer, btnAjouter);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 18, 0));

        // Table
        table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Label placeholder = new Label("Aucune catégorie trouvée.");
        placeholder.getStyleClass().add("card-text");
        table.setPlaceholder(placeholder);

        TableColumn<Categorie, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("idCategorie")); cId.setPrefWidth(65);

        TableColumn<Categorie, String> cLib = new TableColumn<>("Libellé de la catégorie");
        cLib.setCellValueFactory(new PropertyValueFactory<>("libelle")); cLib.setPrefWidth(380);

        TableColumn<Categorie, Void> cActions = new TableColumn<>("Actions");
        cActions.setPrefWidth(185); cActions.setResizable(false);
        cActions.setCellFactory(c -> new TableCell<>() {
            final Button btnE = btn("✏  Modifier",   "btn-info");
            final Button btnD = btn("🗑  Supprimer", "btn-danger");
            { btnE.setOnAction(e -> ouvrirDialogue(getTableView().getItems().get(getIndex())));
              btnD.setOnAction(e -> supprimer(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                HBox b = new HBox(8, btnE, btnD); b.setAlignment(Pos.CENTER); setGraphic(b);
            }
        });

        table.getColumns().addAll(cId, cLib, cActions);
        data = FXCollections.observableArrayList();
        table.setItems(data);
        charger();

        VBox layout = new VBox(0, header, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(layout);
    }

    private Button btn(String label, String style) {
        Button b = new Button(label); b.getStyleClass().addAll("btn-sm", style); return b;
    }

    private void charger() { data.setAll(dao.listerTout()); }

    private void supprimer(Categorie c) {
        if (!AlertHelper.confirmer("Suppression",
            "Supprimer la catégorie « " + c.getLibelle() + " » ?\nImpossible si des cassettes y sont rattachées.")) return;
        if (dao.supprimer(c.getIdCategorie())) charger();
        else AlertHelper.erreur("Suppression impossible",
            "Des cassettes sont rattachées à cette catégorie.\nModifiez d'abord ces cassettes.");
    }

    private void ouvrirDialogue(Categorie cat) {
        boolean isNew = cat == null;
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle(isNew ? "Ajouter une catégorie" : "Modifier la catégorie");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okBtn = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setText(isNew ? "Ajouter" : "Enregistrer");
        okBtn.getStyleClass().add("btn-primary-small");

        TextField tf = new TextField(cat != null ? cat.getLibelle() : "");
        tf.setPromptText("Ex : Science-Fiction, Comédie…");
        tf.getStyleClass().add("field-input");
        tf.setMaxWidth(Double.MAX_VALUE); tf.setPrefWidth(300);

        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(10); grid.setPadding(new Insets(20, 24, 12, 24));
        ColumnConstraints c1 = new ColumnConstraints(100), c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS); grid.getColumnConstraints().addAll(c1, c2);
        Label lbl = new Label("Libellé *"); lbl.getStyleClass().add("field-label");
        grid.add(lbl, 0, 0); grid.add(tf, 1, 0);
        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().setPrefWidth(400);

        okBtn.addEventFilter(ActionEvent.ACTION, ev -> {
            if (tf.getText().trim().isEmpty()) {
                AlertHelper.erreur("Champ obligatoire", "Le libellé est obligatoire."); ev.consume();
            }
        });

        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            boolean ok = isNew ? dao.ajouter(tf.getText().trim()) : dao.modifier(cat.getIdCategorie(), tf.getText().trim());
            if (ok) charger();
            else AlertHelper.erreur("Erreur", "Opération échouée. Ce libellé existe peut-être déjà.");
        });
    }

    public BorderPane getRoot() { return root; }
}
