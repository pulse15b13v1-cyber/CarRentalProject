package Main;

import view.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

public class Main {
	public static void main(String[] args) {
	try {
		UIManager.setLookAndFeel( new FlatMacDarkLaf() );
	}catch (Exception ex) {
        System.err.println("Failed to initialize LaF: " + ex.getMessage());
    }
	SwingUtilities.invokeLater(() ->  new LoginFrame());
	}
}
