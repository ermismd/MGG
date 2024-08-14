package be.kuleuven.mgG.internal.tasks;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

class DummyErrorTask extends AbstractTask {
    private final String message;

    public DummyErrorTask(String message) {
        this.message = message;
    }

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setStatusMessage(message);
		
	}
}