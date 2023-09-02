## WEEK 15 & WEEK 16


1. MGGEdgePanel: Displays and filters edge-related information within MGGCytoPanel.
2. ShowResultsPanel: Dynamically supplies relevant network data to MGGCytoPanel.


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
  <h1>MGGEdgePanel</h1>
  <button onclick="MGGEdgePanel()">Expand</button>
  <div class="panel" id="MGGEdgePanel">
    <pre>


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
          		      

		    		

   </pre>
  </div>


  <h2>ShowResultsPanelTask</h2>
  <button onclick="ShowResultsPanelTask()">Expand</button>
  <div class="panel" id="ShowResultsPanelTask">
    <pre>
 
                	public class ShowResultsPanelTask extends AbstractTask {
                	final MGGManager manager;
                	final ShowResultsPanelTaskFactory factory;
                	final boolean show;
                
                	public ShowResultsPanelTask(final MGGManager manager, 
                	                            final ShowResultsPanelTaskFactory factory, boolean show) {
                		this.manager = manager;
                		this.factory = factory;
                		this.show = show;
                	}
                
                	public void run(TaskMonitor monitor) {
                		monitor.setTitle("Show/hide results panel");
                
                		CySwingApplication swingApplication = manager.getService(CySwingApplication.class);
                		CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.EAST);
                
                		// If the panel is not already registered, create it
                		if (cytoPanel.indexOfComponent("be.kuleuven.mgG.internal.MGG") < 0) {
                			CytoPanelComponent2 panel = new MGGCytoPanel(manager);
                
                			// Register it
                			manager.registerService(panel, CytoPanelComponent.class, new Properties());
                
                			if (cytoPanel.getState() == CytoPanelState.HIDE)
                				cytoPanel.setState(CytoPanelState.DOCK);
                
                		} else {
                			int compIndex = cytoPanel.indexOfComponent("be.kuleuven.mgG.internal.MGG");
                			Component panel = cytoPanel.getComponentAt(compIndex);
                			if (panel instanceof CytoPanelComponent2) {
                				// Unregister it
                				manager.unregisterService(panel, CytoPanelComponent.class);
                				manager.setCytoPanel(null);
                			}
                		}
                
                		// factory.reregister();
                	}
                
                	public static boolean isPanelRegistered(MGGManager manager) {
                		CySwingApplication swingApplication = manager.getService(CySwingApplication.class);
                		CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.EAST);
                
                		if (cytoPanel.indexOfComponent("be.kuleuven.mgG.internal.MGG") >= 0) 
                			return true;
                
                		return false;
                	}
                }

          	

     </pre>
  </div>

  <script>
    function MGGEdgePanel() {
      var panel = document.getElementById("MGGEdgePanel");
      if (panel.style.display === "none") {
        panel.style.display = "block";
      } else {
        panel.style.display = "none";
      }
    }
    
    function ShowResultsPanelTask() {
      var panel = document.getElementById("ShowResultsPanelTask");
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
