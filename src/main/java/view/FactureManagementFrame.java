package view;

import Controller.FactureController;
import Model.Facture;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import security.AuthorizationUtils;

public class FactureManagementFrame extends JFrame {

    private final DefaultTableModel model;
    private final JTable table;

    public FactureManagementFrame() {
        AuthorizationUtils.requireAuthenticated(AuthorizationUtils.currentUser());
        setTitle("Gestion des factures");
        setSize(700, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] cols = {"ID", "Client", "Voiture", "Montant", "Date", "Location"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addBtn = new JButton("Ajouter");
        JButton delBtn = new JButton("Supprimer");
        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn);
        btnPanel.add(delBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // ➕ Ajouter une facture
        addBtn.addActionListener(e -> new FactureFormDialog(this, this::loadData));

        // ❌ Supprimer une facture
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Supprimer cette facture ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    if (FactureController.deleteFacture(id)) {
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "Erreur de suppression.",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez une facture.");
            }
        });

        loadData();
        setVisible(true);
    }

    /** Recharge les factures depuis la base */
    private void loadData() {
        model.setRowCount(0);
        List<Facture> factures = FactureController.getAllFactures();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Facture f : factures) {
            model.addRow(new Object[]{
                    f.getId(),
                    f.getClientNom(),
                    f.getVoitureModele(),
                    f.getMontant(),
                    sdf.format(f.getDateFacture()),
                    f.getIdLocation()
            });
        }
    }
}
