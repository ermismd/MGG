	package be.kuleuven.mgG.internal.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import be.kuleuven.mgG.internal.model.MGGManager;

import be.kuleuven.mgG.internal.utils.Mutils;
import be.kuleuven.mgG.internal.utils.SwingLink;
import be.kuleuven.mgG.internal.utils.SwingLinkCellRenderer;








public class MGGEdgePanel extends AbstractMggPanel {

    JButton fetchEdges;
    JPanel subScorePanel;
    JPanel scorePanel;
    JButton deleteEdges;
    private JPanel WeightPanel = null;
    JPanel seedPanel;
    private Color defaultBackground;
    
    

    private JPanel edgesSPanel = null;


    private Map < CyNetwork, Map < String, Boolean >> colors;


    public MGGEdgePanel(final MGGManager manager) {

        super(manager);
        filters.get(currentNetwork).put("weight", new HashMap < > ());
        //filters.get(currentNetwork).put("seed", new HashMap<>());
        filters.get(currentNetwork).put(Mutils.Seed_NAMESPACE, new HashMap<>());
      
	
        colors = new HashMap < > ();
        colors.put(currentNetwork, new HashMap < > ());

        init();
        revalidate();
        repaint();
    }

    private void init() {
        setLayout(new GridBagLayout());

        EasyGBC c = new EasyGBC();

        JPanel controlPanel = createControlPanel();
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        add(controlPanel, c.anchor("west").down().noExpand());

        JPanel mainPanel = new JPanel(); {
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.setBackground(defaultBackground);
            EasyGBC d = new EasyGBC();
            mainPanel.add(createWeightPanel(), d.down().anchor("west").expandHoriz());
            mainPanel.add(createSeedPanel(), d.down().anchor("west").expandHoriz());
            mainPanel.add(createEdgesPanel(), d.down().anchor("west").expandHoriz());
            

            mainPanel.add(new JLabel(""), d.down().anchor("west").expandBoth());
        }
        JScrollPane scrollPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(scrollPane, c.down().anchor("west").expandBoth());
    }







    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        GridLayout layout = new GridLayout(2, 2);
        EasyGBC d = new EasyGBC();
        //layout.setVgap(0);
        controlPanel.setLayout(layout);
        
        
        JPanel upperPanel = new JPanel(new GridBagLayout());
        
