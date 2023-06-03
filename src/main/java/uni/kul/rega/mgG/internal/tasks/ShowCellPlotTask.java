package uni.kul.rega.mgG.internal.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import uni.kul.rega.mgG.internal.api.Category;
import uni.kul.rega.mgG.internal.api.Experiment;
import uni.kul.rega.mgG.internal.api.Matrix;
import uni.kul.rega.mgG.internal.api.Metadata;
import uni.kul.rega.mgG.internal.model.MatrixMarket;
import uni.kul.rega.mgG.internal.model.ScNVManager;
import uni.kul.rega.mgG.internal.utils.HTTPUtils;
import uni.kul.rega.mgG.internal.view.ViewUtils;


// TODO: Consider random subsampling?
// TODO: Expose filter criteria
public class ShowCellPlotTask extends AbstractTask {
  final ScNVManager manager;

	@Tunable (description="Experiment to show plot for")
	public ListSingleSelection<String> accession = null;

  @Tunable (description="Category",context="nogui",
            longDescription="Category to use for coloring the plot")
  public String category;

  @Tunable (description="Category row",context="nogui",
            longDescription="Category row to use for coloring the plot")
  public int categoryRow;

  @Tunable (description="Gene",context="nogui",
            longDescription="Gene to use for coloring the plot")
  public String gene;

	public ShowCellPlotTask(final ScNVManager manager) {
		List<String> accessions = new ArrayList<String>(manager.getExperimentAccessions());
		accession = new ListSingleSelection<>(new ArrayList<String>(manager.getExperimentAccessions()));
    this.manager = manager;
	}

	public void run(TaskMonitor monitor) {
		// Get the experiment
		Experiment exp = manager.getExperiment(accession.getSelectedValue());
		// Get the MatrixMarket matrix
		Matrix mtx = exp.getMatrix();
		if (!(mtx instanceof MatrixMarket)) {
			monitor.showMessage(TaskMonitor.Level.ERROR,"Matrix must be of type MatrixMarket");
			return;
		}
		MatrixMarket mmtx = (MatrixMarket)mtx;

    if (exp.getPlotType() == null) {
			monitor.showMessage(TaskMonitor.Level.ERROR,"Plot must be calculated first");
			return;
    }

    showPlot(exp, category, categoryRow, gene);
	}

  protected void showPlot(Experiment exp, String categoryName, int categoryRow, String gene) {
    Category cat = null;
    if (categoryName != null) {
		  List<Category> categories = exp.getCategories();
		  if (categories != null) {
        for (Category c : categories) {
          if (c.toString().equalsIgnoreCase(categoryName)) {
            cat = c;
            break;
          }
        }
      }
		}

    int rowNumber = -1;
    if (gene != null) {
      int row = 0;
      for (String rowName: exp.getMatrix().getRowLabels(0)) {
        if (rowName.equalsIgnoreCase(gene)) {
          rowNumber = row;
          break;
        }
        row++;
      }
    }
    ViewUtils.showPlot(manager, exp, cat, categoryRow, rowNumber);
  }
}
