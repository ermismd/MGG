package be.kuleuven.mgG.internal.tasks;

import java.awt.Color;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cytoscape.application.swing.AbstractCyAction;
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
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.NodeShape;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

import be.kuleuven.mgG.internal.model.MGGManager;

public class CreateMGGVisualStyle extends AbstractCyAction {

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
	
	
	
	public CreateMGGVisualStyle(MGGManager mggManager){
		super("Create MGG Visual Style");
		this.mggManager = mggManager;
		
		setPreferredMenu("Apps.MGG.Create MGG Visual Style");
		setMenuGravity(4);
		useCheckBoxMenuItem = true;
		insertSeparatorBefore = false;
		
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
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {

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

				VisualStyle style = visualStyleFactory.createVisualStyle("MGG Visual Style");
				this.vmmServiceRef.addVisualStyle(style);
				
				//String columnName = "taxonomy-level";

				// VisualProperty for node fill color
				VisualProperty<?> vp = BasicVisualLexicon.NODE_FILL_COLOR;

				style.setDefaultValue(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.ELLIPSE);

				// Node Borders
				style.setDefaultValue(BasicVisualLexicon.NODE_BORDER_WIDTH, 2.0);
				style.setDefaultValue(BasicVisualLexicon.NODE_BORDER_PAINT, Color.DARK_GRAY);
				style.setDefaultValue(BasicVisualLexicon.EDGE_WIDTH, 2.0); // Set default edge width
				
				// Node shape mapping based on "taxonomy-level"
		        String columnName = "taxonomy-level";
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
				DiscreteMapping<String, Paint> colorMapping= (DiscreteMapping<String, Paint>) discreteMappingFactory
																			.createVisualMappingFunction(columnName, String.class, vp);
				Map<String, Paint> speciesColorMap = getSpeciesColorMap();
				for (Map.Entry<String, Paint> entry : speciesColorMap.entrySet()) {
					colorMapping.putMapValue(entry.getKey(), entry.getValue());
				}
				style.addVisualMappingFunction(colorMapping); // Add the mapping function to the visual style
				
				// Get the current network
				//CyNetwork currentNetwork = ((MGGManager) networkManager).getCurrentNetwork();
				CyNetwork currentNetwork= mggManager.getCurrentNetwork();

				// Check if the network is not null
				if (currentNetwork != null) {
				    // Get the network view for the current network
				    Collection<CyNetworkView> views = networkViewManager.getNetworkViews(currentNetwork);

				    // Check if there are views associated with the network
				    if (views != null && !views.isEmpty()) {
				        // Use the first view (most networks will only have one view)
				        CyNetworkView currentNetworkView = views.iterator().next();
				    
				        // Apply the visual style to the current network view
				        style.apply(currentNetworkView);

				        vmmServiceRef.addVisualStyle(style);
				        vmmServiceRef.setCurrentVisualStyle(style);
				        currentNetworkView.updateView();
				
				    	// layout ( "force-directed")
						CyLayoutAlgorithm layout = layoutAlgorithmManager.getLayout("force-directed");
						if (layout == null) {
							// layout is not found, use the default layout
							layout = layoutAlgorithmManager.getDefaultLayout();
						}
					// Create a TaskIterator to apply the layout
						TaskIterator layoutTask = layout.createTaskIterator(currentNetworkView, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, null);

					// Execute the TaskIterator using a TaskManager
						TaskManager<?, ?> taskManager = mggManager.getService(TaskManager.class);
						taskManager.execute(layoutTask);
						
						// After layout application, update the view to reflect the changes
					    currentNetworkView.updateView();
					    		
					    
			}	
				        
				   
			           
			               
			     
			// Create a continuous mapping for edge color based on the "weight" attribute
			   ContinuousMapping<Double, Paint> edgeColorMapping = (ContinuousMapping<Double, Paint>) mggManager.getService(VisualMappingFunctionFactory.class, "(mapping.type=continuous)")
			            .createVisualMappingFunction("weight", Double.class, BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT);

			 /// Define the points at which the color changes
			    BoundaryRangeValues<Paint> negativeRange = new BoundaryRangeValues<>(Color.PINK, Color.PINK, Color.PINK); // for values from -1 to -0.01
			    BoundaryRangeValues<Paint> neutralRange = new BoundaryRangeValues<>(Color.BLACK, Color.BLACK, Color.BLACK); // for values from 0 to 0.3
			    BoundaryRangeValues<Paint> positiveRange = new BoundaryRangeValues<>(Color.GREEN, Color.GREEN, Color.GREEN); // for values from 0.3 to 1

			    // Set the boundary points and associated colors
			    edgeColorMapping.addPoint(-1.0, negativeRange); // values from -1 to -0.01 are Red
			    edgeColorMapping.addPoint(-0.01, negativeRange); // this ensures that the Red continues until -0.01
			    edgeColorMapping.addPoint(0.0, neutralRange); // values from 0 to 0.3 are Black
			    edgeColorMapping.addPoint(0.3, neutralRange); // this ensures that the Black continues until 0.3
			    edgeColorMapping.addPoint(0.3, positiveRange); // values from 0.3 to 1 are Green
			    edgeColorMapping.addPoint(1.0, positiveRange); // this ensures that the Green continues up to 1
			    
			    // Add the mapping function to the visual style
			    style.addVisualMappingFunction(edgeColorMapping);        
				        
				    
	}
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