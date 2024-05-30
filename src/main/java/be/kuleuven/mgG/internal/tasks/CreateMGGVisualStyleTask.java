package be.kuleuven.mgG.internal.tasks;

import java.awt.Color;
import java.awt.Paint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.util.color.Palette;
import org.cytoscape.util.color.PaletteProvider;
import org.cytoscape.util.color.PaletteProviderManager;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.NodeShape;
import org.cytoscape.view.presentation.property.values.LineType;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

import be.kuleuven.mgG.internal.model.MGGManager;



public class CreateMGGVisualStyleTask extends AbstractTask {
	
	final MGGManager manager;

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

	
	
	public CreateMGGVisualStyleTask(MGGManager manager) {
		
		this.manager = manager;
		
	
		this.networkFactory = manager.getService(CyNetworkFactory.class);
		this.networkManager = manager.getService(CyNetworkManager.class);
		this.networkViewFactory = manager.getService(CyNetworkViewFactory.class);
		this.networkViewManager = manager.getService(CyNetworkViewManager.class);
		this.visualStyleFactory = manager.getService(VisualStyleFactory.class);
		this.discreteMappingFactory = manager.getService(VisualMappingFunctionFactory.class,
				"(mapping.type=discrete)");

		this.vmfFactoryP = manager.getService(VisualMappingFunctionFactory.class, "(mapping.type=passthrough)");
		this.vmmServiceRef = manager.getService(VisualMappingManager.class);

		this.layoutAlgorithmManager = manager.getService(CyLayoutAlgorithmManager.class);
		this.paletteManager = manager.getService(PaletteProviderManager.class);
		
		
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		
		// If the style already existed, remove it first
				Iterator it = vmmServiceRef.getAllVisualStyles().iterator();
				while (it.hasNext()){
					VisualStyle curVS = (VisualStyle)it.next();
					if (curVS.getTitle().equalsIgnoreCase("MGG Visual Style"))
					{
						vmmServiceRef.removeVisualStyle(curVS);
						break;
					}
					
				}
				
		
		// ----------------------------------------------------------------------------------------------------

				// VISUAL STYLE

				
				CyNetworkView networkView= manager.getCurrentNetworkView();
		
				VisualStyle style = visualStyleFactory.createVisualStyle("MGG Visual Style");
				this.vmmServiceRef.addVisualStyle(style);
				
				//String columnName = "taxonomy-level";

				// VisualProperty for node fill color
				VisualProperty<?> vp = BasicVisualLexicon.NODE_FILL_COLOR;
				VisualProperty<?> et = BasicVisualLexicon.EDGE_LINE_TYPE;


				// Node Borders
				style.setDefaultValue(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.ELLIPSE);
				style.setDefaultValue(BasicVisualLexicon.NODE_BORDER_WIDTH, 2.0);
				style.setDefaultValue(BasicVisualLexicon.NODE_BORDER_PAINT, Color.DARK_GRAY);
				style.setDefaultValue(BasicVisualLexicon.EDGE_WIDTH, 2.0); // Set default edge width
				style.setDefaultValue(BasicVisualLexicon.EDGE_STROKE_SELECTED_PAINT, Color.ORANGE); // Set default color for when selecting an edge
				
				// style.setDefaultValue(BasicVisualLexicon.EDGE_LINE_TYPE, LineTypeVisualProperty.EQUAL_DASH); 
				
				
				// Node shape mapping based on "taxonomy-level"
		        String columnName = "microbetag::ncbi-tax-level";
		        VisualProperty<NodeShape> nodeShapeVP = BasicVisualLexicon.NODE_SHAPE;
		        DiscreteMapping<String, NodeShape> shapeMapping = (DiscreteMapping<String, NodeShape>) discreteMappingFactory
						.createVisualMappingFunction(columnName, String.class, nodeShapeVP);
		        Map<String, NodeShape> taxonomyShapeMap = getTaxonomyShapeMap(); // Assuming you have this method defined somewhere
		        for (Map.Entry<String, NodeShape> entry : taxonomyShapeMap.entrySet()) {
		            shapeMapping.putMapValue(entry.getKey(), entry.getValue());
		        }
		        style.addVisualMappingFunction(shapeMapping);


				// Node Labels
				style.setDefaultValue(BasicVisualLexicon.NODE_LABEL, "");
				PassthroughMapping<String, String> labelMapping = (PassthroughMapping<String, String>) vmfFactoryP
						.createVisualMappingFunction("name", String.class, BasicVisualLexicon.NODE_LABEL);
				style.addVisualMappingFunction(labelMapping);


				// discrete mapping function(species-colors)
				DiscreteMapping<String, Paint> colorMapping = (DiscreteMapping<String, Paint>) discreteMappingFactory
				                    .createVisualMappingFunction(columnName, String.class, vp);

				Map<String, Paint> speciesColorMap = getSpeciesColorMap();
				for (Map.Entry<String, Paint> entry : speciesColorMap.entrySet()) {
				    colorMapping.putMapValue(entry.getKey(), entry.getValue());
				}
				style.addVisualMappingFunction(colorMapping); // Add the mapping function to the visual style
							
				
				//	EDGES
				String columnEdgeType = "shared interaction";
				DiscreteMapping<String, LineType> edgeTypeMapping = (DiscreteMapping<String, LineType>) discreteMappingFactory
	                    .createVisualMappingFunction(columnEdgeType, String.class, et);
				
				
				Map<String, LineType> edgeTypeMap = getEdgeTypeMap();
				for (Map.Entry<String, LineType> entry : edgeTypeMap.entrySet()) {
					edgeTypeMapping.putMapValue(entry.getKey(), entry.getValue());
				}
				style.addVisualMappingFunction(edgeTypeMapping);

				
				
				// Get the current network
				//CyNetwork currentNetwork = ((MGGManager) networkManager).getCurrentNetwork();
				CyNetwork currentNetwork= manager.getCurrentNetwork();

				// Check if the network is not null
				if (currentNetwork != null) {
				    CyTable nodeTable = currentNetwork.getDefaultNodeTable();

				    // Get the network view for the current network
				    Collection<CyNetworkView> views = networkViewManager.getNetworkViews(currentNetwork);

				    // Apply visual styles if there are views associated with the network
				    if (views != null && !views.isEmpty()) {
				        CyNetworkView currentNetworkView = views.iterator().next();
				        style.apply(currentNetworkView);

				        vmmServiceRef.addVisualStyle(style);
				        vmmServiceRef.setCurrentVisualStyle(style);
				        currentNetworkView.updateView();
				    }

				    // Check if the node table does not contain the microbetag::cluster column
				    if (nodeTable.getColumn("microbetag::cluster") == null) {
				        // Apply force-directed layout 
				        if (views != null && !views.isEmpty()) {
				            CyNetworkView currentNetworkView = views.iterator().next();

				            CyLayoutAlgorithm layout = layoutAlgorithmManager.getLayout("force-directed");
				            if (layout == null) {
				                layout = layoutAlgorithmManager.getDefaultLayout();
				            }

				            TaskIterator layoutTask = layout.createTaskIterator(currentNetworkView, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, null);
				            TaskManager<?, ?> taskManager = manager.getService(TaskManager.class);
				            taskManager.execute(layoutTask);

				            currentNetworkView.updateView();
				        }
					} else {
				        // The manta::cluster column is present, do not apply force-directed layout
				       
				    }
				}
								        
			               
			     
			   // Create a continuous mapping for edge color based on the "weight" attribute
			   ContinuousMapping<Double, Paint> edgeColorMapping = (ContinuousMapping<Double, Paint>) 
					   manager.getService(VisualMappingFunctionFactory.class, "(mapping.type=continuous)")
			            .createVisualMappingFunction("microbetag::weight", 
			            		Double.class, 
			            		BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT
			            		);

			    // Define the points at which the color changes
			    BoundaryRangeValues<Paint> negativeRange = new BoundaryRangeValues<>(
					    new Color(219, 076, 119),
					    new Color(219, 076, 119),
					    new Color(219, 076, 119)
			    		); // for values from -1 to -0.01
			    BoundaryRangeValues<Paint> neutralRange = new BoundaryRangeValues<>(Color.BLACK, Color.BLACK, Color.BLACK); // for values from 0 to 0.3
			    BoundaryRangeValues<Paint> positiveRange = new BoundaryRangeValues<>(
			    		new Color(016,85,154), 
			    		new Color(016,85,154),
			    		new Color(016,85,154)
			    		); // for values from 0.3 to 1
			    
			    // Set the boundary points and associated colors
			    edgeColorMapping.addPoint(-1.0, negativeRange); // values from -1 to -0.01 are Red
			    edgeColorMapping.addPoint(-0.1, negativeRange); // this ensures that the Red continues until -0.01
			    edgeColorMapping.addPoint(-0.1, neutralRange); // values from 0 to 0.3 are Black
			    edgeColorMapping.addPoint(0.1, neutralRange); // this ensures that the Black continues until 0.3
			    edgeColorMapping.addPoint(0.1, positiveRange); // values from 0.3 to 1 are Green
			    edgeColorMapping.addPoint(1.0, positiveRange); // this ensures that the Green continues up to 1
							    
			    style.addVisualMappingFunction(edgeColorMapping);

	}

	 
	 
