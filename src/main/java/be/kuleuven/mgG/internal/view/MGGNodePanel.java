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
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.Border;
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






public class MGGNodePanel extends AbstractMggPanel {

    private JCheckBox highlightBox;
    private boolean updating = false;
    private JPanel nodesPanel = null;
    private Color defaultBackground;
    private JPanel PhendbScPanel = null;

    private JPanel PhenDbFilterPanel = null;

    private JPanel subScorePanel = null;

    //private Map<CyNetwork, Map<String, Boolean>> phn;


    public MGGNodePanel(final MGGManager manager) {

        super(manager);
        filters.get(currentNetwork).put("phendbScore", new HashMap < > ());

        //phn = new HashMap<>();
        //phn.put(currentNetwork, new HashMap<>());


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
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        add(controlPanel, c.anchor("west").down().noExpand());

        JPanel mainPanel = new JPanel(); {
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.setBackground(defaultBackground);
            EasyGBC d = new EasyGBC();
            mainPanel.add(createNodesPanel(), d.down().anchor("west").expandHoriz());
            mainPanel.add(createPhenDbScoresPanel(), d.down().anchor("west").expandHoriz());

            mainPanel.add(createPhenDbPanel(), d.down().anchor("west").expandHoriz());

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
            upperPanel.add(highlightBox, upperGBC.right().insets(0, 10, 0, 0).noExpand());
        }

        upperPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        controlPanel.add(upperPanel, d.anchor("northwest").expandHoriz());

        updateControls();
        // TODO: change max size when more buttons get added?
        controlPanel.setMaximumSize(new Dimension(300, 100));
        controlPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return controlPanel;
    }

    public void selectedNodes(Collection < CyNode > nodes) {
        // Clear the nodes panel
        nodesPanel.removeAll();
        EasyGBC c = new EasyGBC();
        Mutils.clearHighlight(manager, manager.getCurrentNetworkView());

        for (CyNode node: nodes) {
            JPanel newPanel = createNodePanel(node);
            newPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            nodesPanel.add(newPanel, c.anchor("west").down().expandHoriz());
        }

        if (manager.highlightNeighbors()) {
            doHighlight(manager.getCurrentNetworkView());
        } else {
            clearHighlight(manager.getCurrentNetworkView());
        }
        revalidate();
        repaint();
    }

