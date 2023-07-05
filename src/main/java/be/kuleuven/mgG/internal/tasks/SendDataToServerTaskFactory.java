package be.kuleuven.mgG.internal.tasks;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import be.kuleuven.mgG.internal.model.MGGManager;



public class SendDataToServerTaskFactory implements TaskFactory {
    private JSONArray jsonArray;
    private final MGGManager mggManager;
   
    
    
    
    public SendDataToServerTaskFactory(JSONArray jsonArray,MGGManager mggManager) {
        this.jsonArray = jsonArray;
        this.mggManager=mggManager;
    }

    @Override
    public TaskIterator createTaskIterator() {
        return new TaskIterator(new SendDataToServerTask( jsonArray, mggManager));
    }

    @Override
    public boolean isReady() {
        return true;
    }
}
