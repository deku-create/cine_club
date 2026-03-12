package clubvideo.view;

import clubvideo.MainApp;
import clubvideo.dao.UtilisateurDAO;
import clubvideo.model.Utilisateur;
import clubvideo.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginView {

    private final VBox root;

    public LoginView() {
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("login-bg");
        root.setFillWidth(true);

        VBox card = new VBox(14);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(420);
        card.setPadding(new Insets(44, 40, 36, 40));
        card.setAlignment(Pos.TOP_LEFT);

        // Logo
        VBox logoBox = new VBox(6);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(0, 0, 8, 0));
        Label icon = new Label("🎬");
        icon.setStyle("-fx-font-size:50px;");
        Label title = new Label("CinéClub");
        title.getStyleClass().add("login-title");
        Label subtitle = new Label("Système de gestion de club vidéo");
        subtitle.getStyleClass().add("login-subtitle");
        logoBox.getChildren().addAll(icon, title, subtitle);

        Separator sep = new Separator();

        // Login
        Label lblLogin = new Label("IDENTIFIANT");
        lblLogin.getStyleClass().add("field-label");
        TextField tfLogin = new TextField();
        tfLogin.setPromptText("Votre identifiant");
        tfLogin.getStyleClass().add("field-input");
        tfLogin.setMaxWidth(Double.MAX_VALUE);

        // Password
        Label lblPwd = new Label("MOT DE PASSE");
        lblPwd.getStyleClass().add("field-label");
        PasswordField pfPwd = new PasswordField();
        pfPwd.setPromptText("••••••••");
        pfPwd.getStyleClass().add("field-input");
        pfPwd.setMaxWidth(Double.MAX_VALUE);

        // Erreur
        Label lblError = new Label();
        lblError.getStyleClass().add("error-label");
        lblError.setMaxWidth(Double.MAX_VALUE);
        lblError.setVisible(false);
        lblError.setManaged(false);
        lblError.setWrapText(true);

        // Bouton
        Button btnLogin = new Button("Se connecter →");
        btnLogin.getStyleClass().add("btn-primary");
        btnLogin.setMaxWidth(Double.MAX_VALUE);

        // Hint
        Label hint = new Label("Comptes démo :  admin / admin123   •   employe / employe123");
        hint.getStyleClass().add("hint-label");
        hint.setWrapText(true);
        hint.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(logoBox, sep, lblLogin, tfLogin, lblPwd, pfPwd, lblError, btnLogin, hint);
        root.getChildren().add(card);

        // Logique
        Runnable doLogin = () -> {
            String login = tfLogin.getText().trim();
            String pwd   = pfPwd.getText();
            if (login.isEmpty() || pwd.isEmpty()) {
                showError(lblError, "⚠  Veuillez remplir tous les champs.");
                return;
            }
            Utilisateur u = new UtilisateurDAO().authentifier(login, pwd);
            if (u == null) {
                showError(lblError, "❌  Identifiant ou mot de passe incorrect.");
                pfPwd.clear();
                pfPwd.requestFocus();
            } else {
                Session.connecter(u);
                MainApp.showMain();
            }
        };

        btnLogin.setOnAction(e -> doLogin.run());
        pfPwd.setOnAction(e -> doLogin.run());
        tfLogin.setOnAction(e -> pfPwd.requestFocus());
    }

    private void showError(Label lbl, String msg) {
        lbl.setText(msg); lbl.setVisible(true); lbl.setManaged(true);
    }

    public VBox getRoot() { return root; }
}
