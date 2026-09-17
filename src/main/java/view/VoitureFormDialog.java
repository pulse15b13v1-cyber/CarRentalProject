package view;

import Controller.VoitureController;
import Model.Voiture;

import javax.swing.*;
import java.awt.*;

public class VoitureFormDialog extends JDialog {
    public VoitureFormDialog(JFrame parent, Voiture voiture, Runnable onSuccess) {
        super(parent, voiture == null ? "Ajouter une voiture" : "Modifier une voiture", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        JTextField marqueField = new JTextField();
        JTextField modeleField = new JTextField();
        JTextField immatField = new JTextField();
        JTextField tarifField = new JTextField();
        JCheckBox dispoBox = new JCheckBox("Disponible");

        if (voiture != null) {
            marqueField.setText(voiture.getMarque());
            modeleField.setText(voiture.getModele());
            immatField.setText(voiture.getImmatricule());
            tarifField.setText(String.valueOf(voiture.getTarif()));
            dispoBox.setSelected(voiture.isDisponible());
        }

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel("Marque:")); panel.add(marqueField);
        panel.add(new JLabel("Modèle:")); panel.add(modeleField);
        panel.add(new JLabel("Immatricule:")); panel.add(immatField);
        panel.add(new JLabel("Tarif:")); panel.add(tarifField);
        panel.add(new JLabel("Disponible:")); panel.add(dispoBox);
        JButton saveBtn = new JButton("Enregistrer");
        panel.add(new JLabel());
        panel.add(saveBtn);
        add(panel);

        saveBtn.addActionListener(e -> {
            try {
                Voiture v = new Voiture(
                    voiture != null ? voiture.getId() : 0,
                    marqueField.getText(), modeleField.getText(), immatField.getText(),
                    Double.parseDouble(tarifField.getText()), dispoBox.isSelected()
                );
                boolean ok = voiture == null ? VoitureController.addVoiture(v) : VoitureController.updateVoiture(v);
                if (ok) {
                    onSuccess.run();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur d'enregistrement");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Entrée invalide");
            }
        });
        setVisible(true);
    }
}
