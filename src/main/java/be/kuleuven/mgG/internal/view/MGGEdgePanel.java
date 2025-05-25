package be.kuleuven.mgG.internal.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.JComboBox;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;

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
import be.kuleuven.mgG.internal.utils.BlueUnderlineHTMLRenderer;
import be.kuleuven.mgG.internal.utils.Mutils;
import be.kuleuven.mgG.internal.utils.SwingLink;
import be.kuleuven.mgG.internal.utils.SwingLinkCellRenderer;
import be.kuleuven.mgG.internal.utils.ViewUtils;
import be.kuleuven.mgG.internal.utils.LogUtils;
import java.util.ArrayList;


public class MGGEdgePanel extends AbstractMggPanel {

	JButton fetchEdges;
	private JPanel subScorePanel = null;

	// JPanel scorePanel;
	JButton deleteEdges;
	private JPanel WeightPanel = null;
	private boolean showComplEdgesState;
	private boolean showSeedComplEdgesState;

	// JPanel seedPanel;
	private Color defaultBackground;
	private JButton showSeedComplEdgesButton;
	private JButton showComplEdgesButton;

	private JPanel edgesSPanel = null;
	private Map<CyNetwork, Map<String, Boolean>> colors;
	
	// MY MAP FOR SIGNS 
	private Map<String, JComboBox<String>> signCombos = new HashMap<>();

	// Function to check whether a column is []
	public boolean isNotEmpty(String[] entries) {
		for (String entry : entries) {
			// Check if the current element is not equal to "[ ]"
			if (!entry.equals("[]")) {
				// If at least one element is not "[ ]", return true
				return true;
			}
		}
		return false;
	}

	// Function to check if an edge is not empty
	public boolean checkIfEdgeIsNotEmpty(CyNetwork net, CyEdge edge, CyColumn column) {

		Object value = net.getRow(edge).get(column.getName(), column.getType());

		if (value == null) {
			return false;
		}

		// Check if value is indeed a List
		if (value instanceof List) {
			List<?> list = (List<?>) value;

			// Iterate over list items and process each item (which is a String)
			for (Object entry : list) {
				String[] entries = entry.toString().split(",");
				boolean isIt = isNotEmpty(entries);
//	            String strIsIt = Boolean.toString(isIt);
				return isIt;
			}
		}

		// In case it's not a list or something else goes wrong
		LogUtils.info("The value is not a list.");
		return false;
	}

	public MGGEdgePanel(final MGGManager manager) {

		super(manager);
		filters.get(currentNetwork).put(Mutils.Weight_NAMESPACE, new HashMap<>());
		filters.get(currentNetwork).put(Mutils.Seed_NAMESPACE, new HashMap<>());

		// Initialize filterSigns map for current network
        filterSign.get(currentNetwork).put(Mutils.Weight_NAMESPACE, new HashMap < > ());    // new String()
        filterSign.get(currentNetwork).put(Mutils.Seed_NAMESPACE, new HashMap < > ());

		//
		colors = new HashMap<>();
		colors.put(currentNetwork, new HashMap<>());

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

		JPanel mainPanel = new JPanel();
		{
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

//-------------------------TOGGLE BUTTONS FOR TYPES OF COMPLEMENS  -------------------------------------

	//---------
	// SHOW ONLY EDGES WITH PATHWAY OR SEED COMPLEMENTS...
	//---------	
	private JPanel createControlPanel() {

		JPanel controlPanel = new JPanel();
		GridLayout layout = new GridLayout(2, 2);
		EasyGBC d = new EasyGBC();
		// layout.setVgap(0);
		controlPanel.setLayout(layout);

		JPanel upperPanel = new JPanel(new GridBagLayout());

		// Initialize the showSeedComplEdgesButton
		showSeedComplEdgesButton = new JButton("Edges with Seed Complementarities");
		showSeedComplEdgesButton.setFont(labelFont);
		showSeedComplEdgesButton.setToolTipText("Show/Hide Edges with Seed Complementarities");

		showSeedComplEdgesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Toggle the state
				showSeedComplEdgesState = !showSeedComplEdgesState;

				// Update the button label based on the current state
				if (showSeedComplEdgesState) {
					showSeedComplEdgesButton.setText("All Edges ");
					showSeedComplEdgesState = true;
					doShowSeedComplEdges(true); // edges with Compl values
					showComplEdgesButton.setEnabled(false); // Disable pathway complements button

				} else {
					showSeedComplEdgesButton.setText("Edges with Seed Complementarities");
					showSeedComplEdgesState = false;
					doShowSeedComplEdges(false); // Show all edges
					showComplEdgesButton.setEnabled(true); // Enable pathway complements button
				}
			}
		});
		
		upperPanel.add(showSeedComplEdgesButton);

		// Initialize the showComplEdgesButton
		showComplEdgesButton = new JButton("Edges with Pathway Complementarities");
		showComplEdgesButton.setFont(labelFont);
		showComplEdgesButton.setToolTipText("Show/Hide Edges with Pathway Complementarities");

		showComplEdgesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Toggle the state
				showComplEdgesState = !showComplEdgesState;

//              // Update the button label 
				if (showComplEdgesState) {
					showComplEdgesButton.setText("All Edges");
					showComplEdgesState = true;
					doShowComplEdges(true);
					showSeedComplEdgesButton.setEnabled(false); // Disable seed complementarities button
				} else {
					showComplEdgesButton.setText("Edges with Pathway Complementarities");
					showComplEdgesState = false;
					doShowComplEdges(false); // Show all edges
					showSeedComplEdgesButton.setEnabled(true); // Enable seed complementatities button
				}
			}
		});

		upperPanel.add(showComplEdgesButton);

		upperPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
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
			if (edgeView == null)
				continue;

			boolean hasComplValue = false;
			for (CyColumn column : net.getDefaultEdgeTable().getColumns()) {
				if (column.getName().startsWith("compl::")) {

					if (checkIfEdgeIsNotEmpty(net, edge, column)) {
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

		view.updateView();
	}

	private void doShowSeedComplEdges(boolean show) {
		CyNetworkView view = manager.getCurrentNetworkView();
		CyNetwork net = view.getModel();

		// Iterate over all edges
		for (CyEdge edge : net.getEdgeList()) {
			View<CyEdge> edgeView = view.getEdgeView(edge);
			if (edgeView == null)
				continue;

			boolean hasComplValue = false;
			for (CyColumn column : net.getDefaultEdgeTable().getColumns()) {

				if (column.getName().startsWith("seedCompl::")) {
					
					LogUtils.info("about to check if edge is empty");
					
					if (checkIfEdgeIsNotEmpty(net, edge, column)) {
						hasComplValue = true;
						break;
					}
				}
			}
			if (show) {
//            	LogUtils.info("I AM IN THE  seed  SHOW");
				edgeView.setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, hasComplValue);
			} else {
				edgeView.setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, true);
			}
		}

		view.updateView();
	}

