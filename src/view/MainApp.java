package src.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX application.
 *
 * Manages the primary Stage and provides:
 *  - Login / Registration screens
 *  - Navigation shell (sidebar + content area)
 *  - Screen switching via navigateTo()
 *
 * Other developers (Ines, Idriss) create their screens as methods or classes
 * that return a Node/Pane, then register them in the sidebar.
 */
 
public class MainApp extends Application {

    private static Stage stage;

    @Override
    public void start(Stage stage) {
        MainApp.stage = stage;
        stage.setTitle("CareLink");
        stage.setWidth(1050);
        stage.setHeight(680);
        loadScreen("login.fxml");
        stage.show();
    }

    /**
     * Switches the entire scene to a different FXML file.
     * Call from any controller: MainApp.loadScreen("shell.fxml");
     */
    public static void loadScreen(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource(fxmlFile));
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Stage getStage() {
        return stage;
    }
}