package be.kuleuven.mgG.internal.tasks;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import be.kuleuven.mgG.internal.utils.AboutPanel;

public class AboutTask extends AbstractTask {
		
	
	 
	    @Override
	    public void run(TaskMonitor taskMonitor) throws Exception {
	        SwingUtilities.invokeLater(this::showAboutPanel);
	    }
	
	    private void showAboutPanel() {
	        // Create a new dialog to display the AboutPanel
	        JDialog aboutDialog = new JDialog();
	        aboutDialog.setTitle("About MGG");
	        
	     
	     // make dialog  modal
	        aboutDialog.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
	        
	        //  dialog always on top
	        //aboutDialog.setAlwaysOnTop(true);

	        // Add AboutPanel 
	        AboutPanel aboutPanel = new AboutPanel();
	        aboutDialog.add(aboutPanel);

	        //  dialog props
	        aboutDialog.pack();
	        aboutDialog.setSize(200, 200); // Set the desired width and height
	        aboutDialog.setLocationRelativeTo(null); // Center on screen
	        aboutDialog.setVisible(true);
	        
	    }
	}
	


