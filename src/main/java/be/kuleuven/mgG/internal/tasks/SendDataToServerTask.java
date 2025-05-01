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
import org.cytoscape.work.util.ListSingleSelection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.utils.HTTPUtils;

import be.kuleuven.mgG.internal.view.JSONDisplayPanel;

import java.io.BufferedReader;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;



/**
 * This class represents a task for sending data to a server.
 * The task sends a JSON array as string to microbetag server URL and retrieves the server's response.
 */


public class SendDataToServerTask extends AbstractTask {
 
	private  String serverResponse; // Stores the server response
	private final JSONObject dataObject; // The JSON array to send to the server
	private final JSONObject metaDataObject;
	private final JSONObject networkObject;
	//private final JSONObject jsonObject;
    private final MGGManager mggManager;  // The MGGManager instance for retrieving the JSON array
    
   
    
 	// @Tunables part 
     
    
     @Tunable(
    		 description="Choose input type", 
    		 groups={"Input Parameters"}, 
    		 gravity=1.0, 
    		 required=true
    )
     public ListSingleSelection<String> INPUT = new ListSingleSelection<>("abundance table", "network");
     
     @Tunable(
    		 description="heterogeneous",
    		 tooltip="Consider confounding factors" , 
    		 groups={"Additional Parameter if Input is Abudance Table"}, 
    		 dependsOn = "INPUT=abundance table", 
    		 gravity=10.0, 
    		 required=true
    )
     public boolean HETEROGENEOUS=false;
     
     @Tunable(
    		 description="sensitive",
    		 tooltip="Use full abundance information (default: discretized)" , 
    		 groups={"Additional Parameter if Input is Abudance Table"}, 
    		 dependsOn = "INPUT=abundance table", 
    		 gravity=11.0, 
    		 required=true
    )
     public boolean SENSITIVE=false;
  
     @Tunable(description="Choose delimiter", groups={"Input Parameters"},tooltip="Delimiter used in your taxonomy" ,gravity=2.0, required=true)
     public ListSingleSelection<String> DELIMITER = new ListSingleSelection<>(";", "|","__","_");

     @Tunable(description="Choose taxonomy Database",tooltip="Choose the taxonomy in the abudance table among GTDB,"
     		+ " Silva(as in Dada2), microbetag_prep or other", groups={"Input Parameters"}, gravity=3.0, required=true)
     public ListSingleSelection<String> TAXONOMY = new ListSingleSelection<>("GTDB", "Silva","microbetag_prep", "other");
     
     @Tunable(description="Phenotrex-based annotations", longDescription="Choose whether to get PHEN_TRAITS information.",
    		 groups={"Input Parameters"}, 
     		tooltip="Choose whether to get Phenotypic traits based on genomic information" ,gravity=4.0, exampleStringValue="True, False", required=true)
     public boolean PHEN_TRAITS=true;

     @Tunable(description="FAPROTAX annotations", longDescription="Choose whether to get FAPROTAX information.", groups={"Input Parameters"}, 
     		tooltip="Choose whether to get Phenotypic traits based on literature" , gravity=5.0, exampleStringValue="True, False", required=true)
     public boolean FAPROTAX=true; 

     @Tunable(description="Pathway Complementarity", longDescription="Choose whether to get the pathway complementarity.", 
     		 tooltip="Choose whether to get Pathway Complementarity annotations" ,groups={"Input Parameters"}, gravity=6.0, exampleStringValue="True, False", required=true)
     public boolean PATH_COMPLEMENTS=true;
     
     @Tunable(description="Seed scores and complements", tooltip="Choose whether to get the Seed Scores and it's complements.",
    		 longDescription="Choose whether to get the Seed Scores and  complements.", groups={"Input Parameters"}, gravity=7.0, exampleStringValue="True, False", required=true)
     public boolean SEED_COMPLEMENTS= false;
     
     @Tunable(description="Consider Children taxa", groups={"Input Parameters"}, 
     		tooltip="Use strain genomes in case no type species genome supported" , gravity=8.0, exampleStringValue="True, False", required=true)
     public boolean GET_CHILDREN=false; 
     
     @Tunable(description="Network clustering", longDescription="Choose whether to get NETWORK_CLUSTERING clustering", groups={"Input Parameters"}, 
     		tooltip="Choose whether to get NETWORK_CLUSTERING clustering" , gravity=9.0, exampleStringValue="True, False", required=true)
     public boolean NETWORK_CLUSTERING=false; 
     
     //@Tunable(description="NetCmpt", longDescription="Choose whether to use NetCmpt.", groups={"Input Settings"}, gravity=6.0, exampleStringValue="True, False", required=true)
     //public boolean netCmpt= true;
    				
    
   
    /**
     * Constructs a new SendDataToServerTask object.
     *
     * @param jsonArray   The JSON array to send to the server.
     * @param mggManager  The MGGManager instance for retrieving the JSON array.
     */
    
    public SendDataToServerTask(MGGManager mggManager) {
    	
		this.mggManager     = mggManager;
    	this.dataObject     = mggManager.getJsonObject();
    	this.metaDataObject = mggManager.getMetadataJsonObject();
    	this.networkObject  = mggManager.getNetworkObject();
    }

    
    
