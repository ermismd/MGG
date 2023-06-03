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
import uni.kul.rega.mgG.internal.model.DifferentialExpression;
import uni.kul.rega.mgG.internal.model.ScNVManager;

public class ExportDiffExpTask extends AbstractTask {
	final ScNVManager manager;
	Experiment experiment = null;

	@Tunable (description="Experiment to export the differential expression from")
	public ListSingleSelection<Experiment> accession = null;

	@ContainsTunables
	public ExportCSVTask exportCSVTask;

	public ExportDiffExpTask(final ScNVManager manager) {
		super();
		this.manager = manager;
		accession = new ListSingleSelection<>(manager.getExperiments());
		exportCSVTask = new ExportCSVTask(manager);
	}

	public void run(TaskMonitor monitor) {
		experiment = accession.getSelectedValue();
		DifferentialExpression diffExp = experiment.getDiffExp();
		if (diffExp == null) {
			monitor.showMessage(TaskMonitor.Level.ERROR, "No differential expression calculated for "+experiment.toString());
			return;
		}
		exportCSVTask.setMatrix(diffExp);
		insertTasksAfterCurrentTask(exportCSVTask);
	}

	@ProvidesTitle
	public String title() { return "ExportExperiments"; }

}

