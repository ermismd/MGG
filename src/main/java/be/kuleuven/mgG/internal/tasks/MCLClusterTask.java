package be.kuleuven.mgG.internal.tasks;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.utils.Mutils;



public class MCLClusterTask extends AbstractTask implements TaskObserver {

	private final MGGManager manager;
	private TaskMonitor taskMonitor;
	
	@Tunable(description = "Objective function",
			 longDescription = "Whether to use the Constant Potts Model (CPM) or modularity. Must be either \"CPM\" or \"modularity\".",
			 exampleStringValue = "CPM",
			 tooltip = "<html>Whether to use the Constant Potts Model (CPM) or modularity. Must be either 'CPM' or 'modularity'.</html>",
			 groups = {"Leiden Advanced Settings"}, gravity = 1.0)
	public ListSingleSelection<String> objective_function = new ListSingleSelection<>("CPM", "modularity");
	
	
	public void setattribute(ListSingleSelection<String> attr) { }
	
	@Tunable(description = "Resolution parameter",
			 longDescription = "The resolution parameter to use. "
			 		+ "Higher resolutions lead to more smaller communities, "
			 		+ "while lower resolutions lead to fewer larger communities.",
			 exampleStringValue = "1.0",
			 tooltip = "<html>The resolution parameter to use. Higher resolutions lead to more smaller communities,<br/>"
			 		+ "while lower resolutions lead to fewer larger communities.</html>",
			 groups = {"Leiden Advanced Settings"}, gravity = 3.0)
	public double resolution_parameter = 1.0;
	
	@Tunable(description = "Beta value",
			 longDescription = "Parameter affecting the randomness in the Leiden algorithm. This affects only the refinement step of the algorithm.",
			 exampleStringValue = "0.01",
			 tooltip = "<html>Parameter affecting the randomness in the Leiden algorithm. This affects only the refinement step of the algorithm.</html>",
			 groups = {"Leiden Advanced Settings"}, gravity = 4.0)
	public double beta = 0.01;
	
	@Tunable(description = "Number of iterations",
			 longDescription = "The number of iterations to iterate the Leiden algorithm. Each iteration may improve the partition further.",
			 exampleStringValue = "2",
			 tooltip = "<html>The number of iterations to iterate the Leiden algorithm. Each iteration may improve the partition further.</html>",
			 groups = {"Leiden Advanced Settings"}, gravity = 5.0)
	public int n_iterations = 2;
	

	
	public MCLClusterTask(final MGGManager manager) {
		this.manager = manager;
		this.taskMonitor = null;
		CyNetwork network = manager.getCurrentNetwork();
	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		
		
		
		this.taskMonitor = arg0;
		if (!manager.haveClusterMaker()) {
			this.taskMonitor.setStatusMessage("Installing clusterMaker2");
			Map<String, Object> args = new HashMap<>();
			args.put("app", "clusterMaker2");
			manager.executeCommand("apps", "install", args, this);
		} else {
			doClustering();	
			
			}
		}
	

	@ProvidesTitle
	public String getTitle() {
		return "Cluster network using Leiden";
	}

	@Override
	public void taskFinished(ObservableTask task) {
		doClustering();
	}

	@Override
	public void allFinished(FinishStatus finishStatus) {
	}


	public void doClustering() {
		this.taskMonitor.setStatusMessage("Clustering network using Leiden ...");
		Map<String, Object> args = new HashMap<>();
		args.put("resolution_parameter", resolution_parameter);
		args.put("beta",beta);
		args.put("iterations", n_iterations);
		args.put("objective_function", objective_function.getSelectedValue());
		args.put("attribute", "microbetag::weight");
		args.put("network", "current");
		args.put("showUI", "true");
		insertTasksAfterCurrentTask(manager.getCommandTaskIterator("cluster", "leiden", args, null));		
	}
	
}
