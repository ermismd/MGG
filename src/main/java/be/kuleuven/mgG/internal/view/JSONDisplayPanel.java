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
 
    
    
    public JSONDisplayPanel(final MGGManager manager,JSONArray jsonArray) {
        super(new BorderLayout());
        createTable(jsonArray);
        JScrollPane scrollPane = new JScrollPane(table);
      
        this.manager = manager;
	
        // Set the scroll bar policies
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Set the preferred size of the scroll pane
        scrollPane.setPreferredSize(new Dimension(800, 600));
        
        // Add the scroll pane to the center of the JSONDisplayPanel
        add(scrollPane, BorderLayout.CENTER);
        
        
        // Add the button that will execute the SendDataToServerTask when clicked
        JButton sendButton = new JButton("Get Network from Server");
        sendButton.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {
              
            	 TaskIterator taskIterator = new SendDataToServerTaskFactory(jsonArray, manager).createTaskIterator();
                 manager.executeTasks(taskIterator);
            }

        
    });
     // Set button appearance
        sendButton.setForeground(Color.BLACK); // Set the text color of the button
        sendButton.setFont(sendButton.getFont().deriveFont(Font.BOLD, 16f)); // Set the font style and size of the button text
        sendButton.setBackground(new Color(0, 176, 80)); // Set the background color of the button
        sendButton.setFocusPainted(false); // Remove the focus border around the button

        // Add the button to the JSONDisplayPanel
        add(sendButton, BorderLayout.NORTH);
    
    }
    
    private void createTable(JSONArray jsonArray) {
        DefaultTableModel tableModel = new DefaultTableModel();
        table = new JTable(tableModel);

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
    }


	
}


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