//--------------------------  SHOW COOCCURENCE EDGES WITH SCORE -------------------------------------
	
	
	private JPanel createWeightPanel() {

		WeightPanel = new JPanel();
		WeightPanel.setLayout(new GridBagLayout());
		EasyGBC c = new EasyGBC();

		List<String> WeightList = Mutils.getWeightList(currentNetwork);

		// -------         CO OCCURENCE COLOR
		
		{
			JPanel colorPanel = new JPanel();
			colorPanel.setMinimumSize(new Dimension(25, 30));
			colorPanel.setLayout(new GridBagLayout());
			EasyGBC d = new EasyGBC();
			JLabel lbl = new JLabel("");
//			lbl.setToolTipText("Color edges with co-occurrence weight of..");
			lbl.setFont(labelFont);
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			colorPanel.add(lbl, d.anchor("north").noExpand());

//			for (String weight : WeightList) {
//				colorPanel.add(createScoreCheckBox(weight), d.down().expandVert());
//			}

			WeightPanel.add(colorPanel, c.anchor("northwest").expandVert());
		}
		
		// -------         CO OCCURENCE LABEL TYPE
		
		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new GridBagLayout());
			EasyGBC d = new EasyGBC();

			JLabel lbl = new JLabel("");    // Co-occurrence
			lbl.setFont(labelFont);
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			labelPanel.add(lbl, d.anchor("north").noExpand());
			for (String weight : WeightList) {
				JLabel weightLabel = new JLabel("");   // weight
				weightLabel.setFont(textFont);
				weightLabel.setMinimumSize(new Dimension(100, 30));
				weightLabel.setMaximumSize(new Dimension(100, 30));
				labelPanel.add(weightLabel, d.down().expandVert());
			}
			labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			WeightPanel.add(labelPanel, c.right().expandVert());
		}
		
		// -------         CO OCCURENCE SIGN 
		
		{
		    JPanel signPanel = new JPanel();
		    signPanel.setMinimumSize(new Dimension(25, 30));
		    signPanel.setLayout(new GridBagLayout());
		    EasyGBC d = new EasyGBC();

		    JLabel lbl = new JLabel("Sign");
		    lbl.setToolTipText("Set if the score should be greater or lower than the threshold.");
		    lbl.setFont(labelFont);
		    lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		    signPanel.add(lbl, d.anchor("north").noExpand());

		    // Store selected sign for each seedScore using a JComboBox
		    for (String weight : WeightList) {

		        JComboBox<String> combo = new JComboBox<>(new String[]{">", "<"});
		        combo.setName("sign_" + weight); // helpful for later retrieval
		        signCombos.put(weight, combo);
		        signPanel.add(combo, d.down().expandVert());
		    }
		    
		    WeightPanel.add(signPanel, c.right().anchor("northwest").expandVert());
		}

		// ---- CO - OCCURRENCE SLIDER
		
		{
			JPanel filterPanel = new JPanel();
			filterPanel.setLayout(new GridBagLayout());
			EasyGBC d = new EasyGBC();
			JLabel lbl = new JLabel("Correlation score");
			lbl.setToolTipText("Hide edges with a seed score higher/lower than the threshold set.");
			lbl.setFont(labelFont);
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			filterPanel.add(lbl, d.anchor("north").noExpand());

			for (String weight : WeightList) {
				JComponent scoreSlider = createFilterSlider(
						Mutils.Weight_NAMESPACE, 
						weight, 
						currentNetwork, 
						false, 
						100.0
				);
				scoreSlider.setMinimumSize(new Dimension(100, 30));
				// scoreSlider.setMaximumSize(new Dimension(100,30));
				filterPanel.add(scoreSlider, d.down().expandBoth());

			}
			
			// filterPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			WeightPanel.add(filterPanel, c.right().expandBoth());
		}
		
		
		// --------		ADD COLLADBSABLE PANEL
		
		CollapsablePanel collapsablePanel = new CollapsablePanel(
				iconFont,
				"Filter-out co-occurrence / co-exclusion edges", 
				WeightPanel, 
				false, 
				12
		);
		collapsablePanel.setToolTipText(
				"Hide edges with a co-occurrence score not reaching the threshold provided."
		);
		collapsablePanel.setBorder(BorderFactory.createEtchedBorder());
		collapsablePanel.setAlwaysExpanded();
		return collapsablePanel;
	
	}


	public void updateWeightPanel() {
		if (WeightPanel == null)
			return;
		WeightPanel.removeAll();
		EasyGBC c = new EasyGBC();

		List<String> WeightList = Mutils.getWeightList(currentNetwork);
		
		// -------         CO OCCURENCE COLOR
		
		{
			JPanel colorPanel = new JPanel();
			colorPanel.setMinimumSize(new Dimension(25, 30));
			colorPanel.setLayout(new GridBagLayout());
			EasyGBC d = new EasyGBC();
			JLabel lbl = new JLabel("");
//			lbl.setToolTipText("Color edges with the selected metabolic index.");
			lbl.setFont(labelFont);
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			colorPanel.add(lbl, d.anchor("north").noExpand());

//			for (String weight : WeightList) {
//				colorPanel.add(createScoreCheckBox(weight), d.down().expandVert());
//			}

			WeightPanel.add(colorPanel, c.anchor("northwest").expandVert());
		}
		
		// -------         CO OCCURENCE LABEL TYPE
		
		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new GridBagLayout());
			EasyGBC d = new EasyGBC();

			JLabel lbl = new JLabel("");
			lbl.setFont(labelFont);
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			labelPanel.add(lbl, d.anchor("north").noExpand());

			for (String weight : WeightList) {
				JLabel weightLabel = new JLabel("");
				weightLabel.setFont(textFont);
				weightLabel.setMinimumSize(new Dimension(100, 30));
				weightLabel.setMaximumSize(new Dimension(100, 30));
				labelPanel.add(weightLabel, d.down().expandVert());
			}
			labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			WeightPanel.add(labelPanel, c.right().expandVert());
		}
		
		// -------         CO OCCURENCE SIGN 
		
		{
		    JPanel signPanel = new JPanel();
		    signPanel.setMinimumSize(new Dimension(25, 30));
		    signPanel.setLayout(new GridBagLayout());
		    EasyGBC d = new EasyGBC();

		    JLabel lbl = new JLabel("Sign");
		    lbl.setToolTipText("Choose if the score should be greater or lower than the threshold.");
		    lbl.setFont(labelFont);
		    lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		    signPanel.add(lbl, d.anchor("north").noExpand());

		    // Store selected sign for each seedScore using a JComboBox
		    for (String weight : WeightList) {

		        JComboBox<String> combo = new JComboBox<>(new String[]{">", "<"});
		        combo.setName("sign_" + weight); // helpful for later retrieval
		        signCombos.put(weight, combo);
		        signPanel.add(combo, d.down().expandVert());
		    }
		    
		    WeightPanel.add(signPanel, c.right().anchor("northwest").expandVert());
		}

		// ---- CO - OCCURRENCE SLIDER
		
		{
			JPanel filterPanel = new JPanel();
			filterPanel.setLayout(new GridBagLayout());
			EasyGBC d = new EasyGBC();
			JLabel lbl = new JLabel("Correlation score");
			lbl.setToolTipText("Hide edges with a correlation score higher/lower than the threshold set.");
			lbl.setFont(labelFont);
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			filterPanel.add(lbl, d.anchor("north").noExpand());

			for (String weight : WeightList) {
				JComponent scoreSlider = createFilterSlider(
						Mutils.Weight_NAMESPACE, 
						weight, 
						currentNetwork, 
						false, 
						100.0
				);
				scoreSlider.setMinimumSize(new Dimension(100, 30));
				// scoreSlider.setMaximumSize(new Dimension(100,30));
				filterPanel.add(scoreSlider, d.down().expandBoth());

			}
			
			// filterPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			WeightPanel.add(filterPanel, c.right().expandBoth());
		}

		return;
	}


