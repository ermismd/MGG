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
    	
             
        //JScrollPane scrollPane = new JScrollPane(table);
      
       
//        // Set the scroll bar policies
//        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        
//        // Set the preferred size of the scroll pane
//        scrollPane.setPreferredSize(new Dimension(800, 600));
//        
//        
//       
//        
//        // Add the scroll pane to the center of the JSONDisplayPanel
//        add(scrollPane, BorderLayout.CENTER);
        
        
        // Check if the jsonObject contains an array named "data"
        if (jsonObject.containsKey("data")) {
            JSONArray dataJsonArray = (JSONArray) jsonObject.get("data");
            createTable(dataJsonArray, "Data Table");

            // Since "data" array exists, add the button for annotated network
            JButton sendButton = createSendButton(jsonObject);
            add(sendButton, BorderLayout.NORTH);
        } 
        else if (jsonObject.containsKey("metadata")) {
            // If there's no "data" but "metadata" exists, display metadata table without button
            JSONArray metadataJsonArray = (JSONArray) jsonObject.get("metadata");
            createTable(metadataJsonArray, "Metadata Table");
        }}

    private JButton createSendButton(JSONObject jsonObject) {
        JButton sendButton = new JButton("Get Annotated Network");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TaskIterator taskIterator = new SendDataToServerTaskFactory( manager).createTaskIterator();
                manager.executeTasks(taskIterator);
            }
        });

        // Configure the appearance of the button
        configureButtonAppearance(sendButton);

        return sendButton;
    }

    private void configureButtonAppearance(JButton button) {
        button.setForeground(Color.BLACK);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setBackground(new Color(144, 238, 144));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Create a rounded border for the button
        int borderRadius = 20;
        int borderThickness = 2;
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, borderThickness),
                BorderFactory.createEmptyBorder(borderRadius, borderRadius, borderRadius, borderRadius)));

        // Add hover effect for the button
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
            JTable localTable = new JTable(tableModel); // Local table variable

            // Set the column names
            JSONArray headers = (JSONArray) jsonArray.get(0);
            for (Object header : headers) {
                tableModel.addColumn(header.toString());
            }

            // Add the data to the table model
            for (int i = 1; i < jsonArray.size(); i++) {
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
            scrollPane.setPreferredSize(new Dimension(800, 600));
            scrollPane.setBorder(BorderFactory.createTitledBorder(tableName));

            // Add the scroll pane to the panel
            add(scrollPane, BorderLayout.CENTER);
    }
}}


/*	@Override
public Component getComponent() {
	// TODO Auto-generated method stub
	return this;
}

@Override
public CytoPanelName getCytoPanelName() {
	// TODO Auto-generated method stub
	return  CytoPanelName.SOUTH;
}

@Override
public String getTitle() {
	// TODO Auto-generated method stub
	return "OTU/ASV Data";
}

@Override
public Icon getIcon() {
	// TODO Auto-generated method stub
	return null;*/


