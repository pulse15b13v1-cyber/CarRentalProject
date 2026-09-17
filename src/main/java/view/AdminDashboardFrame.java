package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter; // Import pour WindowAdapter
import java.awt.event.WindowEvent;  // Import pour WindowEvent
import security.AuthorizationUtils;

public class AdminDashboardFrame extends JFrame {
    public AdminDashboardFrame() {
        AuthorizationUtils.requireAdmin(AuthorizationUtils.currentUser());
        setTitle("Dashboard Admin");
        // Utiliser un layout plus flexible pour ajouter facilement un bouton en bas
        setLayout(new BorderLayout(10, 10)); // BorderLayout principal
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marge autour

        // Panel pour les boutons de navigation
        JPanel navigationPanel = new JPanel(new GridLayout(0, 1, 10, 10)); // 0 lignes = autant que nécessaire

        JButton btnVoitures = new JButton("Gérer les voitures");
        JButton btnClients = new JButton("Gérer les clients");
        JButton btnLocations = new JButton("Gérer les locations");
        JButton btnFactures = new JButton("Gérer les factures");

        // Appliquer une police et une taille un peu plus grandes
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        btnVoitures.setFont(buttonFont);
        btnClients.setFont(buttonFont);
        btnLocations.setFont(buttonFont);
        btnFactures.setFont(buttonFont);

        navigationPanel.add(btnVoitures);
        navigationPanel.add(btnClients);
        navigationPanel.add(btnLocations);
        navigationPanel.add(btnFactures);

        add(navigationPanel, BorderLayout.CENTER); // Ajoute le panel de navigation au centre

        // Panel pour le bouton de déconnexion en bas
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Aligner à droite
        JButton btnLogout = new JButton("Déconnexion");
        btnLogout.setFont(buttonFont);
        // Optionnel: Mettre une couleur distinctive
        // btnLogout.setBackground(new Color(220, 53, 69));
        // btnLogout.setForeground(Color.WHITE);
        bottomPanel.add(btnLogout);
        add(bottomPanel, BorderLayout.SOUTH); // Ajoute le panel avec déconnexion en bas

        // Actions des boutons de navigation
        btnVoitures.addActionListener(e -> SwingUtilities.invokeLater(() -> new VoitureManagementFrame()));
        btnClients.addActionListener(e -> SwingUtilities.invokeLater(() -> new ClientManagementFrame()));
        btnLocations.addActionListener(e -> SwingUtilities.invokeLater(() -> new LocationManagementFrame()));
        btnFactures.addActionListener(e -> SwingUtilities.invokeLater(() -> new FactureManagementFrame()));

        // Action du bouton Déconnexion
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    AdminDashboardFrame.this,
                    "Êtes-vous sûr de vouloir vous déconnecter ?",
                    "Confirmation de Déconnexion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                AdminDashboardFrame.this.dispose();
                AuthorizationUtils.clearCurrentUser();
                // Model.Database.closeConnection(); // Si vous avez cette méthode et souhaitez l'utiliser
                SwingUtilities.invokeLater(() -> new LoginFrame());
            }
        });

        // Gestion de la fermeture de la fenêtre principale
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Empêche la fermeture directe
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Simule un clic sur le bouton déconnexion pour la confirmation
                // Ceci assure que l'utilisateur est invité à confirmer avant de quitter
                // ou que la logique de déconnexion est exécutée.
                btnLogout.doClick();
            }
        });

        setSize(400, 350); // Légèrement plus grand pour le bouton de déconnexion
        setLocationRelativeTo(null);
        setVisible(true);
    }
}