//------------------------  SHOW COMPLEMENTARITY EDGES WITH SCORE -------------------------------------
	
	
	private JPanel createSeedPanel() {

		subScorePanel = new JPanel();
		subScorePanel.setLayout(new GridBagLayout());
		EasyGBC c = new EasyGBC();

		List<String> seedList = Mutils.getSeedList(currentNetwork);
		
		// create 4 panels: Color, Label, Sign and Score SLIDER

//		-------
//		COLOR 
//		-------
		{
			JPanel colorPanel = new JPanel();
			colorPanel.setMinimumSize(new Dimension(25, 30));
			colorPanel.setLayout(new GridBagLayout());
			EasyGBC d = new EasyGBC();
			JLabel lbl = new JLabel("Color");
			lbl.setToolTipText("Color edges with the selected metabolic index.");
			lbl.setFont(labelFont);
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			colorPanel.add(lbl, d.anchor("north").noExpand());

			for (String seedScore : seedList) {
				colorPanel.add(createScoreCheckBox(seedScore), d.down().expandVert());
			}

			// colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			subScorePanel.add(colorPanel, c.anchor("northwest").expandVert());
		}

//		-------
//		SEED INDEX TYPE (LABEL)
//		-------
		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new GridBagLayout());
			EasyGBC d = new EasyGBC();
			JLabel lbl = new JLabel("Seed Index");
			lbl.setFont(labelFont);
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			labelPanel.add(lbl, d.anchor("north").noExpand());
			for (String seedScore : seedList) {
				JLabel scoreLabel = new JLabel(seedScore);
				scoreLabel.setFont(textFont);
				scoreLabel.setMinimumSize(new Dimension(100, 30));
				scoreLabel.setMaximumSize(new Dimension(100, 30));
				labelPanel.add(scoreLabel, d.down().expandVert());
			}
			labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			subScorePanel.add(labelPanel, c.right().expandVert());
		}

		
//		-------------------------------------
//		SIGN FOR SEEDS  
//		-------------------------------------
				
		{
		    JPanel signPanel = new JPanel();
		    signPanel.setMinimumSize(new Dimension(25, 30));
		    signPanel.setLayout(new GridBagLayout());
		    EasyGBC d = new EasyGBC();

		    JLabel lbl = new JLabel("Sign");
		    
		    lbl.setToolTipText("Choose if the score should be greater or lower than the threshold.");
		    lbl.setFont(labelFont);
		    lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		    signPanel.add(lbl, d.anchor("north").noExpand());

		    // Store selected sign for each seedScore using a JComboBox
		    for (String seedScore : seedList) {

		        JComboBox<String> combo = new JComboBox<>(new String[]{">", "<"});
		        combo.setName("sign_" + seedScore); // helpful for later retrieval
		        signCombos.put(seedScore, combo);
		        signPanel.add(combo, d.down().expandVert());
		    }
		    
		    subScorePanel.add(signPanel, c.right().anchor("northwest").expandVert());
		}

		
//		-------		
//		Score SLIDER
//		-------
		{
			JPanel filterPanel = new JPanel();
			filterPanel.setLayout(new GridBagLayout());
			EasyGBC d = new EasyGBC();
			JLabel lbl = new JLabel("Seed Score");
			lbl.setToolTipText("Hide edges with a seed score higher/lowe than the threshold set.");
			lbl.setFont(labelFont);
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			filterPanel.add(lbl, d.anchor("north").noExpand());

			for (String seedScore : seedList) {
				JComponent scoreSlider = createFilterSlider3(
						Mutils.Seed_NAMESPACE, 
						seedScore, 
						currentNetwork, 
						false, 
						100.0
				);
				scoreSlider.setMinimumSize(new Dimension(100, 30));
				// scoreSlider.setMaximumSize(new Dimension(100,30));
				filterPanel.add(scoreSlider, d.down().expandBoth());

			}
			
			// filterPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			subScorePanel.add(filterPanel, c.right().expandBoth());
		}

		CollapsablePanel collapsablePanel = new CollapsablePanel(
				iconFont, 
				"Filter-out complementarity edges", 
				subScorePanel, 
				false, 12
		);
		collapsablePanel.setToolTipText("Hidee edges with seed, and probably pathway, complementarities based on a seed index score.");
		collapsablePanel.setBorder(BorderFactory.createEtchedBorder());
		collapsablePanel.setAlwaysExpanded();
		return collapsablePanel;

	}
	
	
	public void updateSeedPanel() {

		if (subScorePanel == null)
			return;
		subScorePanel.removeAll();
		EasyGBC c = new EasyGBC();
		List<String> seedList = Mutils.getSeedList(currentNetwork);

		// create 4 panels: Color, Greater/Lower, Label, and Filter

//		-------
//		UPDATE COLOR 
//		-------
		
		{
			
			System.out.println("updaTing color"); 
			
			JPanel colorPanel = new JPanel();
			colorPanel.setMinimumSize(new Dimension(25, 30));
			colorPanel.setLayout(new GridBagLayout());

			EasyGBC d = new EasyGBC();
			JLabel lbl = new JLabel("Color");
			lbl.setToolTipText("Color edges with the selected metabolic index.");
			lbl.setFont(labelFont);
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			colorPanel.add(lbl, d.anchor("north").noExpand());

			for (String seedScore : seedList) {
				colorPanel.add(createScoreCheckBox(seedScore), d.down().expandVert());
			}
			subScorePanel.add(colorPanel, c.anchor("northwest").expandVert());
		}

		
//		--------------
//		UPDATE SEED INDEX TYPE (LABEL) 
//		--------------
		
		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new GridBagLayout());
			EasyGBC d = new EasyGBC();
			JLabel lbl = new JLabel("Seed Index");
			lbl.setFont(labelFont);
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			labelPanel.add(lbl, d.anchor("north").noExpand());
			for (String seedScore : seedList) {
				JLabel scoreLabel = new JLabel(seedScore);
				scoreLabel.setFont(textFont);
				scoreLabel.setMinimumSize(new Dimension(100, 30));
				scoreLabel.setMaximumSize(new Dimension(100, 30));
				labelPanel.add(scoreLabel, d.down().expandVert());
			}
			labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			subScorePanel.add(labelPanel, c.right().expandVert());
		}
		
