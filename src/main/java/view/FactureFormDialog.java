package view;

import Controller.FactureController;
import Controller.LocationController;
import Model.Location;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FactureFormDialog extends JDialog {
    public FactureFormDialog(JFrame parent, Runnable onSuccess) {
        super(parent, "Nouvelle facture", true);
        setSize(450, 300);
        setLocationRelativeTo(parent);

        List<Location> locations = LocationController.getAllLocations();
        JComboBox<String> locBox = new JComboBox<>();
        for (Location l : locations) locBox.addItem(l.getId() + " - " + l.getNomClient() + " / " + l.getModeleVoiture());

        JTextField montantField = new JTextField();
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        JPanel panel = new JPanel(new GridLayout(4,2,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        panel.add(new JLabel("Contrat:")); panel.add(locBox);
        panel.add(new JLabel("Montant:")); panel.add(montantField);
        panel.add(new JLabel("Date (yyyy-MM-dd):")); panel.add(dateField);
        JButton saveBtn = new JButton("Enregistrer"); panel.add(saveBtn);
        add(panel);

        saveBtn.addActionListener(e -> {
            try {
                int idLoc = Integer.parseInt(locBox.getSelectedItem().toString().split(" - ")[0]);
                double montant = Double.parseDouble(montantField.getText());
                Date d = new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText());
                // Retrieve client id via locations list
                int idClient = locations.stream().filter(l->l.getId()==idLoc).findFirst().get().getIdClient();
                boolean ok = FactureController.addFacture(montant, d, idClient, idLoc);
                if(ok){ JOptionPane.showMessageDialog(this,"Facture ajoutée."); onSuccess.run(); dispose(); }
                else JOptionPane.showMessageDialog(this,"Erreur ajout","Erreur",JOptionPane.ERROR_MESSAGE);
            } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Entrées invalides","Erreur",JOptionPane.ERROR_MESSAGE); }
        });
        setVisible(true);
    }
}
