//package uni.kul.rega.mgG.internal.tasks;
//
//import java.util.Collections;
//import java.util.List;
//import javax.swing.SwingUtilities;
//
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
////import uni.kul.rega.mgG.internal.view.TPMTab;
//
//public class ShowExperimentTableTask extends AbstractTask {
//	final ScNVManager manager;
//	Experiment experiment = null;
//	String tab = null;
//
//	@Tunable (description="Experiment to show")
//	public ListSingleSelection<Experiment> accession = null;
//
//	public ShowExperimentTableTask(final ScNVManager manager) {
//		super();
//		this.manager = manager;
//		accession = new ListSingleSelection<>(manager.getExperiments());
//	}
//
//	public ShowExperimentTableTask(final ScNVManager manager, Experiment experiment) {
//		super();
//		this.manager = manager;
//		this.experiment = experiment;
//		accession = new ListSingleSelection<Experiment>(Collections.singletonList(experiment));
//	}
//
//	public ShowExperimentTableTask(final ScNVManager manager, Experiment experiment, String tab) {
//		super();
//		this.manager = manager;
//		this.experiment = experiment;
//		accession = new ListSingleSelection<Experiment>(Collections.singletonList(experiment));
//		this.tab = tab;
//	}
//
//	public void run(TaskMonitor monitor) {
//		if (accession != null)
//			experiment = accession.getSelectedValue();
//
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				ExperimentFrame frame = manager.getExperimentFrame(experiment);
//				if (frame == null) {
//					// Create the Experiment Frame
//					frame = new ExperimentFrame(manager, experiment);
//					// Add our TPM tab
//					String accession = experiment.getMetadata().get(Metadata.ACCESSION).toString();
//          System.out.println("accession = "+accession);
//				//	frame.addTPMContent(accession+": TPM Tab", new TPMTab(manager, experiment, frame));
//					// Add our Categories tab if we have any categories
//					List<Category> categories = experiment.getCategories();
//					if (categories != null && categories.size() > 0)
//						frame.addCategoriesContent(accession+": Categories Tab", new CategoriesTab(manager, experiment, frame));
//
//					// Add our Differential Expression tab (if we have one)
//				//	DifferentialExpression diffExp = experiment.getDiffExp();
//					//if (diffExp != null) {
//					//	DiffExpTab dTab = new DiffExpTab(manager, experiment, frame, diffExp.getCurrentCategory(), diffExp);
//					//	frame.addDiffExpContent(accession+": DiffExp Tab", dTab);
//				//	}
//					manager.addExperimentFrame(experiment, frame);
//				} else {
//					frame.setVisible(true);
//				}
//				if (tab != null) {
//					frame.selectTab(tab);
//				}
//			}
//		});
//	}
//}
