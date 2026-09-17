package view;

import javax.swing.*;
import java.awt.*;
import security.AuthorizationUtils;

public class ClientDashboardFrame extends JFrame {
    public ClientDashboardFrame() {
        AuthorizationUtils.requireAuthenticated(AuthorizationUtils.currentUser());
        setTitle("Espace Client");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2, 1, 10, 10));

        JButton btnLocations = new JButton("Mes réservations");
        JButton btnFactures = new JButton("Mes factures");

        add(btnLocations);
        add(btnFactures);

        btnLocations.addActionListener(e -> new LocationManagementFrame());
        btnFactures.addActionListener(e -> new FactureManagementFrame());

        setVisible(true);
    }
}