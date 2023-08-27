package be.kuleuven.mgG.internal.tasks;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import be.kuleuven.mgG.internal.model.MGGManager;



public class SendDataToServerTaskFactory implements TaskFactory {
    
    private final MGGManager mggManager;
    private JSONObject jsonObject;
    
    
    
    public SendDataToServerTaskFactory(JSONObject jsonObject,MGGManager mggManager) {
    	this.jsonObject = jsonObject;
        this.mggManager=mggManager;
    }

    @Override
    public TaskIterator createTaskIterator() {
        return new TaskIterator(2,new SendDataToServerTask(jsonObject, mggManager),new CreateNetworkTask(mggManager));
        
        
    }

    @Override
    public boolean isReady() {
        return true;
    }
}
