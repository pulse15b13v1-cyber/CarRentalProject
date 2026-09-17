package view;

import Controller.AuthController;
import Model.Utilisateur;
import security.AuthorizationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private final JTextField loginField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton registerButton;

    public LoginFrame() {
        setTitle("Connexion - Car Rental");
        setDefaultCloseOperation(EXIT_ON_CLOSE); // L'application se termine si cette fenêtre est fermée

        // --- Panneau principal avec GridBagLayout ---
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Marges autour du panneau
        gbc.insets = new Insets(5, 5, 5, 5); // Espacement entre les composants
        gbc.fill = GridBagConstraints.HORIZONTAL; // Les composants s'étirent horizontalement

        // --- Titre de l'application ---
        JLabel titleLabel = new JLabel("CAR RENTAL", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Police plus grande et en gras
        // Si vous utilisez FlatLaf, vous pouvez utiliser des styles sémantiques :
        // titleLabel.putClientProperty("FlatLaf.styleClass", "h1");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Le titre s'étend sur 2 colonnes
        gbc.weighty = 0.3; // Donne un peu d'espace vertical au-dessus
        panel.add(titleLabel, gbc);
        gbc.weighty = 0; // Réinitialiser le poids vertical
        gbc.gridwidth = 1; // Réinitialiser la largeur à 1 colonne

        // --- Champ Identifiant ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END; // Aligner le label à droite
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Identifiant:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START; // Aligner le champ de texte à gauche
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginField = new JTextField(15); // Taille préférée de 15 caractères
        panel.add(loginField, gbc);

        // --- Champ Mot de passe ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Mot de passe:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // --- Panneau pour les boutons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Centrer les boutons
        loginButton = new JButton("Se connecter");
        registerButton = new JButton("S'inscrire");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // S'étend sur 2 colonnes
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER; // Centrer le panneau de boutons
        gbc.weighty = 0.3; // Donne un peu d'espace vertical en dessous
        panel.add(buttonPanel, gbc);

        // --- Actions des composants ---
        // Action pour le champ mot de passe (appui sur Entrée)
        passwordField.addActionListener(e -> handleLogin());
        // Action pour le bouton "Se connecter"
        loginButton.addActionListener(e -> handleLogin());

        // Action pour le bouton "S'inscrire"
        registerButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new RegisterFrame()); // Lancer dans le thread de dispatching des événements Swing
            dispose(); // Ferme la fenêtre de login actuelle
        });

        // Pour que le bouton par défaut soit "Se connecter" si on appuie sur Entrée n'importe où (avec focus approprié)
        getRootPane().setDefaultButton(loginButton);

        // --- Ajout du panneau à la JFrame et affichage ---
        getContentPane().add(panel);
        pack(); // Ajuste la taille de la fenêtre au contenu préféré des composants
        setLocationRelativeTo(null); // Centre la fenêtre sur l'écran
        setVisible(true);
    }

    private void handleLogin() {
        String login = loginField.getText().trim(); // trim() pour enlever les espaces avant/après
        String password = new String(passwordField.getPassword());

        // Validation simple des champs
        if (login.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez saisir votre identifiant et votre mot de passe.",
                    "Champs Requis",
                    JOptionPane.WARNING_MESSAGE);
            if (login.isEmpty()) {
                loginField.requestFocusInWindow();
            } else {
                passwordField.requestFocusInWindow();
            }
            return;
        }

        // Appel au contrôleur d'authentification
        Utilisateur utilisateur = AuthController.login(login, password);

        if (utilisateur != null) {
            AuthorizationUtils.setCurrentUser(utilisateur);
            // Succès de la connexion
        	JOptionPane.showMessageDialog(this,
        		    "Bienvenue " + utilisateur.getLogin() + " (" + utilisateur.getTypeUtilisateur() + ") !",
        		    "Connexion Réussie",
        		    JOptionPane.INFORMATION_MESSAGE);
            // Redirection en fonction du type d'utilisateur
            if ("admin".equalsIgnoreCase(utilisateur.getTypeUtilisateur())) {
                SwingUtilities.invokeLater(() -> new AdminDashboardFrame());
            } else if ("client".equalsIgnoreCase(utilisateur.getTypeUtilisateur())) {
                SwingUtilities.invokeLater(() -> new ClientReservationFrame(utilisateur));
            } else {
                AuthorizationUtils.clearCurrentUser();
                JOptionPane.showMessageDialog(this,
                        "Type utilisateur non autorisé.",
                        "Erreur d'Autorisation",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            dispose(); // Ferme la fenêtre de login après une redirection réussie

        } else {
            // Échec de la connexion
            JOptionPane.showMessageDialog(this,
                    "Identifiant ou mot de passe incorrect.",
                    "Erreur de Connexion",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText(""); // Vider le champ mot de passe
            loginField.requestFocusInWindow(); // Remettre le focus sur le champ login
        }
    }

    // Méthode main pour tester cette fenêtre spécifiquement (optionnel)
    // public static void main(String[] args) {
    //     // Configurer FlatLaf ici si vous testez uniquement cette fenêtre
    //     try {
    //         UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
    //     } catch (Exception ex) {
    //         System.err.println("Failed to initialize LaF");
    //     }
    //     SwingUtilities.invokeLater(() -> new LoginFrame());
    // }
}