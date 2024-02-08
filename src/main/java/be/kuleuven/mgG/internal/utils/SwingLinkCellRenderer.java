package be.kuleuven.mgG.internal.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class SwingLinkCellRenderer extends DefaultTableCellRenderer {

//    @Override
//    public Component getTableCellRendererComponent(JTable table, Object value,
//                                                   boolean isSelected, boolean hasFocus,
//                                                   int row, int column) {
//    	
//    	
//         
//        if (value instanceof SwingLink) {
//            return (SwingLink) value;
//        } else {
//            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//        
//        }
//    }
//}
	
	
@Override
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    if (value instanceof SwingLink) {
        SwingLink link = (SwingLink) value;
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, link.getText(), isSelected, hasFocus, row, column);
        label.setText("<html><div style='padding:22px;'>" + link.getText() + "</div></html>"); 
        return label;
    }
    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
}
}
	