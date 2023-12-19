package be.kuleuven.mgG.internal.tasks;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.json.simple.JSONObject;

import be.kuleuven.mgG.internal.model.MGGManager;

public class CheckMetaDataFileTaskFactory  implements TaskFactory {

	
	
private final MGGManager mggManager;
    
	
	public CheckMetaDataFileTaskFactory(MGGManager mggManager) {
		
        this.mggManager=mggManager;
    }
	
	
	@Override
	public TaskIterator createTaskIterator() {
		// TODO Auto-generated method stub
		 return new TaskIterator(new CheckMetaDataFileTask( mggManager));
	}

	@Override
	public boolean isReady() {
		JSONObject metaDataObject=mggManager.getMetadataJsonObject();
		
		 return metaDataObject != null && metaDataObject.containsKey("metadata");
	}

}
