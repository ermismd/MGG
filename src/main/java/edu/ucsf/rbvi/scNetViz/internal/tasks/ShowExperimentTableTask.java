package edu.ucsf.rbvi.scNetViz.internal.tasks;

import javax.swing.SwingUtilities;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.scNetViz.internal.api.Experiment;
import edu.ucsf.rbvi.scNetViz.internal.api.Metadata;
import edu.ucsf.rbvi.scNetViz.internal.model.ScNVManager;
import edu.ucsf.rbvi.scNetViz.internal.model.DifferentialExpression;
import edu.ucsf.rbvi.scNetViz.internal.view.ExperimentFrame;
import edu.ucsf.rbvi.scNetViz.internal.view.CategoriesTab;
import edu.ucsf.rbvi.scNetViz.internal.view.DiffExpTab;
import edu.ucsf.rbvi.scNetViz.internal.view.TPMTab;

public class ShowExperimentTableTask extends AbstractTask {
	final ScNVManager manager;
	Experiment experiment = null;

	@Tunable (description="Experiment to show")
	public ListSingleSelection<Experiment> experiments = null;

	public ShowExperimentTableTask(final ScNVManager manager) {
		super();
		this.manager = manager;
		experiments = new ListSingleSelection<>(manager.getExperiments());
	}

	public ShowExperimentTableTask(final ScNVManager manager, Experiment experiment) {
		super();
		this.manager = manager;
		this.experiment = experiment;
		experiments = null;
	}

	public void run(TaskMonitor monitor) {
		if (experiments != null)
			experiment = experiments.getSelectedValue();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ExperimentFrame frame = manager.getExperimentFrame(experiment);
				if (frame == null) {
					// Create the Experiment Frame
					frame = new ExperimentFrame(manager);
					// Add our TPM tab
					String accession = experiment.getMetadata().get(Metadata.ACCESSION).toString();
					frame.addTPMContent(accession+": TPM Tab", new TPMTab(manager, experiment, frame));
					// Add our Categories tab
					frame.addCategoriesContent(accession+": Categories Tab", new CategoriesTab(manager, experiment, frame));
					// Add our Differential Expression tab (if we have one)
					DifferentialExpression diffExp = experiment.getDiffExp();
					if (diffExp != null) {
						DiffExpTab dTab = new DiffExpTab(manager, experiment, frame, diffExp.getCurrentCategory(), diffExp);
						frame.addDiffExpContent(accession+": DiffExp Tab", dTab);
					}
					manager.addExperimentFrame(experiment, frame);
				} else {
					frame.setVisible(true);
				}
			}
		});
	}
}
