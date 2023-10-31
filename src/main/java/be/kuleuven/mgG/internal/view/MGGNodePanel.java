package be.kuleuven.mgG.internal.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.utils.Mutils;






public class MGGNodePanel extends AbstractMggPanel {

	private JCheckBox highlightBox;
	private boolean updating = false;
	private JPanel nodesPanel = null;
	private Color defaultBackground;
	
	
	public MGGNodePanel(final MGGManager manager) {
		
		super(manager);
		filters.get(currentNetwork).put("tissue", new HashMap<>());
		filters.get(currentNetwork).put("compartment", new HashMap<>());
		init();
		revalidate();
		repaint();
		
		
	}
	
	
	public void updateControls() {
		updating = true;
		

		// TODO: fix me
		highlightBox.setSelected(manager.highlightNeighbors());
		//if (!manager.showGlassBallEffect())
			//showStructure.setEnabled(false);
		//else
			//showStructure.setEnabled(true);
		updating = false;
	}
	
	private void init() {
		setLayout(new GridBagLayout());

		EasyGBC c = new EasyGBC();

		JPanel controlPanel = createControlPanel();
		controlPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
		add(controlPanel, c.anchor("west").down().noExpand());

		JPanel mainPanel = new JPanel();
		{
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.setBackground(defaultBackground);
			EasyGBC d = new EasyGBC();
			
			mainPanel.add(createNodesPanel(), d.down().anchor("west").expandHoriz());
			mainPanel.add(new JLabel(""), d.down().anchor("west").expandBoth());
		}
		JScrollPane scrollPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		                                         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(scrollPane, c.down().anchor("west").expandBoth());
	}
	
	
	
