package be.kuleuven.mgG.internal.utils;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class AboutPanel extends JPanel {
	
	  public AboutPanel() {
	        setLayout(new BorderLayout());

	        // Main content
	        JPanel contentPanel = new JPanel();
	        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
	        contentPanel.add(new JLabel("App Version: 1.0.0"));
	        // 
	        add(contentPanel, BorderLayout.CENTER);

	        // Link label
	        JLabel linkLabel = new JLabel("<html><a href=''> Documentation</a></html>");
	        linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	        linkLabel.addMouseListener(new MouseAdapter() {
	            public void mouseClicked(MouseEvent e) {
	                openWebpage("https://hariszaf.github.io/microbetag/docs/cytoApp/");
	            }
	        });
	        add(linkLabel, BorderLayout.SOUTH);

	       
	        setBorder(BorderFactory.createEtchedBorder());
	    }

	    private void openWebpage(String url) {
	        try {
	            Desktop.getDesktop().browse(new URI(url));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}


