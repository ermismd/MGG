package uni.kul.rega.mgG.internal.sources.gxa.tasks;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;

import uni.kul.rega.mgG.internal.model.ScNVManager;
import uni.kul.rega.mgG.internal.sources.gxa.GXASource;

public class GXAShowEntriesTask extends AbstractTask {
	final ScNVManager scManager;
	final GXASource gxaSource;

	public GXAShowEntriesTask(final ScNVManager scManager, final GXASource gxaSource) {
		super();
		this.scManager = scManager;
		this.gxaSource = gxaSource;
	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		taskMonitor.setTitle("Show Single Cell Expression Atlas Entries Table");
		taskMonitor.setStatusMessage("Showing Single Cell Expression Atlas Table");
		gxaSource.showEntriesTable(true);
	}

	@ProvidesTitle
	public String getTitle() {return "Show Single Cell Expression Atlas Entries";}
}
