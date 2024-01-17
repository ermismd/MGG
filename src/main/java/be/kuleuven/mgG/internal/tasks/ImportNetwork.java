package be.kuleuven.mgG.internal.tasks;

import java.util.List;

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

	    // Send the network object to mggManager
	    mggManager.setNetworkObject(networkJsonObject);
	
		
		//taskMonitor.setStatusMessage("NetworkArray" + networkData.toString());
		//taskMonitor.setStatusMessage("networkJsonObjec" + networkJsonObject.toString());

		// Sending the structured network object to mggManager
		mggManager.setNetworkObject(networkJsonObject);
	}
		
		
	private String getNodeIdentifier(CyNetwork network, CyNode node) {
	    // Example: Getting the name attribute of the node
	    // Replace "name" with the actual column name for your node names
	    return network.getRow(node).get("name", String.class);
	}
		
		
		
	}


