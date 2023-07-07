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
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskFactory;
import org.json.simple.JSONArray;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.kuleuven.mgG.internal.tasks.CreateNetworkTaskFactory;
import be.kuleuven.mgG.internal.tasks.ImportFileTaskFactory;
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
		CyApplicationManager appManager = getService(bc, CyApplicationManager.class);
        CySwingApplication swingApplication = getService(bc, CySwingApplication.class);

        CyNetworkFactory networkFactory =getService(bc, CyNetworkFactory.class);
        CyNetworkManager networkManager = getService(bc, CyNetworkManager.class);
        
        
        // Register taskfactory
        ImportFileTaskFactory mggImportFileTaskFactory = new ImportFileTaskFactory(swingApplication, appManager, MGGManager);
        Properties props = new Properties();
		props.setProperty(TITLE, "Import from file...");
		props.setProperty(PREFERRED_MENU, "Apps.MGG.Load TSV/CSV[10.1]");
		props.setProperty(IN_TOOL_BAR, "FALSE");
		props.setProperty(IN_MENU_BAR, "TRUE");
		props.setProperty(MENU_GRAVITY, "30.0");
		props.setProperty(COMMAND_NAMESPACE, "MGG");
		props.setProperty(COMMAND_DESCRIPTION, "Load a file from csv");
		props.setProperty(COMMAND, "load csv");
	     
        registerService(bc, mggImportFileTaskFactory, TaskFactory.class, props);
        

        
        
        //createnetworktaskfactory
        TaskFactory createNetworkTaskFactory = new CreateNetworkTaskFactory(MGGManager, networkFactory, networkManager);
        
        Properties propsNetwork = new Properties();
        propsNetwork.setProperty(TITLE, "Create Network");
        propsNetwork.setProperty(PREFERRED_MENU, "Apps.MGG");
        propsNetwork.setProperty(IN_TOOL_BAR, "FALSE");
        propsNetwork.setProperty(IN_MENU_BAR, "TRUE");
        propsNetwork.setProperty(MENU_GRAVITY, "10.0");
        propsNetwork.setProperty(COMMAND_NAMESPACE, "MGG");
        propsNetwork.setProperty(COMMAND_DESCRIPTION, "Create a network from server response");
        propsNetwork.setProperty(COMMAND, "create network");
        

        // Register the task factory
        registerService(bc, createNetworkTaskFactory, TaskFactory.class,propsNetwork );
        
        
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
