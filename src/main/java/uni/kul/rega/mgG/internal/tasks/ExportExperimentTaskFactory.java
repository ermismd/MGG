//package uni.kul.rega.mgG.internal.tasks;
//
//import org.cytoscape.work.AbstractTaskFactory;
//import org.cytoscape.work.TaskIterator;
//
//import uni.kul.rega.mgG.internal.model.ScNVManager;
//
//public class ExportExperimentTaskFactory extends AbstractTaskFactory {
//	final ScNVManager manager;
//
//	public ExportExperimentTaskFactory(final ScNVManager manager) {
//		super();
//		this.manager = manager;
//	}
//
//	public TaskIterator createTaskIterator() {
//		return new TaskIterator(new ExportExperimentTask(manager));
//	}
//
//	public boolean isReady() {
//		return true;
//	}
//
//}
//
