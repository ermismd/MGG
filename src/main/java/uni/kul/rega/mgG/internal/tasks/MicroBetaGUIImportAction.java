package uni.kul.rega.mgG.internal.tasks;

import org.apache.http.HttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedEvent;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedListener;
import org.cytoscape.application.swing.events.CytoPanelStateChangedEvent;
import org.cytoscape.application.swing.events.CytoPanelStateChangedListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskMonitor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uni.kul.rega.mgG.internal.utils.CSVReader;
import uni.kul.rega.mgG.internal.utils.HTTPUtils;
import uni.kul.rega.mgG.internal.view.JSONDisplayPanel;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CySwingApplication;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.util.List;

/*
 * 
 * The MicroBetaGUIImportAction represents an action in the mgG application + 
 * that allows users to import a CSV file ,process it, display the JSON data in a panel, +
 * and send the JSON data to a server
 * 
*/

public class MicroBetaGUIImportAction extends AbstractCyAction {

	  private final CySwingApplication swingApplication;
	    private final CytoPanel cytoPanelWest;
	    private final CyApplicationManager cyApplicationManager;  
	    
	    
	    public MicroBetaGUIImportAction(CySwingApplication cytoscapeDesktopService,CyApplicationManager cyApplicationManager2) {
	        super("Import CSV File");

	        this.swingApplication = cytoscapeDesktopService;
	        this.cytoPanelWest = swingApplication.getCytoPanel(CytoPanelName.WEST);
			this.cyApplicationManager = cyApplicationManager2;

	        setPreferredMenu("Apps.MicroBetaGUI");
	        setMenuGravity(1.0f);
	        
	            
	        
	    }

	  
		@Override
	    public void actionPerformed(ActionEvent e) {
	        //  dialog to choose the CSV 
	        JFileChooser fileChooser = new JFileChooser();
	        int option = fileChooser.showOpenDialog(null);
	        if (option == JFileChooser.APPROVE_OPTION) {
	            //   select csv, perform the import  here
	            File selectedFile = fileChooser.getSelectedFile();
	            String filePath = selectedFile.getAbsolutePath();
	            // Call the method to process the CSV 
	            processCSVFile(null, filePath);
	        }
	    }

		private void processCSVFile(final TaskMonitor monitor,String filePath) {
			try {
		        // Call CSVReader from Utils to parse the TSV/CSV file with tab delimiter
				 List<String[]> csvData = CSVReader.readCSV(monitor, filePath);

				// Find the headers(the first row that has more than 1 columns)
			        String[] headers = null;
			        for (String[] row : csvData) {
			            if (row.length > 1) {
			                headers = row;
			                break;
			            }
			        }

			        // Create JSONArray to hold the JSONObjects
			        JSONArray jsonArray = new JSONArray();

			        // Iterate each row of CSV 
			        for (String[] values : csvData) {
			            // Skip rows with only one column
			            if (values.length <= 1) {
			                continue;
			            }

			            // Create a JSONObject for each row of CSV 
			            JSONObject jsonObject = new JSONObject();
			            for (int j = 0; j < headers.length; j++) {
			                if (j < values.length) {
			                    jsonObject.put(headers[j], values[j]);
			                }
			            }

			            // Add the JSONObject to the JSONArray
			            jsonArray.add(jsonObject);
			        }


		        // Write  JSON data to a file
		        String jsonFilePath = filePath + ".json";
		        FileWriter writer = new FileWriter(jsonFilePath);
		        writer.write(jsonArray.toJSONString());
		        writer.close();

		        // Show the JSON data in a panel
		        showDataInPanel(jsonArray);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
	    }
		
		
		/*
		 * showDataInPanel method  displays the JSON data passed as a JSONArray. It creates a new  JSONDisplayPanel  from +
		 * the internal.view package
		 */
		
		private void showDataInPanel(JSONArray jsonArray) {
		    JSONDisplayPanel panel = new JSONDisplayPanel(jsonArray);

		    JFrame frame = new JFrame("JSON Data");
		    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    frame.getContentPane().add(panel);
		    frame.pack();
		    frame.setVisible(true);
		}
		
		
		
		
		  private void sendJSONDataToServer(JSONArray jsonArray) {
		        try {
		            // Convert the JSONArray to a JSON string
		            String jsonQuery = jsonArray.toJSONString();

		            // Set the server URL
		            String serverURL = "https://example.com/api/endpoint"; // Replace with the actual server URL

		            // Create an instance of CloseableHttpClient
		            CloseableHttpClient httpclient = HttpClients.createDefault();

		            // Send the JSON data to the server
		            JSONObject jsonResponse = HTTPUtils.postJSON(serverURL, httpclient, jsonQuery, null);

		            // Process the server response if needed
		            if (jsonResponse != null) {
		            	
		                // Process the response here
		            	
		            	String response = jsonResponse.toJSONString();
		                // Do something with the response
		            	
		            }
		            

		            // Close the HttpClient
		            httpclient.close();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }

		

		
}
