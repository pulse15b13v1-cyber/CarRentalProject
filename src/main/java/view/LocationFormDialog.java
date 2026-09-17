package view;

import Controller.ClientController;
import Controller.LocationController;
import Controller.VoitureController;
import Model.Client;
import Model.Location;
import Model.Voiture;

import javax.swing.*;
import javax.swing.text.DateFormatter;
// import javax.swing.text.DefaultFormatterFactory; // Pas utilisé directement
import java.awt.*;
// import java.awt.event.ActionEvent; // Pas utilisé directement
// import java.awt.event.ActionListener; // Pas utilisé directement
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects; // Pour Objects.equals
import java.util.concurrent.TimeUnit;

public class LocationFormDialog extends JDialog {

    private JComboBox<ClientItem> clientBox;
    private JComboBox<VoitureItem> voitureBox;
    private JFormattedTextField dateDebutField;
    private JFormattedTextField dateFinField;
    private JTextField prixField;
    private JTextField statutField; // Pourra devenir une JComboBox si plus de statuts
    private JTextArea conditionsArea;
    private JButton saveBtn;
    private JButton calculerPrixBtn;

    private Runnable onSuccessCallback;
    private Location currentLoc; // Pour stocker la location en cours d'édition
    private boolean isEditMode;  // Pour savoir si on est en mode ajout ou édition

    // Classe interne pour afficher les clients dans la JComboBox
    private static class ClientItem {
        private int id;
        private String displayName;

        public ClientItem(int id, String nom, String prenom) {
            this.id = id;
            this.displayName = nom + " " + prenom + " (ID: " + id + ")";
        }

        public int getId() { return id; }

