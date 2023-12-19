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

import be.kuleuven.mgG.internal.tasks.CheckAbudanceFileTaskFactory;
import be.kuleuven.mgG.internal.tasks.CheckMetaDataFileTaskFactory;
import be.kuleuven.mgG.internal.tasks.CreateMGGVisualStyle;
import be.kuleuven.mgG.internal.tasks.CreateMGGVisualStyleTaskFactory;
import be.kuleuven.mgG.internal.tasks.CreateNetworkTaskFactory;
import be.kuleuven.mgG.internal.tasks.ImportFileTaskFactory;
import be.kuleuven.mgG.internal.tasks.ImportMetadataTaskFactory;
import be.kuleuven.mgG.internal.tasks.SendDataToServerTaskFactory;
import be.kuleuven.mgG.internal.tasks.ShowResultsPanelAction;
import be.kuleuven.mgG.internal.tasks.ShowResultsPanelTaskFactory;
import be.kuleuven.mgG.internal.tasks.examoleFactory;
import be.kuleuven.mgG.internal.utils.Mutils;
import be.kuleuven.mgG.internal.view.JSONDisplayPanel;

import be.kuleuven.mgG.internal.model.MGGManager;





public class CyActivator extends AbstractCyActivator {

	// the AbstractCyActivator includes the registerService method 
	
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
        props.setProperty(COMMAND_DESCRIPTION, "Load abudance table(TSV/CSV)");
        props.setProperty(COMMAND, "Load_Abudance");

        registerService(bc, mggImportFileTaskFactory, TaskFactory.class, props);

        ImportMetadataTaskFactory mggImportMetaDataTaskFactory = new ImportMetadataTaskFactory(MGGManager);
        Properties metadataprops = new Properties();
        metadataprops.setProperty(TITLE, "Import MetaData");
        metadataprops.setProperty(PREFERRED_MENU, "Apps.MGG.Import Data");
        metadataprops.setProperty(IN_TOOL_BAR, "FALSE");
        metadataprops.setProperty(IN_MENU_BAR, "TRUE");
        metadataprops.setProperty(MENU_GRAVITY, "2");
        metadataprops.setProperty(COMMAND_NAMESPACE, "MGG");
        metadataprops.setProperty(COMMAND_DESCRIPTION, "Load Metadata File");
        metadataprops.setProperty(COMMAND, "Load_MetaData");

        registerService(bc, mggImportMetaDataTaskFactory, TaskFactory.class, metadataprops);
        
        
        CheckAbudanceFileTaskFactory mggCheckAbudanceFileTaskFactory = new CheckAbudanceFileTaskFactory(MGGManager);
        Properties checkdataprops = new Properties();
        checkdataprops.setProperty(TITLE, "Check Abudance Data");
        checkdataprops.setProperty(PREFERRED_MENU, "Apps.MGG.Import Data.Check Data Files");
        checkdataprops.setProperty(IN_TOOL_BAR, "FALSE");
        checkdataprops.setProperty(IN_MENU_BAR, "TRUE");
        checkdataprops.setProperty(MENU_GRAVITY, "1");
        checkdataprops.setProperty(COMMAND_NAMESPACE, "MGG");
        checkdataprops.setProperty(COMMAND_DESCRIPTION, "Check abudance data File");
        checkdataprops.setProperty(COMMAND, "Check_Abudance_Data");

        registerService(bc, mggCheckAbudanceFileTaskFactory, TaskFactory.class, checkdataprops);
        
        
        
        CheckMetaDataFileTaskFactory mggCheckMetaDataFileTaskFactory = new CheckMetaDataFileTaskFactory(MGGManager);
        Properties checkMetaDataprops = new Properties();
        checkMetaDataprops.setProperty(TITLE, "Check  MetaData");
        checkMetaDataprops.setProperty(PREFERRED_MENU, "Apps.MGG.Import Data.Check Data Files");
        checkMetaDataprops.setProperty(IN_TOOL_BAR, "FALSE");
        checkMetaDataprops.setProperty(IN_MENU_BAR, "TRUE");
        checkMetaDataprops.setProperty(MENU_GRAVITY, "2");
        checkMetaDataprops.setProperty(COMMAND_NAMESPACE, "MGG");
        checkMetaDataprops.setProperty(COMMAND_DESCRIPTION, "Check metadata File");
        checkMetaDataprops.setProperty(COMMAND, "Check_MetaData");

        registerService(bc, mggCheckMetaDataFileTaskFactory, TaskFactory.class, checkMetaDataprops);
        
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

        SendDataToServerTaskFactory sendDataToServerTaskFactory = new SendDataToServerTaskFactory( MGGManager);
        Properties Sendprops = new Properties();
        Sendprops.setProperty(TITLE, "Get Annotated Network");
        Sendprops.setProperty(PREFERRED_MENU, "Apps.MGG");
        Sendprops.setProperty(IN_TOOL_BAR, "FALSE");
        Sendprops.setProperty(IN_MENU_BAR, "TRUE");
        Sendprops.setProperty(MENU_GRAVITY, "2");
        Sendprops.setProperty(COMMAND_NAMESPACE, "MGG");
        Sendprops.setProperty(COMMAND_DESCRIPTION, "Upload data to Microbetag Server and Get the Network");
        Sendprops.setProperty(COMMAND, "Get_Network");

        registerService(bc, sendDataToServerTaskFactory, TaskFactory.class, Sendprops);




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




        CreateMGGVisualStyle createVisualStyleAction = new CreateMGGVisualStyle(MGGManager);

        registerService(bc, createVisualStyleAction, CyAction.class, new Properties());

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









    }




}









