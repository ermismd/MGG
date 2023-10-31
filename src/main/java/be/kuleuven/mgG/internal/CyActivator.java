package be.kuleuven.mgG.internal;

import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_EXAMPLE_JSON;
import static org.cytoscape.work.ServiceProperties.COMMAND_LONG_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.COMMAND_SUPPORTS_JSON;
import static org.cytoscape.work.ServiceProperties.ID;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.IN_TOOL_BAR;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_BEFORE;
import static org.cytoscape.work.ServiceProperties.LARGE_ICON_URL;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;
import static org.cytoscape.work.ServiceProperties.TOOL_BAR_GRAVITY;
import static org.cytoscape.work.ServiceProperties.TOOLTIP;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedListener;
import org.cytoscape.io.BasicCyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskFactory;
import org.json.simple.JSONArray;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.kuleuven.mgG.internal.tasks.CreateMGGVisualStyle;
import be.kuleuven.mgG.internal.tasks.CreateMGGVisualStyleTaskFactory;
import be.kuleuven.mgG.internal.tasks.CreateNetworkTaskFactory;
import be.kuleuven.mgG.internal.tasks.ImportFileTaskFactory;
import be.kuleuven.mgG.internal.tasks.SendDataToServerTaskFactory;
import be.kuleuven.mgG.internal.tasks.ShowResultsPanelAction;
import be.kuleuven.mgG.internal.tasks.ShowResultsPanelTaskFactory;
import be.kuleuven.mgG.internal.tasks.examoleFactory;
import be.kuleuven.mgG.internal.utils.Mutils;
import be.kuleuven.mgG.internal.view.JSONDisplayPanel;

import be.kuleuven.mgG.internal.model.MGGManager;





public class CyActivator extends AbstractCyActivator {

	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		final StreamUtil streamUtil = getService(bc, StreamUtil.class);
		final CyServiceRegistrar serviceRegistrar = getService(bc, CyServiceRegistrar.class);

		final MGGManager MGGManager = new MGGManager(serviceRegistrar);

		//MGGManager.addSource(new FileSource(NGGManager));
		
		
		// Get services
		//CyApplicationManager appManager = getService(bc, CyApplicationManager.class);
       // CySwingApplication swingApplication = getService(bc, CySwingApplication.class);

        //CyNetworkFactory networkFactory =getService(bc, CyNetworkFactory.class);
       // CyNetworkManager networkManager = getService(bc, CyNetworkManager.class);
        
	
		
        
        // Register taskfactory
        
        ImportFileTaskFactory mggImportFileTaskFactory = new ImportFileTaskFactory(MGGManager);
        Properties props = new Properties();
		props.setProperty(TITLE, "Import Abundance Data");
		props.setProperty(PREFERRED_MENU, "Apps.MGG.Import Data");
		props.setProperty(IN_TOOL_BAR, "FALSE");
		props.setProperty(IN_MENU_BAR, "TRUE");
		props.setProperty(MENU_GRAVITY, "1");
		props.setProperty(COMMAND_NAMESPACE, "MGG");
		props.setProperty(COMMAND_DESCRIPTION, "Load abudance table(TSV/CSV) or network");
		props.setProperty(COMMAND, "Load_File");
	     
        registerService(bc, mggImportFileTaskFactory, TaskFactory.class, props);
        
        
        //---------------------
        examoleFactory examole = new examoleFactory(MGGManager);
        Properties exaprops1 = new Properties();
        exaprops1.setProperty(TITLE, "examole");
        exaprops1.setProperty(PREFERRED_MENU, "Apps.MGG.examole");
        exaprops1.setProperty(IN_TOOL_BAR, "FALSE");
        exaprops1.setProperty(IN_MENU_BAR, "TRUE");
        exaprops1.setProperty(MENU_GRAVITY, "3");
        exaprops1.setProperty(COMMAND_NAMESPACE, "MGG");
        exaprops1.setProperty(COMMAND_DESCRIPTION, "examole");
        exaprops1.setProperty(COMMAND, "examole");
	     
        registerService(bc, examole, TaskFactory.class, exaprops1);
        
        
        // Register taskfactory
        
