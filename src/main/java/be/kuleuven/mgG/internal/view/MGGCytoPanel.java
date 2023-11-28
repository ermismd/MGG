package be.kuleuven.mgG.internal.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.SelectedNodesAndEdgesEvent;
import org.cytoscape.model.events.SelectedNodesAndEdgesListener;
import org.cytoscape.util.swing.TextIcon;

import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.utils.Mutils;


public class MGGCytoPanel extends JPanel
		implements CytoPanelComponent2, SetCurrentNetworkListener, SelectedNodesAndEdgesListener {

	final MGGManager manager;

	// Define new colors
	public static final Color[] MY_COLORS = new Color[] { Color.BLACK, Color.RED, Color.BLUE, Color.YELLOW };
	// Create a Font object
	private static final Font myFont = new Font("Arial", Font.PLAIN, 16);

	private JTabbedPane tabs;
	private MGGNodePanel  nodePanel;
	private MGGEdgePanel edgePanel;
	private boolean registered = false;
	private static final Icon icon = new TextIcon(new String[] { "MGG" }, new Font[] { myFont }, MY_COLORS, 16, 16);

	public MGGCytoPanel(final MGGManager manager) {
		this.manager = manager;
		this.setLayout(new BorderLayout());
		tabs = new JTabbedPane(JTabbedPane.BOTTOM);
		nodePanel = new MGGNodePanel(manager);
		tabs.add("Nodes", nodePanel);
		edgePanel = new MGGEdgePanel(manager);
		tabs.add("Edges", edgePanel);
		this.add(tabs, BorderLayout.CENTER);
		manager.setCytoPanel(this);
		manager.registerService(this, SetCurrentNetworkListener.class, new Properties());
		manager.registerService(this, SelectedNodesAndEdgesListener.class, new Properties());
		registered = true;
		revalidate();
		repaint();
	}

	public void showCytoPanel() {
		// System.out.println("show panel");
		CySwingApplication swingApplication = manager.getService(CySwingApplication.class);
		CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.EAST);
		if (!registered) {
			manager.registerService(this, CytoPanelComponent.class, new Properties());
			registered = true;
		}
		if (cytoPanel.getState() == CytoPanelState.HIDE)
			cytoPanel.setState(CytoPanelState.DOCK);

		// Tell tabs
		 nodePanel.networkChanged(manager.getCurrentNetwork());
		edgePanel.networkChanged(manager.getCurrentNetwork());
	}

	public void reinitCytoPanel() {
		CySwingApplication swingApplication = manager.getService(CySwingApplication.class);
		CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.EAST);
		if (!registered) {
			manager.registerService(this, CytoPanelComponent.class, new Properties());
			registered = true;
		}
		if (cytoPanel.getState() == CytoPanelState.HIDE)
			cytoPanel.setState(CytoPanelState.DOCK);

		// Tell tabs & remove/undo filters
		CyNetwork current = manager.getCurrentNetwork();
		nodePanel.removeFilters(current);
		nodePanel.undoFilters();
		nodePanel.networkChanged(current);
		edgePanel.removeFilters(current);
		edgePanel.undoFilters();
		edgePanel.networkChanged(current);
	}

	public void hideCytoPanel() {
		manager.unregisterService(this, CytoPanelComponent.class);
		registered = false;
	}

	public String getIdentifier() {
		return "be.kuleuven.mgG.internal.MGG";
	}

	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

	public CytoPanelName getCytoPanelName() {
		// TODO Auto-generated method stub
		return CytoPanelName.EAST;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getTitle() {
		return "MGG";
	}

	public void updateControls() {
		nodePanel.updateControls();
		edgePanel.updateWeightPanelPanel();
		
		edgePanel.updateSeedPanel();
	}

	@Override
	public void handleEvent(SelectedNodesAndEdgesEvent event) {
		if (!registered)
			return;
		// Pass selected nodes to nodeTab
		 nodePanel.selectedNodes(event.getSelectedNodes());
		// Pass selected edges to edgeTab
		edgePanel.selectedEdges(event.getSelectedEdges());
	}

	@Override
	public void handleEvent(SetCurrentNetworkEvent event) {
		CyNetwork network = event.getNetwork();

		
		if (Mutils.isMGGNetworkMicrobetagDB(network)) {
			if (!registered) {
				showCytoPanel();
			}

			// Tell tabs
		   nodePanel.networkChanged(network);
		   edgePanel.networkChanged(network);
			
		} else {
			hideCytoPanel();
		}
	}
		
	

	}

