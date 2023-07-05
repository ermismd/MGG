package be.kuleuven.mgG.internal.tasks;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.utils.HTTPUtils;
import be.kuleuven.mgG.internal.view.JSONDisplayPanel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;



/**
 * This class represents a task for sending data to a server.
 * The task sends a JSON array as string to microbetag server URL and retrieves the server's response.
 */


public class SendDataToServerTask extends AbstractTask {
 
	private  String serverResponse; // Stores the server response
	private final JSONArray jsonArray; // The JSON array to send to the server
    private final MGGManager mggManager;  // The MGGManager instance for retrieving the JSON array

    
    
    /**
     * Constructs a new SendDataToServerTask object.
     *
     * @param jsonArray   The JSON array to send to the server.
     * @param mggManager  The MGGManager instance for retrieving the JSON array.
     */
    
    public SendDataToServerTask(JSONArray jsonArray,MGGManager mggManager) {
    	this.mggManager=mggManager;
    	 this.jsonArray = mggManager.getJsonArray();
    }

    /**
     * Runs the task to send data to the server.
     *
     * @param taskMonitor The task monitor to display progress and status messages.
     */
    
    @Override
    public void run(TaskMonitor taskMonitor) {
        taskMonitor.setTitle("Sending Data to Server");
        taskMonitor.setStatusMessage("Sending data...");
        
    
        try {
        	
        	// Create an HttpClient
        	  CloseableHttpClient httpClient = HttpClients.createDefault();
        	  
        	  
              String jsonQuery = jsonArray.toJSONString();

              String serverURL = "https://msysbio.gbiomed.kuleuven.be/upload-abundance-table";

              HttpPost httpPost = new HttpPost(serverURL);
              
              // Set the JSON payload as a StringEntity
              StringEntity entity = new StringEntity(jsonQuery);
              httpPost.setEntity(entity);
              
              // Set the request headers
              httpPost.setHeader("Accept", "application/json");
              httpPost.setHeader("Content-type", "application/json");

          
              
           // Execute the HTTP request and obtain the response
              try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                  int statusCode = response.getStatusLine().getStatusCode();

                  if (statusCode != 200 && statusCode != 202) {
                      taskMonitor.showMessage(TaskMonitor.Level.ERROR, "Got " + statusCode + " code from server");
                      return;
                  }

                  // Get the response content
                  HttpEntity responseEntity = response.getEntity();
                  String responseString = EntityUtils.toString(responseEntity);
                  
                  // Set the server response as the status message
                  taskMonitor.setStatusMessage("Server Response: " + responseString);

                  // Parse the response as a JSONObject and JSONArray
                  JSONParser parser = new JSONParser();
                  JSONObject jsonResponse = (JSONObject) parser.parse(new InputStreamReader(responseEntity.getContent()));
                  JSONArray jsonResponse2 = (JSONArray) parser.parse(responseString);

                  taskMonitor.setStatusMessage("Processing server response");

                  taskMonitor.setStatusMessage("Data sent to server and retrieved successfully!");
                  
                  
                  // Display the server response in the panel using SwingUtilities.invokeLater
                  SwingUtilities.invokeLater(() -> showDataInPanel(jsonResponse2));
                  
                  
                  // Set the JSON array in the MGGManager
                  mggManager.setServerResponse(jsonResponse2);
                  
                  
              } // The response  closes here

              httpClient.close();
          } catch (Exception e) {
        	  	// Handle and display error messages
              taskMonitor.showMessage(TaskMonitor.Level.ERROR, "Error while sending JSON data to server: " + e.getMessage());
              e.printStackTrace();
          }

          taskMonitor.setStatusMessage("Data sent to server successfully!");
      }
	
    
    
    private void showDataInPanel(JSONArray jsonResponse) {
    	  JSONDisplayPanel panel = new JSONDisplayPanel(mggManager, jsonResponse);

          JFrame frame = new JFrame("Abundance Data");
          frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
          frame.getContentPane().add(panel);
          frame.pack();
          frame.setVisible(true);
    
    }
    
    
    
    
    
    
    
    
    
    
    
    /*
	 * private void sendJSONDataToServer(JSONArray jsonArray, TaskMonitor
	 * taskMonitor) { try {
	 * 
	 * 
	 * // Convert the JSONArray to a JSON string String jsonQuery =
	 * jsonArray.toJSONString();
	 * 
	 * System.out.println(jsonQuery);
	 * 
	 * // Set the server URL String serverURL =
	 * "https://msysbio.gbiomed.kuleuven.be/upload-abundance-table"; // Replace with
	 * the actual server URL
	 * 
	 * 
	 * // Create an instance of CloseableHttpClient CloseableHttpClient httpclient =
	 * HttpClients.createDefault();
	 * 
	 * taskMonitor.setStatusMessage("Sending data to server");
	 * 
	 * // Send the JSON data to the server JSONObject jsonResponse =
	 * HTTPUtils.postJSON(serverURL, httpclient, jsonQuery, taskMonitor);
	 * 
	 * // Process the server response if needed if (jsonResponse != null) {
	 * taskMonitor.setStatusMessage("Processing server response");
	 * 
	 * // Process the response here serverResponse = jsonResponse.toJSONString();
	 * mggManager.setServerResponse(jsonResponse);
	 * 
	 * }
	 * 
	 * 
	 * 
	 * taskMonitor.setStatusMessage("Closing connection");
	 * 
	 * // Close the HttpClient httpclient.close(); } catch (Exception e) {
	 * taskMonitor.showMessage(TaskMonitor.Level.ERROR,
	 * "Error while sending JSON data to server: " + e.getMessage());
	 * e.printStackTrace();
	 * 
	 * // Show the JSON data in a panel if showJSONInPanel
	 * 
	 * SwingUtilities.invokeLater(() ->showDataInPanel(jsonArray));
	 * 
	 * 
	 * 
	 * } }
	 */  
    

}