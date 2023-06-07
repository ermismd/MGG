//package uni.kul.rega.mgG.internal.tasks;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Properties;
//import javax.swing.SwingUtilities;
//
//import org.cytoscape.application.events.SetCurrentNetworkListener;
//import org.cytoscape.application.swing.CySwingApplication;
//import org.cytoscape.application.swing.CytoPanel;
//import org.cytoscape.application.swing.CytoPanelComponent;
//import org.cytoscape.application.swing.CytoPanelComponent2;
//import org.cytoscape.application.swing.CytoPanelName;
//import org.cytoscape.application.swing.CytoPanelState;
//import org.cytoscape.work.AbstractTask;
//import org.cytoscape.work.TaskMonitor;
//import org.cytoscape.work.Tunable;
//import org.cytoscape.work.util.ListSingleSelection;
//
//import uni.kul.rega.mgG.internal.api.Category;
//import uni.kul.rega.mgG.internal.api.Experiment;
//import uni.kul.rega.mgG.internal.api.Metadata;
////import uni.kul.rega.mgG.internal.model.DifferentialExpression;
//import uni.kul.rega.mgG.internal.model.ScNVManager;
//import uni.kul.rega.mgG.internal.view.CategoriesTab;
////import uni.kul.rega.mgG.internal.view.DiffExpTab;
//import uni.kul.rega.mgG.internal.view.ExperimentFrame;
//import uni.kul.rega.mgG.internal.view.ScNVCytoPanel;
////import uni.kul.rega.mgG.internal.view.TPMTab;
//
//public class ShowResultsPanelTask extends AbstractTask {
//	final ScNVManager manager;
//	Experiment experiment = null;
//	ScNVCytoPanel resultsPanel;
//
//	@Tunable (description="Experiment to show")
//	public ListSingleSelection<Experiment> accession = null;
//
//	public ShowResultsPanelTask(final ScNVManager manager) {
//		super();
//		this.manager = manager;
//		accession = new ListSingleSelection<>(manager.getExperiments());
//	}
//
//	public ShowResultsPanelTask(final ScNVManager manager, Experiment experiment) {
//		super();
//		this.manager = manager;
//		this.experiment = experiment;
//		accession = new ListSingleSelection<Experiment>(Collections.singletonList(experiment));
//	}
//
//	public void run(TaskMonitor monitor) {
//		if (accession != null)
//			experiment = accession.getSelectedValue();
//
//		CySwingApplication swingApp = manager.getService(CySwingApplication.class);
//		CytoPanel panel = swingApp.getCytoPanel(CytoPanelName.EAST);
//		panel.setState(CytoPanelState.DOCK);
//		int id = panel.indexOfComponent("edu.ucsf.rbvi.scNetViz.ResultsPanel");
//		if (id < 0) {
//			// System.out.println("Experiment = "+experiment);
//			resultsPanel = new ScNVCytoPanel(manager, experiment);
//			manager.registerService(resultsPanel, CytoPanelComponent.class, new Properties());
//			manager.registerService(resultsPanel, SetCurrentNetworkListener.class, new Properties());
//		} else {
//			resultsPanel = (ScNVCytoPanel)panel.getComponentAt(id);
//			resultsPanel.setExperiment(experiment);
//			panel.setSelectedIndex(id);
//		}
//
//	}
//}
