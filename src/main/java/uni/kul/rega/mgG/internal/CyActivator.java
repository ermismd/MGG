package uni.kul.rega.mgG.internal;

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

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uni.kul.rega.mgG.internal.sources.file.tasks.MicroBetaGUIImportAction;



//import uni.kul.rega.mgG.internal.model.ScNVManager;
//import uni.kul.rega.mgG.internal.model.Species;
//import uni.kul.rega.mgG.internal.sources.file.FileSource;

//import uni.kul.rega.mgG.internal.tasks.CalculateDECommandTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.CreateNetworkTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.DeleteExperimentTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.ExportCategoryTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.ExportDiffExpTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.ExportExperimentTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.GetExperimentTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.ListExperimentsTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.ProcessAllTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.RemoteGraphTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.RemoteLeidenTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.RemoteLouvainTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.RemoteTSNETaskFactory;
//import uni.kul.rega.mgG.internal.tasks.RemoteUMAPTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.SelectTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.SettingsTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.ShowCellPlotTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.ShowDiffPlotTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.ShowExperimentTableTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.ShowResultsPanelTaskFactory;
//import uni.kul.rega.mgG.internal.tasks.tSNETaskFactory;

public class CyActivator extends AbstractCyActivator {

	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		//final StreamUtil streamUtil = getService(bc, StreamUtil.class);
		final CyServiceRegistrar serviceRegistrar = getService(bc, CyServiceRegistrar.class);

		//final ScNVManager scNVManager = new ScNVManager(serviceRegistrar);

		// Commands
		//
		// Source-specific commands (registered by source)
		// *scnetviz load experiment file file=yyyy
		// *scnetviz load category file file=yyyy accession=yyyy experiment=exp
		// *scnetviz load experiment gxa accession=yyyy
		// *scnetviz list gxa entries
		//
		// Manager commands
		// *scnetviz list experiments
		// scnetviz remove experiment accession=yyyy
		// *scnetviz get experiment accession=yyyy
		// *scnetviz show experiment table accession=yyyy
		// *scnetviz export experiment accession=yyyy file=*
		

		// scNVManager.addSource(new HCASource(scNVManager));
		//scNVManager.addSource(new FileSource(scNVManager));
		
		
		// Get services
        CySwingApplication cytoscapeDesktopService = getService(bc, CySwingApplication.class);
        CyApplicationManager cyApplicationManager = getService(bc, CyApplicationManager.class);

        // Register the action
        MicroBetaGUIImportAction action = new MicroBetaGUIImportAction(cytoscapeDesktopService, cyApplicationManager);
        registerService(bc, action, CyAction.class);
    

		

		
		
		// Register our menu items

//
//		{
//			DeleteExperimentTaskFactory deleteExp = new DeleteExperimentTaskFactory(scNVManager);
//			Properties props = new Properties();
//			props.setProperty(TITLE, "Remove Experiment");
//			props.setProperty(PREFERRED_MENU, "Apps.scNetViz");
//			props.setProperty(IN_TOOL_BAR, "FALSE");
//			props.setProperty(IN_MENU_BAR, "TRUE");
//			props.setProperty(INSERT_SEPARATOR_BEFORE, "TRUE");
//			props.setProperty(MENU_GRAVITY, "310.0");
//			props.setProperty(COMMAND_NAMESPACE, "scnetviz");
//			props.setProperty(COMMAND, "delete experiment");
//			props.setProperty(COMMAND_DESCRIPTION, "Remove an experiment");
//			props.setProperty(COMMAND_LONG_DESCRIPTION, "");
//			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
//			props.setProperty(COMMAND_EXAMPLE_JSON, "{}");
//			scNVManager.registerService(deleteExp, TaskFactory.class, props);
//		}
//
//	
//
//		// Commands
//		{
//			ProcessAllTaskFactory processAll = new ProcessAllTaskFactory(scNVManager);
//			Properties props = new Properties();
//			props.setProperty(COMMAND_NAMESPACE, "scnetviz");
//			props.setProperty(COMMAND, "create all");
//			props.setProperty(COMMAND_DESCRIPTION, "Calculate differential expression and create networks");
//			props.setProperty(COMMAND_LONG_DESCRIPTION, "Use default cluster to calculate differential expression and create networks.");
//			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
//			props.setProperty(COMMAND_EXAMPLE_JSON, "{}");
//			scNVManager.registerService(processAll, TaskFactory.class, props);
//		}

//
//		{
//			SelectTaskFactory select = new SelectTaskFactory(scNVManager);
//			Properties props = new Properties();
//			props.setProperty(COMMAND_NAMESPACE, "scnetviz");
//			props.setProperty(COMMAND, "select");
//			props.setProperty(COMMAND_DESCRIPTION, "Select genes or assays in current tables");
//			props.setProperty(COMMAND_LONG_DESCRIPTION, "");
//			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
//			props.setProperty(COMMAND_EXAMPLE_JSON, "{}");
//			scNVManager.registerService(select, TaskFactory.class, props);
//		}
//

//

//		
//
//

//		
//		
//		{
//			ExportExperimentTaskFactory expExp = new ExportExperimentTaskFactory(scNVManager);
//			Properties props = new Properties();
//			props.setProperty(COMMAND_NAMESPACE, "scnetviz");
//			props.setProperty(COMMAND, "export experiment");
//			props.setProperty(COMMAND_DESCRIPTION, "Export a currently loaded experiment table");
//			props.setProperty(COMMAND_LONG_DESCRIPTION, "");
//			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
//			props.setProperty(COMMAND_EXAMPLE_JSON, "{}");
//			scNVManager.registerService(expExp, TaskFactory.class, props);
//		}

		// Start the thread the loads all of the species
		//Species.loadSpecies(scNVManager);
	}
}
