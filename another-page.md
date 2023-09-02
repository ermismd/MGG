## INFO WEEK1 AND WEEK2



#### Created 2 classes that allows users to import a CSV/TSV file ,process it, display the JSON data in a panel and send the JSON data to a server.

 * * *

 
 
<html>
<head>
  <style>
   .panel {
      display: none;
      background-color: #f1f1f1;
      padding: 10px;
      margin-top: 10px;
      font-size: 10px; /* Increase the font size as needed */
      width: 800px; /* Increase the width as needed */
    }

    h2 {
      font-size: 14px; /* Decrease the font size of the headers */
	margin-top: 20px; /* Increase the margin-top for the header */
	  margin-bottom: 20px; /* Add margin-bottom for spacing */
    }

    .panel-button {
      margin-bottom: 20px; /* Add space between each panel button */
    }
  </style>
</head>
<body>
  <h2>Class 1</h2>
  <button onclick="ImportFileTask ()">MicroBetaGUIImportAction</button>
  <div class="panel" id="ImportFileTask ">
    <pre>
 
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
import org.cytoscape.work.util.ListSingleSelection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.utils.CSVReader;
import be.kuleuven.mgG.internal.utils.HTTPUtils;
import be.kuleuven.mgG.internal.view.JSONDisplayPanel;
import be.kuleuven.mgG.internal.view.JsonResultPanel;




/**
 * This class represents a task for importing a CSV file and processing it into a JSON array.
 * 
 * The CSV file is read and parsed into a list of string arrays, where each array represents a row in the CSV file.
 * The task then creates a JSON array where each JSON object corresponds to a row in the CSV file.
 * The JSON array is then set in the MGGManager
 *
 * The task also provides options to display the JSON data in a panel and to write the JSON data to a file.
 * 
 */





public class ImportFileTask extends AbstractTask {
   
	final CySwingApplication swingApplication;
    final CyApplicationManager cyApplicationManager;
    
    private final MGGManager mggManager;
    
    private String filePath;
    
    private JSONObject jsonObject;
    
    
    /*
	 * @Tunable(description="Take back the network from Microbetag",
	 * longDescription="Send the JSON array that was created by the imported CSV to the microbetag server to get back the network."
	 * , tooltip="If checked, the JSON will be sent to the server", gravity=3.0)
	 * public boolean sendToServer = true;
	 */
    
    @Tunable(description = "Display Data", groups = { "Display Settings" }, tooltip="If checked, the Data will be displayed in a panel")
    public boolean showJSONInPanel = true;
    
    @Tunable(description="Write JSON to file",groups = { "Create File Settings" },tooltip="If checked, a new JSON file will be created in the same path as the original file",exampleStringValue="true")
   public boolean writeToFile = true;  
    
    @Tunable(description="Choose input type", groups={"Input Settings"}, gravity=1.0, required=true)
    public ListSingleSelection<String> input = new ListSingleSelection<>("abundance_table", "network");

    @Tunable(description="Choose taxonomy Database", groups={"Input Settings"}, gravity=2.0, required=true)
    public ListSingleSelection<String> taxonomy = new ListSingleSelection<>("gtdb", "dada2", "qiime2");
    
    @Tunable(description="PhenDB", longDescription="Choose whether to use PhenDB.", groups={"Input Settings"}, gravity=3.0, exampleStringValue="True, False", required=true)
    public boolean phenDB;

    @Tunable(description="FAPROTAX", longDescription="Choose whether to use FAPROTAX.", groups={"Input Settings"}, gravity=4.0, exampleStringValue="True, False", required=true)
    public boolean faproTax;

    @Tunable(description="NetCooperate", longDescription="Choose whether to use NetCooperate.", groups={"Input Settings"}, gravity=5.0, exampleStringValue="True, False", required=true)
    public boolean netCooperate;

    @Tunable(description="NetCmpt", longDescription="Choose whether to use NetCmpt.", groups={"Input Settings"}, gravity=6.0, exampleStringValue="True, False", required=true)
    public boolean netCmpt;

    @Tunable(description="Pathway Complementarity", longDescription="Choose whether to use pathway complementarity.", groups={"Input Settings"}, gravity=7.0, exampleStringValue="True, False", required=true)
    public boolean pathwayComplementarity;
    
    
    
    
    
    /**
     * Constructor for the ImportFileTask class.
     * 
     * @param cytoscapeDesktopService The CySwingApplication service, which provides access to Cytoscape desktop components.
     * @param cyApplicationManager2 The CyApplicationManager service, which provides access to the current network and view.
     * @param filePath The path of the CSV file to import.
     * 
     */
    
