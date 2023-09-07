package be.kuleuven.mgG.internal.model;


import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.IN_TOOL_BAR;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import be.kuleuven.mgG.internal.tasks.ImportFileTaskFactory;
import be.kuleuven.mgG.internal.tasks.SendDataToServerTaskFactory;
import be.kuleuven.mgG.internal.view.JSONDisplayPanel;
import be.kuleuven.mgG.internal.view.MGGCytoPanel;


import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.command.AvailableCommands;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.property.CyProperty;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.events.SessionAboutToBeSavedEvent;
import org.cytoscape.session.events.SessionAboutToBeSavedListener;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskObserver;



/**
 * The MGGManager class is responsible for managing the state of the MGG application.
 * It provides methods to store and retrieve data, execute tasks, and register services for the tasks and taskfactories to use instead of cyactivator
 * 
 */



public class MGGManager implements SessionAboutToBeSavedListener, SessionLoadedListener {
	
	
	public final static String APP_NAME = "be.kuleuven.mgG";
	public final static String SERVER_RESPONSE_FILE = "Response.json";
	
	//-----------------------------------------------------------
	final CommandExecutorTaskFactory commandExecutorTaskFactory;
	final SynchronousTaskManager<?> synchronousTaskManager;
	final TaskManager<?,?> dialogTaskManager;
	
	//----------------------------------------------------------------
	final TaskManager taskManager;
	final SynchronousTaskManager syncTaskManager;
	
	final CyServiceRegistrar cyRegistrar; 
	
	final AvailableCommands availableCommands;
	final CommandExecutorTaskFactory ceTaskFactory;
	//-----------------------------------------------------------
	private MGGCytoPanel cytoPanel = null;
	
	  private CyNetwork newNetwork = null;
	//----------------------------------------------------------
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
	
  //-----------------------------addition------------------------------for cytopanel------------------------------------------------------------------------------------------  
    
    public void setCytoPanel(MGGCytoPanel panel) {
  		this.cytoPanel = panel;
  	}
      
    public CyNetwork getCurrentNetwork() {
		CyNetwork network = cyRegistrar.getService(CyApplicationManager.class).getCurrentNetwork();
    if (network != null) return network;
    return newNetwork;
	}

    
    
    //------------------------------------------------SErvice Register and execute Tasks-----------------------------------------------------------------------------------------------
    
    
    
    
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
	
    
    
    
    
    
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
   
    
    
    
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





	
	
	
	
	
	
	
	
	


	
	/*
	 * public void executeTasks(TaskIterator tasks) { taskManager.execute(tasks); }
	 * 
	 * public void executeTasks(TaskIterator tasks, TaskObserver observer) {
	 * taskManager.execute(tasks, observer); }
	 * 
	 * public void executeTasks(TaskFactory factory) {
	 * taskManager.execute(factory.createTaskIterator()); }
	 * 
	 * public void executeTasks(TaskFactory factory, TaskObserver observer) {
	 * taskManager.execute(factory.createTaskIterator(), observer); }
	 */
	  
	  
	  
	  
	  
	  
	  
	
	 
	
	
	/*
	 * public void executeCommand(String namespace, String command,Map<String,
	 * Object> args, TaskObserver observer) { if (ceTaskFactory == null)
	 * ceTaskFactory = getService(CommandExecutorTaskFactory.class); if
	 * (availableCommands == null) availableCommands=
	 * getService(AvailableCommands.class); if (syncTaskManager == null)
	 * syncTaskManager = getService(SynchronousTaskManager.class); if
	 * (availableCommands.getNamespaces() == null ||
	 * !availableCommands.getCommands(namespace).contains(command)) throw new
	 * RuntimeException("Can’t find command" +namespace+ "+command"); TaskIterator
	 * ti = ceTaskFactory.createTaskIterator(namespace, command, args, observer);
	 * syncTaskManager.execute(ti); }
	 */

	





}