//		--------------
//		UPDATE SIGN 
//		--------------
		
		{
		    JPanel signPanel = new JPanel();
		    signPanel.setMinimumSize(new Dimension(25, 30));
		    signPanel.setLayout(new GridBagLayout());
		    EasyGBC d = new EasyGBC();

		    JLabel lbl = new JLabel("Sign");
		    lbl.setToolTipText("Choose if the score should be greater or lower than the threshold.");
		    lbl.setFont(labelFont);
		    lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		    signPanel.add(lbl, d.anchor("north").noExpand());

		    // Store selected sign for each seedScore using a JComboBox
		    for (String seedScore : seedList) {

		    	System.out.println("updating filter panel for sign: " + seedScore);
		    	
		    	JComboBox<String> combo = new JComboBox<>(new String[]{">", "<"});		        
		        combo.setName("sign_" + seedScore); // helpful for later retrieval
		        signCombos.put(seedScore, combo);
		        signPanel.add(combo, d.down().expandVert());
		    }
		    
		    System.out.println("\n\n\n\n this is my updatedd combo: " + signCombos);

		    subScorePanel.add(signPanel, c.right().anchor("northwest").expandVert());
		}
		
//		-------		
//		UPDATE Score SLIDER
//		-------
		
		{

			JPanel filterPanel = new JPanel();
			filterPanel.setLayout(new GridBagLayout());
			EasyGBC d = new EasyGBC();
			JLabel lbl = new JLabel("Seed Score");
			lbl.setToolTipText("Hide edges score below the chosen.");
			lbl.setFont(labelFont);
			lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
			filterPanel.add(lbl, d.anchor("north").noExpand());
			for (String seedScore : seedList) {

				JComponent scoreSlider = createFilterSlider3(
						Mutils.Seed_NAMESPACE, 
						seedScore, 
						currentNetwork, 
						false, 
						100.0
				);
				
				scoreSlider.setMinimumSize(new Dimension(100, 30));
				filterPanel.add(scoreSlider, d.down().expandBoth());

			}
			// filterPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			subScorePanel.add(filterPanel, c.right().expandBoth());
		}
		return;
	}

	
