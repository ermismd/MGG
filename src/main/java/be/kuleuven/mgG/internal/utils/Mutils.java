package be.kuleuven.mgG.internal.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.customgraphics.CyCustomGraphics;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import be.kuleuven.mgG.internal.model.MGGManager;




public class Mutils {

	// Namespaces
		public static String  MY_NAMESPACE = "MGGid";
		public static String MY_ATTRIBUTE = "id";
		
	public static boolean isMGGNetwork(CyNetwork network) {
		CyTable nodeTable = network.getDefaultNodeTable();
		if (nodeTable.getColumn("id") == null)
			return false;
		// Enough to check for id in the node columns and score in the edge columns
		//if (nodeTable.getColumn(SPECIES) == null)
		//	return false;
		//if (nodeTable.getColumn(CANONICAL) == null)
		//	return false;
		CyTable edgeTable = network.getDefaultEdgeTable();
		if (edgeTable.getColumn("weight") == null )
			return false;
		return true;
	}

	// This method will tell us if we have the new side panel functionality (i.e. namespaces)
		public static boolean ifHaveMGG(CyNetwork network) {
			if (network == null) return false;
			CyRow netRow = network.getRow(network);
			Collection<CyColumn> columns = network.getDefaultNodeTable().getColumns("id");
			if ( columns != null && columns.size() > 0)
				return true;
			return false;
		}
	
	
		public static void clearHighlight(MGGManager manager, CyNetworkView view) {
			// if (node == null) return;
			// View<CyNode> nodeView = view.getNodeView(node);
			if (view == null) return;

			VisualLexicon lex = manager.getService(RenderingEngineManager.class).getDefaultVisualLexicon();
			VisualProperty customGraphics1 = lex.lookup(CyNode.class, "NODE_CUSTOMGRAPHICS_1");
			VisualProperty customGraphics2 = lex.lookup(CyNode.class, "NODE_CUSTOMGRAPHICS_2");
			VisualProperty customGraphics3 = lex.lookup(CyNode.class, "NODE_CUSTOMGRAPHICS_3");

			for (View<CyNode> nv: view.getNodeViews()) {
				nv.clearValueLock(customGraphics1);
				nv.clearValueLock(customGraphics2);
				nv.clearValueLock(customGraphics3);
				nv.clearValueLock(BasicVisualLexicon.NODE_TRANSPARENCY);
			}

			for (View<CyEdge> ev: view.getEdgeViews()) {
				ev.clearValueLock(BasicVisualLexicon.EDGE_TRANSPARENCY);
			}
		}

		
		public static void highlight(MGGManager manager, CyNetworkView view, List<CyNode> nodes) {
			CyNetwork net = view.getModel();

			List<CyEdge> edgeList = new ArrayList<CyEdge>();
			List<CyNode> nodeList = new ArrayList<CyNode>();
		 	for (CyNode node: nodes) {
				edgeList.addAll(net.getAdjacentEdgeList(node, CyEdge.Type.ANY));
				nodeList.addAll(net.getNeighborList(node, CyEdge.Type.ANY));
			}


			VisualLexicon lex = manager.getService(RenderingEngineManager.class).getDefaultVisualLexicon();
			VisualProperty customGraphics1 = lex.lookup(CyNode.class, "NODE_CUSTOMGRAPHICS_1");
			VisualProperty customGraphics2 = lex.lookup(CyNode.class, "NODE_CUSTOMGRAPHICS_2");
			VisualProperty customGraphics3 = lex.lookup(CyNode.class, "NODE_CUSTOMGRAPHICS_3");

			CyCustomGraphics cg = new EmptyCustomGraphics();

			// Override our current style through overrides
			for (View<CyNode> nv: view.getNodeViews()) {
				if (nodeList.contains(nv.getModel()) || nodes.contains(nv.getModel())) {
					nv.setLockedValue(BasicVisualLexicon.NODE_TRANSPARENCY, 255);
				} else {
					nv.setLockedValue(customGraphics1, cg);
					nv.setLockedValue(customGraphics2, cg);
					nv.setLockedValue(customGraphics3, cg);
					nv.setLockedValue(BasicVisualLexicon.NODE_TRANSPARENCY, 20);
				}
			}
			for (View<CyEdge> ev: view.getEdgeViews()) {
				if (edgeList.contains(ev.getModel())) {
					ev.setLockedValue(BasicVisualLexicon.EDGE_TRANSPARENCY, 255);
				} else {
					ev.setLockedValue(BasicVisualLexicon.EDGE_TRANSPARENCY, 20);
				}
			}
		}
		
		
		public static CyNetworkView getNetworkView(MGGManager manager, CyNetwork network) {
			Collection<CyNetworkView> views = 
							manager.getService(CyNetworkViewManager.class).getNetworkViews(network);

			// At some point, figure out a better way to do this
			for (CyNetworkView view: views) {
				return view;
			}
			return null;
		}
		
		
//	public static boolean isMGGNetwork(CyNetwork network) {
//		// This is a string network only if we have a confidence score in the network table,
//		// "@id" column in the node table, and a "score" column in the edge table
//		if (network == null || network.getRow(network).get(CONFIDENCE, Double.class) == null)
//			return false;
//		return isMergedStringNetwork(network);
//	}
	
	
	
}
