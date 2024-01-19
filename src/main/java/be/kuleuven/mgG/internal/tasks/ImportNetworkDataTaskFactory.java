package be.kuleuven.mgG.internal.tasks;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.json.simple.JSONObject;

import be.kuleuven.mgG.internal.model.MGGManager;

public class ImportNetworkDataTaskFactory implements TaskFactory {
	
	private final MGGManager mggManager;
	
	public ImportNetworkDataTaskFactory (MGGManager mggManager) {
		this.mggManager=mggManager;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new ImportNetworkData(mggManager));
	}

	@Override
	public boolean isReady() {
		
		JSONObject dataJsonObject = mggManager.getJsonObject();
        return dataJsonObject != null;
	}

}
