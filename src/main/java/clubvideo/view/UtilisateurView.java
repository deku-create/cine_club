package clubvideo.view;

import clubvideo.dao.UtilisateurDAO;
import clubvideo.model.Utilisateur;
import clubvideo.util.AlertHelper;
import clubvideo.util.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class UtilisateurView {

    private final BorderPane root;
    private final UtilisateurDAO dao = new UtilisateurDAO();
    private TableView<Utilisateur> table;
    private ObservableList<Utilisateur> data;

    public UtilisateurView() {
        root = new BorderPane();
        root.getStyleClass().add("view-root");
        root.setPadding(new Insets(34, 40, 34, 40));

        // Header
        VBox titles = new VBox(3);
        Label titre = new Label("👥  Utilisateurs");
        titre.getStyleClass().add("page-title");
        Label sub = new Label("Gestion des comptes d'accès — ADMIN uniquement");
        sub.getStyleClass().add("page-subtitle");
        titles.getChildren().addAll(titre, sub);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAjouter = new Button("＋  Ajouter un compte");
        btnAjouter.getStyleClass().addAll("btn", "btn-primary-small");
        btnAjouter.setOnAction(e -> ouvrirDialogAjout());

        HBox header = new HBox(12, titles, spacer, btnAjouter);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 18, 0));

        // Table
        table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Label placeholder = new Label("Aucun utilisateur.");
        placeholder.getStyleClass().add("card-text");
        table.setPlaceholder(placeholder);

        TableColumn<Utilisateur, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("idUser")); cId.setPrefWidth(55);

        TableColumn<Utilisateur, String> cLogin = new TableColumn<>("Identifiant");
        cLogin.setCellValueFactory(new PropertyValueFactory<>("login")); cLogin.setPrefWidth(220);

        TableColumn<Utilisateur, String> cRole = new TableColumn<>("Rôle");
        cRole.setCellValueFactory(new PropertyValueFactory<>("role")); cRole.setPrefWidth(130);
        cRole.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setGraphic(null); return; }
                Label lbl = new Label(v);
                lbl.getStyleClass().add("ADMIN".equals(v) ? "badge-danger" : "badge-info");
                setGraphic(lbl); setText(null);
            }
        });

        TableColumn<Utilisateur, Void> cActions = new TableColumn<>("Actions");
        cActions.setPrefWidth(220); cActions.setResizable(false);
        cActions.setCellFactory(c -> new TableCell<>() {
            final Button btnP = btn("🔑  Chg. MDP",  "btn-accent");
            final Button btnD = btn("🗑  Supprimer", "btn-danger");
            { btnP.setOnAction(e -> changerMDP(getTableView().getItems().get(getIndex())));
              btnD.setOnAction(e -> supprimer(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) { setGraphic(null); return; }
                Utilisateur u = getTableView().getItems().get(getIndex());
                btnD.setDisable(u.getIdUser() == Session.getUtilisateur().getIdUser());
                HBox b = new HBox(8, btnP, btnD); b.setAlignment(Pos.CENTER); setGraphic(b);
            }
        });

        table.getColumns().addAll(cId, cLogin, cRole, cActions);
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

    private void supprimer(Utilisateur u) {
        if (u.getIdUser() == Session.getUtilisateur().getIdUser()) {
            AlertHelper.erreur("Action interdite", "Vous ne pouvez pas supprimer votre propre compte."); return;
        }
        if (!AlertHelper.confirmer("Suppression", "Supprimer le compte « " + u.getLogin() + " » définitivement ?")) return;
        if (dao.supprimer(u.getIdUser())) charger();
        else AlertHelper.erreur("Erreur", "La suppression a échoué.");
    }

    private void changerMDP(Utilisateur u) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Changer le mot de passe");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okBtn = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setText("Enregistrer"); okBtn.getStyleClass().add("btn-primary-small");

        PasswordField pfN = pwf("Nouveau mot de passe");
        PasswordField pfC = pwf("Confirmer le mot de passe");

        GridPane grid = grid();
        Label lUser = new Label(u.getLogin());
        lUser.setStyle("-fx-text-fill:#38bdf8; -fx-font-weight:bold;");
        row(grid, 0, "Compte",        lUser);
        row(grid, 1, "Nouveau MDP *", pfN);
        row(grid, 2, "Confirmation *",pfC);
        dlg.getDialogPane().setContent(grid); dlg.getDialogPane().setPrefWidth(420);

        okBtn.addEventFilter(ActionEvent.ACTION, ev -> {
            if (pfN.getText().isEmpty())             { AlertHelper.erreur("Champ vide",    "Le mot de passe est obligatoire."); ev.consume(); return; }
            if (!pfN.getText().equals(pfC.getText())){ AlertHelper.erreur("Non concordant","Les mots de passe ne correspondent pas."); ev.consume(); return; }
            if (pfN.getText().length() < 4)          { AlertHelper.erreur("Trop court",    "Minimum 4 caractères."); ev.consume(); }
        });

        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            if (dao.changerMotDePasse(u.getIdUser(), pfN.getText()))
                AlertHelper.info("Succès", "Mot de passe de « " + u.getLogin() + " » mis à jour.");
            else AlertHelper.erreur("Erreur", "La mise à jour a échoué.");
        });
    }

    private void ouvrirDialogAjout() {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Nouveau compte utilisateur");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okBtn = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setText("Créer le compte"); okBtn.getStyleClass().add("btn-primary-small");

        TextField    tfLogin = tf("identifiant unique");
        PasswordField pfPwd  = pwf("mot de passe");
        PasswordField pfPwd2 = pwf("confirmer le mot de passe");
        ComboBox<String> cbRole = new ComboBox<>(FXCollections.observableArrayList("EMPLOYE", "ADMIN"));
        cbRole.setValue("EMPLOYE"); cbRole.getStyleClass().add("combo-box"); cbRole.setMaxWidth(Double.MAX_VALUE);

        GridPane grid = grid();
        row(grid, 0, "Login *",        tfLogin);
        row(grid, 1, "Mot de passe *", pfPwd);
        row(grid, 2, "Confirmation *", pfPwd2);
        row(grid, 3, "Rôle",           cbRole);
        dlg.getDialogPane().setContent(grid); dlg.getDialogPane().setPrefWidth(440);

        okBtn.addEventFilter(ActionEvent.ACTION, ev -> {
            if (tfLogin.getText().trim().isEmpty())   { AlertHelper.erreur("Champ vide","Le login est obligatoire."); ev.consume(); return; }
            if (pfPwd.getText().isEmpty())            { AlertHelper.erreur("Champ vide","Le mot de passe est obligatoire."); ev.consume(); return; }
            if (!pfPwd.getText().equals(pfPwd2.getText())) { AlertHelper.erreur("Non concordant","Les mots de passe ne correspondent pas."); ev.consume(); }
        });

        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            boolean ok = dao.ajouter(tfLogin.getText().trim(), pfPwd.getText(), cbRole.getValue());
            if (ok) charger();
            else AlertHelper.erreur("Erreur", "Création échouée. Ce login existe peut-être déjà.");
        });
    }

    private TextField tf(String prompt) {
        TextField t = new TextField(); t.setPromptText(prompt); t.getStyleClass().add("field-input"); t.setMaxWidth(Double.MAX_VALUE); return t;
    }
    private PasswordField pwf(String prompt) {
        PasswordField p = new PasswordField(); p.setPromptText(prompt); p.getStyleClass().add("field-input"); p.setMaxWidth(Double.MAX_VALUE); return p;
    }
    private GridPane grid() {
        GridPane g = new GridPane(); g.setHgap(14); g.setVgap(10); g.setPadding(new Insets(20, 24, 12, 24));
        ColumnConstraints c1 = new ColumnConstraints(150), c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS); g.getColumnConstraints().addAll(c1, c2); return g;
    }
    private void row(GridPane g, int r, String text, javafx.scene.Node n) {
        Label l = new Label(text); l.getStyleClass().add("field-label"); g.add(l, 0, r); g.add(n, 1, r);
    }

    public BorderPane getRoot() { return root; }
}