        @Override
        public String toString() { return displayName; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClientItem that = (ClientItem) o;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    // Classe interne pour afficher les voitures dans la JComboBox
    private static class VoitureItem {
        private int id;
        private String displayName;
        private double tarifJournalier;

        public VoitureItem(int id, String marque, String modele, double tarif) {
            this.id = id;
            this.displayName = marque + " " + modele + " (Tarif: " + String.format("%.2f", tarif) + ", ID: " + id + ")";
            this.tarifJournalier = tarif;
        }

        public int getId() { return id; }
        public double getTarifJournalier() { return tarifJournalier; }

        @Override
        public String toString() { return displayName; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VoitureItem that = (VoitureItem) o;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    // Constructeur pour l'AJOUT d'une nouvelle location
    public LocationFormDialog(JFrame parent, Runnable onSuccess) {
        super(parent, "Nouvelle location", true);
        this.currentLoc = null; // Pas de location existante pour l'ajout
        this.isEditMode = false;
        this.onSuccessCallback = onSuccess;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    // Constructeur pour la MODIFICATION d'une location existante
    public LocationFormDialog(JFrame parent, Location loc, Runnable onSuccess) {
        super(parent, "Modifier la location (ID: " + loc.getId() + ")", true);
        this.currentLoc = loc; // Stocker la location à modifier
        this.isEditMode = true;
        this.onSuccessCallback = onSuccess;
        initComponents();
        fillFormForEdit(); // Pré-remplir le formulaire
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Client
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Client:"), gbc);
        clientBox = new JComboBox<>();
        List<Client> clients = ClientController.getAllClients();
        for (Client c : clients) {
            clientBox.addItem(new ClientItem(c.getId(), c.getNom(), c.getPrenom()));
        }
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; panel.add(clientBox, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        // Voiture
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Voiture:"), gbc);
        voitureBox = new JComboBox<>();
        List<Voiture> voitures = VoitureController.getAllVoitures(); // Obtenir TOUTES les voitures
        for (Voiture v : voitures) {
            // En mode ajout, n'afficher que les voitures disponibles
            // En mode édition, afficher toutes les voitures pour pouvoir sélectionner celle déjà louée
            if (!isEditMode && v.isDisponible()) {
                voitureBox.addItem(new VoitureItem(v.getId(), v.getMarque(), v.getModele(), v.getTarif()));
            } else if (isEditMode) {
                // Si en mode édition, inclure la voiture actuelle même si elle n'est pas "disponible"
                // et les autres voitures disponibles pour un éventuel changement.
                 if (v.isDisponible() || (currentLoc != null && v.getId() == currentLoc.getIdVoiture())) {
                    voitureBox.addItem(new VoitureItem(v.getId(), v.getMarque(), v.getModele(), v.getTarif()));
                 }
            }
        }
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; panel.add(voitureBox, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormatter dateFormatter = new DateFormatter(dateFormat);
        dateFormatter.setAllowsInvalid(false);
        dateFormatter.setOverwriteMode(true);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Date début (yyyy-MM-dd):"), gbc);
        dateDebutField = new JFormattedTextField(dateFormatter);
        dateDebutField.setValue(new Date());
        dateDebutField.setColumns(10);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; panel.add(dateDebutField, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Date fin (yyyy-MM-dd):"), gbc);
        dateFinField = new JFormattedTextField(dateFormatter);
        dateFinField.setValue(new Date());
        dateFinField.setColumns(10);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; panel.add(dateFinField, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Prix total:"), gbc);
        prixField = new JTextField(10);
        prixField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL; panel.add(prixField, gbc);
        
        calculerPrixBtn = new JButton("Calculer Prix");
        gbc.gridx = 2; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; panel.add(calculerPrixBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 5; panel.add(new JLabel("Statut:"), gbc);
        statutField = new JTextField("Active", 10);
        statutField.setEditable(isEditMode); // Modifiable seulement en mode édition
        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; panel.add(statutField, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0; gbc.gridy = 6; panel.add(new JLabel("Conditions:"), gbc);
        conditionsArea = new JTextArea(3, 20);
        JScrollPane scrollPaneConditions = new JScrollPane(conditionsArea);
        gbc.gridx = 1; gbc.gridy = 6; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0; panel.add(scrollPaneConditions, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;

        saveBtn = new JButton(isEditMode ? "Mettre à jour" : "Enregistrer");
        gbc.gridx = 1; gbc.gridy = 7; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST; panel.add(saveBtn, gbc);

        calculerPrixBtn.addActionListener(e -> calculerEtAfficherPrix());
        saveBtn.addActionListener(e -> enregistrerOuModifierLocation());
        
        getRootPane().setDefaultButton(saveBtn);
        add(panel);
    }
    
    private void calculerEtAfficherPrix() {
        VoitureItem selectedVoiture = (VoitureItem) voitureBox.getSelectedItem();
        Date dateDebut = (Date) dateDebutField.getValue();
        Date dateFin = (Date) dateFinField.getValue();

        if (selectedVoiture == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une voiture.", "Erreur", JOptionPane.ERROR_MESSAGE);
            prixField.setText(""); return;
        }
        if (dateDebut == null || dateFin == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner les dates de début et de fin.", "Erreur", JOptionPane.ERROR_MESSAGE);
            prixField.setText(""); return;
        }
        if (dateFin.before(dateDebut)) {
            JOptionPane.showMessageDialog(this, "La date de fin ne peut pas être antérieure à la date de début.", "Erreur de Dates", JOptionPane.ERROR_MESSAGE);
            prixField.setText(""); return;
        }
        
        long diffInMillies = dateFin.getTime() - dateDebut.getTime(); // Pas besoin de Math.abs si dateFin est après dateDebut
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        
        if (dateDebut.equals(dateFin)) { // Location pour le même jour
            diffInDays = 1;
        } else { // Si dates différentes, ajouter 1 pour inclure le dernier jour
            diffInDays += 1;
        }

        if (diffInDays <= 0) {
            JOptionPane.showMessageDialog(this, "La durée de la location doit être d'au moins un jour.", "Erreur de Durée", JOptionPane.ERROR_MESSAGE);
            prixField.setText(""); return;
        }

        double prixTotal = diffInDays * selectedVoiture.getTarifJournalier();
        prixField.setText(String.format("%.2f", prixTotal).replace(",", ".")); // Assurer le point comme séparateur décimal
    }

    private void enregistrerOuModifierLocation() {
        calculerEtAfficherPrix();
        if (prixField.getText().isEmpty()) {
             if(isEditMode && currentLoc != null && currentLoc.getPrix() > 0){
                // Si en mode édition et que le prix était déjà défini, on peut potentiellement continuer
                // Mais il est préférable que le calcul soit toujours fait.
                // Pour l'instant, on force le calcul.
             } else {
                JOptionPane.showMessageDialog(this, "Veuillez d'abord calculer le prix.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
             }
        }

        ClientItem selectedClient = (ClientItem) clientBox.getSelectedItem();
        VoitureItem selectedVoiture = (VoitureItem) voitureBox.getSelectedItem();
        Date dateDebut = (Date) dateDebutField.getValue();
        Date dateFin = (Date) dateFinField.getValue();
        String statut = statutField.getText().trim();
        String conditions = conditionsArea.getText().trim();
        double prix;

        try {
            prix = Double.parseDouble(prixField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erreur de format du prix. Veuillez recalculer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedClient == null || selectedVoiture == null || dateDebut == null || dateFin == null || statut.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs obligatoires (Client, Voiture, Dates, Statut).", "Champs Requis", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Location locationData = new Location(
                isEditMode ? currentLoc.getId() : 0, // Utiliser l'ID existant en mode édition
                dateDebut,
                dateFin,
                prix,
                statut,
                conditions,
                selectedVoiture.getId(),
                selectedClient.getId(),
                selectedClient.toString(), 
                selectedVoiture.toString()
        );

        boolean success;
        String successMessage;

        if (isEditMode) {
            success = LocationController.updateLocation(locationData);
            successMessage = "Location (ID: " + currentLoc.getId() + ") modifiée avec succès !";
            // Gérer la disponibilité de l'ancienne voiture si la voiture a été changée
            if(currentLoc.getIdVoiture() != selectedVoiture.getId()){
                VoitureController.updateDisponibilite(currentLoc.getIdVoiture(), true); // Rendre l'ancienne dispo
                VoitureController.updateDisponibilite(selectedVoiture.getId(), false); // Marquer la nouvelle comme non dispo
            }

        } else { // Mode Ajout
            success = LocationController.addLocation(locationData);
            successMessage = "Nouvelle location enregistrée avec succès !";
            if(success) {
                 VoitureController.updateDisponibilite(selectedVoiture.getId(), false);
            }
        }

        if (success) {
            JOptionPane.showMessageDialog(this, successMessage, "Succès", JOptionPane.INFORMATION_MESSAGE);
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement/modification de la location.\nVeuillez vérifier la console pour plus de détails.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillFormForEdit() {
        if (currentLoc != null) {
            // Sélectionner le client
            for (int i = 0; i < clientBox.getItemCount(); i++) {
                if (clientBox.getItemAt(i).getId() == currentLoc.getIdClient()) {
                    clientBox.setSelectedIndex(i);
                    break;
                }
            }
            // Sélectionner la voiture (elle a déjà été ajoutée à la liste)
            boolean voitureTrouvee = false;
            for (int i = 0; i < voitureBox.getItemCount(); i++) {
                if (voitureBox.getItemAt(i).getId() == currentLoc.getIdVoiture()) {
                    voitureBox.setSelectedIndex(i);
                    voitureTrouvee = true;
                    break;
                }
            }
            // Si la voiture de la location n'est plus dans la liste (cas rare, ex: supprimée), il faut la rajouter.
            // La logique dans initComponents() pour le mode édition devrait déjà l'inclure.
            if (!voitureTrouvee) {
                 System.err.println("Attention: La voiture de la location ID " + currentLoc.getIdVoiture() + " n'a pas été trouvée dans la JComboBox.");
                 // Tu pourrais ici essayer de la recharger et l'ajouter si elle n'y est pas.
            }


            dateDebutField.setValue(currentLoc.getDateDebut());
            dateFinField.setValue(currentLoc.getDateFin());
            prixField.setText(String.format("%.2f", currentLoc.getPrix()).replace(",","."));
            statutField.setText(currentLoc.getStatut());
            conditionsArea.setText(currentLoc.getConditions());
        }
    }
}