	package be.kuleuven.mgG.internal.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
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





public class MGGEdgePanel extends AbstractMggPanel {

    JButton fetchEdges;
    JPanel subScorePanel;
    JPanel scorePanel;
    JButton deleteEdges;
    private JPanel WeightPanel = null;
    private Color defaultBackground;

    private JPanel edgesSPanel = null;


    private Map < CyNetwork, Map < String, Boolean >> colors;


    public MGGEdgePanel(final MGGManager manager) {

        super(manager);
        filters.get(currentNetwork).put("weight", new HashMap < > ());
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
        //layout.setVgap(0);
        controlPanel.setLayout(layout);
        //		
        controlPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlPanel.setMaximumSize(new Dimension(100, 100));
        return controlPanel;
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


        CollapsablePanel collapsablePanel = new CollapsablePanel(iconFont, "weight Filters", WeightPanel, true, 10);
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
        //String name = null;


        Object nameValue = (edgeTable.getColumn("name") != null) ? edgeTable.getRow(edge.getSUID()).get("name", edgeTable.getColumn("name").getType()) : null;
        JTextArea nameArea = new JTextArea("Name: " + (nameValue != null ? nameValue.toString() : "null"));
        setJTextAreaAttributes(nameArea);
        panel.add(nameArea, gbc);
        gbc.gridy++;

        // Split the name to get Donor and Beneficiary
        String[] nameParts = nameValue != null ? nameValue.toString().split(" \\(comp_coop\\) | \\(cooccurs\\) ") : new String[] {
            "",
            ""
        };
        String donor = nameParts.length > 0 ? nameParts[0] : "";
        String beneficiary = nameParts.length > 1 ? nameParts[1] : "";


        Object interactionValue = (edgeTable.getColumn("interaction") != null) ? edgeTable.getRow(edge.getSUID()).get("interaction", edgeTable.getColumn("interaction").getType()) : null;
        JTextArea interactionArea = new JTextArea("Interaction: " + (interactionValue != null ? interactionValue.toString() : "null"));
        setJTextAreaAttributes(interactionArea);
        panel.add(interactionArea, gbc);
        gbc.gridy++;



        Object cooperationSeedValue = (edgeTable.getColumn("seed::cooperation") != null) ? edgeTable.getRow(edge.getSUID()).get("seed::cooperation", edgeTable.getColumn("seed::cooperation").getType()) : null;
        JTextArea cooperationSeedArea = new JTextArea("Seed Scores: Cooperation : " + (cooperationSeedValue != null ? cooperationSeedValue.toString() : "null"));
        setJTextAreaAttributes(cooperationSeedArea);
        panel.add(cooperationSeedArea, gbc);
        gbc.gridy++;

        Object competitionSeedValue = (edgeTable.getColumn("seed::competition") != null) ? edgeTable.getRow(edge.getSUID()).get("seed::competition", edgeTable.getColumn("seed::competition").getType()) : null;
        JTextArea competitionSeedArea = new JTextArea("Seed Scores: Competition: " + (competitionSeedValue != null ? competitionSeedValue.toString() : "null"));
        setJTextAreaAttributes(competitionSeedArea);
        panel.add(competitionSeedArea, gbc);
        gbc.gridy++;

        // Create the table model
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Donor: " + donor);
        tableModel.addColumn("Beneficiary: " + beneficiary);



        // Loop through columns and add rows to the table
        for (CyColumn column: edgeTable.getColumns()) {
            if (column.getName().startsWith("compl::") && edgeTable.getRow(edge.getSUID()).get(column.getName(), column.getType()) != null) {
                String columnName = column.getName().substring(7); // Remove "compl::"
                String[] parts = columnName.split(":");
                if (parts.length == 2) {
                    tableModel.addRow(new Object[] {
                        parts[0], parts[1]
                    });
                }
            }
        }


        // Create the table and add it to the panel
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 10)); // Smaller font size
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(panel.getWidth() - 100, 200));
        gbc.gridy++;
        panel.add(tableScrollPane, gbc);


        //	


        String edgeId = (nameValue != null) ? nameValue.toString() : "Selected Edges";

        CollapsablePanel collapsablePanel = new CollapsablePanel(iconFont, edgeId, panel, false, 10);
        Border etchedBorder = BorderFactory.createEtchedBorder();
        Border emptyBorder = BorderFactory.createEmptyBorder(0, 5, 0, 0);
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
        // This part may vary depending on how you manage your network views.
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

}