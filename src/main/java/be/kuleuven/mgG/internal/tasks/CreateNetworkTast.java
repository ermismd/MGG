package be.kuleuven.mgG.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import be.kuleuven.mgG.internal.model.MGGManager;




public class CreateNetworkTast extends AbstractTask {
	
	
	 private final CyNetworkManager networkManager;
	
	private final  CyNetworkFactory networkFactory;
	private final MGGManager mggmanager;  
	
	 
	 public CreateNetworkTast(MGGManager mggmanager, CyNetworkFactory  networkFactory, CyNetworkManager networkManager){
				this.networkFactory= networkFactory;
				this.networkManager=networkManager;
				this.mggmanager=mggmanager;
	}

	 @Override
		public void run(TaskMonitor taskMonitor) throws Exception {
			taskMonitor.setTitle("Creating the network");
			taskMonitor.setStatusMessage("Creating the network...");
			
			JSONArray jsonResponse=mggmanager.getServerResponse();
			
					//Create the network
			CyNetwork network=networkFactory.createNetwork();
			network.getDefaultNodeTable().createColumn("NodeID", String.class, true);
			networkManager.addNetwork(network);

			
			 // Process the server response array to create nodes and edges
	        for (Object object : jsonResponse) {
	            JSONObject nodeData = (JSONObject) object;
	            String nodeId = (String) nodeData.get("id");

	            // Create a node
	            CyNode node = network.addNode();
	            CyTable nodeTable = network.getDefaultNodeTable();
	            nodeTable.getRow(node.getSUID()).set("NodeID", nodeId);

	            // Process the edges for this node if available
	            JSONArray edges = (JSONArray) nodeData.get("edges");
	            if (edges != null) {
	                for (Object edgeObject : edges) {
	                    String targetId = (String) edgeObject;

	                    // Create an edge
	                    CyNode targetNode = getNodeByNodeId(network, nodeTable, targetId);
	                    if (targetNode != null) {
	                        network.addEdge(node, targetNode, true);
	                    }
	                }
	            }
	        }

	        taskMonitor.setStatusMessage("Network created successfully!");
	        taskMonitor.setProgress(1.0);

	        // Set the created network as the result of this task
	        insertTasksAfterCurrentTask(new TaskIterator(new SetCurrentNetworkTask(network)));
	        
	        
	    }

			
	 }
	 

	
	
	
	
	
