package be.kuleuven.mgG.internal.tasks;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.json.simple.JSONObject;

import be.kuleuven.mgG.internal.model.MGGManager;

public class CheckNetworkTaskFactory implements TaskFactory {
	
private final MGGManager mggManager;
    
	
	public CheckNetworkTaskFactory(MGGManager mggManager) {
		
        this.mggManager=mggManager;
    }
	@Override
	public TaskIterator createTaskIterator() {
		
		return new TaskIterator(new CheckNetworkTask ( mggManager));
	}

	@Override
	public boolean isReady() {
		
		JSONObject networkObject=mggManager.getNetworkObject();
		
		 return networkObject != null && networkObject.containsKey("network");
	}
	}