        SendDataToServerTaskFactory sendDataToServerTaskFactory = new SendDataToServerTaskFactory(MGGManager.getJsonObject(), MGGManager);
        Properties Sendprops = new Properties();
		Sendprops.setProperty(TITLE, "Get Annotated Network");
		Sendprops.setProperty(PREFERRED_MENU, "Apps.MGG.Import Data");
		Sendprops.setProperty(IN_TOOL_BAR, "FALSE");
		Sendprops.setProperty(IN_MENU_BAR, "TRUE");
		Sendprops.setProperty(MENU_GRAVITY, "2");
		Sendprops.setProperty(COMMAND_NAMESPACE, "MGG");
		Sendprops.setProperty(COMMAND_DESCRIPTION, "Upload data to Microbetag Server and Get the Network");
		Sendprops.setProperty(COMMAND, "Get_Network");
	     
        registerService(bc,sendDataToServerTaskFactory, TaskFactory.class, Sendprops);
        
        
        
        
//        CreateMGGVisualStyleTaskFactory mggVisualStyleTaskFactory=new  CreateMGGVisualStyleTaskFactory(MGGManager);
//        
//        Properties Visualprops = new Properties();
//        Visualprops.setProperty(TITLE, "Apply MGG Visual Style");
//        Visualprops.setProperty(PREFERRED_MENU, "Apps.MGG.Visual Style");
//        Visualprops.setProperty(IN_TOOL_BAR, "FALSE");
//        Visualprops.setProperty(IN_MENU_BAR, "TRUE");
//        Visualprops.setProperty(MENU_GRAVITY, "1");
//        Visualprops.setProperty(COMMAND_NAMESPACE, "MGG");
//        Visualprops.setProperty(COMMAND_DESCRIPTION, "Get MGG visual Style");
//        Visualprops.setProperty(COMMAND, "Get_Style");
//	     
//        registerService(bc,mggVisualStyleTaskFactory, TaskFactory.class, Visualprops);
//        
        
        
        
        
        CreateMGGVisualStyle createVisualStyleAction = new CreateMGGVisualStyle(MGGManager );
		
		registerService(bc,createVisualStyleAction,CyAction.class, new Properties());
        
        {
			ShowResultsPanelAction sra = new ShowResultsPanelAction("Show results panel", MGGManager);
			registerService(bc, sra, CyAction.class);

			ShowResultsPanelTaskFactory showResults = new ShowResultsPanelTaskFactory(MGGManager);				
			//showResults.reregister();
			MGGManager.setShowResultsPanelTaskFactory(showResults);
			
			// Now bring up the side panel if the current network is a STRING network
			CyNetwork current = MGGManager.getCurrentNetwork();
			if (Mutils.ifHaveMGG(current)) {
				// It's the current network.  Bring up the results panel
				MGGManager.execute(showResults.createTaskIterator(), true);
			}
		}	
	
       
        
        
    	
        
    	
    	
    	
    	
    	
    	
        //createnetworktaskfactory
        
		/*
		 * TaskFactory createNetworkTaskFactory = new
		 * CreateNetworkTaskFactory(MGGManager);
		 * 
		 * Properties propsNetwork = new Properties(); propsNetwork.setProperty(TITLE,
		 * "Visualize Network"); propsNetwork.setProperty(PREFERRED_MENU,
		 * "Apps.MGG.Import Experiment"); propsNetwork.setProperty(IN_TOOL_BAR,
		 * "FALSE"); propsNetwork.setProperty(IN_MENU_BAR, "TRUE");
		 * propsNetwork.setProperty(MENU_GRAVITY, "3");
		 * propsNetwork.setProperty(COMMAND_NAMESPACE, "MGG");
		 * propsNetwork.setProperty(COMMAND_DESCRIPTION,
		 * "Create a network from server response"); propsNetwork.setProperty(COMMAND,
		 * "create network");
		 * 
		 * 
		 * // Register the task factory registerService(bc, createNetworkTaskFactory,
		 * TaskFactory.class,propsNetwork );
		 */
        
    }
	
	
	    
    
//  MGGManager mggManager = getService(bc, MGGManager.class);
//	JSONDisplayPanel displayPanel = new JSONDisplayPanel(mggManager, mggManager.getJsonArray());
//
//  // Register the JSONDisplayPanel as a service
//  registerService(bc, displayPanel, CytoPanelComponent.class);
//  
  
		
		
		// Register our menu items


		// Start the thread the loads all of the species
		//Species.loadSpecies(scNVManager);
	
}
