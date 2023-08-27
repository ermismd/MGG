
package be.kuleuven.mgG.internal.tasks;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;
import java.util.Map;

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

		// Create a new network
		CyNetwork network = networkFactory.createNetwork();

		// Add the network to the network manager
		networkManager.addNetwork(network);

		// default node table
		CyTable nodeTable = network.getDefaultNodeTable();

		
		  // Create columns if they don't exist for 'data' attributes if
		 if (nodeTable.getColumn("id") == null) { 
			 nodeTable.createColumn("id", String.class, false); 
		 } 
		 
		 if (nodeTable.getColumn("taxonomy") == null){	
			 nodeTable.createColumn("taxonomy", String.class, false);
		 } 
		 if (nodeTable.getColumn("NCBI-Tax-Id") == null) {
			 	nodeTable.createColumn("NCBI-Tax-Id", String.class, false);
		  } 
		 if
		  (nodeTable.getColumn("GTDB-representative") == null) {
			 nodeTable.createColumn("GTDB-representative", String.class, false); }
		 if
		  (nodeTable.getColumn("taxonomy-level") == null) {
			 nodeTable.createColumn("taxonomy-level", String.class, false); } 
		 if
		  (nodeTable.getColumn("degree_layout") == null) {
			 nodeTable.createColumn("degree_layout", Long.class, false); }
		 if
		  (nodeTable.getColumn("name") == null){
			 nodeTable.createColumn("name", String.class, false); 
		  }
		 

		// if (nodeTable.getColumn("phenotypic_traits") == null) {
		// nodeTable.createColumn("phenotypic_traits", String.class, false);
		// }

		// Add nodes
		JSONArray nodes = (JSONArray) elements.get("nodes");
		for (Object nodeObj : nodes) {
			JSONObject nodeData = (JSONObject) ((JSONObject) nodeObj).get("data");
			// String id = (String) nodeData.get("id");
			CyNode node = network.addNode();

			network.getRow(node).set(CyNetwork.NAME, (String) nodeData.get("id"));
			network.getRow(node).set("selected", (Boolean) nodeData.get("selected"));
			network.getRow(node).set("taxonomy", (String) nodeData.get("taxonomy"));
			network.getRow(node).set("NCBI-Tax-Id", (String) nodeData.get("NCBI-Tax-Id"));
			network.getRow(node).set("GTDB-representative", (String) nodeData.get("GTDB-representative"));
			network.getRow(node).set("taxonomy-level", (String) nodeData.get("taxonomy-level"));
			network.getRow(node).set("degree_layout", (Long) nodeData.get("degree_layout"));
			//network.getRow(node).set("name", (String) nodeData.get("name"));

			/*
			 * network.getRow(node).set(CyNetwork.NAME, (String) nodeData.get("id"));
			 * network.getRow(node).set("selected", (Boolean) nodeData.get("selected"));
			 * network.getRow(node).set("taxonomy", (String) nodeData.get("taxonomy"));
			 * network.getRow(node).set("NCBI-Tax-Id", (String)
			 * nodeData.get("NCBI-Tax-Id")); network.getRow(node).set("GTDB-representative",
			 * (String) nodeData.get("GTDB-representative"));
			 * network.getRow(node).set("taxonomy-level", (String)
			 * nodeData.get("taxonomy-level"));
			 */

		}

		// default edge table
		CyTable edgeTable = network.getDefaultEdgeTable();

		// Create columns if they don't exist
		
		/*
		 * if (edgeTable.getColumn(CyNetwork.NAME) == null) {
		 * edgeTable.createColumn(CyNetwork.NAME, String.class, false); }
		 */
		 
		  if (edgeTable.getColumn("shared_name") == null) {
		  edgeTable.createColumn("shared_name", String.class, false); } 
		  if
		  (edgeTable.getColumn("flashweave-score") == null) {
		  edgeTable.createColumn("flashweave-score", Double.class, false); } 
		  if
		  (edgeTable.getColumn("SUID") == null) { edgeTable.createColumn("SUID",
				 Long.class, false); } 
		  if (edgeTable.getColumn("source-ncbi-tax-id") ==
		  null) { edgeTable.createColumn("source-ncbi-tax-id", String.class, false); }
		  if (edgeTable.getColumn("target-ncbi-tax-id") == null) {
		  edgeTable.createColumn("target-ncbi-tax-id", String.class, false); } 
		  if
		  (edgeTable.getColumn("selected") == null) {
		  edgeTable.createColumn("selected", Boolean.class, false);
		  } 
		  if
		  (edgeTable.getColumn("source") == null) { 
			  edgeTable.createColumn("source", String.class, false);
			} 
		  if (edgeTable.getColumn("target") == null) {
		  edgeTable.createColumn("target", String.class, false); 
		  }
		  
		/*
		 * // Create columns if they don't exist if
		 * (edgeTable.getColumn("shared_interaction") == null) {
		 * edgeTable.createColumn("shared_interaction", String.class, false); } if
		 * (edgeTable.getColumn("interaction") == null) {
		 * edgeTable.createColumn("interaction", String.class, false); } if
		 * (edgeTable.getColumn("shared_name") == null) {
		 * edgeTable.createColumn("shared_name", String.class, false); } if
		 * (edgeTable.getColumn("source") == null) { edgeTable.createColumn("source",
		 * String.class, false); } if (edgeTable.getColumn("target") == null) {
		 * edgeTable.createColumn("target", String.class, false); } if
		 * (edgeTable.getColumn("selected") == null) {
		 * edgeTable.createColumn("selected", Boolean.class, false); }
		 */
		 

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
		            String flashweaveScoreStr = (String) edgeData.get("flashweave-score");
		            double flashweaveScore = Double.parseDouble(flashweaveScoreStr);
		            network.getRow(edge).set("flashweave-score", flashweaveScore);
		            //network.getRow(edge).set("flashweave-score", (Double) edgeData.get("flashweave-score"));  // Parsing string to long
		            network.getRow(edge).set("SUID", Long.parseLong((String) edgeData.get("SUID")));  // Parsing string to long
		            network.getRow(edge).set("source-ncbi-tax-id", (String) edgeData.get("source-ncbi-tax-id"));
		            network.getRow(edge).set("target-ncbi-tax-id", (String) edgeData.get("target-ncbi-tax-id"));
		            network.getRow(edge).set("selected", (Boolean) edgeData.get("selected"));
		        }
		    
			
		
			
			/*
			 * if (sourceNode != null && targetNode != null) { CyEdge edge
			 * =network.addEdge(sourceNode, targetNode, false);
			 * 
			 * network.getRow(edge).set(CyNetwork.NAME, (String) edgeData.get("id"));
			 * network.getRow(edge).set("shared_interaction",
			 * (String)edgeData.get("shared_interaction"));
			 * 
			 * network.getRow(edge).set("interaction",(String) edgeData.get("interaction"));
			 * 
			 * network.getRow(edge).set("shared_name", (String)edgeData.get("shared_name"));
			 * 
			 * network.getRow(edge).set("source", (String)edgeData.get("source"));
			 * 
			 * network.getRow(edge).set("target", (String)edgeData.get("target"));
			 * 
			 * network.getRow(edge).set("selected", (Boolean)edgeData.get("selected")); }
			 */
			 
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
		taxonomyShapeMap.put("genus", NodeShapeVisualProperty.DIAMOND);
		taxonomyShapeMap.put("family", NodeShapeVisualProperty.ROUND_RECTANGLE);
		taxonomyShapeMap.put("mspecies", NodeShapeVisualProperty.ELLIPSE);
		taxonomyShapeMap.put("null", NodeShapeVisualProperty.OCTAGON);
		taxonomyShapeMap.put("species", NodeShapeVisualProperty.HEXAGON);
		// Add more taxonomy-level to shape mappings as needed
		return taxonomyShapeMap;
	}

	private Map<String, Paint> getSpeciesColorMap() {

		Map<String, Paint> speciesColorMap = new HashMap<>();

		PaletteProvider colorBrewerPaletteProvider = paletteManager.getPaletteProvider("ColorBrewer");
		Palette set1 = colorBrewerPaletteProvider.getPalette("Set1 colors");
		// Palette set1= paletteProvider.getPalette("Set1 colors");
		Color[] set1Palette = set1.getColors(9);

		speciesColorMap.put("genus", set1Palette[4]);
		speciesColorMap.put("family", set1Palette[1]);
		speciesColorMap.put("mspecies", set1Palette[2]);
		speciesColorMap.put("null", set1Palette[8]);
		speciesColorMap.put("species", set1Palette[0]);
		// speciesColorMap.put("GRC5", set1Palette[5]);

		return speciesColorMap;
	}

}