	private JPanel createControlPanel() {
		JPanel controlPanel = new JPanel();
		EasyGBC d = new EasyGBC();
		controlPanel.setLayout(new GridBagLayout());

		EasyGBC upperGBC = new EasyGBC();
		JPanel upperPanel = new JPanel(new GridBagLayout());
		
		
		{
			highlightBox = new JCheckBox("Highlight first neighbors");
			highlightBox.setFont(labelFont);
			highlightBox.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							manager.setHighlightNeighbors(true);
							doHighlight(manager.getCurrentNetworkView());
						} else {
							manager.setHighlightNeighbors(false);
							clearHighlight(manager.getCurrentNetworkView());
						}
					}
			});
			// highlightBox.setAlignmentX( Component.LEFT_ALIGNMENT );
			// highlightBox.setBorder(BorderFactory.createEmptyBorder(10,2,10,0));
			upperPanel.add(highlightBox, upperGBC.right().insets(0,10,0,0).noExpand());
		}
		
		upperPanel.setBorder(BorderFactory.createEmptyBorder(5,0,10,0));

		controlPanel.add(upperPanel, d.anchor("northwest").expandHoriz());
		
		updateControls();
		// TODO: change max size when more buttons get added?
		controlPanel.setMaximumSize(new Dimension(300,100));
		controlPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		return controlPanel;
	}
	
	public void selectedNodes(Collection<CyNode> nodes) {
		// Clear the nodes panel
		nodesPanel.removeAll();
		EasyGBC c = new EasyGBC();
		Mutils.clearHighlight(manager, manager.getCurrentNetworkView());

		for (CyNode node: nodes) {
			JPanel newPanel = createNodePanel(node);
			newPanel.setAlignmentX( Component.LEFT_ALIGNMENT );

			nodesPanel.add(newPanel, c.anchor("west").down().expandHoriz());
		}

		if(manager.highlightNeighbors()) {
			doHighlight(manager.getCurrentNetworkView());
		} else {
			clearHighlight(manager.getCurrentNetworkView());
		}
		revalidate();
		repaint();
	}
	
	private void doHighlight(CyNetworkView networkView) {

		if (networkView != null) {
			List<CyNode> nodes = CyTableUtil.getNodesInState(networkView.getModel(), CyNetwork.SELECTED, Boolean.TRUE);
			if (nodes == null || nodes.size() == 0) {
				return;
			}

			Mutils.clearHighlight(manager, networkView);
			Mutils.highlight(manager, networkView, nodes);
		}
	}

	private void clearHighlight(CyNetworkView networkView) {
		Mutils.clearHighlight(manager, networkView);
	}
	
	
	
	
	private JPanel createNodesPanel() {
		nodesPanel = new JPanel();
		nodesPanel.setLayout(new GridBagLayout());
		EasyGBC c = new EasyGBC();

		if (currentNetwork != null) {
			List<CyNode> nodes = CyTableUtil.getNodesInState(currentNetwork, CyNetwork.SELECTED, true);
			for (CyNode node: nodes) {
				JPanel newPanel = createNodePanel(node);
				newPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
	
				nodesPanel.add(newPanel, c.anchor("west").down().expandHoriz());
			}
		}
		nodesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		CollapsablePanel collapsablePanel = new CollapsablePanel(iconFont, "Selected nodes", nodesPanel, false, 10);
		collapsablePanel.setBorder(BorderFactory.createEtchedBorder());
		return collapsablePanel;
	}



	private void updateNodesPanel() {
		if (nodesPanel == null) return;
		nodesPanel.removeAll();
		EasyGBC c = new EasyGBC();

		List<CyNode> nodes = CyTableUtil.getNodesInState(currentNetwork, CyNetwork.SELECTED, true);
		// TODO: test if this improves performance with large networks!
		if (nodes.size() > 50) {
			return;
		}
		for (CyNode node: nodes) {
			JPanel newPanel = createNodePanel(node);
			newPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
			nodesPanel.add(newPanel, c.anchor("west").down().expandHoriz());
		}
		return ;
	}
	
	
	
	
	private JPanel createNodePanel(CyNode node) {
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0; // This makes sure components use the full horizontal space
		
		// Set default constraints
		gbc.gridx = 0;  // Column 0
		gbc.gridy = 0;  // Start from row 0
		gbc.anchor = GridBagConstraints.WEST;  // Left-align components
		gbc.insets = new Insets(5, 5, 5, 5);  // 5-pixel margins

		// Obtain the current network
		CyNetwork currentNetwork = manager.getCurrentNetwork();
		if (currentNetwork == null) return panel;

		// Get the node table for the current network
		CyTable nodeTable = currentNetwork.getDefaultNodeTable();
		//String name = null;


		
		Object idValue = (nodeTable.getColumn("@id") != null) ? nodeTable.getRow(node.getSUID()).get("@id", nodeTable.getColumn("@id").getType()) : null;
		JTextArea idArea = new JTextArea("ID: " + (idValue != null ? idValue.toString() : "null"));
		setJTextAreaAttributes(idArea);
		panel.add(idArea, gbc);
		gbc.gridy++;

		Object taxonValue = (nodeTable.getColumn("microbetag::taxon name") != null) ? nodeTable.getRow(node.getSUID()).get("microbetag::taxon name", nodeTable.getColumn("microbetag::taxon name").getType()) : null;
		JTextArea taxonArea = new JTextArea("Taxon Name: " + (taxonValue != null ? taxonValue.toString() : "null"));
		setJTextAreaAttributes(taxonArea);
		panel.add(taxonArea, gbc);
		gbc.gridy++;

		Object taxonomyValue = (nodeTable.getColumn("microbetag::ncbi-tax-level") != null) ? nodeTable.getRow(node.getSUID()).get("microbetag::taxonomy", nodeTable.getColumn("microbetag::taxonomy").getType()) : null;
		JTextArea taxonomyArea = new JTextArea("Ncbi-tax-level: " + (taxonomyValue != null ? taxonomyValue.toString() : "null"));
		setJTextAreaAttributes(taxonomyArea);
		panel.add(taxonomyArea, gbc);
		gbc.gridy++;
		
		

		String[] attributes = {"microbetag::gtdb-genomes", "microbetag::ncbi-tax-id", "microbetag:: ncbi-tax-level"};
		for (String attribute : attributes) {
		    if (nodeTable.getColumn(attribute) != null) {
		        Object attrValue = nodeTable.getRow(node.getSUID()).get(attribute, nodeTable.getColumn(attribute).getType());
		        
		        // Extract the attribute name without the namespace
		        String attributeName = attribute.split("::")[1];
		        
		        JTextArea attributeArea = new JTextArea(attributeName + ": " + attrValue);
		        setJTextAreaAttributes(attributeArea);
		        panel.add(attributeArea, gbc);
		        gbc.gridy++;
		    }
		}
		
		
		
		// -------------------------------------------------------For the nested phenDB CollapsablePanel------------------------------------------------------------------
		JPanel phenDBPanel = new JPanel(new BorderLayout()); // Change layout manager

		// 1. Create a model for the table
		DefaultTableModel model = new DefaultTableModel() {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		        return false; // This will make all cells uneditable
		    }
		};
		model.addColumn("Feature");
		model.addColumn("Present");
		model.addColumn("Score");

		// 2. Instantiate the table with the model
		JTable table = new JTable(model);

		// Adjust column widths
		TableColumnModel columnModel = table.getColumnModel();
		// Modify the widths as per your preference
		columnModel.getColumn(0).setPreferredWidth(5);  // Feature column
		columnModel.getColumn(1).setPreferredWidth(5);  // Present column
		columnModel.getColumn(2).setPreferredWidth(5);  // Score column
		
			

		
		for (CyColumn column : nodeTable.getColumns()) {
		    String columnName = column.getName();
		    
		    // Check if column starts with "phendb::" and does NOT have "Score" as its namespace
		    if (columnName.startsWith("phendb::") && !columnName.contains("phendbScore::")) {
		        
		        // Extract feature name
		        String feature = columnName.replace("phendb::", "");
		        
		        // Check if corresponding "Score" column exists under "phendbScore::" namespace
		        CyColumn scoreColumn = nodeTable.getColumn("phendbScore::" + feature + "Score");
		        if (scoreColumn != null) {
		            
		            // Get "true/false" value
		            Object presentObj = nodeTable.getRow(node.getSUID()).get(columnName, column.getType());
		            String presentValue = (presentObj == null) ? "null" : presentObj.toString();
		            
		            // Get score value
		            Object scoreObj = nodeTable.getRow(node.getSUID()).get(scoreColumn.getName(), scoreColumn.getType());
		            String scoreValue = (scoreObj == null) ? "null" : scoreObj.toString();
		            
		            // Format the score value if it's a float/double to reduce the precision for better display
		            try {
		                double scoreAsDouble = Double.parseDouble(scoreValue);
		                scoreValue = String.format("%.2f", scoreAsDouble);  // 2 decimal places
		            } catch(NumberFormatException e) {
		                // If scoreValue is not a number, leave it as is
		            }
		            
		            // Add values to the table model
		            model.addRow(new Object[]{feature, presentValue, scoreValue});
		        }
		    }
		}

		// Add table to a scroll pane:
		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.setPreferredSize(new Dimension(panel.getWidth() - 20, 200));
		phenDBPanel.add(tableScrollPane, BorderLayout.CENTER);

		// 3. Ensure the main panel revalidates and repaints to reflect these changes:
		phenDBPanel.revalidate();
		phenDBPanel.repaint();

		// 2. Wrap the phenDBPanel inside a CollapsablePanel:

		CollapsablePanel phenDBCollapsablePanel = new CollapsablePanel(iconFont, "phenDB attributes", phenDBPanel, false, 10);
		Border etchedBorder = BorderFactory.createEtchedBorder();
		Border emptyBorder = BorderFactory.createEmptyBorder(0,5,0,0);
		phenDBCollapsablePanel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, etchedBorder));

		// 3. Add the phenDBCollapsablePanel to the main panel:

		panel.add(phenDBCollapsablePanel, gbc);
		gbc.gridy++;
		

	//--------------------------------------------------------------------------------------------------------------------------------------------
		
