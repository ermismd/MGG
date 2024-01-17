package be.kuleuven.mgG.internal.view;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.tasks.SendDataToServerTaskFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JSONDisplayPanel extends JPanel  {
    private JTable table;
    final MGGManager manager;
 
    
    
    public JSONDisplayPanel(final MGGManager manager,JSONObject jsonObject) {
        super(new BorderLayout());
        this.manager = manager;
    	
        try {
            if (jsonObject.containsKey("data")) {
                JSONArray dataJsonArray = (JSONArray) jsonObject.get("data");
                createTable(dataJsonArray, "Data Table");
            }

            if (jsonObject.containsKey("network")) {
                JSONArray networkJsonArray = (JSONArray) jsonObject.get("network");
                createTable(networkJsonArray, "Network Table");
            }

            if (jsonObject.containsKey("metadata")) {
                JSONArray metadataJsonArray = (JSONArray) jsonObject.get("metadata");
                createTable(metadataJsonArray, "Metadata Table");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error processing JSON data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }   
        
//        // Check if the jsonObject contains an array named "data"
//        if (jsonObject.containsKey("data")) {
//            JSONArray dataJsonArray = (JSONArray) jsonObject.get("data");
//            createTable(dataJsonArray, "Data Table");
//
//            //add the button for annotated network
//           // JButton sendButton = createSendButton(jsonObject);
//           // add(sendButton, BorderLayout.NORTH);
//        } else if (jsonObject.containsKey("network")) {
//            // If there's no "data" but "metadata" exists, display metadata table without button
//            JSONArray networkJsonArray = (JSONArray) jsonObject.get("network");
//            createTable(networkJsonArray , "Network Table");}
//        
//        else if (jsonObject.containsKey("metadata")) {
//            // If there's no "data" but "metadata" exists, display metadata table without button
//            JSONArray metadataJsonArray = (JSONArray) jsonObject.get("metadata");
//            createTable(metadataJsonArray, "Metadata Table");
//        }}

    //private JButton createSendButton(JSONObject jsonObject) {
      //  JButton sendButton = new JButton("Get Annotated Network");
      //  sendButton.addActionListener(new ActionListener() {
       //     public void actionPerformed(ActionEvent e) {
           //     TaskIterator taskIterator = new SendDataToServerTaskFactory( manager).createTaskIterator();
           //     manager.executeTasks(taskIterator);
         //   }
       // });

        
       // configureButtonAppearance(sendButton);

      //  return sendButton;
    //}

    private void configureButtonAppearance(JButton button) {
        button.setForeground(Color.BLACK);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setBackground(new Color(144, 238, 144));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // border for button
        int borderRadius = 20;
        int borderThickness = 2;
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, borderThickness),
                BorderFactory.createEmptyBorder(borderRadius, borderRadius, borderRadius, borderRadius)));

        // hover effect for the button
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.GREEN);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(144, 238, 144));
            }
        });
    }

    private void createTable(JSONArray jsonArray, String tableName) {
    	 if (jsonArray != null && !jsonArray.isEmpty()) {
    	        DefaultTableModel tableModel = new DefaultTableModel();
    	        JTable localTable = new JTable(tableModel); 

    	        // Set the column names
    	        JSONArray headers = (JSONArray) jsonArray.get(0);
    	        for (Object header : headers) {
    	            tableModel.addColumn(header.toString());
    	        }

    	        // Add the data to the table model
    	        int numberOfRows = Math.min(jsonArray.size(), 21); //Limit to 20 rows plus header
    	        for (int i = 1; i < numberOfRows; i++) {
    	            JSONArray row = (JSONArray) jsonArray.get(i);
    	            Object[] rowData = new Object[row.size()];
    	            for (int j = 0; j < row.size(); j++) {
    	                rowData[j] = row.get(j);
    	            }
    	            tableModel.addRow(rowData);
    	        }

    	        JScrollPane scrollPane = new JScrollPane(localTable);
    	        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    	        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    	        scrollPane.setPreferredSize(new Dimension(800, 400));
    	        scrollPane.setBorder(BorderFactory.createTitledBorder(tableName));

    	      
    	        add(scrollPane, BorderLayout.CENTER);
    	    }
    }
}




