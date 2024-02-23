package be.kuleuven.mgG.internal.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.cytoscape.util.swing.IconManager;

import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.utils.EnrichmentResult;
import be.kuleuven.mgG.internal.utils.EnrichmentsTableModel;


public class EnrichmentResultPanel extends JPanel {
    private JTable table;
    private List<EnrichmentResult> results; // Assume this is filled with your analysis results
    final MGGManager manager;
    private JButton exportButton;
    final Font iconFont;
    final String butExportTableDescr = "Export enrichment table";

    public EnrichmentResultPanel(final MGGManager manager) {
  
    	
    	IconManager iconManager = manager.getService(IconManager.class);
    	iconFont = iconManager.getIconFont(22.0f);
    	this.manager = manager;
        List<EnrichmentResult> results = new ArrayList<>(); // Populate this list with your results
        table = new JTable(new EnrichmentsTableModel(results));
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableColumnModel tcm = table.getColumnModel();
        tcm.getColumn(3).setCellRenderer(new DecimalFormatRenderer());
        tcm.getColumn(4).setCellRenderer(new DecimalFormatRenderer());
        // Add the table to the panel
        add(scrollPane);
       
        
        setLayout(new BorderLayout()); //BorderLayout for the panel
        add(scrollPane, BorderLayout.CENTER); 
       
        
        JPanel exportPanel = new JPanel();
        JButton exportButton =  new JButton(IconManager.ICON_SAVE);
        exportButton.setToolTipText(butExportTableDescr);
        exportButton.addActionListener(e -> exportTable());
         exportButton.setFont(iconFont);
         exportButton.setBorderPainted(false);
         exportButton.setContentAreaFilled(false);
         exportButton.setFocusPainted(false);
         exportButton.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 10));
        exportPanel.add(exportButton);

        // exportPanel to the NORTH 
        add(exportPanel, BorderLayout.NORTH);
    }

    
    
   
//    public void updateResults(List<EnrichmentResult> newResults) {
//        EnrichmentsTableModel model = (EnrichmentsTableModel) this.table.getModel();
//       
//        model.setResults(newResults);
//        model.fireTableDataChanged(); // Notify the table model to refresh the table data
//    }
    
    public void updateResults(List<EnrichmentResult> newResults) {
        EnrichmentsTableModel model = (EnrichmentsTableModel) table.getModel();
        model.setResults(newResults);
    }
    
    

//    static class DecimalFormatRenderer extends DefaultTableCellRenderer {
//        private static final DecimalFormat formatter = new DecimalFormat("0.#####E0");
//
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object value,
//                boolean isSelected, boolean hasFocus, int row, int column) {
//            try {
//                if (value != null && (double) value < 0.001) {
//                    value = formatter.format((Number) value);
//                }
//            } catch (Exception ex) {
//                // ignore and return original value
//            }
//            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//        }
//	}
//    
    
    
    static class DecimalFormatRenderer extends DefaultTableCellRenderer {
        private static final DecimalFormat formatter = new DecimalFormat("0.#####E0");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            // Check if the value is a number and less than 0.001 (abs value for negative numbers)
            if (value instanceof Number && Math.abs(((Number) value).doubleValue()) < 0.001) {
                // Treat negative values as zero or a very small positive number
                double adjustedValue = Math.max(((Number) value).doubleValue(), 1E-15);
                // Format the adjusted value
                value = formatter.format(adjustedValue);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
    
    private void exportTable() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                fileToSave = new File(fileToSave + ".csv");
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileToSave))) {
                for (int i = 0; i < table.getColumnCount(); i++) {
                    bw.write(table.getColumnName(i));
                    if (i < table.getColumnCount() - 1) {
                        bw.write(";"); 
                    }
                }
                bw.newLine();
                for (int i = 0; i < table.getRowCount(); i++) {
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        String value = table.getValueAt(i, j).toString();
                        value = value.contains(";") ? "\"" + value + "\"" : value; 
                        bw.write(value);
                        if (j < table.getColumnCount() - 1) {
                            bw.write(";"); 
                        }
                    }
                    bw.newLine();
                }
                JOptionPane.showMessageDialog(this, "Data exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error occurred while saving the file.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    }
    