//-----------------------------------------------------------------------panelforfaprotax--------------------------------------------------------
		
		// Create a new JPanel for the faprotax. attributes
		JPanel faprotaxPanel = new JPanel(new GridBagLayout());
		GridBagConstraints fapGBC = new GridBagConstraints();
		fapGBC.fill = GridBagConstraints.HORIZONTAL;
		fapGBC.weightx = 1.0;
		fapGBC.gridx = 0;
		fapGBC.gridy = 0;
		fapGBC.anchor = GridBagConstraints.WEST;
		fapGBC.insets = new Insets(5, 5, 5, 5);

		// Loop through the nodeTable columns and find the ones starting with faprotax::
		for (CyColumn column : nodeTable.getColumns()) {
		    String columnName = column.getName();
		    if (columnName.startsWith("faprotax::")) {
		        Object attrValue = nodeTable.getRow(node.getSUID()).get(columnName, column.getType());
		        if (attrValue != null && attrValue instanceof Boolean) { // Ensure the value is Boolean
		            // Split the column name at "::" and take the second part as the display name
		            String displayName = columnName.split("::")[1];
		            
		            JCheckBox checkBox = new JCheckBox(displayName);  
		            checkBox.setSelected((Boolean) attrValue);
		            checkBox.setEnabled(false);  // Make it non-clickable
		            faprotaxPanel.add(checkBox, fapGBC);
		            fapGBC.gridy++;
		        }
		    }
		}

		// Wrap the faprotaxPanel inside a CollapsablePanel
		CollapsablePanel faprotaxCollapsablePanel = new CollapsablePanel(iconFont, "Faprotax Attributes", faprotaxPanel, false, 10);
		faprotaxCollapsablePanel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, etchedBorder));

		// Add the faprotaxCollapsablePanel to the main panel
		panel.add(faprotaxCollapsablePanel, gbc);
		gbc.gridy++;
		
		//--------------------------------------------------------------------------------------------------------------------------------
		
