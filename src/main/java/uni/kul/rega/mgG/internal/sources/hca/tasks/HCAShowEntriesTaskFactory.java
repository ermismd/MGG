package uni.kul.rega.mgG.internal.sources.hca.tasks;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import uni.kul.rega.mgG.internal.model.ScNVManager;
import uni.kul.rega.mgG.internal.sources.hca.HCASource;

public class HCAShowEntriesTaskFactory extends AbstractTaskFactory {
	final ScNVManager scManager;
	final HCASource hcaSource;

	public HCAShowEntriesTaskFactory(final ScNVManager scManager, final HCASource hcaSource) {
		super();
		this.scManager = scManager;
		this.hcaSource = hcaSource;
	}

	@Override
	public TaskIterator createTaskIterator() {
		TaskIterator ti = new TaskIterator();
		if (hcaSource.getMetadata().size() == 0) {
			ti.append(new HCAFetchEntriesTask(scManager, hcaSource));
		}
		ti.append(new HCAShowEntriesTask(scManager, hcaSource));
		return ti;
	}

	@Override
	public boolean isReady() { return true; }

}
