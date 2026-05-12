package src;

import src.view.MainApp;
import javafx.application.Application;

/**
 * Entry point for CareLink.
 *
 * Why a separate launcher?
 * JavaFX fat JARs fail if the main class extends Application directly
 * (module system issue). This class delegates to MainApp.launch() and
 * works both with "mvn javafx:run" and "java -jar CareLink.jar".
 */
public class Main {
    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }
}
