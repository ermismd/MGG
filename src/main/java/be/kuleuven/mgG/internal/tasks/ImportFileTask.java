package be.kuleuven.mgG.internal.tasks;

import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.IN_TOOL_BAR;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.awt.BorderLayout;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.utils.CSVReader;
import be.kuleuven.mgG.internal.utils.HTTPUtils;
import be.kuleuven.mgG.internal.view.JSONDisplayPanel;

public class ImportFileTask extends AbstractTask {
   
	private final CySwingApplication swingApplication;
    private final CyApplicationManager cyApplicationManager;
    private final MGGManager mggManager;
    
    private String filePath;
    
    private JSONArray jsonArray;
    
    

	/*
	 * @Tunable(description="Show JSON in panel",
	 * longDescription="Choose if you want to visualize the JSON array.",
	 * tooltip="If checked, the JSON will be displayed in a panel", gravity=1.0)
	 * public boolean showJSONInPanel = true;
	 * 
	 * @Tunable(description="Take back the network from Microbetag",
	 * longDescription="Send the JSON array that was created by the imported CSV to the microbetag server to get back the network."
	 * , tooltip="If checked, the JSON will be sent to the server", gravity=3.0)
	 * public boolean sendToServer = true;
	 */

    @Tunable(description="Write JSON to file", 
            longDescription="Choose whether to write the JSON data to a file in the sanme path as the original file.",
            tooltip="If checked, a new JSON file will be created in the same path as the original file",
            exampleStringValue="true")
   public boolean writeToFile = true;  
    
    
    
    
    
    public ImportFileTask(CySwingApplication cytoscapeDesktopService, CyApplicationManager cyApplicationManager2, String filePath,MGGManager mggManager) {
        this.swingApplication = cytoscapeDesktopService;
        this.cyApplicationManager = cyApplicationManager2;
        this.filePath = filePath;
        this.mggManager = mggManager;
               
    }
    
  
    @Override
    public void run(TaskMonitor taskMonitor) {
    	taskMonitor.setTitle("Import CSV File");
        taskMonitor.setStatusMessage("Reading CSV file");

        try {
        	
            // Call CSVReader from Utils to parse the TSV/CSV file with tab delimiter
            List<String[]> csvData = CSVReader.readCSV(taskMonitor, filePath);
            
            // Find the headers(the first row that has more than 1 columns)
            String[] headers = null;
            for (int i = 0; i < csvData.size(); i++) {
                String[] row = csvData.get(i);
                if (row.length > 1) {
                    headers = row;
                    csvData.remove(i);  // remove the header row
                    break;
		            }
		        }
		        
            taskMonitor.setStatusMessage("Processing CSV data");


            // Create JSONArray to hold the JSONObjects
            
	        JSONArray jsonArray = new JSONArray();
	        
	        
	        JSONArray header = new JSONArray();
	        
	        for (String hdr:headers) {
	        	header.add(hdr);
	        }
	        
	        jsonArray.add(header);
	        
	        
	        // Iterate each row of CSV 
	        for (String[] values : csvData) {
	            // Skip rows with only one column
	            if (values.length <= 1) {
	                continue;
	            }
	            
	            JSONArray row=new JSONArray();
	            	
	            for (String value:values) {
	            	row.add(value);
	            }

	            jsonArray.add(row);
	            
	        }
	         
	     
	        // Set the JSON array in the MGGManager
            mggManager.setJsonArray(jsonArray);
            
          
            taskMonitor.setStatusMessage("Displaying data in panel");

          
            if (writeToFile) {
            	try {
            	String jsonFilePath = filePath + ".json";
                FileWriter writer = new FileWriter(jsonFilePath);
                writer.write(jsonArray.toJSONString());
                writer.close();
            } catch (IOException e) {
                taskMonitor.showMessage(TaskMonitor.Level.ERROR, "Error while writing the file: " + e.getMessage());
                e.printStackTrace();
            }
            }
            
            
			  // Show the JSON data in a panel if showJSONInPanel 
            	SwingUtilities.invokeLater(() ->showDataInPanel(jsonArray)); 
               
			 
	        
            taskMonitor.setProgress(1.0);
            taskMonitor.setStatusMessage("Finished processing CSV file.");
            
                                   
            
            
        } catch (IOException e) {
            taskMonitor.showMessage(TaskMonitor.Level.ERROR, " Error while processing the file: " + e.getMessage());
            e.printStackTrace();
               
        }}
    
    
   
        
        private void showDataInPanel(JSONArray jsonArray) {
		    JSONDisplayPanel panel = new JSONDisplayPanel(mggManager, jsonArray);
		  
		    
		    JFrame frame = new JFrame("OTU/ASV Data");
		    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    frame.getContentPane().add(panel);
		    frame.pack();
		    frame.setVisible(true);
    }
        

	
        
}