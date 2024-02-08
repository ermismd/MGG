package be.kuleuven.mgG.internal.utils;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class BlueUnderlineHTMLRenderer extends DefaultTableCellRenderer {
	    @Override
	    public Component getTableCellRendererComponent(JTable table, Object value,
	                                                   boolean isSelected, boolean hasFocus,
	                                                   int row, int column) {
	        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        if (value instanceof String) {
	            String htmlValue = "<html><font color='blue'><u>" + value.toString() + "</u></font></html>";
	            setText(htmlValue);
	        }
	        return this;
	    }
	}

