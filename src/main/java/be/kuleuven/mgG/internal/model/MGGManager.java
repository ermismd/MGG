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
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskObserver;



public class MGGManager implements SessionAboutToBeSavedListener, SessionLoadedListener {
	
	
	public final static String APP_NAME = "be.kuleuven.mgG";
	public final static String SERVER_RESPONSE_FILE = "Response.json";
	
	final TaskManager taskManager;
	final CyServiceRegistrar cyRegistrar; 
	
	private JSONObject serverResponse;
	
	 AvailableCommands availableCommands=null;
	 CommandExecutorTaskFactory ceTaskFactory=null;
	 SynchronousTaskManager syncTaskManager=null;

	
	
	//private Icon MGGicon;




	public MGGManager(final CyServiceRegistrar cyRegistrar) {
		
		this.cyRegistrar = cyRegistrar;
		this.taskManager = cyRegistrar.getService(TaskManager.class);
		
		//MGGicon = new ImageIcon(getClass().getResource("/images/scNetViz.png"));

		cyRegistrar.registerService(this, SessionAboutToBeSavedListener.class, new Properties());
		cyRegistrar.registerService(this, SessionLoadedListener.class, new Properties());
		
		
	
		
	}
	
	
	// Method to set the server response
    public void setServerResponse(JSONObject jsonResponse) {
        this.serverResponse = jsonResponse;
    }
	

    // Method to get the server response
    public JSONObject getServerResponse() {
        return this.serverResponse;
    }
	
    
    
    
    
    
	
    public void executeTasks(TaskIterator tasks) {
		taskManager.execute(tasks);
	}

	public void executeTasks(TaskIterator tasks, TaskObserver observer) {
		taskManager.execute(tasks, observer);
	}

	public void executeTasks(TaskFactory factory) {
		taskManager.execute(factory.createTaskIterator());
	}

	public void executeTasks(TaskFactory factory, TaskObserver observer) {
		taskManager.execute(factory.createTaskIterator(), observer);
	}
	
	
	
	
	
	
	
	public <S> S getService(Class<S> serviceClass) {
		 return cyRegistrar.getService(serviceClass);
		 }
		
	public <S> S getService(Class<S> serviceClass, String filter) {
		return cyRegistrar.getService(serviceClass, filter);
	}

	public void registerService(Object service, Class<?> serviceClass, Properties props) {
		cyRegistrar.registerService(service, serviceClass, props);
	}

	public void unregisterService(Object service, Class<?> serviceClass) {
		cyRegistrar.unregisterService(service, serviceClass);
	}
	
	
	
	
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





}