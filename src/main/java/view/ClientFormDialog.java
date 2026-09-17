package view;

import Controller.ClientController;
import Model.Client;
import Model.Utilisateur;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;

public class ClientFormDialog extends JDialog {
    private JTextField nomField;
    private JTextField prenomField;
    private JFormattedTextField telField;
    private JTextField mailField;
    private Client currentClient;
    private Runnable onSuccessCallback;
    private int idUtilisateurAssocie;

    public ClientFormDialog(JFrame parent, Client client, Runnable onSuccess, Utilisateur utilisateurConnecte) {
        // ... (constructeur existant) ...
        // Mettre l'appel à initComponents() à la fin après avoir initialisé les variables de classe
        this(parent, client, onSuccess); // Appelle l'autre constructeur
        if (utilisateurConnecte != null) {
            this.idUtilisateurAssocie = utilisateurConnecte.getId();
        } else {
            this.idUtilisateurAssocie = 0; 
        }
        // initComponents(); // Déplacé dans l'autre constructeur ou après l'appel de this()
    }

    public ClientFormDialog(JFrame parent, Client client, Runnable onSuccess) {
        super(parent, client == null ? "Ajouter un client" : "Modifier un client", true);
        this.currentClient = client;
        this.onSuccessCallback = onSuccess;
        this.idUtilisateurAssocie = (client != null && client.getIdUtilisateur() != 0) ? client.getIdUtilisateur() : 0;

        initComponents(); // Appeler ici après l'initialisation des variables de classe
        pack();
        setLocationRelativeTo(parent);
        // setVisible(true); // Le setVisible(true) est mieux à la fin du constructeur appelant si possible,
                           // mais ici c'est ok car c'est la dernière instruction avant la fin du constructeur.
                           // Ou, si vous appelez ce constructeur depuis un autre, déplacez setVisible(true) à la fin de ce constructeur là.
                           // Pour l'instant, on le laisse ici.
        setVisible(true); 
    }

    private void initComponents() {
        nomField = new JTextField(20);
        prenomField = new JTextField(20);
        mailField = new JTextField(20);

        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        integerFormat.setGroupingUsed(false);
        NumberFormatter numberFormatter = new NumberFormatter(integerFormat);
        numberFormatter.setValueClass(Long.class);
        numberFormatter.setAllowsInvalid(false);
        numberFormatter.setCommitsOnValidEdit(true);
        telField = new JFormattedTextField(numberFormatter);
        telField.setColumns(20);

        if (currentClient != null) {
            nomField.setText(currentClient.getNom());
            prenomField.setText(currentClient.getPrenom());
            if (currentClient.getTelephone() != 0) {
                telField.setValue((long) currentClient.getTelephone());
            }
            mailField.setText(currentClient.getMail());
            // idUtilisateurAssocie est déjà initialisé
        }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; panel.add(nomField, gbc);
        // gbc.fill = GridBagConstraints.NONE; // Pas nécessaire de le reset à chaque fois si la prochaine est HORIZONTAL

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Prénom:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; /*gbc.fill = GridBagConstraints.HORIZONTAL;*/ panel.add(prenomField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Téléphone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; /*gbc.fill = GridBagConstraints.HORIZONTAL;*/ panel.add(telField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; /*gbc.fill = GridBagConstraints.HORIZONTAL;*/ panel.add(mailField, gbc);
        gbc.fill = GridBagConstraints.NONE; // Reset ici avant les boutons

        // Panel pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // Aligner à droite
        JButton saveBtn = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveBtn);

        gbc.gridx = 0; // Commencer à la colonne 0 pour le panel de boutons
        gbc.gridy = 4;
        gbc.gridwidth = 2; // S'étend sur les deux colonnes
        gbc.anchor = GridBagConstraints.EAST; // Aligner le panel de boutons à droite
        panel.add(buttonPanel, gbc);

        saveBtn.addActionListener(e -> enregistrerClient());
        cancelButton.addActionListener(e -> {
            // Demander confirmation si des champs ont été modifiés (optionnel mais bonne UX)
            // Pour la simplicité, on ferme directement.
            dispose(); // Ferme simplement la boîte de dialogue
        });
        
        getRootPane().setDefaultButton(saveBtn);

        add(panel);
    }

    private void enregistrerClient() {
        // ... (votre logique existante pour enregistrerClient) ...
        // Assurez-vous que currentClient est utilisé pour déterminer si c'est un ajout ou une modification
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String mail = mailField.getText().trim();
        int telephoneNum = 0;

        if (nom.isEmpty() || prenom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom et le prénom sont obligatoires.", "Champs Requis", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!mail.isEmpty() && !mail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) { // Validation email simple
            JOptionPane.showMessageDialog(this, "Format d'email invalide.", "Erreur de Saisie", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object telValue = telField.getValue();
        if (telValue instanceof Long) {
            long telLong = (Long) telValue;
            if (telLong > Integer.MAX_VALUE || telLong < Integer.MIN_VALUE) {
                JOptionPane.showMessageDialog(this, "Numéro de téléphone hors limites.", "Erreur de Saisie", JOptionPane.ERROR_MESSAGE);
                return;
            }
            telephoneNum = (int) telLong;
        } else if (telField.getText().trim().isEmpty()) {
            // OK si le téléphone est optionnel
        } else {
            JOptionPane.showMessageDialog(this, "Format de téléphone invalide. Saisissez uniquement des chiffres.", "Erreur de Saisie", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Client c = new Client(
                (currentClient != null) ? currentClient.getId() : 0,
                nom,
                prenom,
                telephoneNum,
                mail,
                idUtilisateurAssocie
        );

        boolean ok;
        String successMessage;

        if (currentClient == null) { // Ajout
            ok = ClientController.addClient(c);
            successMessage = "Client ajouté avec succès !";
        } else { // Modification
            ok = ClientController.updateClient(c);
            successMessage = "Client modifié avec succès !";
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, successMessage, "Succès", JOptionPane.INFORMATION_MESSAGE);
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Erreur d'enregistrement du client.\nVeuillez vérifier la console pour plus de détails.", "Erreur d'Enregistrement", JOptionPane.ERROR_MESSAGE);
        }
    }
}