package clubvideo.view;

import clubvideo.MainApp;
import clubvideo.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MainView {

    private final BorderPane root;
    private final StackPane  contentArea;

    // Garder une référence active de la vue courante pour éviter GC
    private Node currentView;

    public MainView() {
        root = new BorderPane();
        root.getStyleClass().add("main-root");

        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");

        root.setLeft(buildSidebar());
        root.setCenter(contentArea);

        showAccueil();
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(224);
        sidebar.setMinWidth(224);
        sidebar.setMaxWidth(224);

        // Logo
        VBox logoBox = new VBox(3);
        logoBox.getStyleClass().add("sidebar-logo");
        logoBox.setPadding(new Insets(24, 16, 20, 16));
        Label logo = new Label("🎬  CinéClub");
        logo.getStyleClass().add("sidebar-logo-title");
        Label ver  = new Label("v1.0 — " + Session.getUtilisateur().getRole());
        ver.getStyleClass().add("sidebar-logo-sub");
        logoBox.getChildren().addAll(logo, ver);

        // Navigation
        VBox nav = new VBox(2);
        nav.getStyleClass().add("sidebar-nav");
        nav.setPadding(new Insets(10, 8, 10, 8));
        VBox.setVgrow(nav, Priority.ALWAYS);

        nav.getChildren().addAll(
            navSection("NAVIGATION"),
            navItem("🏠", "Accueil",         () -> showAccueil()),
            navItem("📊", "Tableau de bord", () -> showDashboard()),
            navSection("GESTION"),
            navItem("📼", "Cassettes",       () -> show(new CassetteView().getRoot())),
            navItem("👤", "Abonnés",         () -> show(new AbonneView().getRoot())),
            navItem("🔄", "Locations",       () -> show(new LocationView().getRoot())),
            navItem("📂", "Catégories",      () -> show(new CategorieView().getRoot()))
        );

        if (Session.isAdmin()) {
            nav.getChildren().addAll(
                navSection("ADMINISTRATION"),
                navItem("👥", "Utilisateurs", () -> show(new UtilisateurView().getRoot()))
            );
        }

        // Spacer + bas de sidebar
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label userLbl = new Label("👤  " + Session.getUtilisateur().getLogin());
        userLbl.getStyleClass().add("sidebar-user");
        userLbl.setMaxWidth(Double.MAX_VALUE);

        Button btnLogout = new Button("⬅  Déconnexion");
        btnLogout.getStyleClass().add("btn-logout");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> { Session.deconnecter(); MainApp.showLogin(); });

        VBox bottom = new VBox(6);
        bottom.setPadding(new Insets(0, 8, 16, 8));
        bottom.getChildren().addAll(new Separator(), userLbl, btnLogout);

        nav.getChildren().addAll(spacer, bottom);
        sidebar.getChildren().addAll(logoBox, new Separator(), nav);
        return sidebar;
    }

    private Label navSection(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("nav-section");
        lbl.setPadding(new Insets(12, 8, 2, 8));
        lbl.setMaxWidth(Double.MAX_VALUE);
        return lbl;
    }

    private Button navItem(String icon, String text, Runnable action) {
        Button btn = new Button(icon + "  " + text);
        btn.getStyleClass().add("nav-item");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private void show(Node node) {
        currentView = node;
        contentArea.getChildren().setAll(node);
    }

    private void showAccueil() {
        show(new AccueilView().getRoot());
    }

    private void showDashboard() {
        show(new DashboardView().getRoot());
    }

    public BorderPane getRoot() { return root; }
}
