	
package be.kuleuven.mgG.internal.tasks;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	// Cytoscape services used by the task
	final CyNetworkFactory networkFactory;
	final CyNetworkManager networkManager;
	final CyNetworkViewFactory networkViewFactory;
	final CyNetworkViewManager networkViewManager;
	final VisualStyleFactory visualStyleFactory;
	final VisualMappingFunctionFactory discreteMappingFactory;
	final VisualMappingFunctionFactory vmfFactoryP;
	final VisualMappingManager vmmServiceRef;

	final CyLayoutAlgorithmManager layoutAlgorithmManager;

	final PaletteProviderManager paletteManager;
	
	public static final String MY_NAMESPACE = "MGGid";
	public static final String MY_ATTRIBUTE = "id";

	// final PaletteProvider paletteProvider;

	public CreateNetworkTask(MGGManager mggManager) {

		this.mggManager = mggManager;

		this.networkFactory = mggManager.getService(CyNetworkFactory.class);
		this.networkManager = mggManager.getService(CyNetworkManager.class);
		this.networkViewFactory = mggManager.getService(CyNetworkViewFactory.class);
		this.networkViewManager = mggManager.getService(CyNetworkViewManager.class);
		this.visualStyleFactory = mggManager.getService(VisualStyleFactory.class);
		this.discreteMappingFactory = mggManager.getService(VisualMappingFunctionFactory.class,
				"(mapping.type=discrete)");

		this.vmfFactoryP = mggManager.getService(VisualMappingFunctionFactory.class, "(mapping.type=passthrough)");
		this.vmmServiceRef = mggManager.getService(VisualMappingManager.class);

		this.layoutAlgorithmManager = mggManager.getService(CyLayoutAlgorithmManager.class);
		this.paletteManager = mggManager.getService(PaletteProviderManager.class);

		// this.paletteProvider=mggManager.getService(PaletteProvider.class);

		// paletteProvider=Palette.getPalette("ColorBrewer");

		// paletteProvider=Palette.getPalette("ColorBrewer");
		// PaletteProvider paletteProvider=Palette.getPalette("ColorBrewer");

		// PaletteProvider paletteProvider = new PaletteProvider();

	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		taskMonitor.setTitle("Creating the network");
		taskMonitor.setStatusMessage("Creating the network...");

		JSONObject jsonResponse = mggManager.getServerResponse();
		

		JSONObject elements = (JSONObject) jsonResponse.get("elements");
		JSONArray nodesArray = (JSONArray) elements.get("nodes");
        JSONArray edgesArray = (JSONArray) elements.get("edges");
        
        

  
        
        
        
        
		// Create a new network
		CyNetwork network = networkFactory.createNetwork();
		

		// Add the network to the network manager
		networkManager.addNetwork(network);

		// default node table
		CyTable nodeTable = network.getDefaultNodeTable();
		
		
		// Map to keep track of created nodes by ID for connecting edges
        Map<String, CyNode> nodeMap = new HashMap<>();

		

        
        
     // Check and create columns if they don't exist
        String[] columns = {"id", "taxonomy", "NCBI-Tax-Id", "GTDB-representative", "assignment","cluster",
                            "taxonomy-level", "degree_layout", "name", "faprotax-assignments", 
                             "shared name"};

        for (String col : columns) {
            if (nodeTable.getColumn(col) == null) {
                if (col.equals("faprotax-assignments")) {
                    nodeTable.createListColumn(col, String.class, false);
                } else if (col.equals("degree_layout")) {
                    nodeTable.createColumn(col, Double.class, false);
                }
                else if (col.equals("NCBI-Tax-Id")) {
                    nodeTable.createColumn(col, Double.class, false);
                }
                  else {
                    nodeTable.createColumn(col, String.class, false);
                }
            }
        } 
        
     // Using a Set to ensure unique trait keys across all nodes
        Set<String> allTraitKeys = new HashSet<>();

        for (Object nodeObj : nodesArray) {
            JSONObject nodeData = (JSONObject) ((JSONObject) nodeObj).get("data");
            JSONObject phenotypicTraitsJson = (JSONObject) nodeData.get("phenotypic-traits");
            
            if (phenotypicTraitsJson != null) {  // Add a null check here
                allTraitKeys.addAll(phenotypicTraitsJson.keySet());
            }
        }

        // For each trait key, determine the data type from the first node (or any node)
        JSONObject anyNodeData = (JSONObject) ((JSONObject) nodesArray.get(0)).get("data");
        JSONObject anyNodePhenotypicTraitsJson = (JSONObject) anyNodeData.get("phenotypic-traits");

        for (String traitKey : allTraitKeys) {
            Object exampleValue = anyNodePhenotypicTraitsJson.get(traitKey); 
            if (nodeTable.getColumn(traitKey) == null) {
                if (exampleValue instanceof String) {
                    nodeTable.createColumn(traitKey, String.class, false);
                } else if (exampleValue instanceof Number) {
                    if (exampleValue instanceof Double) {
                        nodeTable.createColumn(traitKey, Double.class, false);
                    } else {
                        nodeTable.createColumn(traitKey, Long.class, false);
                    }
                } else if (exampleValue instanceof Boolean) {
                    nodeTable.createColumn(traitKey, Boolean.class, false);
                }
                
            }
        }
      


           
     // Add nodes
        JSONArray nodes = (JSONArray) elements.get("nodes");
        for (Object nodeObj : nodes) {
            JSONObject nodeData = (JSONObject) ((JSONObject) nodeObj).get("data");
            CyNode node = network.addNode();

            network.getRow(node).set(CyNetwork.NAME, (String) nodeData.get("id"));
            network.getRow(node).set("id", (String) nodeData.get("id"));
            network.getRow(node).set("taxonomy", (String) nodeData.get("taxonomy"));
            network.getRow(node).set("NCBI-Tax-Id", (Double) nodeData.get("NCBI-Tax-Id"));
            network.getRow(node).set("GTDB-representative", (String) nodeData.get("GTDB-representative"));
            network.getRow(node).set("taxonomy-level", (String) nodeData.get("taxonomy-level"));
            network.getRow(node).set("degree_layout", (Double) nodeData.get("degree_layout"));
            network.getRow(node).set("name", (String) nodeData.get("name"));

            // Handling List<String> for faprotax-assignments
            JSONArray faprotaxAssignmentsJson = (JSONArray) nodeData.get("faprotax-assignments");
            List<String> faprotaxAssignments = new ArrayList<>();
            if(faprotaxAssignmentsJson != null) {
                faprotaxAssignmentsJson.forEach(item -> faprotaxAssignments.add((String) item));
            }
            network.getRow(node).set("faprotax-assignments", faprotaxAssignments);


            JSONObject phenotypicTraitsJson = (JSONObject) nodeData.get("phenotypic-traits");
            
            if (phenotypicTraitsJson != null) {  // Add a null check here
                for (Object objKey : phenotypicTraitsJson.keySet()) {
                    String traitKey = (String) objKey;
                    Object value = phenotypicTraitsJson.get(traitKey);
                    network.getRow(node).set(traitKey, value);  // Set trait value
                }
            }
      

        }
		
		
		
		// default edge table
		CyTable edgeTable = network.getDefaultEdgeTable();
		
		
		// Check and create edge columns if they don't exist
		String[] edgeColumns = {
		    "id", 
		    "shared_name", 
		    "weight", 
		    "SUID", 
		    "source-ncbi-tax-id", 
		    "target-ncbi-tax-id", 
		    "selected", 
		    "source", 
		    "target"
		};

		for (String col : edgeColumns) {
		    if (edgeTable.getColumn(col) == null) {
		        switch(col) {
		            case "weight":
		                edgeTable.createColumn(col, Double.class, false);
		                break;
		            case "source-ncbi-tax-id":
		                edgeTable.createColumn(col, Double.class, false);
		                break;
		            case "SUID":
		                edgeTable.createColumn(col, Double.class, false);
		                break;
		            case "selected":
		                edgeTable.createColumn(col, Boolean.class, false);
		                break;
		            default:
		                edgeTable.createColumn(col, String.class, false);
		        }
		    }
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
				            network.getRow(edge).set("shared_name", (String) edgeData.get("shared_name"));
				            network.getRow(edge).set("weight", (Double) edgeData.get("weight"));  // Parsing string to long
				            network.getRow(edge).set("SUID", (Double) edgeData.get("SUID"));  // Parsing string to long
				            network.getRow(edge).set("source-ncbi-tax-id", (Double) edgeData.get("source-ncbi-tax-id"));
				            network.getRow(edge).set("target-ncbi-tax-id", (String) edgeData.get("target-ncbi-tax-id"));
				            network.getRow(edge).set("selected", (Boolean) edgeData.get("selected"));
				        }
				}

	
		
			

		

		// create a network view
		CyNetworkView networkView = networkViewFactory.createNetworkView(network);
		networkViewManager.addNetworkView(networkView);

		// ----------------------------------------------------------------------------------------------------

		// VISUAL STYLE

		VisualStyle style = visualStyleFactory.createVisualStyle("MGG");

		String columnName = "taxonomy-level";

		// VisualProperty for node fill color
		VisualProperty<?> vp = BasicVisualLexicon.NODE_FILL_COLOR;

		style.setDefaultValue(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.ELLIPSE);

		// Node Borders
		style.setDefaultValue(BasicVisualLexicon.NODE_BORDER_WIDTH, 2.0);
		style.setDefaultValue(BasicVisualLexicon.NODE_BORDER_PAINT, Color.DARK_GRAY);

		// VisualProperty for node shape
		VisualProperty<NodeShape> nodeShapeVP = BasicVisualLexicon.NODE_SHAPE;

		// Create a discrete mapping function for node shape
		DiscreteMapping<String, NodeShape> shapeMapping = (DiscreteMapping<String, NodeShape>) discreteMappingFactory
				.createVisualMappingFunction(columnName, String.class, nodeShapeVP);

		// Map taxonomy-level to shapes
		Map<String, NodeShape> taxonomyShapeMap = getTaxonomyShapeMap();

		// Set taxonomy-level to shape pairs
		for (Map.Entry<String, NodeShape> entry : taxonomyShapeMap.entrySet()) {
			shapeMapping.putMapValue(entry.getKey(), entry.getValue());
		}

		// Add mapping function to the visual style
		style.addVisualMappingFunction(shapeMapping);

		// Node Labels
		style.setDefaultValue(BasicVisualLexicon.NODE_LABEL, "");
		PassthroughMapping<String, String> labelMapping = (PassthroughMapping<String, String>) vmfFactoryP
				.createVisualMappingFunction("name", String.class, BasicVisualLexicon.NODE_LABEL);
		style.addVisualMappingFunction(labelMapping);

		// discrete mapping function
		DiscreteMapping<String, Paint> mapping = (DiscreteMapping<String, Paint>) discreteMappingFactory
				.createVisualMappingFunction(columnName, String.class, vp);

		// map species to colors
		Map<String, Paint> speciesColorMap = getSpeciesColorMap();

		// Set the species-color pairs
		for (Map.Entry<String, Paint> entry : speciesColorMap.entrySet()) {
			mapping.putMapValue(entry.getKey(), entry.getValue());
		}

		// Add the mapping function to the visual style
		style.addVisualMappingFunction(mapping);

		// Apply the visual style to a network view
		style.apply(networkView);

		vmmServiceRef.addVisualStyle(style);
		vmmServiceRef.setCurrentVisualStyle(style);
		networkView.updateView();

		// layout ( "force-directed")
		CyLayoutAlgorithm layout = layoutAlgorithmManager.getLayout("force-directed");
		if (layout == null) {
			// layout is not found, use the default layout
			layout = layoutAlgorithmManager.getDefaultLayout();
		}

		// ----------------------------------------------------------------------

		// Create a task iterator for the layout
		insertTasksAfterCurrentTask(layout.createTaskIterator(networkView, layout.getDefaultLayoutContext(),
				CyLayoutAlgorithm.ALL_NODE_VIEWS, null));
		// -----------------------------------------------------------------------------------
	}

	/*
	 * private CyNode getNodeById(CyNetwork network, String id) { for (CyNode node :
	 * network.getNodeList()) { if (network.getRow(node).get(CyNetwork.NAME,
	 * String.class).equals(id)) { return node; } } return null; }
	 */

	private CyNode getNodeById(CyNetwork network, String nodeId) {
	    for (CyNode node : network.getNodeList()) {
	        String id = network.getRow(node).get(CyNetwork.NAME, String.class);
	        if (nodeId.equals(id)) {
	            return node;
	        }
	    }
	    return null;
	}
	
	
	
	private Map<String, NodeShape> getTaxonomyShapeMap() {
		Map<String, NodeShape> taxonomyShapeMap = new HashMap<>();
		taxonomyShapeMap.put("genus", NodeShapeVisualProperty.ELLIPSE);
		taxonomyShapeMap.put("family", NodeShapeVisualProperty.ELLIPSE);
		taxonomyShapeMap.put("mspecies", NodeShapeVisualProperty.ELLIPSE);
		taxonomyShapeMap.put("null", NodeShapeVisualProperty.ELLIPSE);
		taxonomyShapeMap.put("species", NodeShapeVisualProperty.ELLIPSE);
		// Add more taxonomy-level to shape mappings as needed
		return taxonomyShapeMap;
	}

	private Map<String, Paint> getSpeciesColorMap() {

		Map<String, Paint> speciesColorMap = new HashMap<>();

		PaletteProvider colorBrewerPaletteProvider = paletteManager.getPaletteProvider("ColorBrewer");
		Palette set1 = colorBrewerPaletteProvider.getPalette("Set1 colors");
		// Palette set1= paletteProvider.getPalette("Set1 colors");
		Color[] set1Palette = set1.getColors(9);

		speciesColorMap.put("genus", set1Palette[7]);
		speciesColorMap.put("family", set1Palette[3]);
		speciesColorMap.put("mspecies", set1Palette[2]);
		speciesColorMap.put("null", set1Palette[8]);
		speciesColorMap.put("species", set1Palette[1]);
		// speciesColorMap.put("GRC5", set1Palette[5]);

		return speciesColorMap;
	}

}
