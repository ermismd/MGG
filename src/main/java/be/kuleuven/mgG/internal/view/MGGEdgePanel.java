	package be.kuleuven.mgG.internal.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import be.kuleuven.mgG.internal.model.MGGManager;







public class MGGEdgePanel extends AbstractMggPanel {
	
	JButton fetchEdges;
	JPanel subScorePanel;
	JPanel scorePanel;
	JButton deleteEdges;
	private JPanel edgesPanel = null;
	
	
	private Map<CyNetwork, Map<String, Boolean>> colors;
	private JSlider scoreSlider;
	
	public MGGEdgePanel(final MGGManager manager) {
		super(manager);
		filters.get(currentNetwork).put("weight", new HashMap<>());

		colors = new HashMap<>();
		colors.put(currentNetwork, new HashMap<>());

		init();
		revalidate();
		repaint();
   }
   
	private void init() {
		setLayout(new GridBagLayout());
		{
			EasyGBC c = new EasyGBC();
			
			
			JComponent scoreSlider = createFilterSlider("weight", "Weight", currentNetwork, true, 100.0);
			
			{
				scorePanel = new JPanel();
				scorePanel.setLayout(new GridBagLayout());
				EasyGBC d = new EasyGBC();
				scorePanel.add(scoreSlider, d.anchor("northwest").expandHoriz());
				
				JPanel controlPanel = createControlPanel();
				controlPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
				scorePanel.add(controlPanel, d.anchor("west").down().noExpand());				
			}
			add(scorePanel, c.down().anchor("west").expandHoriz());

			/*
			 * { subScorePanel = new JPanel(); subScorePanel.setLayout(new GridBagLayout());
			 * EasyGBC d = new EasyGBC(); subScorePanel.add(createSubScorePanel(),
			 * d.anchor("west").expandHoriz()); subScorePanel.add(new JPanel(),
			 * d.down().anchor("west").expandBoth()); }
			 * 
			 * JScrollPane scrollPane = new JScrollPane(subScorePanel,
			 * JScrollPane.VERTICAL_SCROLLBAR_NEVER,
			 * JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			 * 
			 * add(scrollPane, c.down().anchor("west").expandBoth()); // add(new JPanel(),
			 * c.down().anchor("west").expandBoth());
			 */		}
		
		
		
		
		
	}
	
	private JPanel createControlPanel() {
		JPanel controlPanel = new JPanel();
		GridLayout layout = new GridLayout(2,2);
		//layout.setVgap(0);
		controlPanel.setLayout(layout);
//		{
//			fetchEdges = new JButton("Fetch extra edges");
//			fetchEdges.setToolTipText("Decrease the network weight to the chosen .");
//			fetchEdges.setFont(labelFont);
//			fetchEdges.setEnabled(false);
//			controlPanel.add(fetchEdges);
//			fetchEdges.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					if (filters.containsKey(currentNetwork)
//							&& filters.get(currentNetwork).containsKey("weight")
//							&& filters.get(currentNetwork).get("weight").containsKey("Weight")) {
//						Map<String, Object> args = new HashMap<>();
//						args.put("network", "current");
//						args.put("weight", String.valueOf(filters.get(currentNetwork).get("weight").get("weight").doubleValue()));
//						manager.executeCommand("string", "change weight", args, null);
//						fetchEdges.setEnabled(false);
//					}
//				}
//			});
//		}
//		{
//			deleteEdges = new JButton("Delete hidden edges");
//			deleteEdges.setToolTipText("Increase the network weight to the chosen score.");
//			deleteEdges.setFont(labelFont);
//			deleteEdges.setEnabled(false);
//			controlPanel.add(deleteEdges);
//			deleteEdges.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					//ChangeConfidenceTaskFactory tf = new ChangeConfidenceTaskFactory(manager);
//					if (filters.containsKey(currentNetwork)
//							&& filters.get(currentNetwork).containsKey("weight")
//							&& filters.get(currentNetwork).get("weight").containsKey("weight")) {
//						Map<String, Object> args = new HashMap<>();
//						args.put("network", "current");
//						args.put("weight", String.valueOf(filters.get(currentNetwork).get("weight").get("weight").doubleValue()));
//						manager.executeCommand("string", "change weight", args, null);
//						deleteEdges.setEnabled(false);
//					}
//				}
//			});
	//	}
		controlPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		controlPanel.setMaximumSize(new Dimension(100,100));
		return controlPanel;
		}
	
	
	
	
	void undoFilters() {
		CyNetworkView view = manager.getCurrentNetworkView();
		if (view != null) {
			for (View<CyEdge> edge: view.getEdgeViews()) {
				edge.clearValueLock(BasicVisualLexicon.EDGE_VISIBLE);
			}
	
		}
}

	

	
	@Override
	double initFilter(String type, String label) {
		
	
		   double minValue = Double.MAX_VALUE; // Start with the highest value to ensure you find the lowest one available.
		    for (CyEdge edge : currentNetwork.getEdgeList()) {
		        CyRow edgeRow = currentNetwork.getRow(edge);
		        Double edgeScore = edgeRow.get("weight", Double.class); // Get the edge weight based on the type parameter.
		        if (edgeScore == null) 
		            continue;  // Skip if no weight is defined for this edge.
		        
		        if (edgeScore < minValue) {
		            minValue = edgeScore; // New minimum found, save it.
		        }
		    }
		    
		    // If no valid (non-null) weights were found, default to 1.0.
		    if (minValue == Double.MAX_VALUE) {
		        return 1.0;
		    }
		    
		    return minValue; // Return the smallest weight found.
		}
	
	
	
	
	
