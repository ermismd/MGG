package be.kuleuven.mgG.internal.utils;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JTextArea;

public class ViewUtils {
	
	
	
	
	 public static void setJTextAreaAttributes(JTextArea textArea) {
	       // textArea.setWrapStyleWord(true);
	        textArea.setLineWrap(true);
	        textArea.setEditable(false);
	        textArea.setFont(new Font("Arial", Font.PLAIN, 10));
	        textArea.setOpaque(false);
	        textArea.setBorder(null);
	        textArea.setPreferredSize(new Dimension(400, 15));
	    }
	 
	 
	 public static void setJTextAreaAttributesEdges(JTextArea textArea) {
	        textArea.setWrapStyleWord(true);
	        textArea.setLineWrap(true);
	        textArea.setEditable(false);
	        textArea.setFont(new Font("Arial", Font.PLAIN, 10));
	        textArea.setOpaque(false);
	        textArea.setBorder(null);
	        textArea.setPreferredSize(new Dimension(400, 15));
	    }
	 
	 
	 

}
