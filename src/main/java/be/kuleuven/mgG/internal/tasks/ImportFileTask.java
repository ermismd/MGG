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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    
   
 
    @Tunable(description = "Display Data", groups = { "Display Settings" }, tooltip="If checked, the Data will be displayed in a panel")
    	public boolean showJSONInPanel = true;
//    
    @Tunable(description="Write JSON to file",groups = { "Create File Settings" },tooltip="If checked, a new JSON file will be created in the same path as the original file",exampleStringValue="true")
   public boolean writeToFile = false;  
//    
//    @Tunable(description="Choose input type", groups={"Input Settings"}, gravity=1.0, required=true)
//    public ListSingleSelection<String> input = new ListSingleSelection<>("abundance_table", "network");
//    
//    @Tunable(description="Choose if heterogeneous", groups={"Additional Input if chosen abudance_table"}, gravity=10.0, required=true)
//    public boolean heterogeneous=false;
//    
//    @Tunable(description="Choose if sensitive", groups={"Additional Input if chosen abudance_table"}, gravity=11.0, required=true)
//    public boolean sensitive=false;
// 
//    @Tunable(description="Choose delimiter", groups={"Input Settings"}, gravity=2.0, required=true)
//    public ListSingleSelection<String> delimiter = new ListSingleSelection<>(";", "|","__","_");
//
//    @Tunable(description="Choose taxonomy Database", groups={"Input Settings"}, gravity=3.0, required=true)
//    public ListSingleSelection<String> taxonomy = new ListSingleSelection<>("gtdb", "dada2", "other");
//    
//    @Tunable(description="PhenDB", longDescription="Choose whether to get PhenDB.", groups={"Input Settings"}, 
//    		tooltip="Choose whether to get PhenDB values annotations" ,gravity=4.0, exampleStringValue="True, False", required=true)
//    public boolean phenDB=true;
//
//    @Tunable(description="FAPROTAX", longDescription="Choose whether to get FAPROTAX.", groups={"Input Settings"}, 
//    		tooltip="Choose whether to get FAPROTAX values annotations" , gravity=5.0, exampleStringValue="True, False", required=true)
//    public boolean faproTax=true; 
//
//    @Tunable(description="Pathway Complementarity", longDescription="Choose whether to get the pathway complementarity.", 
//    		 tooltip="Choose whether to get Pathway Complementarity annotations" ,groups={"Input Settings"}, gravity=6.0, exampleStringValue="True, False", required=true)
//    public boolean pathway_complement=true;
//    
//    @Tunable(description="Seed Scores", longDescription="Choose whether to get the Seed Scores.", groups={"Input Settings"}, gravity=7.0, exampleStringValue="True, False", required=true)
//    public boolean seed_scores= false;
//    
//    @Tunable(description="Get_Children", longDescription="Choose whether to get Children(different strains).", groups={"Input Settings"}, 
//    		tooltip="Choose whether to get strains from the same species" , gravity=8.0, exampleStringValue="True, False", required=true)
//    public boolean get_children=false; 
//    
//    @Tunable(description="Manta", longDescription="Choose whether to get Manta annotations.", groups={"Input Settings"}, 
//    		tooltip="Choose whether to get Manta annotations" , gravity=9.0, exampleStringValue="True, False", required=true)
//    public boolean manta=false; 
    
    //@Tunable(description="NetCmpt", longDescription="Choose whether to use NetCmpt.", groups={"Input Settings"}, gravity=6.0, exampleStringValue="True, False", required=true)
    //public boolean netCmpt= true;
    
    
    
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
	        
	        
	        
	        
//     // Mapping the arguments from the list to their respective values
//	        Map<String, Object> argumentsMap = new HashMap<>();
//	        argumentsMap.put("input_category", input.getSelectedValue());
//            argumentsMap.put("taxonomy", taxonomy.getSelectedValue());
//            argumentsMap.put("delimiter", delimiter.getSelectedValue());
//	        argumentsMap.put("get_children", get_children); // Assuming get_children is a predefined variable
//	        argumentsMap.put("sensitive", sensitive);       // Assuming sensitive is a predefined variable
//	        argumentsMap.put("heterogeneous", heterogeneous); // Assuming heterogeneous is a predefined variable
//	        argumentsMap.put("phenDB", phenDB);             // Assuming phenDB is a predefined variable
//	        argumentsMap.put("faprotax", faproTax);         // Assuming faproTax is a predefined variable
//	        argumentsMap.put("pathway_complement", pathway_complement); // Assuming pathway_complement is a predefined variable
//	        argumentsMap.put("seed_scores", seed_scores);   // Assuming seed_scores is a predefined variable
//	        argumentsMap.put("manta", manta);               // Assuming manta is a predefined variable
//
//	        // Add the argumentsMap directly to the jsonObject as a JSONObject
//	        jsonObject.put("inputParameters", new JSONObject(argumentsMap));
	        
	        
	        
	     
	    
	  
	       
	        
	        
	        
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
            taskMonitor.setStatusMessage("Abudance data imported successfully.");
            
                                   
            
            
        } catch (IOException e) {
            taskMonitor.showMessage(TaskMonitor.Level.ERROR, " Error while processing the file: " + e.getMessage());
            e.printStackTrace();
               
        }}
    
    
   
        
        private void showDataInPanel(JSONObject jsonObject) {
		    JSONDisplayPanel panel = new JSONDisplayPanel(mggManager, jsonObject);
		   // JsonResultPanel panel = new JsonResultPanel(mggManager, jsonObject);
		   // mggManager.registerService(panel, CytoPanelComponent.class, new Properties());
		    
			
			 JFrame frame = new JFrame("Imported OTU/ASV");
			 frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			 frame.getContentPane().add(panel); frame.pack(); frame.setVisible(true);
			 
    }
        

	
        
}