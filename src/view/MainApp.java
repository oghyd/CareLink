package src.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import src.controller.AuthController;
import src.model.Admin;
import src.model.Etudiant;
import src.model.User;
import src.utils.SessionManager;

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

    private Stage primaryStage;
    private BorderPane mainLayout;     // sidebar left + content center
    private StackPane contentArea;     // swappable content zone
    private AuthController authController;

    // ──────────────────────────────────────────────────
    //  Lifecycle
    // ──────────────────────────────────────────────────

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.authController = new AuthController();

        primaryStage.setTitle("CareLink — Gestion des demandes et réclamations");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(650);

        showLoginScreen();
        primaryStage.show();
    }

    // ──────────────────────────────────────────────────
    //  Login screen
    // ──────────────────────────────────────────────────

    private void showLoginScreen() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: #f5f6fa;");

        // Title
        Text title = new Text("CareLink");
        title.setFont(Font.font("System", FontWeight.BOLD, 36));
        title.setFill(Color.web("#2c3e50"));

        Text subtitle = new Text("Connexion à votre compte");
        subtitle.setFont(Font.font("System", 16));
        subtitle.setFill(Color.web("#7f8c8d"));

        // Form
        TextField emailField = new TextField();
        emailField.setPromptText("Adresse email");
        emailField.setMaxWidth(320);
        emailField.setStyle(fieldStyle());

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setMaxWidth(320);
        passwordField.setStyle(fieldStyle());

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#e74c3c"));

        Button loginBtn = new Button("Se connecter");
        loginBtn.setMaxWidth(320);
        loginBtn.setStyle(primaryBtnStyle());
        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            User user = authController.login(email, password);
            if (user != null) {
                showMainShell();
            } else {
                errorLabel.setText("Email ou mot de passe incorrect, ou compte inactif.");
            }
        });

        // Register link
        Hyperlink registerLink = new Hyperlink("Pas de compte ? S'inscrire");
        registerLink.setOnAction(e -> showRegistrationScreen());

        root.getChildren().addAll(title, subtitle,
                new Region() {{ setPrefHeight(10); }},
                emailField, passwordField, errorLabel, loginBtn, registerLink);

        Scene scene = new Scene(root, 1000, 650);
        primaryStage.setScene(scene);
    }

    // ──────────────────────────────────────────────────
    //  Registration screen (étudiant self-signup)
    // ──────────────────────────────────────────────────

    private void showRegistrationScreen() {
        VBox root = new VBox(14);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f5f6fa;");

        Text title = new Text("Créer un compte étudiant");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setFill(Color.web("#2c3e50"));

        TextField nomField      = makeField("Nom", 320);
        TextField prenomField   = makeField("Prénom", 320);
        TextField emailField    = makeField("Email", 320);
        TextField matriculeField = makeField("Matricule", 320);
        TextField telephoneField = makeField("Téléphone", 320);
        TextField typeHandicapField = makeField("Type de handicap", 320);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setMaxWidth(320);
        passwordField.setStyle(fieldStyle());

        Label statusLabel = new Label();

        Button registerBtn = new Button("S'inscrire");
        registerBtn.setMaxWidth(320);
        registerBtn.setStyle(primaryBtnStyle());
        registerBtn.setOnAction(e -> {
            Etudiant etudiant = new Etudiant(
                0,
                nomField.getText().trim(),
                prenomField.getText().trim(),
                emailField.getText().trim(),
                passwordField.getText(),
                false, // inactive until admin approves
                matriculeField.getText().trim(),
                typeHandicapField.getText().trim(),
                telephoneField.getText().trim()
            );

            src.controller.CompteController compteCtrl = new src.controller.CompteController();
            boolean ok = compteCtrl.sInscrire(etudiant);

            if (ok) {
                statusLabel.setTextFill(Color.web("#27ae60"));
                statusLabel.setText("Compte créé ! En attente d'activation par l'administration.");
            } else {
                statusLabel.setTextFill(Color.web("#e74c3c"));
                statusLabel.setText("Erreur lors de l'inscription. Vérifiez vos informations.");
            }
        });

        Hyperlink backLink = new Hyperlink("← Retour à la connexion");
        backLink.setOnAction(e -> showLoginScreen());

        root.getChildren().addAll(title,
                nomField, prenomField, emailField, matriculeField,
                telephoneField, typeHandicapField, passwordField,
                statusLabel, registerBtn, backLink);

        Scene scene = new Scene(root, 1000, 650);
        primaryStage.setScene(scene);
    }

    // ──────────────────────────────────────────────────
    //  Main shell (sidebar + content area)
    // ──────────────────────────────────────────────────

    private void showMainShell() {
        mainLayout = new BorderPane();

        // --- Sidebar ---
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(220);
        sidebar.setPadding(new Insets(20, 12, 20, 12));
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        // User greeting
        User currentUser = SessionManager.getCurrentUser();
        String displayName = currentUser.getPrenom() + " " + currentUser.getNom();
        String roleLabel = SessionManager.isAdmin() ? "Administrateur" : "Étudiant";

        Label nameLabel = new Label(displayName);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.WHITE);

        Label roleTagLabel = new Label(roleLabel);
        roleTagLabel.setTextFill(Color.web("#bdc3c7"));
        roleTagLabel.setFont(Font.font("System", 12));

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #34495e;");

        sidebar.getChildren().addAll(nameLabel, roleTagLabel, sep);

        // --- Menu items based on role ---
        if (SessionManager.isEtudiant()) {
            sidebar.getChildren().addAll(
                sidebarButton("Mes demandes",      () -> navigateTo(placeholder("Mes demandes — Ines"))),
                sidebarButton("Mes réclamations",   () -> navigateTo(placeholder("Mes réclamations — Ines"))),
                sidebarButton("Mon profil",          () -> navigateTo(placeholder("Mon profil — Ines")))
            );
        }

        if (SessionManager.isAdmin()) {
            sidebar.getChildren().addAll(
                sidebarButton("Tableau de bord",     () -> navigateTo(placeholder("Tableau de bord — Omar"))),
                sidebarButton("Gestion des comptes", () -> navigateTo(placeholder("Gestion des comptes — Idriss"))),
                sidebarButton("Demandes",            () -> navigateTo(placeholder("Gestion des demandes — Idriss"))),
                sidebarButton("Réclamations",        () -> navigateTo(placeholder("Gestion des réclamations — Idriss"))),
                sidebarButton("Historique",           () -> navigateTo(placeholder("Historique / Archivage — Omar")))
            );
        }

        // Spacer then logout
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Déconnexion");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; "
                         + "-fx-font-size: 13; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> {
            authController.logout();
            showLoginScreen();
        });

        sidebar.getChildren().addAll(spacer, logoutBtn);

        // --- Content area ---
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(24));
        contentArea.setStyle("-fx-background-color: #ecf0f1;");

        // Default welcome
        navigateTo(placeholder("Bienvenue, " + displayName + " !"));

        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(contentArea);

        Scene scene = new Scene(mainLayout, 1100, 700);
        primaryStage.setScene(scene);
    }

    // ──────────────────────────────────────────────────
    //  Public API — used by Ines & Idriss to swap screens
    // ──────────────────────────────────────────────────

    /**
     * Replaces the content area with the given Node.
     * Usage from any screen class:
     *   mainApp.navigateTo(myNewScreen);
     */
    public void navigateTo(Node screen) {
        contentArea.getChildren().setAll(screen);
    }

    // ──────────────────────────────────────────────────
    //  Helpers
    // ──────────────────────────────────────────────────

    /** Placeholder screen — replace these with real screens as they're built. */
    private Node placeholder(String label) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        Text text = new Text(label);
        text.setFont(Font.font("System", FontWeight.BOLD, 22));
        text.setFill(Color.web("#7f8c8d"));
        Text hint = new Text("Écran à implémenter");
        hint.setFont(Font.font("System", 14));
        hint.setFill(Color.web("#bdc3c7"));
        box.getChildren().addAll(text, hint);
        return box;
    }

    private Button sidebarButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1; "
                   + "-fx-font-size: 14; -fx-padding: 10 16; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #34495e; -fx-text-fill: white; "
              + "-fx-font-size: 14; -fx-padding: 10 16; -fx-cursor: hand; -fx-background-radius: 6;"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #ecf0f1; "
              + "-fx-font-size: 14; -fx-padding: 10 16; -fx-cursor: hand;"));
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private TextField makeField(String prompt, double maxWidth) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setMaxWidth(maxWidth);
        field.setStyle(fieldStyle());
        return field;
    }

    private String fieldStyle() {
        return "-fx-padding: 10 14; -fx-font-size: 14; -fx-background-radius: 6; "
             + "-fx-border-radius: 6; -fx-border-color: #bdc3c7; -fx-border-width: 1;";
    }

    private String primaryBtnStyle() {
        return "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 15; "
             + "-fx-font-weight: bold; -fx-padding: 12 24; -fx-background-radius: 6; -fx-cursor: hand;";
    }
}