    public ImportFileTask(String filePath,MGGManager mggManager) {
    	
    	this.swingApplication = mggManager.getService(CySwingApplication.class);
        this.cyApplicationManager = mggManager.getService(CyApplicationManager.class);
        this.filePath = filePath;
        this.mggManager = mggManager;
               
    }
    
  
    @Override
    public void run(TaskMonitor taskMonitor) {
    	taskMonitor.setTitle("Importing File");
        taskMonitor.setStatusMessage("Reading file");

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
		        
            taskMonitor.setStatusMessage("Processing data");


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
	         
	     // Create a new JSONObject
	        JSONObject jsonObject = new JSONObject();

	        // Add the jsonArray to the jsonObject
	        jsonObject.put("data", jsonArray);

	        // Create a new JSONArray for the input parameters
	        JSONArray inputParameters = new JSONArray();
	        inputParameters.add(input.getSelectedValue());
	        inputParameters.add(taxonomy.getSelectedValue());
	        inputParameters.add(phenDB);
	        inputParameters.add(faproTax);
	        inputParameters.add(netCooperate);
	        inputParameters.add(netCmpt);
	        inputParameters.add(pathwayComplementarity);

	        // Add the input parameters to the jsonObject
	        jsonObject.put("inputParameters", inputParameters);
	        
	  
	        
	        // Set the JSON array in the MGGManager
            mggManager.setJsonObject(jsonObject);
            
          
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
            if (showJSONInPanel) {
                SwingUtilities.invokeLater(() -> showDataInPanel(jsonObject));
            }
			 
	        
            taskMonitor.setProgress(1.0);
            taskMonitor.setStatusMessage("Finished processing  file.");
            
                                   
            
            
        } catch (IOException e) {
            taskMonitor.showMessage(TaskMonitor.Level.ERROR, " Error while processing the file: " + e.getMessage());
            e.printStackTrace();
               
        }}
    
    
   
        
        private void showDataInPanel(JSONObject jsonObject) {
		    //JSONDisplayPanel panel = new JSONDisplayPanel(mggManager, jsonObject);
		    JsonResultPanel panel = new JsonResultPanel(mggManager, jsonObject);
		    mggManager.registerService(panel, CytoPanelComponent.class, new Properties());
		    
			/*
			 * JFrame frame = new JFrame("OTU/ASV Data");
			 * frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			 * frame.getContentPane().add(panel); frame.pack(); frame.setVisible(true);
			 */
    }
                
}
    </pre>
  </div>






  <h2>Class 2</h2>
  <button onclick="ImportFileTaskFactory()">JSONDisplayPanel</button>
  <div class="panel" id="ImportFileTaskFactory">
    <pre>
 
package be.kuleuven.mgG.internal.tasks;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import be.kuleuven.mgG.internal.model.MGGManager;

public class ImportFileTaskFactory implements TaskFactory {
    
    private final MGGManager mggManager;
    
    
    public ImportFileTaskFactory(MGGManager mggManager) {
      
        this.mggManager=mggManager;
    }

    
	@Override
    public TaskIterator createTaskIterator() {
		  // Use a JFileChooser to get the file path
	    JFileChooser fileChooser = new JFileChooser();
	    int option = fileChooser.showOpenDialog(null);
	    if (option == JFileChooser.APPROVE_OPTION) {
	        File selectedFile = fileChooser.getSelectedFile();
	        String filePath = selectedFile.getAbsolutePath();

	        return new TaskIterator(new ImportFileTask(filePath, mggManager));
	    } else if (option == JFileChooser.CANCEL_OPTION) {
	        // User cancelled the file selection, return an empty TaskIterator
	        return new TaskIterator();
	    } else {
	        // An error occurred or no file was selected
	        String errorMessage = "Error selecting file";
	        // You can display an error message or handle the error in any other way appropriate for your application
	        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
	        // Return an empty TaskIterator or any other appropriate error handling
	        return new TaskIterator();
	    }
	}
	

    @Override
    public boolean isReady() {  
        return true;
    }
}
}
      
    </pre>
  </div>

  <script>
    function showMicroBetaGUIImportAction() {
      var panel = document.getElementById("ImportFileTask");
      if (panel.style.display === "none") {
        panel.style.display = "block";
      } else {
        panel.style.display = "none";
      }
    }
    
    function showJSONDisplayPanel() {
      var panel = document.getElementById("ImportFileTaskFactory");
      if (panel.style.display === "none") {
        panel.style.display = "block";
      } else {
        panel.style.display = "none";
      }
    }
  </script>
</body>
</html>

	
	
<br> <!-- Add an empty line -->



[back](./)
