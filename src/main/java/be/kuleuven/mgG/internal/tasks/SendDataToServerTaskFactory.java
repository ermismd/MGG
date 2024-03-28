package be.kuleuven.mgG.internal.tasks;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import be.kuleuven.mgG.internal.model.MGGManager;



public class SendDataToServerTaskFactory implements TaskFactory {
    
    private final MGGManager mggManager;
    
    
    
    
    public SendDataToServerTaskFactory(MGGManager mggManager) {
    	
        this.mggManager=mggManager;
       
    	
    }
    
    @Override
    public TaskIterator createTaskIterator() {
        return new TaskIterator(3,new SendDataToServerTask( mggManager),new CreateNetworkTask(mggManager),new CreateMGGVisualStyleTask(mggManager));
        
        	
    }
    	
    @Override
    public boolean isReady() {
        
    	   if (mggManager.getJsonObject() != null && mggManager.getJsonObject().containsKey("data")) {
    	        
    	        //Object data = dataObject.get("data");
    	        //if (data instanceof JSONArray) {
    	            
    	            return true;
    	        }
    	    
    	    return false; 
    	}
}
