package be.kuleuven.mgG.internal.tasks;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import be.kuleuven.mgG.internal.utils.CSVReader;
import be.kuleuven.mgG.internal.utils.HTTPUtils;
import be.kuleuven.mgG.internal.view.JSONDisplayPanel;

public class ImportFileTask extends AbstractTask {
    private final CySwingApplication swingApplication;
    private final CyApplicationManager cyApplicationManager;

    private String filePath;

    public ImportFileTask(CySwingApplication cytoscapeDesktopService, CyApplicationManager cyApplicationManager2, String filePath) {
        this.swingApplication = cytoscapeDesktopService;
        this.cyApplicationManager = cyApplicationManager2;
        this.filePath = filePath;
    }

    @Override
    public void run(TaskMonitor taskMonitor) {
    	taskMonitor.setTitle("Import CSV File");
        taskMonitor.setStatusMessage("Reading CSV file...");

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
		        
            taskMonitor.setStatusMessage("Processing CSV data...");


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

            taskMonitor.setStatusMessage("Displaying data in panel...");

            
            // Write  JSON data to a file
	        String jsonFilePath = filePath + ".json";
	        FileWriter writer = new FileWriter(jsonFilePath);
	        writer.write(jsonArray.toJSONString());
	        writer.close();
            
	        
	     // Show the JSON data in a panel
	        SwingUtilities.invokeLater(() -> showDataInPanel(jsonArray));
	        
	        taskMonitor.setStatusMessage("Sending data to server...");

	        // Send JSON data to server
	        sendJSONDataToServer(jsonArray);
	        
            taskMonitor.setProgress(1.0);
            taskMonitor.setStatusMessage("Finished processing CSV file.");
            
        } catch (IOException e) {
            taskMonitor.showMessage(TaskMonitor.Level.ERROR, " Error while processing the file: " + e.getMessage());
            e.printStackTrace();
            
        } catch (Exception e) {
            taskMonitor.showMessage(TaskMonitor.Level.ERROR, "Unexpected error " + e.getMessage());
            e.printStackTrace();
            
        }}
        
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