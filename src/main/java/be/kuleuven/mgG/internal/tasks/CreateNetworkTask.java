
  package be.kuleuven.mgG.internal.tasks;
  
  import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork; 
  import org.cytoscape.model.CyNetworkFactory; 
  import org.cytoscape.model.CyNetworkManager; 
  import org.cytoscape.model.CyNode;
  import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.json.JSONResult;
import org.json.simple.JSONArray;
  import org.json.simple.JSONObject;
  
  import be.kuleuven.mgG.internal.model.MGGManager;
  
  
  /**
   * This class represents a task for creating a network in Cytoscape based on a JSON response from a server.
   * 
   * The JSON response  contain information about the nodes and edges of the network.
   * For each node and edge, the task creates a corresponding node or edge in the Cytoscape network.
   * The task also sets various attributes for the nodes and edges based on the JSON data.
   * 
   * The task uses the CyNetworkFactory service to create a new network, and the CyNetworkManager service to add the network to Cytoscape through the MGGmanager class
   * 
   */
  
 public class CreateNetworkTask extends AbstractTask {
  
 
  		
  	final MGGManager mggManager;
  	
    // Cytoscape services used by the task
    final CyNetworkFactory networkFactory;
    final CyNetworkManager networkManager;
    final CyNetworkViewFactory networkViewFactory;
    final CyNetworkViewManager networkViewManager;
    
    

  public CreateNetworkTask(MGGManager mggManager ){ 
	  
	  
	  this.mggManager=mggManager; 
	  
	  this.networkFactory = mggManager.getService(CyNetworkFactory.class);
      this.networkManager = mggManager.getService(CyNetworkManager.class);
      this.networkViewFactory = mggManager.getService(CyNetworkViewFactory.class);
      this.networkViewManager = mggManager.getService(CyNetworkViewManager.class);
	  
  }
  
  
	  @Override
	    public void run(TaskMonitor taskMonitor) {
	        taskMonitor.setTitle("Creating the network");
	        taskMonitor.setStatusMessage("Creating the network...");

	        JSONObject jsonResponse = mggManager.getServerResponse();

	 
	        JSONObject elements = (JSONObject) jsonResponse.get("elements");

            // Create a new network
            CyNetwork network = networkFactory.createNetwork();
         // Add the network to the network manager
            networkManager.addNetwork(network);
            
            
         // Get the default node table
            CyTable nodeTable = network.getDefaultNodeTable();

            
         // Create columns if they don't exist
            if (nodeTable.getColumn("alias") == null) {
                nodeTable.createListColumn("alias", String.class, false);
            }
            if (nodeTable.getColumn("SUID") == null) {
                nodeTable.createColumn("SUID", Long.class, false);
            }
            if (nodeTable.getColumn("shared_name") == null) {
                nodeTable.createColumn("shared_name", String.class, false);
            }
            if (nodeTable.getColumn("selected") == null) {
                nodeTable.createColumn("selected", Boolean.class, false);
            }
            
            if (!nodeTable.getColumns().contains("annotation_Taxon")) {
                nodeTable.createColumn("annotation_Taxon", String.class, false);
            }
          
            
            
            // Add nodes
            JSONArray nodes = (JSONArray) elements.get("nodes");
            for (Object nodeObj : nodes) {
                JSONObject nodeData = (JSONObject) ((JSONObject) nodeObj).get("data");
                String id = (String) nodeData.get("id");
                CyNode node = network.addNode();
                network.getRow(node).set(CyNetwork.NAME, id);
                network.getRow(node).set("alias", ((JSONArray) nodeData.get("alias")));
                network.getRow(node).set("SUID", (Long) nodeData.get("SUID"));
                network.getRow(node).set("shared_name", (String) nodeData.get("shared_name"));
                network.getRow(node).set("selected", (Boolean) nodeData.get("selected"));
                network.getRow(node).set("annotation_Taxon", (String) nodeData.get("annotation_Taxon"));
                
            }

            
         // Get the default edge table
            CyTable edgeTable = network.getDefaultEdgeTable();

            // Create columns if they don't exist
            if (edgeTable.getColumn("shared_interaction") == null) {
                edgeTable.createColumn("shared_interaction", String.class, false);
            }
            if (edgeTable.getColumn("interaction") == null) {
                edgeTable.createColumn("interaction", String.class, false);
            }
            if (edgeTable.getColumn("shared_name") == null) {
                edgeTable.createColumn("shared_name", String.class, false);
            }
            if (edgeTable.getColumn("source") == null) {
                edgeTable.createColumn("source", String.class, false);
            }
            if (edgeTable.getColumn("target") == null) {
                edgeTable.createColumn("target", String.class, false);
            }
            if (edgeTable.getColumn("selected") == null) {
                edgeTable.createColumn("selected", Boolean.class, false);
            }


            
            
            
            // Add edges
            JSONArray edges = (JSONArray) elements.get("edges");
            for (Object edgeObj : edges) {
                JSONObject edgeData = (JSONObject) ((JSONObject) edgeObj).get("data");
                String sourceId = (String) edgeData.get("source");
                String targetId = (String) edgeData.get("target");
                CyNode sourceNode = getNodeById(network, sourceId);
                CyNode targetNode = getNodeById(network, targetId);
                if (sourceNode != null && targetNode != null) {
                    CyEdge edge = network.addEdge(sourceNode, targetNode, false);
                    network.getRow(edge).set(CyNetwork.NAME, (String) edgeData.get("id"));
                    network.getRow(edge).set("shared_interaction", (String) edgeData.get("shared_interaction"));
                    network.getRow(edge).set("interaction", (String) edgeData.get("interaction"));
                    network.getRow(edge).set("shared_name", (String) edgeData.get("shared_name"));
                    network.getRow(edge).set("source", (String) edgeData.get("source"));
                    network.getRow(edge).set("target", (String) edgeData.get("target"));
                    network.getRow(edge).set("selected", (Boolean) edgeData.get("selected"));
                }
            }

        
         //  create a network view
            CyNetworkView networkView = networkViewFactory.createNetworkView(network);
            networkViewManager.addNetworkView(networkView);  
            
            
    	 }

        private CyNode getNodeById(CyNetwork network, String id) {
            for (CyNode node : network.getNodeList()) {
                if (network.getRow(node).get(CyNetwork.NAME, String.class).equals(id)) {
                    return node;
                }
            }
            return null;
        }
    }
  
  
  
 