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
import javax.swing.SwingUtilities;



/**
 * This class represents a task for sending data to a server.
 * The task sends a JSON array as string to microbetag server URL and retrieves the server's response.
 */


public class SendDataToServerTask extends AbstractTask {
 
	private  String serverResponse; // Stores the server response
	private final JSONObject dataObject; // The JSON array to send to the server
	private final JSONObject metaDataObject;
	//private final JSONObject jsonObject;
    private final MGGManager mggManager;  // The MGGManager instance for retrieving the JSON array
    
   
    
 	// @Tunables part 
     
    
     @Tunable(description="Choose input type", groups={"Input Parameters"}, gravity=1.0, required=true)
     public ListSingleSelection<String> input = new ListSingleSelection<>("abundance_table", "network");
     
     @Tunable(description="Choose if heterogeneous", groups={"Additional Input if chosen abudance_table"}, gravity=10.0, required=true)
     public boolean heterogeneous=false;
     
     @Tunable(description="Choose if sensitive", groups={"Additional Input if chosen abudance_table"}, gravity=11.0, required=true)
     public boolean sensitive=false;
  
     @Tunable(description="Choose delimiter", groups={"Input Parameters"}, gravity=2.0, required=true)
     public ListSingleSelection<String> delimiter = new ListSingleSelection<>(";", "|","__","_");

     @Tunable(description="Choose taxonomy Database", groups={"Input Parameters"}, gravity=3.0, required=true)
     public ListSingleSelection<String> taxonomy = new ListSingleSelection<>("GTDB", "Silva(as in DADA2)","microbetag_prep", "other");
     
     @Tunable(description="PhenDB", longDescription="Choose whether to get PhenDB.", groups={"Input Parameters"}, 
     		tooltip="Choose whether to get PhenDB values annotations" ,gravity=4.0, exampleStringValue="True, False", required=true)
     public boolean phenDB=true;

     @Tunable(description="FAPROTAX", longDescription="Choose whether to get FAPROTAX.", groups={"Input Parameters"}, 
     		tooltip="Choose whether to get FAPROTAX values annotations" , gravity=5.0, exampleStringValue="True, False", required=true)
     public boolean faproTax=true; 

     @Tunable(description="Pathway Complementarity", longDescription="Choose whether to get the pathway complementarity.", 
     		 tooltip="Choose whether to get Pathway Complementarity annotations" ,groups={"Input Parameters"}, gravity=6.0, exampleStringValue="True, False", required=true)
     public boolean pathway_complement=true;
     
     @Tunable(description="Seed Scores", longDescription="Choose whether to get the Seed Scores.", groups={"Input Parameters"}, gravity=7.0, exampleStringValue="True, False", required=true)
     public boolean seed_scores= false;
     
     @Tunable(description="Get_Children", longDescription="Choose whether to get Children(different strains).", groups={"Input Parameters"}, 
     		tooltip="Choose whether to get strains from the same species" , gravity=8.0, exampleStringValue="True, False", required=true)
     public boolean get_children=false; 
     
     @Tunable(description="Manta", longDescription="Choose whether to get Manta annotations.", groups={"Input Parameters"}, 
     		tooltip="Choose whether to get Manta annotations" , gravity=9.0, exampleStringValue="True, False", required=true)
     public boolean manta=false; 
     
     //@Tunable(description="NetCmpt", longDescription="Choose whether to use NetCmpt.", groups={"Input Settings"}, gravity=6.0, exampleStringValue="True, False", required=true)
     //public boolean netCmpt= true;
    				
    
   
    /**
     * Constructs a new SendDataToServerTask object.
     *
     * @param jsonArray   The JSON array to send to the server.
     * @param mggManager  The MGGManager instance for retrieving the JSON array.
     */
    
    public SendDataToServerTask(MGGManager mggManager) {
    	
		this.mggManager=mggManager;
    	this.dataObject = mggManager.getJsonObject();
    	this.metaDataObject=mggManager.getMetadataJsonObject();
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
        
        taskMonitor.setStatusMessage("data array: " + dataObject.toString());
        
        taskMonitor.setStatusMessage("metadata array: " + metaDataObject.toString());
        
        // Add the 'data' JSONArray from dataObject
        if (dataObject != null && dataObject.containsKey("data")) {
            JSONArray dataJsonArray = (JSONArray) dataObject.get("data");
            jsonObject.put("data", dataJsonArray);
        }

        // Add the 'metadata' JSONArray from metaDataObject
        if (metaDataObject != null && metaDataObject.containsKey("metadata")) {
            JSONArray metaDataJsonArray = (JSONArray) metaDataObject.get("metadata");
            jsonObject.put("metadata", metaDataJsonArray);
        }
    	
        // Create a new JSONArray for the input parameters
	       JSONArray inputParameters = new JSONArray();
	        
	 
	        inputParameters.add("input:"+input.getSelectedValue());
	        inputParameters.add("taxonomy:"+taxonomy.getSelectedValue());
	        inputParameters.add("delimiter:"+delimiter.getSelectedValue()); 
    // inputParameters.add(sensitive);
	      //  inputParameters.add(heterogeneous);
	        inputParameters.add("phenDB:"+phenDB);
	        inputParameters.add("faproTax:"+faproTax);
	        inputParameters.add("pathway_complement:"+pathway_complement);
	        inputParameters.add("seed_scroes:"+seed_scores);
	        inputParameters.add("manta"+manta);
	        	
	 
	        
	        // Add the input parameters to the jsonObject
	        jsonObject.put("inputParameters", inputParameters);
	        
    	
    	
	        taskMonitor.setStatusMessage("Server tried to sent: " +  jsonObject.toString());
    	
    		
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
                         
                          JSONArray jsonResponse1= (JSONArray) new JSONParser().parse(new InputStreamReader(responseEntity.getContent()));
                       
                          
                          
                           // JSONObject jsonResponse = (JSONObject) new JSONParser().parse(new InputStreamReader(responseEntity.getContent()));
                           //  JSONObject jsonResponse=(JSONObject) responseEntity ; 
                  		   //BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity .getContent()));
                  		   //JSONObject jsonResponse= (JSONObject) new JSONParser().parse(reader);
                          
                          
                          
                          taskMonitor.setStatusMessage("Server sent: " + jsonQuery);
                          taskMonitor.setStatusMessage("Processing server response");
                          taskMonitor.setStatusMessage("Data sent to server and retrieved successfully!");
                    
                         //  taskMonitor.setStatusMessage("Server Response: " + jsonResponse1.toJSONString());
                         // taskMonitor.setStatusMessage("Server Response: " + responseEntity);
                          
                         mggManager.setServerResponse(jsonResponse1);
                        
                         
                         
                         
                         // Set jsonObject and metadataObject to null if the response is successful
                         
                         mggManager.setJsonObject(null);
                         mggManager.setMetadataJsonObject(null);

                         taskMonitor.setStatusMessage("Data sent to server and response processed successfully.");
                         
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

    
    
    


    

