//package uni.kul.rega.mgG.internal.tasks;
//
//import org.cytoscape.work.AbstractTaskFactory;
//import org.cytoscape.work.TaskIterator;
//
//import uni.kul.rega.mgG.internal.api.Matrix;
//import uni.kul.rega.mgG.internal.model.ScNVManager;
//
//public class ExportCSVTaskFactory extends AbstractTaskFactory {
//	final ScNVManager manager;
//	final Matrix matrix;
//
//	public ExportCSVTaskFactory(final ScNVManager manager, final Matrix matrix) {
//		super();
//		this.manager = manager;
//		this.matrix = matrix;
//	}
//
//	public TaskIterator createTaskIterator() {
//		return new TaskIterator(new ExportCSVTask(manager, matrix));
//	}
//
//	public boolean isReady() {
//		if (manager.getExperiments() == null || manager.getExperiments().size() == 0)
//			return false;
//		return true;
//	}
//
//}
//
