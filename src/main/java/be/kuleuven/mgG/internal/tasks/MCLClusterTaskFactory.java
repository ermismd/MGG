package be.kuleuven.mgG.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import be.kuleuven.mgG.internal.model.MGGManager;

public class MCLClusterTaskFactory implements TaskFactory{
	
	private final MGGManager mggManager;
	
	
	
		public MCLClusterTaskFactory ( MGGManager mggManager) {
		
			
        this.mggManager=mggManager;
        
    }

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new MCLClusterTask(mggManager));
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return true;
	}

}
