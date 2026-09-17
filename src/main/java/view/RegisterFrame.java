package view;

import Controller.AuthController;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private final JTextField loginField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmPasswordField;

    public RegisterFrame() {
        setTitle("Inscription - Car Rental");
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Ou DISPOSE_ON_CLOSE si LoginFrame reste ouverte
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Créer un compte", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.2;
        panel.add(titleLabel, gbc);
        gbc.weighty = 0;
        gbc.gridwidth = 1;


        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Identifiant:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginField = new JTextField(15);
        panel.add(loginField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Mot de passe:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Confirmer mot de passe:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        confirmPasswordField = new JPasswordField(15);
        panel.add(confirmPasswordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton registerButton = new JButton("S'inscrire");
        JButton cancelButton = new JButton("Annuler");
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

        add(panel);

        registerButton.addActionListener(e -> handleRegister());
        confirmPasswordField.addActionListener(e -> handleRegister()); // Entrée sur confirmation
        
        cancelButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new LoginFrame());
            dispose();
        });
        
        getRootPane().setDefaultButton(registerButton);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void handleRegister() {
        String login = loginField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (login.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez remplir tous les champs.",
                    "Champs Requis",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this,
                    "Les mots de passe ne correspondent pas.",
                    "Erreur de Mot de Passe",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            confirmPasswordField.setText("");
            passwordField.requestFocus();
            return;
        }
        
        // Ajouter ici une validation pour la complexité du mot de passe si désiré

        boolean success = AuthController.register(login, password);
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Inscription réussie ! Vous pouvez maintenant vous connecter.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            SwingUtilities.invokeLater(() -> new LoginFrame());
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Ce login est déjà utilisé ou une erreur est survenue.",
                    "Erreur d'Inscription",
                    JOptionPane.ERROR_MESSAGE);
            loginField.requestFocus();
        }
    }
}