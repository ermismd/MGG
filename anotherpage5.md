## WEEK 15 & WEEK 16


1. MGGCytoPanel serves as the skeletal structure for the Cytopanel (East).
2. AbstractMGGPanel: Abstract class to provide a basis for the data filtering functionalities.

 * * *

 
 
<html>
<head>
  <style>
	  h1 {
      font-size: 18px;  /* Adjust the font size for h1 as needed */
    }
    h2 {
      font-size: 18px;  /* Adjust the font size for h2 as needed */
    }
   .panel {
      display: none;
      background-color: #f1f1f1;
      padding: 10px;
      margin-top: 10px;
      font-size: 10px; /* Increase the font size as needed */
      width: 800px; /* Increase the width as needed */
    }
  </style>
</head>
<body>
  <h1>MGGCytoPanel</h1>
  <button onclick="MGGCytoPanel()">Expand</button>
  <div class="panel" id="MGGCytoPanel">
    <pre>
	 
		public class MGGCytoPanel extends  JPanel implements  CytoPanelComponent2, SetCurrentNetworkListener, SelectedNodesAndEdgesListener  {

	final MGGManager manager;

	// Define new colors
	 public static final Color[] MY_COLORS = new Color[] { Color.BLACK, Color.RED, Color.BLUE, Color.YELLOW };
	 // Create a Font object
	 private static final  Font myFont = new Font("Arial", Font.PLAIN, 16);
	 
	private JTabbedPane tabs;
	//private StringNodePanel nodePanel;
	private MGGEdgePanel edgePanel;
	private boolean registered = false;
	 private static final Icon icon = new TextIcon(new String[] { "MGG" }, new Font[] { myFont }, MY_COLORS, 16, 16);
	
	public MGGCytoPanel(final MGGManager manager) {
		this.manager = manager;
		this.setLayout(new BorderLayout());
		tabs = new JTabbedPane(JTabbedPane.BOTTOM);
		//nodePanel = new StringNodePanel(manager);
		//tabs.add("Nodes", nodePanel);
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
		//nodePanel.networkChanged(manager.getCurrentNetwork());
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
		//nodePanel.removeFilters(current);
		//nodePanel.undoFilters();
		//nodePanel.networkChanged(current);
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
		//nodePanel.updateControls();
		edgePanel.updateScore();
		//edgePanel.updateSubPanel();
	}

	@Override
	public void handleEvent(SelectedNodesAndEdgesEvent event) {
		if (!registered) return;
		// Pass selected nodes to nodeTab
		//nodePanel.selectedNodes(event.getSelectedNodes());
		// Pass selected edges to edgeTab
		edgePanel.selectedEdges(event.getSelectedEdges());
	}

	@Override
	public void handleEvent(SetCurrentNetworkEvent event) {
		  CyNetwork network = event.getNetwork();

		    if (network == null) {
		        hideCytoPanel();
		        return;
		    }

		    // Check for the existence of the "flashweave-score" attribute on edges
		    boolean hasFlashweaveScore = network.getRow(network).get("flashweave-score",Double.class) != null;

			/*
			 * // Further checks can be added to see if the flashweave-scores are unique,
			 * different, etc. // For example: Set<Double> uniqueScores = new HashSet<>();
			 * boolean hasDifferentScores = false; if (hasFlashweaveScore) { for (CyEdge
			 * edge : network.getEdgeList()) { Double score =
			 * network.getRow(edge).get("flashweave-score", Double.class); if (score !=
			 * null) { if (uniqueScores.contains(score)) { hasDifferentScores = true; break;
			 * } uniqueScores.add(score); } } }
			 */

		    // Based on the above checks, decide whether to show the CytoPanel
		    if (hasFlashweaveScore) {
		        if (!registered) {
		            showCytoPanel();
		        }

		        // Inform the tabs
		       // nodePanel.networkChanged(network);
		        edgePanel.networkChanged(network);
		    } else {
		        hideCytoPanel();
		    }
	
	
}}

		    		

   </pre>
  </div>


  <h2>AbstractMggPanel</h2>
  <button onclick="AbstractMggPanel()">Expand</button>
  <div class="panel" id="AbstractMggPanel">
    <pre>
 
		
          	
          	public abstract class AbstractMggPanel extends JPanel {
          
          	protected final MGGManager manager;
          	protected final OpenBrowser openBrowser;
          	protected final Font iconFont;
          	protected final Font labelFont;
          	protected final Font textFont;
          	protected CyNetwork currentNetwork;
          	protected Map<CyNetwork, Map<String,Map<String, Double>>> filters;
          
          	public AbstractMggPanel(final MGGManager manager) {
          		this.manager = manager;
          		this.openBrowser = manager.getService(OpenBrowser.class);
          		this.currentNetwork = manager.getCurrentNetwork();
          		IconManager iconManager = manager.getService(IconManager.class);
          		iconFont = iconManager.getIconFont(17.0f);
          		labelFont = new Font("SansSerif", Font.BOLD, 10);
          		textFont = new Font("SansSerif", Font.PLAIN, 10);
          		filters = new HashMap<>();
          		filters.put(currentNetwork, new HashMap<>());
          	}
          
          	abstract void doFilter(String type);
          	
          	abstract void undoFilters();
          	
          	abstract double initFilter(String type, String text);
          
          	protected JComponent createFilterSlider(String type, String text, CyNetwork network, boolean labels, double max) {
          		double value = 0.0;
          		if (filters.containsKey(network) && 
          		    filters.get(network).containsKey(type) && 
          		    filters.get(network).get(type).containsKey(text)) {
          			value = filters.get(network).get(type).get(text);
          			// System.out.println("value = "+value);
          		} else {
          			value = initFilter(type, text);
          		}
          		Box box = Box.createHorizontalBox();
          		if (labels) {
          			JLabel label = new JLabel(text);
          			label.setFont(labelFont);
          			label.setPreferredSize(new Dimension(100,20));
          			box.add(Box.createRigidArea(new Dimension(10,0)));
          			box.add(label);
          			box.add(Box.createHorizontalGlue());
          		}
          		JSlider slider;
          		slider = new JSlider(0,(int)max,(int)(value*100));
          		slider.setToolTipText("Filter ranges between 0.0 and " + max/100);
          		slider.setPreferredSize(new Dimension(150,20));
          		box.add(slider);
          		// box.add(Box.createHorizontalGlue());
          		JTextField textField;
          		textField = new JTextField(String.format("%.2f",value),4);
          		textField.setPreferredSize(new Dimension(30,20));
          		textField.setMaximumSize(new Dimension(30,20));
          		textField.setFont(textFont);
          		box.add(textField);
          		// Hook it up
          		addChangeListeners(type, text, slider, textField, max);
          		box.setAlignmentX(Component.LEFT_ALIGNMENT);
          		return box;
          	}
          
          	protected void addChangeListeners(String type, String label, JSlider slider, 
          	                                  JTextField textField, double max) {
          		slider.addChangeListener(new ChangeListener() {
          			public void stateChanged(ChangeEvent e) {
          				JSlider sl = (JSlider)e.getSource();
          				int value = sl.getValue();
          				double v = ((double)value)/100.0;
          				textField.setText(String.format("%.2f",v));
          				addFilter(type, label, v);
          				doFilter(type);
          			}
          		});
          
          		textField.addActionListener(new ActionListener() {
          			public void actionPerformed(ActionEvent e) {
          				JTextField field = (JTextField)e.getSource();
          				String text = field.getText();
          				slider.setValue((int)(Double.parseDouble(text)*100.0));
          			}
          		});
          	}
          
          	protected void addFilter(String type, String label, double value) {
          		Map<String,Double> filter = filters.get(currentNetwork).get(type);
          		filter.put(label, value);
          
          		if (value == 0)
          			filter.remove(label);
          	}
          
          	protected void removeFilters(CyNetwork network) {
          		if (network != null && filters.containsKey(network))
          			filters.remove(network);
          	}

    }


     </pre>
  </div>

  <script>
    function MGGCytoPanel() {
      var panel = document.getElementById("MGGCytoPanel");
      if (panel.style.display === "none") {
        panel.style.display = "block";
      } else {
        panel.style.display = "none";
      }
    }
    
    function AbstractMggPanel() {
      var panel = document.getElementById("AbstractMggPanel");
      if (panel.style.display === "none") {
        panel.style.display = "block";
      } else {
        panel.style.display = "none";
      }
    }
	  
  </script>
</body>
</html>

	
	
<br> <!-- Add an empty line -->



[back](./)
