//package uni.kul.rega.mgG.internal.tasks;
//
//import org.cytoscape.work.AbstractTaskFactory;
//import org.cytoscape.work.TaskIterator;
//
//import uni.kul.rega.mgG.internal.model.ScNVManager;
//
//public class SettingsTaskFactory extends AbstractTaskFactory {
//	final ScNVManager manager;
//
//	public SettingsTaskFactory(final ScNVManager manager) {
//		super();
//		this.manager = manager;
//	}
//
//	public TaskIterator createTaskIterator() {
//		return new TaskIterator(new SettingsTask(manager));
//	}
//
//	public boolean isReady() {
//		return true;
//	}
//
//}
//