    /**
     * Runs the task to send data to the server.
     *
     * @param taskMonitor The task monitor to display progress and status messages.
     */
    
    
    @Override
    public void run(TaskMonitor taskMonitor) {
    	
    	   // Create a new JSONObject
        JSONObject jsonObject = new JSONObject();
        
        
        
        // Add the 'data' JSONArray from dataObject
        if (dataObject != null && dataObject.containsKey("data")) {
            JSONArray dataJsonArray = (JSONArray) dataObject.get("data");
            jsonObject.put("MGG_DATA", dataJsonArray);
        }

        // Add the 'metadata' JSONArray from metaDataObject
        if (metaDataObject != null && metaDataObject.containsKey("metadata")) {
            JSONArray metaDataJsonArray = (JSONArray) metaDataObject.get("metadata");
            jsonObject.put("MGG_METADATA", metaDataJsonArray);
        }
        
        // Add the 'network data' JSONArray from networkObject
        if (networkObject != null && networkObject.containsKey("network")) {
            JSONArray networkJsonArray = (JSONArray) networkObject.get("network");
            jsonObject.put("MGG_NETWORK", networkJsonArray);
        }
    	
        // Create a new JSONArray for the input parameters
	       JSONArray inputParameters = new JSONArray();
	        
	 
	        inputParameters.add("MGG_INPUT:" + INPUT.getSelectedValue());
	        inputParameters.add("MGG_TAXONOMY:" + TAXONOMY.getSelectedValue());
	        inputParameters.add("MGG_DELIMITER:" + DELIMITER.getSelectedValue()); 
	        inputParameters.add("MGG_SENSITIVE:" + SENSITIVE);
	        inputParameters.add("MGG_HETEROGENEOUS:" + HETEROGENEOUS);
	        inputParameters.add("MGG_PHEN_TRAITS:" + PHEN_TRAITS);
	        inputParameters.add("MGG_FAPROTAX:" + FAPROTAX);
	        inputParameters.add("MGG_PATH_COMPLEMENTS:" + PATH_COMPLEMENTS);
	        inputParameters.add("MGG_SEED_COMPLEMENTS:" + SEED_COMPLEMENTS);
	        inputParameters.add("MGG_NETWORK_CLUSTERING:" + NETWORK_CLUSTERING);
	        inputParameters.add("MGG_GET_CHILDREN:" + GET_CHILDREN);	
	        
	        // Add the input parameters to the jsonObject
	        jsonObject.put("MGG_PARAMS", inputParameters);
	        
    	
    	
	        //taskMonitor.setStatusMessage("Server tried to sent: " +  jsonObject.toString());
    	
    		
        taskMonitor.setTitle("Sending Data to Server");
        taskMonitor.setStatusMessage("Processing Data on Server( May take some time... )");
          
        //taskMonitor.setStatusMessage("Server Send " + jsonObject.toJSONString());
       

        	
        
        RequestConfig config = RequestConfig.custom()
        	    .setConnectTimeout(600 * 1000)  // time to establish the connection 
        	    .setSocketTimeout(600 * 1000)  // time waiting for data 
        	    .setConnectionRequestTimeout(600 * 1000) // time to wait for a connection from  manager/pool
        	    .build();

        
              CloseableHttpClient httpClient = HttpClients.custom()
                      .setDefaultRequestConfig(config)
                      .build() ;

              
              
              try {
                      String jsonQuery = jsonObject.toJSONString();
//                      String serverURL = "https://msysbio.gbiomed.kuleuven.be/upload-abundance-table-dev";
                      String serverURL = "http://localhost:1337/upload-abundance-table-dev";

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
                          
                          String responseString = EntityUtils.toString(responseEntity);
                         
                         
                         try {
                        	  JSONArray jsonResponse2=  (JSONArray) new JSONParser().parse(responseString);
                        	  mggManager.setServerResponse(jsonResponse2);
                        	  taskMonitor.setStatusMessage("Processing server response");
                        	  taskMonitor.setStatusMessage("Data sent to server and got the response successfully.");
                        	  // Set jsonObject and metadataObject to null if the response is successful
                              if(responseString !=null) {
                             	 mggManager.setJsonObject(null);
                             	 mggManager.setMetadataJsonObject(null);
                             	 mggManager.setNetworkObject(null);
                              }
                              
                            	  
	                  	 } catch (Exception e) {
	                  		 	
	                  	    	JSONObject jsonResponseError=  (JSONObject) new JSONParser().parse(responseString);
	                  	    	taskMonitor.showMessage(TaskMonitor.Level.ERROR,"Error from server: " + jsonResponseError.toString());
	                  				e.printStackTrace(System.out);
	                  				
	                  				// SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, 
	                  					//	"Error from server: " + jsonResponseError.toString(), "Error", JOptionPane.ERROR_MESSAGE));
	                  				
	                  				 // Set jsonObject and metadataObject to null if the response is successful
	                                if(responseString !=null) {
	                               	 mggManager.setJsonObject(null);
	                               	 mggManager.setMetadataJsonObject(null);
	                               	 mggManager.setNetworkObject(null);
	                                }
	                                
	                  	 }
                                                 
                          //taskMonitor.setStatusMessage("Server sent: " + jsonQuery);
                         
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
                  taskMonitor.setStatusMessage("Process finished");
              
    			
    	}
          
    }}

    
    
    


    

