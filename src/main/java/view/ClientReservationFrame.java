package view;

import Controller.LocationController;
import Controller.ClientController;
import Controller.VoitureController;
import Model.Client;
import Model.Location;
import Model.Utilisateur;
import Model.Voiture;
import security.AuthorizationUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.json.ParseException;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List; // Assurez-vous que List est importé
import java.util.concurrent.TimeUnit;

public class ClientReservationFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField dateDebutField;
    private JTextField dateFinField;
    private JTextField prixField;
    private final Utilisateur utilisateur;
    // Déclaration du bouton de déconnexion comme variable de classe
    private JButton btnLogoutClient;


    public ClientReservationFrame(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;

        setTitle("Réserver un véhicule - Client: " + utilisateur.getLogin()); // Personnaliser le titre
        setSize(800, 550); // Augmenter légèrement la hauteur pour le bouton
        // setDefaultCloseOperation(EXIT_ON_CLOSE); // Changé ci-dessous
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10)); // Ajout d'un BorderLayout avec espacement
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10,10,10,10));


        JLabel title = new JLabel("Voitures disponibles pour réservation", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Marque", "Modèle", "Immatricule", "Tarif"}, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre les cellules non éditables
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chargerVoitures();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout()); // Utiliser GridBagLayout pour plus de flexibilité
        GridBagConstraints gbc = new GridBagConstraints();
        formPanel.setBorder(BorderFactory.createTitledBorder("Détails de la Réservation"));
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST; formPanel.add(new JLabel("Date début (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; dateDebutField = new JTextField(); formPanel.add(dateDebutField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Date fin (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; dateFinField = new JTextField(); formPanel.add(dateFinField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Prix total estimé:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; prixField = new JTextField(); prixField.setEditable(false); formPanel.add(prixField, gbc);
        gbc.weightx = 0; // reset

        JButton calculerBtn = new JButton("Calculer Prix");
        JButton reserverBtn = new JButton("Réserver");

        JPanel reservationActionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        reservationActionsPanel.add(calculerBtn);
        reservationActionsPanel.add(reserverBtn);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; formPanel.add(reservationActionsPanel, gbc);


        // Panel pour le bouton de déconnexion
        JPanel bottomOuterPanel = new JPanel(new BorderLayout());
        bottomOuterPanel.add(formPanel, BorderLayout.CENTER);

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLogoutClient = new JButton("Déconnexion");
        logoutPanel.add(btnLogoutClient);
        bottomOuterPanel.add(logoutPanel, BorderLayout.SOUTH);

        add(bottomOuterPanel, BorderLayout.SOUTH);

        calculerBtn.addActionListener(e -> calculerPrix());
        reserverBtn.addActionListener(e -> enregistrerReservation());

        btnLogoutClient.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    ClientReservationFrame.this,
                    "Êtes-vous sûr de vouloir vous déconnecter ?",
                    "Confirmation de Déconnexion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                ClientReservationFrame.this.dispose();
                AuthorizationUtils.clearCurrentUser();
                // Model.Database.closeConnection(); // Optionnel
                SwingUtilities.invokeLater(() -> new LoginFrame());
            }
        });
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Simuler un clic sur le bouton déconnexion pour la confirmation
                btnLogoutClient.doClick();
            }
        });

        setVisible(true);
    }

    private void chargerVoitures() {
        model.setRowCount(0);
        List<Voiture> list = VoitureController.getAllVoitures();
        if (list == null) return; // Sécurité
        for (Voiture v : list) {
            if (v.isDisponible()) {
                model.addRow(new Object[]{v.getId(), v.getMarque(), v.getModele(), v.getImmatricule(), v.getTarif()});
            }
        }
    }

    private void calculerPrix() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            try {
                double tarif = Double.parseDouble(model.getValueAt(row, 4).toString());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date debut = sdf.parse(dateDebutField.getText());
                Date fin = sdf.parse(dateFinField.getText());

                if (fin.before(debut)) {
                     JOptionPane.showMessageDialog(this, "La date de fin ne peut pas être antérieure à la date de début.", "Erreur de Dates", JOptionPane.ERROR_MESSAGE);
                     return;
                }

                long diffInMillies = fin.getTime() - debut.getTime();
                long jours = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                
                if (debut.equals(fin)) { // Location pour le même jour
                    jours = 1;
                } else {
                    jours += 1; // Convention: du 1er au 3 = 3 jours
                }

                if (jours <= 0) { // Sécurité
                     JOptionPane.showMessageDialog(this, "La durée de la location doit être d'au moins un jour.", "Erreur de Durée", JOptionPane.ERROR_MESSAGE);
                     return;
                }
                prixField.setText(String.format("%.2f", jours * tarif).replace(",","."));
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Format de date invalide (yyyy-MM-dd attendu).", "Erreur de Format", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Erreur de format du tarif dans le tableau.", "Erreur de Données", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) { // Capture plus générale
                JOptionPane.showMessageDialog(this, "Erreur lors du calcul du prix : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une voiture dans la liste.", "Aucune Sélection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void enregistrerReservation() {
        int row = table.getSelectedRow();
        // Recalculer le prix au cas où
        calculerPrix(); 
        if (prixField.getText().isEmpty() || Double.parseDouble(prixField.getText()) <= 0) {
             JOptionPane.showMessageDialog(this, "Veuillez calculer un prix valide avant de réserver.", "Prix Non Calculé", JOptionPane.WARNING_MESSAGE);
             return;
        }

        if (row >= 0) { // Vérification que la voiture est toujours sélectionnée
            try {
                int voitureId = (int) model.getValueAt(row, 0);
                String modeleVoiture = model.getValueAt(row, 2).toString(); // Nom du modèle pour le message

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date debut = sdf.parse(dateDebutField.getText());
                Date fin = sdf.parse(dateFinField.getText());
                double prix = Double.parseDouble(prixField.getText());

                // Assurer que l'objet utilisateur et son ID sont valides
                if (utilisateur == null || utilisateur.getId() == 0) {
                     JOptionPane.showMessageDialog(this, "Erreur : Informations utilisateur non valides.", "Erreur Utilisateur", JOptionPane.ERROR_MESSAGE);
                     return;
                }

                Client client = ClientController.getClientByUtilisateurId(utilisateur.getId());
                if (client == null) {
                    JOptionPane.showMessageDialog(this,
                            "Aucun profil client n'est associé à cet utilisateur.",
                            "Profil Client Requis",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Création de l'objet Location
                Location loc = new Location(0, // ID auto-généré par la base
                        debut,
                        fin,
                        prix,
                        "Active", // Statut par défaut
                        "Réservation client via application", // Conditions par défaut
                        voitureId,
                        client.getId(),
                        utilisateur.getLogin(), // Nom du client (login de l'utilisateur)
                        modeleVoiture            // Modèle de la voiture
                );

                boolean ok = LocationController.addLocation(loc);
                if (ok) {
                    VoitureController.reserveVehicle(utilisateur, voitureId); // Marquer la voiture comme non disponible
                    JOptionPane.showMessageDialog(this, "Réservation effectuée avec succès pour : " + modeleVoiture + " !", "Réservation Confirmée", JOptionPane.INFORMATION_MESSAGE);
                    chargerVoitures(); // Rafraîchir la liste des voitures disponibles
                    // Vider les champs après réservation
                    dateDebutField.setText("");
                    dateFinField.setText("");
                    prixField.setText("");
                    table.clearSelection();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement de la réservation.\nConsultez la console pour les détails.", "Erreur d'Enregistrement", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Format de date invalide (yyyy-MM-dd attendu).", "Erreur de Format", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Erreur de format numérique (prix).", "Erreur de Format", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) { // Capture plus générale
                JOptionPane.showMessageDialog(this, "Une erreur inattendue est survenue : " + ex.getMessage(), "Erreur Inattendue", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Important pour le débogage
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une voiture et calculer le prix avant de réserver.", "Sélection Requise", JOptionPane.WARNING_MESSAGE);
        }
    }
}