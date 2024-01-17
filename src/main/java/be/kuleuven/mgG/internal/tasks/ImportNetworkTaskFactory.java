package be.kuleuven.mgG.internal.tasks;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import be.kuleuven.mgG.internal.model.MGGManager;

public class ImportNetworkTaskFactory implements TaskFactory {
	
	private final MGGManager mggManager;
	
	public ImportNetworkTaskFactory (MGGManager mggManager) {
		this.mggManager=mggManager;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new ImportNetwork(mggManager));
	}

	@Override
	public boolean isReady() {
		
		return true;
	}

}
