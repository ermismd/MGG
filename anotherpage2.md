## WEEK 3 AND WEEK 4



 1. Implemented a mock manager class(MGGManajer.java) that will be used to activate future functions 

 2. Implemented a new Class, JsonDisplayPanel that shows the imported file data-OTU/ASV tables)

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
  	font-size: 10px;
  	width: 800px;
  	overflow-x: auto; /* Adds a horizontal scrollbar if text overflows */
 	 overflow-y: auto; /* Adds a vertical scrollbar if text overflows */
 	 max-height: 400px; /* Optional: Set a max height */
    }
  </style>
</head>
<body>
  <h1>MGGManager</h1>
  <button onclick="MGGManager()">Expand</button>
  <div class="panel" id="MGGManager">
    <pre>
	    
			 /**
			 * The MGGManager class is responsible for managing the state of the MGG application.
			 * It provides methods to store and retrieve data, execute tasks, and register services for the tasks and taskfactories to use instead of cyactivator
			 * 
			 */
		
		
		
			public class MGGManager implements SessionAboutToBeSavedListener, SessionLoadedListener {
			
			
			public final static String APP_NAME = "be.kuleuven.mgG";
			public final static String SERVER_RESPONSE_FILE = "Response.json";
			
			
			final CommandExecutorTaskFactory commandExecutorTaskFactory;
			final SynchronousTaskManager<?> synchronousTaskManager;
			final TaskManager<?,?> dialogTaskManager;
			
			
			final TaskManager taskManager;
			final SynchronousTaskManager syncTaskManager;
			
			final CyServiceRegistrar cyRegistrar; 
			
			final AvailableCommands availableCommands;
			final CommandExecutorTaskFactory ceTaskFactory;
			
			private MGGCytoPanel cytoPanel = null;
			
			  private CyNetwork newNetwork = null;
			
			private JSONObject jsonObject;
			private JSONObject serverResponse;
				
			//private Icon MGGicon;
		
			
			 /**
		     * Constructor for the MGGManager class.
		     * This constructor initializes the MGGManager with a CyServiceRegistrar, which is used to access Cytoscape services.
		     * It also registers the MGGManager as a listener for session events, specifically when a session is about to be saved and when a session is loaded.
		     *
		     * @param cyRegistrar The CyServiceRegistrar used to access Cytoscape services.
		     */
			
			public MGGManager(final CyServiceRegistrar cyRegistrar) {
				 // Store the CyServiceRegistrar
				this.cyRegistrar = cyRegistrar;
				
				 // Get Cytoscape services
				this.taskManager = cyRegistrar.getService(TaskManager.class);
				this.availableCommands = cyRegistrar.getService(AvailableCommands.class);
				this.ceTaskFactory = cyRegistrar.getService(CommandExecutorTaskFactory.class);
				this.syncTaskManager = cyRegistrar.getService(SynchronousTaskManager.class);
				
				// Register this manager as a listener for session events
				cyRegistrar.registerService(this, SessionAboutToBeSavedListener.class, new Properties());
				cyRegistrar.registerService(this, SessionLoadedListener.class, new Properties());
				
				synchronousTaskManager = cyRegistrar.getService(SynchronousTaskManager.class);
				commandExecutorTaskFactory = cyRegistrar.getService(CommandExecutorTaskFactory.class);
				dialogTaskManager = cyRegistrar.getService(TaskManager.class);
				//MGGicon = new ImageIcon(getClass().getResource("/images/scNetViz.png"));
							
			}
			
		
			 /**
		     * Sets the JSONArray object.
		     * This method is used to store a JSONArray object which can be used later.
		     *
		     * @param jsonArray The JSONArray object to be stored.
		     */
		    public void setJsonObject(JSONObject jsonObject) {
		        this.jsonObject = jsonObject;
		    }
		
		    /**
		     * Gets the stored JSONArray object.
		     * This method is used to retrieve the stored JSONArray object.
		     *
		     * @return The stored JSONArray object.
		     */
		    public JSONObject getJsonObject() {
		        return jsonObject;
		    }
			
		   
		    /**
		     * Sets the server response.
		     * This method is used to store the server response which can be used later.
		     * 
		     * @param jsonResponse The server response in the form of a JSONObject.
		     */
		    public void setServerResponse(JSONObject jsonResponse) {
		        this.serverResponse = jsonResponse;
		    }
			
		
		    /**
		     * Gets the stored server response.
		     * This method is used to retrieve the stored server response.
		     *
		     * @return The stored server response in the form of a JSONObject.
		     */
		    public JSONObject getServerResponse() {
		        return this.serverResponse;
		    }
			
		  	
		    
		    public void setCytoPanel(MGGCytoPanel panel) {
		  		this.cytoPanel = panel;
		  	}
		      
		    public CyNetwork getCurrentNetwork() {
				CyNetwork network = cyRegistrar.getService(CyApplicationManager.class).getCurrentNetwork();
		    if (network != null) return network;
		    return newNetwork;
			}
		
		    
		    
		    public void executeCommand(String namespace, String command, 
		            Map<String, Object> args, TaskObserver observer) {
			TaskIterator ti = commandExecutorTaskFactory.createTaskIterator(namespace, command, args, observer);
			execute(ti, true);
			}
		    
		    public void execute(TaskIterator iterator, boolean synchronous) {
				if (synchronous) {
					synchronousTaskManager.execute(iterator);
				} else {
					dialogTaskManager.execute(iterator);
				}
			}
		    
		    public CyNetworkView getCurrentNetworkView() {
				return cyRegistrar.getService(CyApplicationManager.class).getCurrentNetworkView();
			}
		    
		    /**
		     * Executes a set of tasks.
		     * This method is used to execute a set of tasks using the task manager.
		     * The tasks are executed in the order they are added to the TaskIterator.
		     *
		     * @param tasks The TaskIterator containing the tasks to be executed.
		     */
		    
		    public void executeTasks(TaskIterator tasks) {
		        taskManager.execute(tasks);
		    } 
		
		    
		
					    /**
					     * Retrieves a service of the specified class.
					     * This method is used to get a service registered in the Cytoscape environment.
					     *
					     * @param serviceClass The class of the service to be retrieved.
					     * @return The service of the specified class.
					     */
		    
		    			public <S> S getService(Class<S> serviceClass) { 
		    				return cyRegistrar.getService(serviceClass); 
		    				
		    			}
		    		  
					    /**
					     * Retrieves a service of the specified class and filter.
					     * This method is used to get a service registered in the Cytoscape environment that matches a specific filter.
					     *
					     * @param serviceClass The class of the service to be retrieved.
					     * @param filter The filter to match the service against.
					     * @return The service of the specified class and filter.
					     */
		    
		    		  public <S> S getService(Class<S> serviceClass, String filter) { return
		    		  cyRegistrar.getService(serviceClass, filter); }
		    		  
		    		  
		    		  /**
		    		     * Registers a service in the Cytoscape environment.
		    		     * This method is used to register a service in the Cytoscape environment with the specified properties.
		    		     *
		    		     * @param service The service to be registered.
		    		     * @param serviceClass The class of the service to be registered.
		    		     * @param props The properties of the service to be registered.
		    		     */
		    		  
		    		  public void registerService(Object service, Class<?> serviceClass, Properties
		    		  props) { cyRegistrar.registerService(service, serviceClass, props); }
		    		  
		    		  
		    		  /**
		    		     * Unregisters a service from the Cytoscape environment.
		    		     * This method is used to unregister a service from the Cytoscape environment.
		    		     *
		    		     * @param service The service to be unregistered.
		    		     * @param serviceClass The class of the service to be unregistered.
		    		     */
		    		  
		    		  public void unregisterService(Object service, Class<?> serviceClass) {
		    		  cyRegistrar.unregisterService(service, serviceClass); }
		
		    
		    	/**
		    	 * Handles the SessionLoadedEvent.
		    	 * This method is called when a session is loaded in Cytoscape.
		    	 * It checks if there are any files related to the MGG application in the session and loads them if they exist.
		    	 *
		    	 * @param e The SessionLoadedEvent.
		    	*/
			
		    	@Override
		    		  // See if we have data in the session, and load it if we do
		    		public void handleEvent(SessionLoadedEvent e) {
					System.out.println("SessionLoaded");
					
					Map<String,List<File>> appFiles = e.getLoadedSession().getAppFileListMap();
					if (!appFiles.containsKey(APP_NAME)) {
						System.out.println("Don't see "+APP_NAME+"!");
						return;
					}
		
					List<File> mggFiles = appFiles.get(APP_NAME);
					Map<String, File> fileMap = new HashMap<>();
					for (File f: mggFiles) {
						System.out.println("File map has file: "+f.getName());
						fileMap.put(f.getName(),f);
					}
		
					if (!fileMap.containsKey(SERVER_RESPONSE_FILE)) {
						System.out.println("Don't see "+SERVER_RESPONSE_FILE+"!");
						return;
					}	
		    	}
		    	
		    	 /**
		         * Handles the SessionAboutToBeSavedEvent.
		         * This method is called when a session is about to be saved in Cytoscape.
		         * It saves the server response to a file and adds it to the session.
		         *
		         * @param e The SessionAboutToBeSavedEvent.
		         */
		    	
			@Override
			public void handleEvent(SessionAboutToBeSavedEvent e) {
				String tmpDir = System.getProperty("java.io.tmpdir");
			    File jsonFile = new File(tmpDir, SERVER_RESPONSE_FILE);
		
			    try {
			        FileOutputStream fos = new FileOutputStream(jsonFile);
			        OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
			        BufferedWriter writer = new BufferedWriter(osw);
		
			        writer.write(serverResponse.toJSONString());
			        writer.close();
			        osw.close();
			        fos.close();
		
			        List<File> files = new ArrayList<File>();
			        files.add(jsonFile);
		
			        try {
			            e.addAppFiles(APP_NAME, files);
			        } catch (Exception add) {
			            add.printStackTrace();
			        }
			    } catch (Exception jsonException) {
			        jsonException.printStackTrace();
			    }
				
			}
		    
		    	
   </pre>
  </div>


  <h2>JSONDisplayPanel</h2>
  <button onclick="JSONDisplayPanel()">Expand</button>
  <div class="panel" id="JSONDisplayPanel">
    <pre>

     
 	public class JSONDisplayPanel extends JPanel  {
   		 private JTable table;
    		final MGGManager manager;
 
    	public JSONDisplayPanel(final MGGManager manager,JSONObject jsonObject) {
        	super(new BorderLayout());
        
        
	        // Extract the JSONArray from the JSONObject
	        JSONArray jsonArray = (JSONArray) jsonObject.get("data");
	        
	        createTable(jsonArray);
	        
	        JScrollPane scrollPane = new JScrollPane(table);
	      
	        this.manager = manager;
		
	        // Set the scroll bar policies
	        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	        
	        // Set the preferred size of the scroll pane
	        scrollPane.setPreferredSize(new Dimension(800, 600));
	        
	        // Add the scroll pane to the center of the JSONDisplayPanel
	        add(scrollPane, BorderLayout.CENTER);
	        
	        
	        // Add the button that will execute the SendDataToServerTask when clicked
	        JButton sendButton = new JButton("Get Annotated Network ");
	        sendButton.addActionListener(new ActionListener() {  
	            public void actionPerformed(ActionEvent e) {
	              
	            	 TaskIterator taskIterator = new SendDataToServerTaskFactory(jsonObject, manager).createTaskIterator();
	                 manager.executeTasks(taskIterator);
	            }
	
	        
	    });
	     // Set button appearance
	        sendButton.setForeground(Color.BLACK); // Set the text color of the button
	        sendButton.setFont(sendButton.getFont().deriveFont(Font.BOLD, 14f)); // Set the font style and size of the button text
	        sendButton.setBackground(new Color(144, 238, 144)); // Set the background color of the button
	        sendButton.setFocusPainted(false); // Remove the focus border around the button
	        sendButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add padding to the button
	
	        // Create a rounded border for the button
	        int borderRadius = 20;
	        int borderThickness = 2;
	        sendButton.setBorder(BorderFactory.createCompoundBorder(
	                BorderFactory.createLineBorder(Color.WHITE, borderThickness),
	                BorderFactory.createEmptyBorder(borderRadius, borderRadius, borderRadius, borderRadius)));
	
	        // Add hover effect for the button
	        sendButton.addMouseListener(new java.awt.event.MouseAdapter() {
	            public void mouseEntered(java.awt.event.MouseEvent evt) {
	                sendButton.setBackground(Color.GREEN); // Set the background color when mouse enters the button
	            }
	
	            public void mouseExited(java.awt.event.MouseEvent evt) {
	                sendButton.setBackground(new Color(144, 238, 144)); // Set the background color when mouse exits the button
	            }
	        });
	        
	        // Add the button to the JSONDisplayPanel
	        add(sendButton, BorderLayout.NORTH);
	    
	    }
	    
	    private void createTable(JSONArray jsonArray) {
	        DefaultTableModel tableModel = new DefaultTableModel();
	        table = new JTable(tableModel);
	
	        // Set the column names
	        JSONArray headers = (JSONArray) jsonArray.get(0);
	        for (Object header : headers) {
	            tableModel.addColumn(header.toString());
	        }
	
	        // Add the data to the table model
	        for (int i = 1; i < jsonArray.size(); i++) {
	            JSONArray row = (JSONArray) jsonArray.get(i);
	            Object[] rowData = new Object[row.size()];
	            for (int j = 0; j < row.size(); j++) {
	                rowData[j] = row.get(j);
	            }
	            tableModel.addRow(rowData);
	        }
	    }
		
	}

 
   </pre>
  </div>

  <script>
    function MGGManager() {
      var panel = document.getElementById("MGGManager");
      if (panel.style.display === "none") {
        panel.style.display = "block";
      } else {
        panel.style.display = "none";
      }
    }
    
    function JSONDisplayPanel() {
      var panel = document.getElementById("JSONDisplayPanel");
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
