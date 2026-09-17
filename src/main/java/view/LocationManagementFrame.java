package view;

import Controller.LocationController;
import Model.Location; // Assure-toi que cette classe a tous les getters nécessaires

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import security.AuthorizationUtils;

public class LocationManagementFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public LocationManagementFrame() {
        AuthorizationUtils.requireAuthenticated(AuthorizationUtils.currentUser());
        setTitle("Gestion des locations");
        setSize(800, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Les noms de colonnes doivent correspondre à ce que tu veux afficher.
        // Si ton modèle Location a getNomClient() et getModeleVoiture(), c'est bon.
        String[] colonnes = {"ID", "Client", "Voiture", "Début", "Fin", "Statut", "Prix"};
        model = new DefaultTableModel(colonnes, 0) {
            // Rendre les cellules non éditables directement dans le JTable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permettre une seule sélection

        JScrollPane scrollPane = new JScrollPane(table);

        JButton ajouterBtn = new JButton("Ajouter");
        JButton modifierBtn = new JButton("Modifier");
        JButton supprimerBtn = new JButton("Supprimer");

        JPanel boutonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Ajout d'espacement
        boutonsPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0)); // Marge en haut/bas du panel
        boutonsPanel.add(ajouterBtn);
        boutonsPanel.add(modifierBtn);
        boutonsPanel.add(supprimerBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(boutonsPanel, BorderLayout.SOUTH);

        // Action pour le bouton Ajouter
        ajouterBtn.addActionListener(e -> {
            // Appelle le constructeur pour l'ajout (celui qui ne prend pas d'objet Location)
            new LocationFormDialog(this, this::chargerLocations);
        });

        // Action pour le bouton Modifier
        modifierBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int locationId = (int) model.getValueAt(selectedRow, 0); // Récupère l'ID de la ligne sélectionnée

                // Récupérer l'objet Location complet depuis la base de données
                // C'est plus sûr que de reconstruire à partir des chaînes du JTable
                Location locToEdit = null;
                List<Location> toutesLesLocations = LocationController.getAllLocations(); // Peut être optimisé si tu as beaucoup de locations
                                                                                       // en ayant une méthode findById dans le controller.
                for (Location l : toutesLesLocations) {
                    if (l.getId() == locationId) {
                        locToEdit = l;
                        break;
                    }
                }

                if (locToEdit != null) {
                    // Appelle le constructeur de LocationFormDialog pour la MODIFICATION
                    // en passant l'objet Location à éditer et le callback pour rafraîchir.
                    new LocationFormDialog(this, locToEdit, this::chargerLocations);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erreur : Impossible de récupérer les détails de la location sélectionnée (ID: " + locationId + ").",
                            "Erreur de Modification", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner une location dans la liste à modifier.",
                        "Aucune Sélection", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Action pour le bouton Supprimer
        supprimerBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) model.getValueAt(selectedRow, 0); // Récupère l'ID de la ligne
                String clientInfo = model.getValueAt(selectedRow, 1).toString(); // Info client pour le message
                String voitureInfo = model.getValueAt(selectedRow, 2).toString(); // Info voiture pour le message

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Êtes-vous sûr de vouloir supprimer la location pour :\nClient: " + clientInfo + "\nVoiture: " + voitureInfo + " (ID: " + id + ") ?",
                        "Confirmation de Suppression",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean ok = LocationController.deleteLocation(id);
                    if (ok) {
                        // Si la suppression réussit, il faudrait aussi rendre la voiture à nouveau disponible
                        // Cela nécessite de connaître l'ID de la voiture associée à cette location
                        // Tu peux l'ajouter comme info cachée dans ton modèle de table, ou le récupérer
                        // en rechargeant l'objet Location avant suppression.
                        // Pour l'instant, on suppose que deleteLocation gère tout ou que ce n'est pas géré ici.
                        JOptionPane.showMessageDialog(this, "Location (ID: " + id + ") supprimée avec succès.", "Suppression Réussie", JOptionPane.INFORMATION_MESSAGE);
                        chargerLocations(); // Rafraîchir la liste
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de la location (ID: " + id + ").\nVeuillez vérifier la console pour plus de détails.", "Erreur de Suppression", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une location à supprimer.", "Aucune Sélection", JOptionPane.WARNING_MESSAGE);
            }
        });

        chargerLocations(); // Charger les données initiales
        setVisible(true);
    }

    private void chargerLocations() {
        model.setRowCount(0); // Vide le tableau avant de recharger
        List<Location> locations = LocationController.getAllLocations();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Format pour les dates

        if (locations == null || locations.isEmpty()) {
            // Optionnel: afficher un message si aucune location n'est trouvée
            // System.out.println("Aucune location à afficher.");
            return;
        }

        for (Location loc : locations) {
            // Assure-toi que les getters de Location (getNomClient, getModeleVoiture, etc.) existent et retournent les bonnes infos
            model.addRow(new Object[]{
                    loc.getId(),
                    loc.getNomClient(),         // Provient de la jointure dans LocationController
                    loc.getModeleVoiture(),     // Provient de la jointure dans LocationController
                    (loc.getDateDebut() != null ? sdf.format(loc.getDateDebut()) : ""),
                    (loc.getDateFin() != null ? sdf.format(loc.getDateFin()) : ""),
                    loc.getStatut(),
                    String.format("%.2f", loc.getPrix()).replace(",",".") // Format prix avec 2 décimales et point
            });
        }
    }

    // Optionnel: main pour tester cette fenêtre seule
    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> new LocationManagementFrame());
    // }
}