    private void doHighlight(CyNetworkView networkView) {

        if (networkView != null) {
            List < CyNode > nodes = CyTableUtil.getNodesInState(networkView.getModel(), CyNetwork.SELECTED, Boolean.TRUE);
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



    //-----------------------Selected nodes----------------------------------
    private JPanel createNodesPanel() {
        nodesPanel = new JPanel();
        nodesPanel.setLayout(new GridBagLayout());
        EasyGBC c = new EasyGBC();

        if (currentNetwork != null) {
            List < CyNode > nodes = CyTableUtil.getNodesInState(currentNetwork, CyNetwork.SELECTED, true);
            for (CyNode node: nodes) {
                JPanel newPanel = createNodePanel(node);
                newPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

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

        List < CyNode > nodes = CyTableUtil.getNodesInState(currentNetwork, CyNetwork.SELECTED, true);

        if (nodes.size() > 50) {
            return;
        }
        for (CyNode node: nodes) {
            JPanel newPanel = createNodePanel(node);
            newPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            nodesPanel.add(newPanel, c.anchor("west").down().expandHoriz());
        }
        return;
    }




    private JPanel createNodePanel(CyNode node) {

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


        CyNetwork currentNetwork = manager.getCurrentNetwork();
        if (currentNetwork == null) return panel;


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



        String[] attributes = {
            "microbetag::gtdb-genomes",
            "microbetag::ncbi-tax-id",
            "microbetag:: ncbi-tax-level"
        };
        for (String attribute: attributes) {
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



        // ----------------------For the nested phenDB CollapsablePanel------------------


        JPanel phenDBPanel = new JPanel(new BorderLayout());


        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; //  make  cells uneditable
            }
        };
        model.addColumn("Feature");
        model.addColumn("Present");
        model.addColumn("Score");

        //  Instantiate table with the model
        JTable table = new JTable(model);

        // Adjust column widths
        TableColumnModel columnModel = table.getColumnModel();
        // Modify the widths 
        columnModel.getColumn(0).setPreferredWidth(5);
        columnModel.getColumn(1).setPreferredWidth(5);
        columnModel.getColumn(2).setPreferredWidth(5);




        for (CyColumn column: nodeTable.getColumns()) {
            String columnName = column.getName();

            // Check if column starts with "phendb" and NOT "phendbScore" 
            if (columnName.startsWith("phendb::") && !columnName.contains("phendbScore::")) {

                // Extract  name
                String feature = columnName.replace("phendb::", "");

                // Check corresponding "Score" column exists under "phendbScore::" 
                CyColumn scoreColumn = nodeTable.getColumn("phendbScore::" + feature + "Score");
                if (scoreColumn != null) {


                    Object presentObj = nodeTable.getRow(node.getSUID()).get(columnName, column.getType());
                    String presentValue = (presentObj == null) ? "null" : presentObj.toString();


                    Object scoreObj = nodeTable.getRow(node.getSUID()).get(scoreColumn.getName(), scoreColumn.getType());
                    String scoreValue = (scoreObj == null) ? "null" : scoreObj.toString();


                    try {
                        double scoreAsDouble = Double.parseDouble(scoreValue);
                        scoreValue = String.format("%.2f", scoreAsDouble);
                    } catch (NumberFormatException e) {

                    }


                    model.addRow(new Object[] {
                        feature,
                        presentValue,
                        scoreValue
                    });
                }
            }
        }

        // Add table to a scroll pane:
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(panel.getWidth() - 20, 200));
        phenDBPanel.add(tableScrollPane, BorderLayout.CENTER);


        phenDBPanel.revalidate();
        phenDBPanel.repaint();

        //  Wrap the phenDBPanel inside a CollapsablePanel

        CollapsablePanel phenDBCollapsablePanel = new CollapsablePanel(iconFont, "phenDB attributes", phenDBPanel, false, 10);
        Border etchedBorder = BorderFactory.createEtchedBorder();
        Border emptyBorder = BorderFactory.createEmptyBorder(0, 5, 0, 0);
        phenDBCollapsablePanel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, etchedBorder));

        // Add the phenDBCollapsablePanel to the main panel:

        panel.add(phenDBCollapsablePanel, gbc);
        gbc.gridy++;




        //---------------------------panel for faprotax--------------------------


        JPanel faprotaxPanel = new JPanel(new GridBagLayout());
        GridBagConstraints fapGBC = new GridBagConstraints();
        fapGBC.fill = GridBagConstraints.HORIZONTAL;
        fapGBC.weightx = 1.0;
        fapGBC.gridx = 0;
        fapGBC.gridy = 0;
        fapGBC.anchor = GridBagConstraints.WEST;
        fapGBC.insets = new Insets(5, 5, 5, 5);

        // Loop throughnodeTable columns for starting faprotax::
        for (CyColumn column: nodeTable.getColumns()) {
            String columnName = column.getName();
            if (columnName.startsWith("faprotax::")) {
                Object attrValue = nodeTable.getRow(node.getSUID()).get(columnName, column.getType());
                if (attrValue != null && attrValue instanceof Boolean) {

                    String displayName = columnName.split("::")[1];

                    JCheckBox checkBox = new JCheckBox(displayName);
                    checkBox.setSelected((Boolean) attrValue);
                    checkBox.setEnabled(false);
                    faprotaxPanel.add(checkBox, fapGBC);
                    fapGBC.gridy++;
                }
            }
        }

        // Wrap faprotaxPanel inside  CollapsablePanel
        CollapsablePanel faprotaxCollapsablePanel = new CollapsablePanel(iconFont, "Faprotax Attributes", faprotaxPanel, false, 10);
        faprotaxCollapsablePanel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, etchedBorder));

        // Add  faprotaxCollapsablePanel to  main panel
        panel.add(faprotaxCollapsablePanel, gbc);
        gbc.gridy++;

        //------------------------------

        //	if (name == null) {
        // name = "Selected Nodes";
        //	}


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



    //------------------------------------ PhenDbscores filter ----------------------

    private JPanel createPhenDbScoresPanel() {

        PhendbScPanel = new JPanel();
        PhendbScPanel.setLayout(new GridBagLayout());
        EasyGBC c = new EasyGBC();

        List < String > phendbScList = Mutils.getPhenDbScList(currentNetwork);

        for (String phendbScore: phendbScList) {
            PhendbScPanel.add(createFilterSlider2("phendbScore", phendbScore, currentNetwork, true, 100.0),
                c.anchor("west").down().expandHoriz());
        }


        CollapsablePanel collapsablePanel = new CollapsablePanel(iconFont, "phenDbScore Filters", PhendbScPanel, true, 10);
        collapsablePanel.setToolTipText("Show nodes with a PhendbScore bigger than the chosen value");
        collapsablePanel.setBorder(BorderFactory.createEtchedBorder());
        return collapsablePanel;
    }

    private void updatePhenDbScoresPanel() {
        if (PhendbScPanel == null) return;
        PhendbScPanel.removeAll();
        EasyGBC c = new EasyGBC();
        List < String > phendbScList = Mutils.getPhenDbScList(currentNetwork);
        for (String phendbScore: phendbScList) {
            PhendbScPanel.add(createFilterSlider2("phendbScore", phendbScore, currentNetwork, true, 100.0),
                c.anchor("west").down().expandHoriz());
        }
        return;
    }

    //------------------------------------------------------------------

    private JPanel createPhenDbPanel() {
        PhenDbFilterPanel = new JPanel();
        PhenDbFilterPanel.setLayout(new BoxLayout(PhenDbFilterPanel, BoxLayout.Y_AXIS));

        EasyGBC c = new EasyGBC();



        List < String > phendbAttributeList = Mutils.getPhendbAttributes(currentNetwork);
        // Add checkboxes for each phendb attribute
        for (String phendbAttribute: phendbAttributeList) {
            PhenDbFilterPanel.add(createPhendbCheckbox(phendbAttribute));
        }

        CollapsablePanel collapsablePanel = new CollapsablePanel(iconFont, "phenDb Filters", PhenDbFilterPanel, true, 10);
        collapsablePanel.setToolTipText("Show nodes that have a phendb attribute ");
        collapsablePanel.setBorder(BorderFactory.createEtchedBorder());
        return collapsablePanel;
    }

    private void updatePhenDbPanel() {
        if (PhenDbFilterPanel == null) return;
        PhenDbFilterPanel.removeAll();
        EasyGBC c = new EasyGBC();

        List < String > phendbAttributeList = Mutils.getPhendbAttributes(currentNetwork);
        // Add checkboxes for each phendb attribute
        for (String phendbAttribute: phendbAttributeList) {
            PhenDbFilterPanel.add(createPhendbCheckbox(phendbAttribute));
        }
    }

    private void filterNodesByPhendbAttribute() {
        CyNetworkView view = manager.getCurrentNetworkView();
        CyNetwork net = view.getModel();

        boolean anyCheckboxSelected = isAnyCheckboxSelected();

        for (CyNode node: net.getNodeList()) {
            View < CyNode > nodeView = view.getNodeView(node);
            if (nodeView == null) continue;

            boolean isVisible = false;
            if (!anyCheckboxSelected) {
                isVisible = true;
            } else {
                for (Component comp: PhenDbFilterPanel.getComponents()) {
                    if (comp instanceof JCheckBox) {
                        JCheckBox checkBox = (JCheckBox) comp;
                        if (checkBox.isSelected()) {
                            CyRow nodeRow = net.getRow(node);
                            Boolean attributeValue = nodeRow.get("phendb::" + checkBox.getText(), Boolean.class);
                            isVisible = isVisible || (attributeValue != null && attributeValue);
                        }
                    }
                }
            }
            nodeView.setLockedValue(BasicVisualLexicon.NODE_VISIBLE, isVisible);
        }
        view.updateView();
    }


    private JCheckBox createPhendbCheckbox(String attributeName) {
        JCheckBox checkBox = new JCheckBox(attributeName);

        checkBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                filterNodesByPhendbAttribute();
            }
        });
        return checkBox;
    }

    private boolean isAnyCheckboxSelected() {
        for (Component comp: PhenDbFilterPanel.getComponents()) {
            if (comp instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) comp;
                if (checkBox.isSelected()) {
                    return true;
                }
            }
        }
        return false;
    }

    //---------------------------------------------------------------------------------



    public void networkChanged(CyNetwork newNetwork) {
        this.currentNetwork = newNetwork;
        if (currentNetwork == null) {
            // Hide results panel?
            if (PhendbScPanel != null)
                PhendbScPanel.removeAll();

            if (PhenDbFilterPanel != null)
                PhenDbFilterPanel.removeAll();
            return;

        }


        if (!filters.containsKey(currentNetwork)) {
            filters.put(currentNetwork, new HashMap < > ());
            filters.get(currentNetwork).put("phendbScore", new HashMap < > ());
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
        //if (!phn.containsKey(currentNetwork)) {
        //phn.put(currentNetwork, new HashMap<>());

        //}
        updateNodesPanel();
        updatePhenDbScoresPanel();
        //updateSubPanel();
        updatePhenDbPanel();
    }









    //------------------------- Filter logic--------------------------------


    @Override
    void doFilter(String type) {

        Map < String, Double > filter = filters.get(currentNetwork).get(type);
        CyNetworkView view = manager.getCurrentNetworkView();
        CyNetwork net = view.getModel();
        for (CyNode node: currentNetwork.getNodeList()) {
            CyRow nodeRow = currentNetwork.getRow(node);

            boolean show = true;
            for (String lbl: filter.keySet()) {
                Double v = nodeRow.get(type, lbl, Double.class);
                double nv = filter.get(lbl);
                if ((v == null && nv > 0) || (v != null && v < nv)) {
                    show = false;
                    break;
                }
            }

            View < CyNode > nv = view.getNodeView(node);
            if (nv == null) continue;
            if (show) {
                nv.clearValueLock(BasicVisualLexicon.NODE_VISIBLE);
                for (CyEdge e: net.getAdjacentEdgeList(node, CyEdge.Type.ANY)) {
                    final View < CyEdge > ev = view.getEdgeView(e);
                    if (ev == null) continue;
                    ev.clearValueLock(BasicVisualLexicon.EDGE_VISIBLE);
                }
            } else {
                nv.setLockedValue(BasicVisualLexicon.NODE_VISIBLE, false);
                net.getRow(node).set(CyNetwork.SELECTED, false);
                for (CyEdge e: net.getAdjacentEdgeList(node, CyEdge.Type.ANY)) {
                    final View < CyEdge > ev = view.getEdgeView(e);
                    if (ev == null) continue;
                    net.getRow(e).set(CyNetwork.SELECTED, false);
                    ev.setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, false);

                }
            }
        }
    }


    @Override
    void undoFilters() {
        CyNetworkView view = manager.getCurrentNetworkView();
        if (view != null) {
            for (View < CyNode > node: view.getNodeViews()) {
                node.clearValueLock(BasicVisualLexicon.NODE_VISIBLE);
            }
        }

    }

    @Override
    double initFilter(String type, String label) {
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