	@Override
	void doFilter(String type) {
		
		System.out.println("doFilter is called");
		System.out.println("Slider value: " + scoreSlider.getValue());
		
		   // Check if the network and filter type exists
		Map<String, Double> filter = filters.get(currentNetwork).get(type);
	        // This part may vary depending on how you manage your network views.
	        CyNetworkView view = manager.getCurrentNetworkView();

	        double weightThreshold = scoreSlider.getValue() / 100.0;
	       // double weightThreshold = filters.get(currentNetwork).get(type).get("weight");
	     
	            // Iterate through each edge in the current network.
	            for (CyEdge edge : currentNetwork.getEdgeList()) {
	                CyRow edgeRow = currentNetwork.getRow(edge);

	                // Retrieve the weight value of the edge.
	                Double edgeWeight = edgeRow.get("weight", Double.class);
	                System.out.println("Edge weight: " + edgeWeight);
	                if (edgeWeight == null) {
	                    continue; // If there's no weight value, skip this edge.
	                    
	                }

	                // Determine visibility of the edge based on its weight.
	                boolean isVisible = edgeWeight >= weightThreshold;

	                if (isVisible) {
	                	   System.out.println("Edge " + edge + " is set to visible");
	                    // If the edge meets the criteria, make it visible.
	                    view.getEdgeView(edge).clearValueLock(BasicVisualLexicon.EDGE_VISIBLE);
	                } else {
	                	System.out.println("Edge " + edge + " is hidden");
	                    // Otherwise, hide the edge and deselect it.
	                    view.getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, false);
	                    view.getModel().getRow(edge).set(CyNetwork.SELECTED, false);
	                }
	            }
	        }

	
	public void updateScore() {
		scorePanel.removeAll();
		EasyGBC d = new EasyGBC();
		JComponent scoreSlider = createFilterSlider("weight", "weight", currentNetwork, true, 100.0);
		scorePanel.add(scoreSlider, d.anchor("northwest").expandHoriz());

		JPanel controlPanel = createControlPanel();
		controlPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
		scorePanel.add(controlPanel, d.anchor("west").down().noExpand());

	}
	
	/*
	 * public void updateSubPanel() { subScorePanel.removeAll(); EasyGBC d = new
	 * EasyGBC(); subScorePanel.add(createSubScorePanel(),
	 * d.anchor("west").expandHoriz()); subScorePanel.add(new JPanel(),
	 * d.down().anchor("west").expandBoth()); }
	 */

	public void networkChanged(CyNetwork newNetwork) {
		this.currentNetwork = newNetwork;
		if (!filters.containsKey(currentNetwork)) {
			filters.put(currentNetwork, new HashMap<>());
			filters.get(currentNetwork).put("weight", new HashMap<>());
		}
		if (!colors.containsKey(currentNetwork)) {
			colors.put(currentNetwork, new HashMap<>());
		}

		
		updateScore();
	}

	public void selectedEdges(Collection<CyEdge> edges) {
	}

}
