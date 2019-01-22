package edu.ucsf.rbvi.scNetViz.internal;

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

import edu.ucsf.rbvi.scNetViz.internal.model.ScNVManager;
import edu.ucsf.rbvi.scNetViz.internal.sources.gxa.GXASource;
import edu.ucsf.rbvi.scNetViz.internal.sources.file.FileSource;
import edu.ucsf.rbvi.scNetViz.internal.tasks.CalculateDECommandTaskFactory;
import edu.ucsf.rbvi.scNetViz.internal.tasks.CreateNetworkTaskFactory;
import edu.ucsf.rbvi.scNetViz.internal.tasks.ExportCategoryTaskFactory;
import edu.ucsf.rbvi.scNetViz.internal.tasks.ExportDiffExpTaskFactory;
import edu.ucsf.rbvi.scNetViz.internal.tasks.ExportExperimentTaskFactory;
import edu.ucsf.rbvi.scNetViz.internal.tasks.GetExperimentTaskFactory;
import edu.ucsf.rbvi.scNetViz.internal.tasks.ListExperimentsTaskFactory;
import edu.ucsf.rbvi.scNetViz.internal.tasks.SelectTaskFactory;
import edu.ucsf.rbvi.scNetViz.internal.tasks.SettingsTaskFactory;
import edu.ucsf.rbvi.scNetViz.internal.tasks.ShowExperimentTableTaskFactory;
import edu.ucsf.rbvi.scNetViz.internal.tasks.ShowResultsPanelTaskFactory;

public class CyActivator extends AbstractCyActivator {

	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		final StreamUtil streamUtil = getService(bc, StreamUtil.class);
		final CyServiceRegistrar serviceRegistrar = getService(bc, CyServiceRegistrar.class);

		final ScNVManager scNVManager = new ScNVManager(serviceRegistrar);

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
		//
		// Category commands
		// *scnetviz export category accession=yyyy category=zzzz file=*
		// ??scnetviz show category accession=yyyy category=zzzz
		// scnetviz calculate diffexp accession=yyyy category=zzzz categoryrow=nnn logfc=ddd min.pct=ppp
		//
		// Differential expression commands
		// *scnetviz export diffexp accesssion=yyyy file=*
		// scnetviz create networks accession=yyyy category=zzzz categoryrow=nnn logfc=ddd min.pct=ppp pvalue=ddd log2fc=nnn topgenes=nnn maxgenes=nnn
		// scnetviz show violin accession=yyyy
		// scnetviz show heatmap accession=yyyy

		// Register our sources
		scNVManager.addSource(new GXASource(scNVManager));
		scNVManager.addSource(new FileSource(scNVManager));

		{
			CalculateDECommandTaskFactory calcDE = new CalculateDECommandTaskFactory(scNVManager);
			Properties props = new Properties();
			props.setProperty(COMMAND_NAMESPACE, "scnetviz");
			props.setProperty(COMMAND, "calculate diffexp");
			props.setProperty(COMMAND_DESCRIPTION, "Calculate the table of differential expressions");
			props.setProperty(COMMAND_LONG_DESCRIPTION, "");
			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(COMMAND_EXAMPLE_JSON, "{}");
			scNVManager.registerService(calcDE, TaskFactory.class, props);
		}
		
		{
			CreateNetworkTaskFactory createNet = new CreateNetworkTaskFactory(scNVManager);
			Properties props = new Properties();
			props.setProperty(COMMAND_NAMESPACE, "scnetviz");
			props.setProperty(COMMAND, "create network");
			props.setProperty(COMMAND_DESCRIPTION, "Create the networks for differentially expressed genes");
			props.setProperty(COMMAND_LONG_DESCRIPTION, "");
			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(COMMAND_EXAMPLE_JSON, "{}");
			scNVManager.registerService(createNet, TaskFactory.class, props);
		}

		{
			ExportCategoryTaskFactory expCat = new ExportCategoryTaskFactory(scNVManager);
			Properties props = new Properties();
			props.setProperty(COMMAND_NAMESPACE, "scnetviz");
			props.setProperty(COMMAND, "export category");
			props.setProperty(COMMAND_DESCRIPTION, "Export a currently loaded category table");
			props.setProperty(COMMAND_LONG_DESCRIPTION, "");
			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(COMMAND_EXAMPLE_JSON, "{}");
			scNVManager.registerService(expCat, TaskFactory.class, props);
		}
		
		{
			ExportDiffExpTaskFactory expDE = new ExportDiffExpTaskFactory(scNVManager);
			Properties props = new Properties();
			props.setProperty(COMMAND_NAMESPACE, "scnetviz");
			props.setProperty(COMMAND, "export diffexp");
			props.setProperty(COMMAND_DESCRIPTION, "Export a differential expression table");
			props.setProperty(COMMAND_LONG_DESCRIPTION, "");
			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(COMMAND_EXAMPLE_JSON, "{}");
			scNVManager.registerService(expDE, TaskFactory.class, props);
		}

