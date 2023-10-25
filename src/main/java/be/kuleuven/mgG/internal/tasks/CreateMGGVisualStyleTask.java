package be.kuleuven.mgG.internal.tasks;

import java.awt.Color;
import java.awt.Paint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.util.color.Palette;
import org.cytoscape.util.color.PaletteProvider;
import org.cytoscape.util.color.PaletteProviderManager;
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
		
		
		
		// ----------------------------------------------------------------------------------------------------

				// VISUAL STYLE

				CyNetwork currentNetwork= manager.getCurrentNetwork();
				CyNetworkView networkView= manager.getCurrentNetworkView();
		
				VisualStyle style = visualStyleFactory.createVisualStyle("MGG Visual Style");
				this.vmmServiceRef.addVisualStyle(style);
				
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
	

