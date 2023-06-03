package uni.kul.rega.mgG.internal.sources.gxa.tasks;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import uni.kul.rega.mgG.internal.model.ScNVManager;
import uni.kul.rega.mgG.internal.sources.gxa.GXASource;

public class GXAShowEntriesTaskFactory extends AbstractTaskFactory {
	final ScNVManager scManager;
	final GXASource gxaSource;

	public GXAShowEntriesTaskFactory(final ScNVManager scManager, final GXASource gxaSource) {
		super();
		this.scManager = scManager;
		this.gxaSource = gxaSource;
	}

	@Override
	public TaskIterator createTaskIterator() {
		TaskIterator ti = new TaskIterator();
		if (gxaSource.getMetadata().size() == 0) {
			ti.append(new GXAFetchEntriesTask(scManager, gxaSource));
		}
		ti.append(new GXAShowEntriesTask(scManager, gxaSource));
		return ti;
	}

	@Override
	public boolean isReady() { return true; }

}
