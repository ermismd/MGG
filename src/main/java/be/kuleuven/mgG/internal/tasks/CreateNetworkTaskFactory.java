package be.kuleuven.mgG.internal.tasks;

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import be.kuleuven.mgG.internal.model.MGGManager;

public class CreateNetworkTaskFactory implements TaskFactory {
    private final MGGManager mggManager;
  

    public CreateNetworkTaskFactory(MGGManager mggManager) {
        this.mggManager = mggManager;
    
    }
    
    @Override
    public TaskIterator createTaskIterator() {
        return new TaskIterator(new CreateNetworkTask(mggManager));
    }

    @Override
    public boolean isReady() {
        // You can add any conditions that need to be met before the task can be executed.
        // For example, you might want to check if the JSON response is not null.
        return mggManager.getServerResponse() != null;
    }
}