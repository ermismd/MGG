package be.kuleuven.mgG.internal.tasks;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.utils.HTTPUtils;

import be.kuleuven.mgG.internal.view.JSONDisplayPanel;
import be.kuleuven.mgG.internal.view.JSONViewerPanel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;



/**
 * This class represents a task for sending data to a server.
 * The task sends a JSON array as string to microbetag server URL and retrieves the server's response.
 */


public class SendDataToServerTask extends AbstractTask {
 
	private  String serverResponse; // Stores the server response
	private final JSONObject jsonObject; // The JSON array to send to the server
    private final MGGManager mggManager;  // The MGGManager instance for retrieving the JSON array
    
   

    				
    
   
    /**
     * Constructs a new SendDataToServerTask object.
     *
     * @param jsonArray   The JSON array to send to the server.
     * @param mggManager  The MGGManager instance for retrieving the JSON array.
     */
    
    public SendDataToServerTask( JSONObject jsonObject, MGGManager mggManager) {
    	this.mggManager=mggManager;
    	this.jsonObject = mggManager.getJsonObject();
    	
    }

    /**
     * Runs the task to send data to the server.
     *
     * @param taskMonitor The task monitor to display progress and status messages.
     */
    
    @Override
    public void run(TaskMonitor taskMonitor) {
    	
    	
    		
        taskMonitor.setTitle("Sending Data to Server");
        taskMonitor.setStatusMessage("Processing Data on Server( May take some time... )");
          
        taskMonitor.setStatusMessage("Server Send " + jsonObject.toJSONString());
       
        	
        RequestConfig config = RequestConfig.custom()
        	    .setConnectTimeout(600 * 1000)  // time to establish the connection with the remote host
        	    .setSocketTimeout(600 * 1000)  // time waiting for data – after the connection was established; maximum time of inactivity between two data packets
        	    .setConnectionRequestTimeout(600 * 1000) // time to wait for a connection from the connection manager/pool
        	    .build();

        
              CloseableHttpClient httpClient = HttpClients.custom()
                      .setDefaultRequestConfig(config)
                      .build() ;

              
              
              try {
                      String jsonQuery = jsonObject.toJSONString();
                      String serverURL = "https://msysbio.gbiomed.kuleuven.be/upload-abundance-table-dev";

                      HttpPost httpPost = new HttpPost(serverURL);
                      httpPost.setConfig(config);
                      
                      StringEntity entity = new StringEntity(jsonQuery);
                      
                      httpPost.setEntity(entity);
                      httpPost.setHeader("Accept", "application/json");
                      httpPost.setHeader("Content-type", "application/json");

                      try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                          int statusCode = response.getStatusLine().getStatusCode();

                          if (statusCode != 200 && statusCode != 202) {
                              taskMonitor.showMessage(TaskMonitor.Level.ERROR, "Got " + statusCode + " code from server");
                              return;
                          }

                          HttpEntity responseEntity = response.getEntity();
                          JSONObject jsonResponse = (JSONObject) new JSONParser().parse(new InputStreamReader(responseEntity.getContent()));
                          
                          taskMonitor.setStatusMessage("Server sent: " + jsonQuery);
                          taskMonitor.setStatusMessage("Processing server response");
                          taskMonitor.setStatusMessage("Data sent to server and retrieved successfully!");
                       // Here's the new line where you set the JSON response as a status message
                          taskMonitor.setStatusMessage("Server Response: " + jsonResponse.toJSONString());
                          mggManager.setServerResponse(jsonResponse);

                      
                      } catch (Exception e) {
                    	 
                          taskMonitor.showMessage(TaskMonitor.Level.ERROR, "Error when waiting for the response: " + e.getMessage());
                          e.printStackTrace(System.out);
                      }
                      
              } catch (Exception e) {
                  taskMonitor.showMessage(TaskMonitor.Level.ERROR, "Error while setting up the request or processing the response: " + e.getMessage());
                  e.printStackTrace(System.out);
              }
    			finally {
    					try {
    							httpClient.close();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
                  taskMonitor.setStatusMessage("Data sent to server successfully!");
              
    			
    	}
          
    }}

    
    
    

/*private void viewData(JSONObject jsonResponse) {
	  JSONViewerPanel viewerPanel = new JSONViewerPanel(jsonResponse);
  
    
    JFrame frame = new JFrame("JSON Viewer");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.getContentPane().add(viewerPanel);
    frame.pack();
    frame.setVisible(true);
    */
    
    
    
    
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
    

