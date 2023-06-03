package uni.kul.rega.mgG.internal.tasks;

import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.json.JSONResult;
import org.cytoscape.work.util.ListSingleSelection;

import uni.kul.rega.mgG.internal.api.Category;
import uni.kul.rega.mgG.internal.api.Experiment;
import uni.kul.rega.mgG.internal.api.Matrix;
import uni.kul.rega.mgG.internal.api.Metadata;
import uni.kul.rega.mgG.internal.model.ScNVManager;

public class ExportExperimentTask extends AbstractTask {
	final ScNVManager manager;
	Experiment experiment = null;

	@Tunable (description="Experiment to export")
	public ListSingleSelection<Experiment> accession = null;

	@ContainsTunables
	public ExportCSVTask exportCSVTask;

	public ExportExperimentTask(final ScNVManager manager) {
		super();
		this.manager = manager;
		accession = new ListSingleSelection<>(manager.getExperiments());
		exportCSVTask = new ExportCSVTask(manager);
	}

	public void run(TaskMonitor monitor) {
		experiment = accession.getSelectedValue();
		Matrix matrix = experiment.getMatrix();
		exportCSVTask.setMatrix(matrix);
		insertTasksAfterCurrentTask(exportCSVTask);
	}

	@ProvidesTitle
	public String title() { return "Export Experiments"; }

}

