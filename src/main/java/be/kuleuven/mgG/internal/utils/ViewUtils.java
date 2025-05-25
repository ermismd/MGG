package be.kuleuven.mgG.internal.utils;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JTextArea;

import javax.swing.*;
import javax.swing.text.*;




public class ViewUtils {	
	
	 public static void setJTextAreaAttributes(JTextArea textArea) {
	       // textArea.setWrapStyleWord(true);
	        textArea.setLineWrap(true);
	        textArea.setEditable(false);
	        textArea.setFont(new Font("Arial", Font.PLAIN, 11));
	        textArea.setOpaque(false);
	        textArea.setBorder(null);
	        textArea.setPreferredSize(new Dimension(400, 15));
	    }
	 
	 
		// pattern based
		public static JTextPane createStyledLabelEdges(String labelText) {
		    JTextPane textPane = new JTextPane();
		    textPane.setEditable(false);
		    textPane.setOpaque(false);
		    textPane.setPreferredSize(new Dimension(400, 15));
		
		    StyledDocument doc = textPane.getStyledDocument();
		
		    // Bold style
		    Style boldStyle = textPane.addStyle("Bold", null);
		    StyleConstants.setBold(boldStyle, true);
		    StyleConstants.setFontFamily(boldStyle, "Arial");
		    StyleConstants.setFontSize(boldStyle, 11);
		
		    // Plain style
		    Style plainStyle = textPane.addStyle("Plain", null);
		    StyleConstants.setBold(plainStyle, false);
		    StyleConstants.setFontFamily(plainStyle, "Arial");
		    StyleConstants.setFontSize(plainStyle, 11);
		
		    int colonIndex = labelText.indexOf(":");
		    try {
		        if (colonIndex != -1) {
		            doc.insertString(doc.getLength(), labelText.substring(0, colonIndex + 1), boldStyle);
		            doc.insertString(doc.getLength(), " " + labelText.substring(colonIndex + 1), plainStyle);
		        } else {
		            doc.insertString(doc.getLength(), labelText, plainStyle);
		        }
		    } catch (BadLocationException e) {
		        e.printStackTrace();
		    }
		
		    return textPane;
		}

}