		{
			ExportExperimentTaskFactory expExp = new ExportExperimentTaskFactory(scNVManager);
			Properties props = new Properties();
			props.setProperty(COMMAND_NAMESPACE, "scnetviz");
			props.setProperty(COMMAND, "export experiment");
			props.setProperty(COMMAND_DESCRIPTION, "Export a currently loaded experiment table");
			props.setProperty(COMMAND_LONG_DESCRIPTION, "");
			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(COMMAND_EXAMPLE_JSON, "{}");
			scNVManager.registerService(expExp, TaskFactory.class, props);
		}

		{
			GetExperimentTaskFactory getExp = new GetExperimentTaskFactory(scNVManager);
			Properties props = new Properties();
			props.setProperty(COMMAND_NAMESPACE, "scnetviz");
			props.setProperty(COMMAND, "show experiment");
			props.setProperty(COMMAND_DESCRIPTION, "Show a currently loaded experiment");
			props.setProperty(COMMAND_LONG_DESCRIPTION, "");
			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(COMMAND_EXAMPLE_JSON, "{}");
			scNVManager.registerService(getExp, TaskFactory.class, props);
		}

		{
			ListExperimentsTaskFactory list = new ListExperimentsTaskFactory(scNVManager);
			Properties props = new Properties();
			props.setProperty(COMMAND_NAMESPACE, "scnetviz");
			props.setProperty(COMMAND, "list experiments");
			props.setProperty(COMMAND_DESCRIPTION, "List the currently loaded experiments");
			props.setProperty(COMMAND_LONG_DESCRIPTION, "");
			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(COMMAND_EXAMPLE_JSON, "{}");
			scNVManager.registerService(list, TaskFactory.class, props);
		}

		{
			SelectTaskFactory select = new SelectTaskFactory(scNVManager);
			Properties props = new Properties();
			props.setProperty(COMMAND_NAMESPACE, "scnetviz");
			props.setProperty(COMMAND, "select");
			props.setProperty(COMMAND_DESCRIPTION, "Select genes or assays in current tables");
			props.setProperty(COMMAND_LONG_DESCRIPTION, "");
			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(COMMAND_EXAMPLE_JSON, "{}");
			scNVManager.registerService(select, TaskFactory.class, props);
		}

		{
			ShowExperimentTableTaskFactory show = new ShowExperimentTableTaskFactory(scNVManager);
			Properties props = new Properties();
			props.put(TITLE, "Show experiment tables");
			props.put(PREFERRED_MENU, "Apps.scNetViz");
			props.setProperty(IN_TOOL_BAR, "FALSE");
			props.setProperty(IN_MENU_BAR, "TRUE");
			props.setProperty(COMMAND_NAMESPACE, "scnetviz");
			props.setProperty(COMMAND, "show experiment table");
			props.setProperty(COMMAND_DESCRIPTION, "Display the experiment table for a single experiment");
			props.setProperty(COMMAND_LONG_DESCRIPTION, "");
			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(COMMAND_EXAMPLE_JSON, "{}");
			scNVManager.registerService(show, TaskFactory.class, props);
		}

		{
			ShowResultsPanelTaskFactory results = new ShowResultsPanelTaskFactory(scNVManager);
			Properties props = new Properties();
			props.put(TITLE, "Show Results Panel");
			props.put(PREFERRED_MENU, "Apps.scNetViz");
			props.setProperty(IN_TOOL_BAR, "FALSE");
			props.setProperty(IN_MENU_BAR, "TRUE");
			scNVManager.registerService(results, TaskFactory.class, props);
		}

		{
			SettingsTaskFactory settings = new SettingsTaskFactory(scNVManager);
			Properties props = new Properties();
			props.put(TITLE, "Settings");
			props.put(PREFERRED_MENU, "Apps.scNetViz");
			props.setProperty(IN_TOOL_BAR, "FALSE");
			props.setProperty(IN_MENU_BAR, "TRUE");
			scNVManager.registerService(settings, TaskFactory.class, props);
		}

		/*
		{
			// This is for the basic reader.  Note that we'll also load a more advanced one below
			final BasicCyFileFilter mtxFileFilter = new BasicCyFileFilter(new String[] { "mtx" },
			                              new String[] { "application/mtx" }, "MTX", DataCategory.TABLE, streamUtil);
			final MTXReaderTaskFactory mtxReaderFactory = new MTXReaderTaskFactory(mtxFileFilter, scNVManager);
	
			Properties mtxReaderProps = new Properties();
			mtxReaderProps.put(ID, "mtxTableReaderFactory");
			registerService(bc, mtxReaderFactory, InputStreamTaskFactory.class, mtxReaderProps);
	
			Properties mtxImporterProps = new Properties();
			mtxImporterProps.setProperty(PREFERRED_MENU, "Apps.MTXImporter");
			mtxImporterProps.setProperty(TITLE, "Import MTX files");
			registerService(bc, mtxReaderFactory, TaskFactory.class, mtxImporterProps);
		}
		*/

	}
}