				private CyNode getNodeById(CyNetwork network, String nodeId) {
				    for (CyNode node : network.getNodeList()) {
				        String id = network.getRow(node).get(CyNetwork.NAME, String.class);
				        if (nodeId.equals(id)) {
				            return node;
				        }
				    }
				    return null;
				}
				
			

				private Map<String, LineType> getEdgeTypeMap(){
					Map<String, LineType> edgeTypeMap = new HashMap<>();
					edgeTypeMap.put("comp_coop", LineTypeVisualProperty.LONG_DASH);
					edgeTypeMap.put("cooccurrence/depletion", LineTypeVisualProperty.SOLID);
					return edgeTypeMap;
				}
				
				private Map<String, NodeShape> getTaxonomyShapeMap() {
					Map<String, NodeShape> taxonomyShapeMap = new HashMap<>();
					taxonomyShapeMap.put("genus", NodeShapeVisualProperty.ELLIPSE);
					taxonomyShapeMap.put("family", NodeShapeVisualProperty.ELLIPSE);
					taxonomyShapeMap.put("mspecies", NodeShapeVisualProperty.ELLIPSE);
					taxonomyShapeMap.put("null", NodeShapeVisualProperty.ELLIPSE);
					taxonomyShapeMap.put("species", NodeShapeVisualProperty.ELLIPSE);
					taxonomyShapeMap.put("envVar", NodeShapeVisualProperty.HEXAGON);
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