//		if (name == null) {
//		    name = "Selected Nodes";
//		}
		
		// Ensure the idValue is appropriately set to a string
		String nodeId = (idValue != null) ? idValue.toString() : "Selected Nodes";


		CollapsablePanel collapsablePanel = new CollapsablePanel(iconFont, nodeId, panel, false, 10);
		//Border etchedBorder = BorderFactory.createEtchedBorder();
		//Border emptyBorder = BorderFactory.createEmptyBorder(0,5,0,0);
		collapsablePanel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, etchedBorder));

		return collapsablePanel;
//		
	}
	
	private void setJTextAreaAttributes(JTextArea textArea) {
		 	textArea.setWrapStyleWord(true);
		    textArea.setLineWrap(true);
		    textArea.setEditable(false);
		    textArea.setFont(new Font("Arial", Font.PLAIN, 10));
		    textArea.setOpaque(false);
		    textArea.setBorder(null);
		    textArea.setPreferredSize(new Dimension(800, 15));
	}
	
	public void networkChanged(CyNetwork newNetwork) {
		this.currentNetwork = newNetwork;
		if (currentNetwork == null) {
			// Hide results panel?
			
			return;
		}

		

		// We need to get the view for the new network since we haven't actually switched yet
		CyNetworkView networkView = Mutils.getNetworkView(manager, currentNetwork);
		if (networkView != null) {
			if (manager.highlightNeighbors()) {
				doHighlight(networkView);
			} else {
				clearHighlight(networkView);
			}
	
		}
		updateNodesPanel();
	}
	
	
	@Override
	void doFilter(String type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void undoFilters() {
		CyNetworkView view = manager.getCurrentNetworkView();
		if (view != null) {
			for (View<CyNode> node: view.getNodeViews()) {
				node.clearValueLock(BasicVisualLexicon.NODE_VISIBLE);
			}
		}
		
	}

	@Override
	double initFilter(String type, String text) {
		// TODO Auto-generated method stub
		return 0;
	}

}
