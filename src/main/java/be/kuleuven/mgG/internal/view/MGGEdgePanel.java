package be.kuleuven.mgG.internal.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import be.kuleuven.mgG.internal.model.MGGManager;



public class MGGEdgePanel extends AbstractMggPanel {
	
	JButton fetchEdges;
	JPanel subScorePanel;
	JPanel scorePanel;
	JButton deleteEdges;
	private Map<CyNetwork, Map<String, Boolean>> colors;
	private JSlider scoreSlider;
	
	public MGGEdgePanel(final MGGManager manager) {
		super(manager);
		filters.get(currentNetwork).put("flashweave-score", new HashMap<>());

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
		
			JComponent scoreSlider = createFilterSlider("flashweave-score", "flashweave-score", currentNetwork, true, 100.0);
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
		{
			fetchEdges = new JButton("Fetch extra edges");
			fetchEdges.setToolTipText("Decrease the network score to the chosen .");
			fetchEdges.setFont(labelFont);
			fetchEdges.setEnabled(false);
			controlPanel.add(fetchEdges);
			fetchEdges.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (filters.containsKey(currentNetwork)
							&& filters.get(currentNetwork).containsKey("flashweave-score")
							&& filters.get(currentNetwork).get("flashweave-score").containsKey("flashweave-score")) {
						Map<String, Object> args = new HashMap<>();
						args.put("network", "current");
						args.put("flashweave-score", String.valueOf(filters.get(currentNetwork).get("flashweave-score").get("flashweave-score").doubleValue()));
						manager.executeCommand("string", "change flashweave-score", args, null);
						fetchEdges.setEnabled(false);
					}
				}
			});
		}
		{
			deleteEdges = new JButton("Delete hidden edges");
			deleteEdges.setToolTipText("Increase the network confidence to the chosen score.");
			deleteEdges.setFont(labelFont);
			deleteEdges.setEnabled(false);
			controlPanel.add(deleteEdges);
			deleteEdges.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//ChangeConfidenceTaskFactory tf = new ChangeConfidenceTaskFactory(manager);
					if (filters.containsKey(currentNetwork)
							&& filters.get(currentNetwork).containsKey("flashweave-score")
							&& filters.get(currentNetwork).get("flashweave-score").containsKey("flashweave-score")) {
						Map<String, Object> args = new HashMap<>();
						args.put("network", "current");
						args.put("flashweave-score", String.valueOf(filters.get(currentNetwork).get("flashweave-score").get("flashweave-score").doubleValue()));
						manager.executeCommand("string", "change flashweave-score", args, null);
						deleteEdges.setEnabled(false);
					}
				}
			});
		}
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
	void doFilter(String type) {
		 if ("flashweave-score".equals(type)) {
		        CyNetworkView networkView = manager.getCurrentNetworkView();
		        if (networkView != null) {
		            CyNetwork network = networkView.getModel();
		            double minScore = (double) scoreSlider.getValue();

		            for (CyEdge edge : network.getEdgeList()) {
		                View<CyEdge> edgeView = networkView.getEdgeView(edge);

		                if (edgeView != null) {
		                    Double edgeScore = network.getRow(edge).get("flashweave-score", Double.class);

		                    if (edgeScore != null && edgeScore < minScore) {
		                        edgeView.setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, false);
		                    } else {
		                        edgeView.setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, true);
		                    }
		                }
		            }

		            networkView.updateView();
		        }
		    }
		
	}

	@Override
	double initFilter(String type, String text) {
		  if ("flashweave-score".equals(type)) {
		        try {
		            double initialScore = Double.parseDouble(text);
		            scoreSlider.setValue((int) initialScore);
		            doFilter("flashweave-score");
		            return initialScore;
		        } catch (NumberFormatException e) {
		            // Handle invalid input
		        }
		    }
		    return 0;
		   }
	

	public void updateScore() {
		scorePanel.removeAll();
		EasyGBC d = new EasyGBC();
		JComponent scoreSlider = createFilterSlider("flashweave-score", "flashweave-score", currentNetwork, true, 100.0);
		scorePanel.add(scoreSlider, d.anchor("northwest").expandHoriz());

		JPanel controlPanel = createControlPanel();
		controlPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
		scorePanel.add(controlPanel, d.anchor("west").down().noExpand());

	}

	public void networkChanged(CyNetwork newNetwork) {
		this.currentNetwork = newNetwork;
		if (!filters.containsKey(currentNetwork)) {
			filters.put(currentNetwork, new HashMap<>());
			filters.get(currentNetwork).put("flashweave-score", new HashMap<>());
		}
		if (!colors.containsKey(currentNetwork)) {
			colors.put(currentNetwork, new HashMap<>());
		}

		
		updateScore();
	}

	public void selectedEdges(Collection<CyEdge> edges) {
	}

}
