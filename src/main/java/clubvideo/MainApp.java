package clubvideo;

import clubvideo.database.DatabaseConnection;
import clubvideo.view.LoginView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("CinéClub — Gestion de Club Vidéo");
        stage.setMinWidth(960);
        stage.setMinHeight(660);

        try {
            DatabaseConnection.getConnection();
        } catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de connexion");
            alert.setHeaderText("Impossible de se connecter à MySQL / XAMPP");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            Platform.exit();
            return;
        }

        showLogin();
        stage.show();
    }

    public static void showLogin() {
        LoginView v = new LoginView();
        Scene scene = new Scene(v.getRoot(), 480, 580);
        applyCSS(scene);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
    }

    public static void showMain() {
        clubvideo.view.MainView v = new clubvideo.view.MainView();
        Scene scene = new Scene(v.getRoot(), 1300, 800);
        applyCSS(scene);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
    }

    public static void applyCSS(Scene scene) {
        try {
            String css = MainApp.class.getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("[CSS] style.css non trouvé.");
        }
    }

    @Override
    public void stop() {
        DatabaseConnection.closeConnection();
    }

    public static void main(String[] args) { launch(args); }
}
