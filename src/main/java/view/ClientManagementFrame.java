package view;

import Controller.ClientController;
import Model.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import security.AuthorizationUtils;

public class ClientManagementFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public ClientManagementFrame() {
        AuthorizationUtils.requireAdmin(AuthorizationUtils.currentUser());
        setTitle("Gestion des clients");
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new String[]{"ID", "Nom", "Prénom", "Téléphone", "Email"}, 0);
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

        ajouterBtn.addActionListener(e -> new ClientFormDialog(this, null, this::chargerClients));
        modifierBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                Client c = new Client(
                    (int) model.getValueAt(row, 0),
                    (String) model.getValueAt(row, 1),
                    (String) model.getValueAt(row, 2),
                    Integer.parseInt(model.getValueAt(row, 3).toString()),
                    (String) model.getValueAt(row, 4)
                );
                new ClientFormDialog(this, c, this::chargerClients);
            }
        });
        supprimerBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "Supprimer ?", "Confirmer", JOptionPane.YES_NO_OPTION) == 0) {
                ClientController.deleteClient((int) model.getValueAt(row, 0));
                chargerClients();
            }
        });

        chargerClients();
        setVisible(true);
    }

    private void chargerClients() {
        model.setRowCount(0);
        for (Client c : ClientController.getAllClients()) {
            model.addRow(new Object[]{c.getId(), c.getNom(), c.getPrenom(), c.getTelephone(), c.getMail()});
        }
    }
}
