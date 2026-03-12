package clubvideo.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class AlertHelper {

    public static void info(String titre, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titre); a.setHeaderText(null); a.setContentText(message);
        a.showAndWait();
    }

    public static void erreur(String titre, String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titre); a.setHeaderText(null); a.setContentText(message);
        a.showAndWait();
    }

    public static boolean confirmer(String titre, String message) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titre); a.setHeaderText(null); a.setContentText(message);
        Optional<ButtonType> r = a.showAndWait();
        return r.isPresent() && r.get() == ButtonType.OK;
    }
}