//	----  END OF UPDATE SeedPanel
	
	
	private JComponent createScoreCheckBox(String seedScore) {

		Map<String, Color> colorMap = manager.getChannelColors();
		JCheckBox cb = new JCheckBox("");
		cb.setMinimumSize(new Dimension(20, 30));
		cb.setMaximumSize(new Dimension(20, 30));
		cb.setBackground(colorMap.get(seedScore));
		cb.setOpaque(true);

		if (
				colors.containsKey(currentNetwork) && 
				colors.get(currentNetwork).containsKey(seedScore) && 
				colors.get(currentNetwork).get(seedScore)
		)
			cb.setSelected(true);

		// Listener		
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

	
	
	// Function to color edges with a certain type of seed index 
	void doColors() {
		Map<String, Boolean> color = colors.get(currentNetwork);
		Map<String, Color> colorMap = manager.getChannelColors();
		CyNetworkView view = manager.getCurrentNetworkView();
		for (CyEdge edge : currentNetwork.getEdgeList()) {
			CyRow edgeRow = currentNetwork.getRow(edge);
			double max = -1;
			Color clr = null;
			for (String lbl : color.keySet()) {
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

	
//	---------------------------------------
	
//	SECOND PART OF THE PANEL FOR THE SELECTED EDGES
	
//	----------------------------------------

	
	
	// ------------------------------------------------------------------------------------

	private JPanel createEdgesPanel() {
		
		edgesSPanel = new JPanel();
		edgesSPanel.setLayout(new GridBagLayout());
		EasyGBC c = new EasyGBC();

		if (currentNetwork != null) {
			List<CyEdge> edges = CyTableUtil.getEdgesInState(currentNetwork, CyNetwork.SELECTED, true);
			for (CyEdge edge : edges) {
				JPanel newPanel = createEdgePanel(edge);
				newPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

				edgesSPanel.add(newPanel, c.anchor("west").down().expandHoriz());
			}
		}
		edgesSPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		CollapsablePanel collapsablePanel = new CollapsablePanel(iconFont, "Selected edges", edgesSPanel, false, 12);
		collapsablePanel.setAlwaysExpanded();
		collapsablePanel.setBorder(BorderFactory.createEtchedBorder());
		return collapsablePanel;
	}


	
	private void updateEdgesPanel() {
		if (edgesSPanel == null)
			return;
		edgesSPanel.removeAll();
		EasyGBC c = new EasyGBC();

		List<CyEdge> edges = CyTableUtil.getEdgesInState(currentNetwork, CyNetwork.SELECTED, true);

		if (edges.size() > 50) {
			return;
		}
		for (CyEdge edge : edges) {
			JPanel newPanel = createEdgePanel(edge);
			newPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			edgesSPanel.add(newPanel, c.anchor("west").down().expandHoriz());
		}
		return;
	}

	// method to get taxon name from node table
	
	
	private String getTaxonName(CyTable nodeTable, CyNode node) {
        if (nodeTable.getColumn("taxonomy::species") != null) {
            Object taxonValue = nodeTable.getRow(node.getSUID()).get("taxonomy::species", String.class);
            return taxonValue != null ? taxonValue.toString() : null;
        }
        return null;
    }


	//	MAIN EDGE PANEL OF THE SELECTED EDGES, e.g. below the filters GUI
	private JPanel createEdgePanel(CyEdge edge) {

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // use the full horizontal space
        gbc.weighty = 1.0;

        // Set constraints
        gbc.gridx = 0; // Column 0
        gbc.gridy = 1; // Start from row 0
        gbc.ipady = 10;  // This sets the height, in pixels of each line in the beneficiary, donor  ids, part of the table
        gbc.anchor = GridBagConstraints.WEST; // Left-align 
        gbc.insets = new Insets(5, 5, 5, 5); // 5pix marg

        CyNetwork currentNetwork = manager.getCurrentNetwork();
        if (currentNetwork == null) return panel;


        CyTable edgeTable = currentNetwork.getDefaultEdgeTable();
        CyTable nodeTable = currentNetwork.getDefaultNodeTable();
        //String name = null;
        
        // Retrieve source and target node of the edge
        CyNode sourceNode = edge.getTarget(); // edge.getSource();
        CyNode targetNode = edge.getSource(); //edge.getTarget();
        
        // Get taxon names for source and target nodes
        String sourceTaxon = getTaxonName(nodeTable, sourceNode);
        String targetTaxon = getTaxonName(nodeTable, targetNode);
                
        JTextPane sourceTaxonPane = ViewUtils.createStyledLabelEdges("Donor Taxon: " + targetTaxon);
        
        panel.add(sourceTaxonPane, gbc);
        gbc.gridy++;
    
        JTextPane targetTaxonPane = ViewUtils.createStyledLabelEdges("Beneficiary Taxon: " + sourceTaxon);
        panel.add(targetTaxonPane, gbc);
        gbc.gridy++;

        Object nameValue = (edgeTable.getColumn("shared name") != null) ? edgeTable.getRow(edge.getSUID()).get("shared name", edgeTable.getColumn("shared name").getType()) : null;

        // Split the name to get Donor and Beneficiary
        String[] nameParts = nameValue != null ? 
        		nameValue
        		.toString()
        		.split(
        				" \\(completed by\\) | \\(cooccurs with\\) | \\(depletes\\)") : new String[] {
		            "",
		            ""
		        };
        
        String donor = nameParts.length > 0 ? nameParts[0] : "";
        String beneficiary = nameParts.length > 1 ? nameParts[1] : "";

        JTextPane donorPane = ViewUtils.createStyledLabelEdges("Donor / Seed Set B: " +  beneficiary );
        panel.add(donorPane, gbc);
        gbc.gridy++;
              
        JTextPane BeneficiaryPane = ViewUtils.createStyledLabelEdges("Beneficiary / Seed Set A: " + donor);
        panel.add(BeneficiaryPane, gbc);
        gbc.gridy++;
        

        Object interactionValue = (
        		edgeTable.getColumn("interaction type") != null) ? 
        				edgeTable.getRow(edge.getSUID())
        				.get(
        						"interaction type", 
        						edgeTable.getColumn("interaction type"
        				).getType()) : null;
        
        JTextPane interactionPane = ViewUtils.createStyledLabelEdges(
        		"Interaction: " + (interactionValue != null ? interactionValue.toString() : "null")
        );        
        panel.add(interactionPane, gbc);
        gbc.gridy++;
        

        boolean showseedpanel=true;
        
        Object cooperationSeedValue = (edgeTable.getColumn("seed::cooperation") != null) ? edgeTable.getRow(edge.getSUID()).get("seed::cooperation", edgeTable.getColumn("seed::cooperation").getType()) : null;
        if (cooperationSeedValue != null && showseedpanel==false) {
            JTextArea cooperationSeedArea = new JTextArea("Seed Scores: Cooperation : " + cooperationSeedValue.toString());
            ViewUtils.setJTextAreaAttributes(cooperationSeedArea);
           panel.add(cooperationSeedArea, gbc);
            gbc.gridy++;
        }

//        Object competitionSeedValue = (edgeTable.getColumn("seed::competition") != null) ? edgeTable.getRow(edge.getSUID()).get("seed::competition", edgeTable.getColumn("seed::competition").getType()) : null;
        Object competitionSeedValue;
        if (edgeTable.getColumn("seed::competition") != null) {

            competitionSeedValue = edgeTable.getRow(edge.getSUID())
                                           .get("seed::competition", edgeTable.getColumn("seed::competition").getType());
        } else {
            competitionSeedValue = null;
        }

        
        if (competitionSeedValue != null &&showseedpanel==false) {
            JTextArea competitionSeedArea = new JTextArea("Seed Scores: Competition: " + competitionSeedValue.toString());
           ViewUtils.setJTextAreaAttributes(competitionSeedArea);
            panel.add(competitionSeedArea, gbc);
            gbc.gridy++;
       }

        
        // Create a sub-panel  for the complement input components
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        // Label 
        JLabel complementLabel = new JLabel("Search KEGG term: ");
        complementLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        inputPanel.add(complementLabel);

        // JTextField for input
        JTextField complementField = new JTextField(10); 
        complementField.setToolTipText("This term should adhere to the format 'K00928' or 'M00001' "); 
        inputPanel.add(complementField);

        // JButton to open the link
        JButton openLinkButton = new JButton("Open Link");
        inputPanel.add(openLinkButton);

    
        panel.add(inputPanel, gbc);
        gbc.gridy++;

        
        openLinkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                String complement = complementField.getText().trim();

                if (!complement.isEmpty()) {
                    complement = complement.replace("[", ""); 
                    
                    String url = "https://www.genome.jp/entry/" + complement;
                    SwingLink link = new SwingLink(complement, url, openBrowser);
                    link.open1(link.getURI()); 
                }
            }
        });


        //------------------- nested Pathways Panel---------------------------------------

        JPanel PathwaysPanel = new JPanel();
        PathwaysPanel.setLayout(new GridBagLayout());
        GridBagConstraints pathgbc = new GridBagConstraints();

        pathgbc.fill = GridBagConstraints.HORIZONTAL;
        pathgbc.weightx = 1.0; // use the full horizontal space

        // Set constraints
        pathgbc.gridx = 0; // Column 0
        pathgbc.gridy = 0; // Start from row 0
        pathgbc.anchor = GridBagConstraints.WEST; // Left-align 
        pathgbc.insets = new Insets(5, 5, 5, 5); // 5pixel marg

        Border etchedBorder = BorderFactory.createEtchedBorder();
        Border emptyBorder = BorderFactory.createEmptyBorder(0, 5, 0, 0);
                        
        
        boolean hasPathwayComplementarities = false; // Flag to track
 
        for (CyColumn column : edgeTable.getColumns()) {

            if (column.getName().startsWith("compl::")) {

            	Object columnValue = edgeTable.getRow(edge.getSUID()).get(column.getName(), column.getType());

                if (columnValue == null) {
                    continue; // Skip to the next iteration if columnValue is null
                }
            	
            	String[] entries = columnValue.toString().split(",");
                boolean keggNotEmpty = false;
                keggNotEmpty = isNotEmpty(entries);
                
                if (keggNotEmpty) {

                	String columnName = column.getName().substring(7); // Extracting column name
                	String panelTitle = "Genomes: " + columnName.replace(":", " : ");
                	JPanel newPanel = new JPanel(new BorderLayout());

                	hasPathwayComplementarities = true;
                	
                	//DefaultTableModel tableM = new DefaultTableModel(new String[]{"Kegg Module", "Complement", "Module Alternative", "Color Map"}, 0);
                	DefaultTableModel tableM = new DefaultTableModel() {
                		 private static final long serialVersionUID = 1L;

						@Override
                		    public Class<?> getColumnClass(int columnIndex) {
                		        switch (columnIndex) {
                		            //case 0: return SwingLink.class; // For Kegg Module links
                		            //case 3: return SwingLink.class; // for Complement
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

                    for (String entry : entries) {
                        String[] parts = entry.split("\\^");
                        if (parts.length >= 6) {
                        	
                        	// Handle Kegg Module Link
                        	
                        	 String moduleId = parts[0].trim(); // Trim to remove before and after spaces
                             moduleId = moduleId.replace("[", "").replace("]", ""); // Remove  unwanted "[" "]" 
                            // SwingLink  link = new SwingLink(moduleId, "https://www.genome.jp/entry/" + moduleId, openBrowser);
                             

                             
                             String colorMapUrlString = parts[5].trim();
                             colorMapUrlString = colorMapUrlString.replace("[", "").replace("]", "");
                             SwingLink  colorMapLink = new SwingLink("     Url", colorMapUrlString, openBrowser);
                            
                             //add the rows to the table
                             tableM.addRow(new Object[]{moduleId, parts[1], parts[2], parts[3], parts[4], colorMapLink});
                 
                        }
                    }
                    
                 
                	JTable table = new JTable(tableM);
                
                	//  custom renderer for SwingLink class
                	table.setDefaultRenderer(SwingLink.class, new SwingLinkCellRenderer());
                	table.getColumnModel().getColumn(0).setCellRenderer(new BlueUnderlineHTMLRenderer());
                	table.getColumnModel().getColumn(3).setCellRenderer(new BlueUnderlineHTMLRenderer());
                	table.getColumnModel().getColumn(4).setCellRenderer(new BlueUnderlineHTMLRenderer());
                	
                	table.addMouseListener(new MouseAdapter() {
                	    private Timer clickTimer = null;
                	    private final int doubleClickDelay = 400; // ms delay 

                	    public void mouseClicked(MouseEvent e) {
                	        int row = table.rowAtPoint(e.getPoint());
                	        int col = table.columnAtPoint(e.getPoint());
                	        Object cellValue = table.getValueAt(row, col);
                	        
                	        if (table.getColumnClass(col).equals(SwingLink.class)) {
                	            SwingLink link = (SwingLink) table.getValueAt(row, col);
                	            link.open1(link.getURI());}

                	        else if (col == 0 || col == 3 || col == 4) {
                	            Runnable action = () -> {
                	                if (cellValue instanceof String) {
                	                    String cellText = (String) cellValue;
                	                    String[] elements = cellText.split(";");

                	                    // Get the x-cord of the click relative to the cell
                	                    int clickX = e.getX() - table.getCellRect(row, col, true).x;

                	                    int cumulativeWidth = 0;
                	                    for (String element : elements) {
                	                        // Calculate the width of the current element
                	                        int elementWidth = table.getFontMetrics(table.getFont()).stringWidth(element);

                	                        // Check if the click is within the current element
                	                        if (clickX >= cumulativeWidth && clickX <= cumulativeWidth + elementWidth) {
                	                            System.out.println("Clicked on: " + element);
                	                            
                	                            SwingLink link = new SwingLink(element, "https://www.genome.jp/entry/" + element, openBrowser);
                	                            link.open1(link.getURI());
                	                            break; // Exit the loop 
                	                        }
                	                        cumulativeWidth += elementWidth;
                	                    }
                	                }
                	            };

                	            if (e.getClickCount() == 1) {
                	                if (clickTimer != null && clickTimer.isRunning()) {
                	                    clickTimer.stop();
                	                    action.run(); // Execute the action immediately on double-click
                	                } else {
                	                    // Start the timer for a single click
                	                    clickTimer = new Timer(doubleClickDelay, ae -> action.run());
                	                    clickTimer.setRepeats(false);
                	                    clickTimer.start();
                	                }
                	            }
                	        }
                	    }
                	});
                	

                	
            
                	// size of the scroll pane based on the number of rows
                	int minHeight = 100; // minimum height in pixels
                    int rowHeight = table.getRowHeight();
                    int tableHeight = (table.getRowCount() * rowHeight) + table.getTableHeader().getPreferredSize().height;
                    JScrollPane scrollPane = new JScrollPane(table);
                    
                    int preferredHeight = Math.max(minHeight, Math.min(tableHeight, 600));
                    
                    scrollPane.setPreferredSize(
                    		new Dimension(
                    				scrollPane.getPreferredSize().width, 
                    				preferredHeight
                    		)
                    ); //  maximum height to 400 pixels

                    newPanel.add(scrollPane, BorderLayout.CENTER);

                	CollapsablePanel collapsablePanel = new CollapsablePanel(iconFont, panelTitle, newPanel, true, 12);
                	collapsablePanel .setBorder(BorderFactory.createCompoundBorder(emptyBorder, etchedBorder));
                	PathwaysPanel.add(collapsablePanel, pathgbc);
                	pathgbc.gridy++;
                }
            }
        }
        
        
        if (hasPathwayComplementarities) {
			CollapsablePanel PathwaysCollapsablePanel = new CollapsablePanel(iconFont, "Pathway Complementarities", PathwaysPanel, true, 12);
			PathwaysCollapsablePanel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, etchedBorder));
			showseedpanel=false;
			panel.add(PathwaysCollapsablePanel, gbc);
			gbc.gridy++;
     
        }
  //----------------------Nested Seed complementarities panel ---------------------------//
     
     JPanel SeedComplementaritiesPanel = new JPanel();
     SeedComplementaritiesPanel.setLayout(new GridBagLayout());
     GridBagConstraints seedgbc = new GridBagConstraints();

     seedgbc.fill = GridBagConstraints.HORIZONTAL;
     seedgbc.weightx = 1.0; // use the full horizontal space

     // Set constraints
     seedgbc.gridx = 0; // Column 0
     seedgbc.gridy = 0; // Start from row 0
     seedgbc.anchor = GridBagConstraints.WEST; // Left-align 
     seedgbc.insets = new Insets(5, 5, 5, 5); // 5pixel marg

       
     if (cooperationSeedValue != null) {
         JLabel cooperationLabel = new JLabel("Cooperation Seed Score: " + cooperationSeedValue.toString());
         cooperationLabel.setFont(textFont);
         SeedComplementaritiesPanel.add(cooperationLabel, seedgbc);
         seedgbc.gridy++;
     }

     if (competitionSeedValue != null) {
         JLabel competitionLabel = new JLabel("Competition Seed Score: " + competitionSeedValue.toString());
         competitionLabel.setFont(textFont);
         SeedComplementaritiesPanel.add(competitionLabel, seedgbc);
         seedgbc.gridy++;
     }
     
     
     boolean hasSeedComplementarities = false;
     
     for (CyColumn column : edgeTable.getColumns()) {

    	 if (column.getName().startsWith("seedCompl::")) {
        	         	 
             Object columnValue = edgeTable.getRow(edge.getSUID()).get(column.getName(), column.getType());
             
             
             if (columnValue == null) {
                 continue; // Skip to the next iteration if columnValue is null
             }
             
             
             String[] entries = columnValue.toString().split(",");

             boolean seedsNotEmpty = false;
             seedsNotEmpty = isNotEmpty(entries);
	          
	          if (seedsNotEmpty) {             

            	String columnName = column.getName().substring(7); // Extracting column name
             	String panelTitle = "Genomes: " + columnName.replace(":", " : ");
             	JPanel newPanel = new JPanel(new BorderLayout());
             	
             	hasSeedComplementarities = true;
            	
            	//DefaultTableModel tableM = new DefaultTableModel(new String[]{"Kegg Module", "Complement", "Module Alternative", "Color Map"}, 0);
            	DefaultTableModel tableM = new DefaultTableModel() {
            		 private static final long serialVersionUID = 1L;

					@Override
            		    public Class<?> getColumnClass(int columnIndex) {
            		        switch (columnIndex) {
            		            //case 2: return SwingLink.class; // For Kegg Module links
            		           // case 3: return SwingLink.class;
            		            //case 3: return SwingLink.class; // for Complement
            		            case 4: return SwingLink.class; //for color map links
            		            default: return Object.class;
            		        }
            		    }
            		};

            	tableM.addColumn("Category");
            	tableM.addColumn("KEGG map");
            	tableM.addColumn("Complements (ModelSeed ids)");
            	tableM.addColumn("Complements (KEGG ids)");
            	tableM.addColumn("Color map");

                for (String entry : entries) {
                    String[] parts = entry.split("\\^");
                    if (parts.length >= 5) {
                    	
                    	 String moduleId = parts[0].trim(); // Trim to remove before and after spaces
                         moduleId = moduleId.replace("[", "").replace("]", ""); // Remove  unwanted "[" "]" 
                         //SwingLink  link = new SwingLink(moduleId, "https://www.genome.jp/entry/" + moduleId, openBrowser);
                         
                         String seedIdlink = parts[2].trim(); // Trim to remove before and after spaces
                        // seedId = seedId.replace("[", "").replace("]", ""); // Remove  unwanted "[" "]" 
                        // SwingLink  seedlink = new SwingLink(seedId, "https://modelseed.org/biochem/compounds/" + seedId, openBrowser);
                         
                         String keggIdlink = parts[3].trim(); // Trim to remove before and after spaces
                        // keggId = keggId.replace("[", "").replace("]", ""); // Remove  unwanted "[" "]" 
                       //  SwingLink  kegglink = new SwingLink(keggId, "https://www.kegg.jp/entry/" + keggId, openBrowser);
                         
                         
                         String colorMapUrlString = parts[4].trim();
                         colorMapUrlString = colorMapUrlString.replace("[", "").replace("]", "");
                         SwingLink  colorMapLink = new SwingLink(" Url", colorMapUrlString, openBrowser);
                        
                         //add the rows to the table
                         tableM.addRow(new Object[]{ moduleId, parts[1], seedIdlink, keggIdlink, colorMapLink});
             
                    }
                }   	
                
            	JTable table = new JTable(tableM);
            	
            	table.getColumnModel().getColumn(4).setCellRenderer(new SwingLinkCellRenderer());
            	table.getColumnModel().getColumn(2).setCellRenderer(new BlueUnderlineHTMLRenderer());
            	table.getColumnModel().getColumn(3).setCellRenderer(new BlueUnderlineHTMLRenderer());

            	table.addMouseListener(new MouseAdapter() {
            	    private Timer clickTimer = null;
            	    private final int doubleClickDelay = 400; // delay 

            	    public void mouseClicked(MouseEvent e) {
            	        int row = table.rowAtPoint(e.getPoint());
            	        int col = table.columnAtPoint(e.getPoint());
            	        Object cellValue = table.getValueAt(row, col);
            	        
            	        if (table.getColumnClass(col).equals(SwingLink.class)) {
            	            SwingLink link = (SwingLink) table.getValueAt(row, col);
            	            link.open1(link.getURI());}

            	            else  if (col == 2 || col == 3) {
            	            Runnable action = () -> {
            	                if (cellValue instanceof String) {
            	                    String cellText = (String) cellValue;
            	                    String[] elements = cellText.split(";");

            	                    int clickX = e.getX() - table.getCellRect(row, col, true).x;
            	                    int cumulativeWidth = 0;
            	                    for (String element : elements) {
            	                        int elementWidth = table.getFontMetrics(table.getFont()).stringWidth(element);
            	                        if (clickX >= cumulativeWidth && clickX <= cumulativeWidth + elementWidth) {
            	                            String url;
            	                            if (col == 2) {
            	                                url = "https://modelseed.org/biochem/compounds/" + element;
            	                            } else {
            	                                // col == 3
            	                                url = "https://www.genome.jp/entry/" + element;
            	                            }
            	                            SwingLink link = new SwingLink(element, url, openBrowser);
            	                            link.open1(link.getURI());
            	                            break;
            	                        }
            	                        cumulativeWidth += elementWidth;
            	                    }
            	                }
            	            };

            	            if (e.getClickCount() == 1) {
            	                if (clickTimer != null && clickTimer.isRunning()) {
            	                    clickTimer.stop();
            	                    action.run(); // Execute immediately for double-click
            	                } else {
            	                    // Start the timer for a single click
            	                    clickTimer = new Timer(doubleClickDelay, ae -> action.run());
            	                    clickTimer.setRepeats(false);
            	                    clickTimer.start();
            	                }
            	            }
            	        }
            	    }
            	});
        
            	// size of the scroll pane based on the number of rows
            	int minHeight = 100; // minimum height in pixels
            	int rowHeight = table.getRowHeight();                
                int tableHeight = (table.getRowCount() * rowHeight) + table.getTableHeader().getPreferredSize().height;
                
                JScrollPane scrollPane = new JScrollPane(table);
                
                int preferredHeight = Math.max(minHeight, Math.min(tableHeight, 600));
                scrollPane.setPreferredSize(
                		new Dimension(
                				scrollPane.getPreferredSize().width, 
                				preferredHeight
                		)
                ); //  maximum height to 400 pixels

                                
                scrollPane.setBorder(BorderFactory.createCompoundBorder(
                	    BorderFactory.createEtchedBorder(), // outer border
                	    BorderFactory.createEmptyBorder(10, 10, 10, 10) // top, left, bottom, right padding
                	));

                
                
                newPanel.add(scrollPane, BorderLayout.CENTER);

            	CollapsablePanel collapsablePanel = new CollapsablePanel(iconFont, panelTitle, newPanel, true, 12);
            	collapsablePanel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, etchedBorder));
            	SeedComplementaritiesPanel.add(collapsablePanel, seedgbc);
            	seedgbc.gridy++;
            }
        }
    }

    
	if(hasSeedComplementarities) {
		CollapsablePanel SeedCollapsablePanel = new CollapsablePanel(iconFont, "Seed Complementarities", SeedComplementaritiesPanel, true, 12);
		SeedCollapsablePanel  .setBorder(BorderFactory.createCompoundBorder(emptyBorder, etchedBorder));
		panel.add(SeedCollapsablePanel , gbc);
		gbc.gridy++;
		
	}

	//---------------------------------------------add the panel--------------------------------
   
        String edgeId = (nameValue != null) ? nameValue.toString() : "Selected Edges";

        CollapsablePanel collapsablePanel = new CollapsablePanel(iconFont, edgeId, panel, false, 12);
        
        collapsablePanel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, etchedBorder));
        collapsablePanel.setAlwaysExpanded();

        return collapsablePanel;

     }


	
	
