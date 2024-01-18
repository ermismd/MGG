package be.kuleuven.mgG.internal.tasks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import be.kuleuven.mgG.internal.model.MGGManager;

public class ImportNetwork extends AbstractTask {
	
	
    private final MGGManager mggManager;
	
    
    public ImportNetwork (MGGManager mggManager) {
    	this.mggManager=mggManager;
    }

    
    
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
				
		//get current network
		
		CyNetwork currentNetwork=mggManager.getCurrentNetwork();
		
		if(currentNetwork==null) {
			taskMonitor.showMessage(TaskMonitor.Level.ERROR, " No Network is currently loaded)");
			return;
		}
		
		//edgetable of the network
		CyTable edgetable=currentNetwork.getDefaultEdgeTable();
		
		   // Initialize JSONArray for network data
	    JSONArray networkData = new JSONArray();

	    // Add headers
	    JSONArray headers = new JSONArray();
	    headers.add("Edge ID");
	    headers.add("Source Node");
	    headers.add("Target Node");
	    networkData.add(headers);

	    // Get list of edges
	    List<CyEdge> edges = currentNetwork.getEdgeList();

	    for (CyEdge edge : edges) {
	        JSONArray edgeArray = new JSONArray();

	        Long edgeId = edge.getSUID();
	        CyNode sourceNode = edge.getSource();
	        CyNode targetNode = edge.getTarget();

	        // Get source and target node IDs or names
	        String sourceNodeId = getNodeIdentifier(currentNetwork, sourceNode);
	        String targetNodeId = getNodeIdentifier(currentNetwork, targetNode);

	        edgeArray.add(edgeId);
	        edgeArray.add(sourceNodeId);
	        edgeArray.add(targetNodeId);

	        networkData.add(edgeArray);
	    }

	    // Creating a new JSONObject for the network
	    JSONObject networkJsonObject = new JSONObject();
	    networkJsonObject.put("network", networkData);
	    
	    JSONObject datajsonObject=new JSONObject();
	    datajsonObject=mggManager.getJsonObject();
	    
	    
	    
	    
	    // Check if source and target node names are in dataJsonObject
	    if (areNodesInDataJson(networkJsonObject,  datajsonObject)) {
	        // If all nodes are present, set the network object
	        mggManager.setNetworkObject(networkJsonObject);
	        taskMonitor.showMessage(TaskMonitor.Level.INFO ,"Network data loaded correctly");
	    } else {
	        // If not all nodes are present, set the network object to null
	        mggManager.setNetworkObject(null);
	        taskMonitor.showMessage(TaskMonitor.Level.WARN, 
	                "Node names in the network are not the same with the names in the abundance table -> network data can't be uploaded");
	    }
	    
	    // Send the network object to mggManager
	   // mggManager.setNetworkObject(networkJsonObject);
	
		
		//taskMonitor.setStatusMessage("NetworkArray" + networkData.toString());
		//taskMonitor.setStatusMessage("networkJsonObjec" + networkJsonObject.toString());

		// Sending the structured network object to mggManager
		//mggManager.setNetworkObject(networkJsonObject);
	}
		
		
	private String getNodeIdentifier(CyNetwork network, CyNode node) {
	    // Example: Getting the name attribute of the node
	    // Replace "name" with the actual column name for your node names
	    return network.getRow(node).get("name", String.class);
	}
		
	private boolean areNodesInDataJson(JSONObject networkJsonObject, JSONObject dataJsonObject) {
	    // Extract node names from the networkJsonObject
	    JSONArray networkData = (JSONArray) networkJsonObject.get("network");
	    Set<String> nodeNamesInNetwork = new HashSet<>();
	    for (int i = 1; i < networkData.size(); i++) { // skip headers
	        JSONArray edge = (JSONArray) networkData.get(i);
	        nodeNamesInNetwork.add((String) edge.get(1)); // Source Node
	        nodeNamesInNetwork.add((String) edge.get(2)); // Target Node
	    }

	    // Check if these node names are in the first row of dataJsonObject
	    if (dataJsonObject.containsKey("data")) {
	        JSONArray data = (JSONArray) dataJsonObject.get("data");
	        JSONArray headerRow = (JSONArray) data.get(0); // First row with column names
	        Set<String> columnNamesInData = new HashSet<>(headerRow);

	        
	        return columnNamesInData.containsAll(nodeNamesInNetwork);
	    }
	    return false;
	}	
		
	}


