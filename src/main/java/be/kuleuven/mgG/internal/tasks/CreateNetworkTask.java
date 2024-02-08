	
package be.kuleuven.mgG.internal.tasks;

import java.awt.Color;
import java.awt.Paint;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.NodeShape;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.json.JSONResult;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.cytoscape.util.color.*;

import be.kuleuven.mgG.internal.model.MGGManager;

/**
 * This class represents a task for creating a network in Cytoscape based on a
 * JSON response from a server.
 * 
 * The JSON response contain information about the nodes and edges of the
 * network. For each node and edge, the task creates a corresponding node or
 * edge in the Cytoscape network. The task also sets various attributes for the
 * nodes and edges based on the JSON data.
 * 
 * The task uses the CyNetworkFactory service to create a new network, and the
 * CyNetworkManager service to add the network to Cytoscape through the
 * MGGmanager class
 * 
 */

public class CreateNetworkTask extends AbstractTask {

	final MGGManager mggManager;

	
	
	public static final String MY_NAMESPACE = "MGGid";
	public static final String MY_ATTRIBUTE = "id";

	// final PaletteProvider paletteProvider;

	public CreateNetworkTask(MGGManager mggManager) {

		this.mggManager = mggManager;

	


	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		
		
		taskMonitor.setTitle("Creating the network");
	    taskMonitor.setStatusMessage("Creating the network...");

	    JSONArray jsonResponse = mggManager.getServerResponse();

	   
	    if (jsonResponse == null) {
	        taskMonitor.showMessage(TaskMonitor.Level.ERROR, "No server response to create the network from.");
	        return; 
	    }

	    try {
	        String cxContent = jsonResponse.toJSONString();
	        String cytoscapeAPIURL = "http://localhost:1234/v1/networks?format=cx";

	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
	            HttpPost httpPost = new HttpPost(cytoscapeAPIURL);
	            StringEntity entity = new StringEntity(cxContent);
	            httpPost.setEntity(entity);
	            httpPost.setHeader("Accept", "application/json");
	            httpPost.setHeader("Content-type", "application/json");

	            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
	                HttpEntity responseEntity = response.getEntity();

	                if (responseEntity != null) {
	                    String result = EntityUtils.toString(responseEntity);
	                    System.out.println(result);
	                    taskMonitor.showMessage(TaskMonitor.Level.INFO, "Network created successfully.");
	                } else {
	                   // taskMonitor.showMessage(TaskMonitor.Level.ERROR, "Failed to create network: Empty response from server.");
	                }
	            }
	        } // HttpClient and HttpResponse  close
	    } catch (IOException e) {
	        taskMonitor.showMessage(TaskMonitor.Level.ERROR, "Error while creating the network: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	}
//		taskMonitor.setTitle("Creating the network");
//		taskMonitor.setStatusMessage("Creating the network...");
//
//		JSONArray jsonResponse = mggManager.getServerResponse();
//		
//		// Check if jsonResponse is not null
//	    if (jsonResponse == null) {
//	        taskMonitor.showMessage(TaskMonitor.Level.ERROR, "No server response to create the network from.");
//	        return; // Exit the method early as there's no response to process
//	    }
//		
//		try {
//		      
//	        String cxContent = jsonResponse.toJSONString();
//	        
//	        
//	        CloseableHttpClient httpClient = HttpClients.createDefault();
//	        String cytoscapeAPIURL = "http://localhost:1234/v1/networks?format=cx";
//	        
//	        
//	        HttpPost httpPost = new HttpPost(cytoscapeAPIURL);
//	        StringEntity entity = new StringEntity(cxContent);
//	        httpPost.setEntity(entity);
//	        httpPost.setHeader("Accept", "application/json");
//	        httpPost.setHeader("Content-type", "application/json");
//	        
//	       
//	        CloseableHttpResponse response = httpClient.execute(httpPost);
//	        HttpEntity responseEntity = response.getEntity();
//	        
//	        if(responseEntity != null) {
//	            String result = EntityUtils.toString(responseEntity);
//	            System.out.println(result);
//	        }
//	        
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//	}
//		
//                   
//	}

	
	