//	---------------------------------------
	
//	END OF SECOND PART OF THE PANEL FOR THE SELECTED EDGES
	
//	----------------------------------------	
	
	

	
//	---------------------------------------
	
//	BACK TO FILTERING AND THE UPPER PART OF THE PANEL
	
//	----------------------------------------	

	
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
	
	    	double minValue = 1.0; 
	    	
	    	for (CyEdge edge: currentNetwork.getEdgeList()) {

	    		CyRow edgeRow = currentNetwork.getRow(edge);
	            
	            Double edgeScore = edgeRow.get(type, label, Double.class); // Get the edge weight 
	            
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
	double initFilterSeed(String type, String label) {
	
    	double minValue = 1.0;
	
		for (CyEdge edge: currentNetwork.getEdgeList()) {

			CyRow edgeRow = currentNetwork.getRow(edge);
			
            Double v = edgeRow.get(type, label, Double.class);
            if (v == null) {
                minValue = 0.0;
                break;
            } else if (v < minValue) {
                minValue = v.doubleValue();
            }
        
		
		}

		return minValue;

	}



//	---------------------------------------------------
	@Override
	String initSign(String type, String label) {

		String v = ">";
		
        return v;
	}
	
//	---------------------------------------------------
	
	
	
	
	
    @Override
    void doFilter(String type) {
    	
    	System.out.println("ini dofilter");
    	
    	// SCORE TYPES
    	List<String> seedList = Mutils.getSeedList(currentNetwork);
    	List<String> WeightList = Mutils.getWeightList(currentNetwork);

    	List<String> mergedList = new ArrayList<>(seedList); 
    	mergedList.addAll(WeightList); 	
    	
    	// SIGNS
    	Map<String, String> signMap = filterSign.get(currentNetwork).get(type);    	
    	for (String score : mergedList) {
    	    JComboBox<String> combo = signCombos.get(score);
    	    if (combo != null) {
    	        String selectedSign = (String) combo.getSelectedItem();
    	        System.out.println("Selected sign for " + score + ": " + selectedSign);    	        
    	        // You can store it in your `filterSign` map if needed:
    	        
    	        if (signMap != null) {
    	            signMap.put(score, selectedSign);
    	        }
    	    }
    	}    	
    	System.out.println("\n\n >>can i print a map?" + signMap);
    	
        // THRESHOLDS --- Check if the network and filter type exists
        Map < String, Double > filter = filters.get(currentNetwork).get(type);
        for (Map.Entry<String, Double> entry : filter.entrySet()) {
	      System.out.println(" --> Label: " + entry.getKey() + ", Score Type: " + entry.getValue());
        }

        // Iterate through each edge in the current network.
        CyNetworkView view = manager.getCurrentNetworkView();

        for (CyEdge edge: currentNetwork.getEdgeList()) {

        	CyRow edgeRow = currentNetwork.getRow(edge);

            boolean show = true;
            
            for (String lbl: filter.keySet()) {            	
            	
            	Double v = edgeRow.get(type, lbl, Double.class);
                Double nv = (filter.get(lbl));

                // If the value is null we need to keep the edge as shown, thus we make it 100.
                double actualValue = (v == null) ? 100.0 : v;

//                System.out.println(">> lbl: " + lbl);
//                System.out.println(">> Actual value: " + actualValue  );
//                System.out.println("~~ My sign: " + signMap.get(lbl));
                                
                String sign = signMap.get(lbl);
                
                if (sign.equals(">") && actualValue < nv) {
                    show = false;
                    break;
                } else if (sign.equals("<") && actualValue > nv) {
                    show = false;
                    break;
                }
            }

            if (show) {
                // Make the edge visible
                view.getEdgeView(edge).clearValueLock(BasicVisualLexicon.EDGE_VISIBLE);

            } else {

            	// Hide the edge and deselect it if it doesn't meet the criteria
                view.getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, false);              
                view.getModel().getRow(edge).set(CyNetwork.SELECTED, false);

            }
        }
    }




    
    public void networkChanged(CyNetwork newNetwork) {

    	
    	System.out.println("network was changed !");
    	
    	
    	this.currentNetwork = newNetwork;

        if (currentNetwork == null) {
            if (subScorePanel != null) subScorePanel.removeAll();
            if (WeightPanel != null) WeightPanel.removeAll();
            return;
        }

        // Initialize filters for the current network
        if (!filters.containsKey(currentNetwork)) {
            Map<String, Map<String, Double>> typeMap = new HashMap<>();
            typeMap.put(Mutils.Weight_NAMESPACE, new HashMap<>());
            typeMap.put(Mutils.Seed_NAMESPACE, new HashMap<>());
            filters.put(currentNetwork, typeMap);
        }

        // Initialize filter signs
        if (!filterSign.containsKey(currentNetwork)) {
            Map<String, Map<String, String>> signMap = new HashMap<>();
            signMap.put(Mutils.Weight_NAMESPACE, new HashMap<>());
            signMap.put(Mutils.Seed_NAMESPACE, new HashMap<>());
            filterSign.put(currentNetwork, signMap);
        }

        // Initialize colors
        if (!colors.containsKey(currentNetwork)) {
            colors.put(currentNetwork, new HashMap<>());
        }

        updateSeedPanel();
        updateWeightPanel();
        updateEdgesPanel();
    }

    
 
    
    public String getSelectedSign(String label) {
        JComboBox<String> combo = signCombos.get(label);
        return (combo != null) ? (String) combo.getSelectedItem() : ">";
    }
    
    
    
    
    public void selectedEdges(Collection < CyEdge > edges) {

        edgesSPanel.removeAll();
        EasyGBC c = new EasyGBC();
        //Mutils.clearHighlight(manager, manager.getCurrentNetworkView());

        for (CyEdge edge: edges) {
            JPanel newPanel = createEdgePanel(edge);
            newPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            edgesSPanel.add(newPanel, c.anchor("west").down().expandHoriz());
        }



        revalidate();
        repaint();
    }


}