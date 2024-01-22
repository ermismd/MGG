package be.kuleuven.mgG.internal.tasks;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import be.kuleuven.mgG.internal.model.MGGManager;

public class CreateMGGVisualStyleTaskFactory implements TaskFactory {
	
	  private final MGGManager manager;
	  
	  
	  public CreateMGGVisualStyleTaskFactory(MGGManager manager) {
		  this.manager = manager;
	  }
	
	  
	  
	  
	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new CreateMGGVisualStyleTask(manager));
	}

	
	
	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return true;
	}

	
	
	
	
}
	
	
	
	
	
	
	
