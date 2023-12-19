package be.kuleuven.mgG.internal.tasks;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.json.simple.JSONObject;

import be.kuleuven.mgG.internal.model.MGGManager;

public class CheckAbudanceFileTaskFactory implements TaskFactory {

	private final MGGManager mggManager;
    
	
	public CheckAbudanceFileTaskFactory(MGGManager mggManager) {
		
        this.mggManager=mggManager;
    }
	
	
	@Override
	public TaskIterator createTaskIterator() {
		// TODO Auto-generated method stub
		 return new TaskIterator(new CheckAbudanceFileTask( mggManager));
	}

	@Override
	public boolean isReady() {
		JSONObject dataObject=mggManager.getJsonObject();
		
		 return dataObject != null && dataObject.containsKey("data");
	}
	}


