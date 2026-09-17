package view;

import Controller.VoitureController;
import Model.Voiture;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import security.AuthorizationUtils;

public class VoitureManagementFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public VoitureManagementFrame() {
        AuthorizationUtils.requireAdmin(AuthorizationUtils.currentUser());
        setTitle("Gestion des voitures");
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new String[]{"ID", "Marque", "Modèle", "Immatricule", "Tarif", "Disponible"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton ajouterBtn = new JButton("Ajouter");
        JButton modifierBtn = new JButton("Modifier");
        JButton supprimerBtn = new JButton("Supprimer");
        JPanel panel = new JPanel();
        panel.add(ajouterBtn);
        panel.add(modifierBtn);
        panel.add(supprimerBtn);
        add(panel, BorderLayout.SOUTH);

        ajouterBtn.addActionListener(e -> new VoitureFormDialog(this, null, this::chargerVoitures));
        modifierBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                Voiture v = new Voiture(
                    (int) model.getValueAt(row, 0),
                    (String) model.getValueAt(row, 1),
                    (String) model.getValueAt(row, 2),
                    (String) model.getValueAt(row, 3),
                    Double.parseDouble(model.getValueAt(row, 4).toString()),
                    (boolean) model.getValueAt(row, 5)
                );
                new VoitureFormDialog(this, v, this::chargerVoitures);
            }
        });
        supprimerBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "Supprimer ?", "Confirmer", JOptionPane.YES_NO_OPTION) == 0) {
                VoitureController.deleteVoiture((int) model.getValueAt(row, 0));
                chargerVoitures();
            }
        });

        chargerVoitures();
        setVisible(true);
    }

    private void chargerVoitures() {
        model.setRowCount(0);
        for (Voiture v : VoitureController.getAllVoitures()) {
            model.addRow(new Object[]{v.getId(), v.getMarque(), v.getModele(), v.getImmatricule(), v.getTarif(), v.isDisponible()});
        }
    }
}