     // Creating the checkbox for Compl edges
        JCheckBox showComplEdges = new JCheckBox("Show Edges with Complements");
        showComplEdges.setFont(labelFont);
        showComplEdges.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    doShowComplEdges(true); // Show edges with Compl values
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    doShowComplEdges(false); // Show all edges
                }
            }
        });
        upperPanel.add(showComplEdges);
        
        upperPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        controlPanel.add(upperPanel, d.anchor("northwest").expandHoriz());
        
       
        controlPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlPanel.setMaximumSize(new Dimension(100, 100));
        return controlPanel;
    }
    
    
    private void doShowComplEdges(boolean show) {
        CyNetworkView view = manager.getCurrentNetworkView();
        CyNetwork net = view.getModel();

        // Iterate over all edges
        for (CyEdge edge : net.getEdgeList()) {
            View<CyEdge> edgeView = view.getEdgeView(edge);
            if (edgeView == null) continue;

            boolean hasComplValue = false;
            for (CyColumn column : net.getDefaultEdgeTable().getColumns()) {
                if (column.getName().startsWith("compl::")) {
                    Object value = net.getRow(edge).get(column.getName(), column.getType());
                    if (value != null) {
                        hasComplValue = true;
                        break;
                    }
                }
            }

            if (show) {
                edgeView.setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, hasComplValue);
            } else {
                edgeView.setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, true);
            }
        }

         view.updateView(); // Uncomment if necessary to refresh the view
    }


    private JPanel createWeightPanel() {

        WeightPanel = new JPanel();
        WeightPanel.setLayout(new GridBagLayout());
        EasyGBC c = new EasyGBC();

        List < String > WeightList = Mutils.getWeightList(currentNetwork);

        for (String weight: WeightList) {
            WeightPanel.add(createFilterSlider("weight", weight, currentNetwork, true, 100.0),
                c.anchor("west").down().expandHoriz());
        }


        CollapsablePanel collapsablePanel = new CollapsablePanel(iconFont, "weight Filters", WeightPanel, false, 10);
        collapsablePanel.setToolTipText("Show edges with Weight bigger than the chosen value");
        collapsablePanel.setBorder(BorderFactory.createEtchedBorder());
        return collapsablePanel;
    }

    public void updateWeightPanelPanel() {
        if (WeightPanel == null) return;
        WeightPanel.removeAll();
        EasyGBC c = new EasyGBC();
        List < String > WeightList = Mutils.getWeightList(currentNetwork);
        for (String weight: WeightList) {
            WeightPanel.add(createFilterSlider("weight", weight, currentNetwork, true, 100.0),
                c.anchor("west").down().expandHoriz());
        }
        return;
    }

    //------------------------------------------------------------------------------------
    private JPanel createSeedPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		EasyGBC c = new EasyGBC();

		List<String> seedList = Mutils.getSeedList(currentNetwork);

		// OK, now we want to create 3 panels: Color, Label, and Filter
		{
			JPanel colorPanel = new JPanel();
			colorPanel.setMinimumSize(new Dimension(25,30));
			colorPanel.setLayout(new GridBagLayout());
			EasyGBC d = new EasyGBC();
			JLabel lbl = new JLabel("Color");
			lbl.setToolTipText("Color edges with this type seed score.");
			lbl.setFont(labelFont);
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			colorPanel.add(lbl, d.anchor("north").noExpand());

			for (String seedScore: seedList) {
				colorPanel.add(createScoreCheckBox(seedScore), d.down().expandVert());
			}

			//colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			panel.add(colorPanel, c.anchor("northwest").expandVert());
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new GridBagLayout());
			EasyGBC d = new EasyGBC();
			JLabel lbl = new JLabel("Seed Score");
			lbl.setFont(labelFont);
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			labelPanel.add(lbl, d.anchor("north").noExpand());
			for (String seedScore: seedList) {
				JLabel scoreLabel = new JLabel(seedScore);
				scoreLabel.setFont(textFont);
				scoreLabel.setMinimumSize(new Dimension(100,30));
				scoreLabel.setMaximumSize(new Dimension(100,30));
				labelPanel.add(scoreLabel, d.down().expandVert());
			}
			labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			panel.add(labelPanel, c.right().expandVert());
		}

		{
			JPanel filterPanel = new JPanel();
			filterPanel.setLayout(new GridBagLayout());
			EasyGBC d = new EasyGBC();
			JLabel lbl = new JLabel("Filters");
			lbl.setToolTipText("Hide edges with a confidence score below the chosen subscore.");
			lbl.setFont(labelFont);
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			filterPanel.add(lbl, d.anchor("north").noExpand());
			for (String seedScore: seedList) {
				JComponent scoreSlider = createFilterSlider3("Seed Score", seedScore, currentNetwork, false, 100.0);
				scoreSlider.setMinimumSize(new Dimension(100,30));
				// scoreSlider.setMaximumSize(new Dimension(100,30));
				filterPanel.add(scoreSlider, d.down().expandBoth());
			}
			//filterPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			panel.add(filterPanel, c.right().expandBoth());
		}

		CollapsablePanel collapsablePanel = new CollapsablePanel(iconFont, "Seed Scores", panel, false, 10);
		collapsablePanel.setBorder(BorderFactory.createEtchedBorder());
		return collapsablePanel;

	}
    public void updateSeedPanel() {
		seedPanel.removeAll();
		EasyGBC d = new EasyGBC();
		seedPanel.add(createSeedPanel(), d.anchor("west").expandHoriz());
		seedPanel.add(new JPanel(), d.down().anchor("west").expandBoth());
	}
    
    private JComponent createScoreCheckBox(String seedScore) {
		Map<String, Color> colorMap = manager.getChannelColors();
		JCheckBox cb = new JCheckBox("");
		cb.setMinimumSize(new Dimension(20,30));
		cb.setMaximumSize(new Dimension(20,30));
		cb.setBackground(colorMap.get(seedScore));
		cb.setOpaque(true);
		if (colors.containsKey(currentNetwork) && colors.get(currentNetwork).containsKey(seedScore) 
				&& colors.get(currentNetwork).get(seedScore))
			cb.setSelected(true);
		
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Boolean selected = Boolean.FALSE;
				if (e.getStateChange() == ItemEvent.SELECTED)
					selected = Boolean.TRUE;

				colors.get(currentNetwork).put(seedScore, selected);

				doColors();
			}
		});
		return cb;
	}
    
    void doColors() {
		Map<String, Boolean> color = colors.get(currentNetwork);
		Map<String, Color> colorMap = manager.getChannelColors();
		CyNetworkView view = manager.getCurrentNetworkView();
		for (CyEdge edge: currentNetwork.getEdgeList()) {
			CyRow edgeRow = currentNetwork.getRow(edge);
			double max = -1;
			Color clr = null;
			for (String lbl: color.keySet()) {
				if (!color.get(lbl))
					continue;
				Double v = edgeRow.get(Mutils.Seed_NAMESPACE, lbl, Double.class);
				if (v != null && v > max) {
					max = v;
					clr = colorMap.get(lbl);
				}
			}
			if (clr == null)
				view.getEdgeView(edge).clearValueLock(BasicVisualLexicon.EDGE_UNSELECTED_PAINT);
			else
				view.getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_UNSELECTED_PAINT, clr);
		}
	}
    //------------------------------------------------------------------------------------

    private JPanel createEdgesPanel() {
        edgesSPanel = new JPanel();
        edgesSPanel.setLayout(new GridBagLayout());
        EasyGBC c = new EasyGBC();

        if (currentNetwork != null) {
            List < CyEdge > edges = CyTableUtil.getEdgesInState(currentNetwork, CyNetwork.SELECTED, true);
            for (CyEdge edge: edges) {
                JPanel newPanel = createEdgePanel(edge);
                newPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                edgesSPanel.add(newPanel, c.anchor("west").down().expandHoriz());
            }
        }
        edgesSPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        CollapsablePanel collapsablePanel = new CollapsablePanel(iconFont, "Selected edges", edgesSPanel, false, 10);
        collapsablePanel.setBorder(BorderFactory.createEtchedBorder());
        return collapsablePanel;
    }




    private void updateEdgesPanel() {
        if (edgesSPanel == null) return;
        edgesSPanel.removeAll();
        EasyGBC c = new EasyGBC();

        List < CyEdge > edges = CyTableUtil.getEdgesInState(currentNetwork, CyNetwork.SELECTED, true);

        if (edges.size() > 50) {
            return;
        }
        for (CyEdge edge: edges) {
            JPanel newPanel = createEdgePanel(edge);
            newPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            edgesSPanel.add(newPanel, c.anchor("west").down().expandHoriz());
        }
        return;
    }

    
    
    // Helper method to get taxon name from node table
    private String getTaxonName(CyTable nodeTable, CyNode node) {
        if (nodeTable.getColumn("microbetag::taxon name") != null) {
            Object taxonValue = nodeTable.getRow(node.getSUID()).get("microbetag::taxon name", String.class);
            return taxonValue != null ? taxonValue.toString() : null;
        }
        return null;
    }
    
    private JPanel createEdgePanel(CyEdge edge) {

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // makes sure components use the full horizontal space

        // Set constraints
        gbc.gridx = 0; // Column 0
        gbc.gridy = 0; // Start from row 0
        gbc.anchor = GridBagConstraints.WEST; // Left-align 
        gbc.insets = new Insets(5, 5, 5, 5); // 5pixel marg

        EasyGBC c = new EasyGBC();

        CyNetwork currentNetwork = manager.getCurrentNetwork();
        if (currentNetwork == null) return panel;


        CyTable edgeTable = currentNetwork.getDefaultEdgeTable();
        CyTable nodeTable = currentNetwork.getDefaultNodeTable();
        //String name = null;
        
     // Retrieve source and target node of the edge
        CyNode sourceNode = edge.getSource();
        CyNode targetNode = edge.getTarget();
        
     // Get taxon names for source and target nodes
        String sourceTaxon = getTaxonName(nodeTable, sourceNode);
        String targetTaxon = getTaxonName(nodeTable, targetNode);
        
        
        JTextArea sourceTaxonArea = new JTextArea("Donor Taxon: " + sourceTaxon);
        setJTextAreaAttributes(sourceTaxonArea);
        panel.add(sourceTaxonArea, gbc);
        gbc.gridy++;
    
        JTextArea targetTaxonArea = new JTextArea("Beneficiary Taxon: " + targetTaxon);
        setJTextAreaAttributes(targetTaxonArea);
        panel.add(targetTaxonArea, gbc);
        gbc.gridy++;

        Object nameValue = (edgeTable.getColumn("shared name") != null) ? edgeTable.getRow(edge.getSUID()).get("shared name", edgeTable.getColumn("shared name").getType()) : null;
       // JTextArea nameArea = new JTextArea("Edge Name: " + (nameValue != null ? nameValue.toString() : "null"));
      //  setJTextAreaAttributes(nameArea);
       // panel.add(nameArea, gbc);
        //gbc.gridy++;

        // Split the name to get Donor and Beneficiary
        String[] nameParts = nameValue != null ? nameValue.toString().split(" \\(completes/competes with\\) | \\(cooccurss with\\) ") : new String[] {
            "",
            ""
        };
        
        String donor = nameParts.length > 0 ? nameParts[0] : "";
        String beneficiary = nameParts.length > 1 ? nameParts[1] : "";


        Object interactionValue = (edgeTable.getColumn("interaction type") != null) ? edgeTable.getRow(edge.getSUID()).get("interaction type", edgeTable.getColumn("interaction type").getType()) : null;
        JTextArea interactionArea = new JTextArea("Interaction: " + (interactionValue != null ? interactionValue.toString() : "null"));
        setJTextAreaAttributes(interactionArea);
        panel.add(interactionArea, gbc);
        gbc.gridy++;
        

        Object cooperationSeedValue = (edgeTable.getColumn("seed::cooperation") != null) ? edgeTable.getRow(edge.getSUID()).get("seed::cooperation", edgeTable.getColumn("seed::cooperation").getType()) : null;
        if (cooperationSeedValue != null) {
            JTextArea cooperationSeedArea = new JTextArea("Seed Scores: Cooperation : " + cooperationSeedValue.toString());
            setJTextAreaAttributes(cooperationSeedArea);
            panel.add(cooperationSeedArea, gbc);
            gbc.gridy++;
        }

        Object competitionSeedValue = (edgeTable.getColumn("seed::competition") != null) ? edgeTable.getRow(edge.getSUID()).get("seed::competition", edgeTable.getColumn("seed::competition").getType()) : null;
        if (competitionSeedValue != null) {
            JTextArea competitionSeedArea = new JTextArea("Seed Scores: Competition: " + competitionSeedValue.toString());
            setJTextAreaAttributes(competitionSeedArea);
            panel.add(competitionSeedArea, gbc);
            gbc.gridy++;
        }
        
        JTextArea donorArea = new JTextArea("Donor: " + donor );
        setJTextAreaAttributes(donorArea);
        panel.add(donorArea, gbc);
        gbc.gridy++;
        
        JTextArea BeneficiaryArea = new JTextArea( "Beneficiary: " + beneficiary);
        setJTextAreaAttributes(BeneficiaryArea);
        panel.add(BeneficiaryArea, gbc);
        gbc.gridy++;
        
  



        //------------------- nested Pathways Panel---------------------------------------

        JPanel PathwaysPanel = new JPanel();
        PathwaysPanel.setLayout(new GridBagLayout());
        GridBagConstraints pathgbc = new GridBagConstraints();

        pathgbc.fill = GridBagConstraints.HORIZONTAL;
        pathgbc.weightx = 1.0; // makes sure components use the full horizontal space

        // Set constraints
        pathgbc.gridx = 0; // Column 0
        pathgbc.gridy = 0; // Start from row 0
        pathgbc.anchor = GridBagConstraints.WEST; // Left-align 
        pathgbc.insets = new Insets(5, 5, 5, 5); // 5pixel marg

        Border etchedBorder = BorderFactory.createEtchedBorder();
        Border emptyBorder = BorderFactory.createEmptyBorder(0, 5, 0, 0);
 
        for (CyColumn column : edgeTable.getColumns()) {
            if (column.getName().startsWith("compl::")) {
                Object columnValue = edgeTable.getRow(edge.getSUID()).get(column.getName(), column.getType());
                if (columnValue != null) {
                	String columnName = column.getName().substring(7); // Extracting column name
                	String panelTitle = "Genomes: " + columnName.replace(":", " : ");
                	JPanel newPanel = new JPanel(new BorderLayout());

                	
                	
                	//DefaultTableModel tableM = new DefaultTableModel(new String[]{"Kegg Module", "Complement", "Module Alternative", "Color Map"}, 0);
                	DefaultTableModel tableM = new DefaultTableModel() {
                		 @Override
                		    public Class<?> getColumnClass(int columnIndex) {
                		        switch (columnIndex) {
                		            case 0: return SwingLink.class; // For Kegg Module links
                		            case 3: return SwingLink.class; // for Complement
                		            case 5: return SwingLink.class; //for color map links
                		            default: return Object.class;
                		        }
                		    }
                		};

                	tableM.addColumn("Kegg Module");
                	tableM.addColumn("Description");
                	tableM.addColumn("Category");
                	tableM.addColumn("Complement");
                	tableM.addColumn("Module Alternative");
                	tableM.addColumn("Color Map");
                	
                    String[] entries = columnValue.toString().split(",");
                  
                    
                    for (String entry : entries) {
                        String[] parts = entry.split("\\^");
                        if (parts.length >= 6) {
                        	
                        	// Handle Kegg Module Link
                        	
                        	 String moduleId = parts[0].trim(); // Trim to remove leading or trailing spaces
                             moduleId = moduleId.replace("[", ""); // Remove  unwanted "[ ]" characters
                             SwingLink  link = new SwingLink(moduleId, "https://www.genome.jp/entry/" + moduleId, openBrowser);
                             

                             
                             String colorMapUrlString = parts[5].trim();
                             colorMapUrlString = colorMapUrlString.replace("[", ""); 
                             SwingLink  colorMapLink = new SwingLink("Url", colorMapUrlString, openBrowser);
                            
                             //add the rows to the table
                             tableM.addRow(new Object[]{link, parts[1], parts[2], parts[3], parts[4], colorMapLink});
                 
                        }
                    }
                    
                 
                	JTable table = new JTable(tableM);
                
                	// Setting the custom renderer for SwingLink class
                	table.setDefaultRenderer(SwingLink.class, new SwingLinkCellRenderer());
                	
                	table.addMouseListener(new MouseAdapter() {
                	    public void mouseClicked(MouseEvent e) {
                	        int row = table.rowAtPoint(e.getPoint());
                	        int column = table.columnAtPoint(e.getPoint());
                	        if (table.getColumnClass(column).equals(SwingLink.class)) {
                	            SwingLink link = (SwingLink) table.getValueAt(row, column);
                	            link.open1(link.getURI());
                	        }
                	        
                	    }
                	   
                	      
                	});
                	
            
                	  //  preferred size of the scroll pane based on the number of rows
                    int rowHeight = table.getRowHeight();
                    int tableHeight = (table.getRowCount() * rowHeight) + table.getTableHeader().getPreferredSize().height;
                    JScrollPane scrollPane = new JScrollPane(table);
                    scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, Math.min(tableHeight, 400))); //  maximum height to 400 pixels

                    newPanel.add(scrollPane, BorderLayout.CENTER);

                	CollapsablePanel collapsablePanel = new CollapsablePanel(iconFont, panelTitle, newPanel, true, 10);
                	collapsablePanel .setBorder(BorderFactory.createCompoundBorder(emptyBorder, etchedBorder));
                	PathwaysPanel.add(collapsablePanel, pathgbc);
                	pathgbc.gridy++;
                }
            }
        }
        
        
    
     CollapsablePanel PathwaysCollapsablePanel = new CollapsablePanel(iconFont, "Pathways", PathwaysPanel, true, 10);
     PathwaysCollapsablePanel .setBorder(BorderFactory.createCompoundBorder(emptyBorder, etchedBorder));
     panel.add(PathwaysCollapsablePanel, gbc);
     gbc.gridy++;
     
  
        
        String edgeId = (nameValue != null) ? nameValue.toString() : "Selected Edges";

        CollapsablePanel collapsablePanel = new CollapsablePanel(iconFont, edgeId, panel, false, 10);
        
        collapsablePanel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, etchedBorder));

        return collapsablePanel;

     }

    
    
    private void setJTextAreaAttributes(JTextArea textArea) {
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 10));
        textArea.setOpaque(false);
        textArea.setBorder(null);
        textArea.setPreferredSize(new Dimension(500, 15));
    }


    //-----------------------------------------------------------------------------


    void undoFilters() {
        CyNetworkView view = manager.getCurrentNetworkView();
        if (view != null) {
            for (View < CyEdge > edge: view.getEdgeViews()) {
                edge.clearValueLock(BasicVisualLexicon.EDGE_VISIBLE);
            }

        }
    }




    @Override

    double initFilter(String type, String label) {


        double minValue = 1.0; // Start with the highest value to ensure you find the lowest one available.
        for (CyEdge edge: currentNetwork.getEdgeList()) {
            CyRow edgeRow = currentNetwork.getRow(edge);
            Double edgeScore = edgeRow.get(type, label, Double.class); // Get the edge weight based on the type parameter.
            // Skip this edge if the score is null.
            if (edgeScore == null) {
                minValue = -1.0;
                break;
            }

            // Update minValue if a lower value is found.
            else if (edgeScore < minValue) {
                minValue = edgeScore.doubleValue();
            }
        }

        return minValue;
            
         
    }




    @Override
    void doFilter(String type) {

        // Check if the network and filter type exists
        Map < String, Double > filter = filters.get(currentNetwork).get(type);
        CyNetworkView view = manager.getCurrentNetworkView();
        CyNetwork net = view.getModel();

        // double weightThreshold = scoreSlider.getValue() / 100.0;
        // double weightThreshold = filters.get(currentNetwork).get(type).get("weight");

        // Iterate through each edge in the current network.
        for (CyEdge edge: currentNetwork.getEdgeList()) {
            CyRow edgeRow = currentNetwork.getRow(edge);

            boolean show = true;
            for (String lbl: filter.keySet()) {
                Double v = edgeRow.get(type, lbl, Double.class);
                double nv = filter.get(lbl);
                if ((v == null && nv > 0) || (v != null && v < nv)) {
                    show = false;
                    break;
                }
            }

            View < CyEdge > edgeView = view.getEdgeView(edge);
            if (edgeView == null) continue;

            if (show) {
                // Make the edge visible
                edgeView.clearValueLock(BasicVisualLexicon.EDGE_VISIBLE);
                edgeView.setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, true);
                System.out.println("Edge " + edge + " is set to visible");
            } else {
                // Hide the edge and deselect it if it doesn't meet the criteria
                edgeView.setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, false);
                net.getRow(edge).set(CyNetwork.SELECTED, false);
                System.out.println("Edge " + edge + " is hidden");

            }
        }
    }




    /*
     * public void updateSubPanel() { subScorePanel.removeAll(); EasyGBC d = new
     * EasyGBC(); subScorePanel.add(createSubScorePanel(),
     * d.anchor("west").expandHoriz()); subScorePanel.add(new JPanel(),
     * d.down().anchor("west").expandBoth()); }
     */

    public void networkChanged(CyNetwork newNetwork) {
        this.currentNetwork = newNetwork;
        if (currentNetwork == null) {
            if (WeightPanel != null)
                WeightPanel.removeAll();
            return;
        }

        if (!filters.containsKey(currentNetwork)) {
            filters.put(currentNetwork, new HashMap < > ());
            filters.get(currentNetwork).put("weight", new HashMap < > ());
        }

        if (!colors.containsKey(currentNetwork)) {
			colors.put(currentNetwork, new HashMap<>());
		}
        
        if (!filters.containsKey(currentNetwork)) {
            filters.put(currentNetwork, new HashMap < > ());
            filters.get(currentNetwork).put(Mutils.Seed_NAMESPACE, new HashMap < > ());
        }
        
        
        updateSeedPanel();
        updateWeightPanelPanel();
        updateEdgesPanel();
    }

    public void selectedEdges(Collection < CyEdge > edges) {

        edgesSPanel.removeAll();
        EasyGBC c = new EasyGBC();
        Mutils.clearHighlight(manager, manager.getCurrentNetworkView());

        for (CyEdge edge: edges) {
            JPanel newPanel = createEdgePanel(edge);
            newPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            edgesSPanel.add(newPanel, c.anchor("west").down().expandHoriz());
        }



        revalidate();
        repaint();
    }

	@Override
	double initFilterSeed(String type, String label) {
		double minValue = 1.0;
        for (CyNode node: currentNetwork.getNodeList()) {
            CyRow nodeRow = currentNetwork.getRow(node);

            Double v = nodeRow.get(type, label, Double.class);
            if (v == null) {
                minValue = 0.0;
                break;
            } else if (v < minValue) {
                minValue = v.doubleValue();
            }
        }
        return minValue;

	}

}