package be.kuleuven.mgG.internal.tasks;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import be.kuleuven.mgG.internal.model.MGGManager;

public class examoleFactory  implements TaskFactory{
	
	private final MGGManager manager;
	
	public examoleFactory(MGGManager manager) {
		this.manager=manager;
	}
	@Override
	public TaskIterator createTaskIterator() {
		// TODO Auto-generated method stub
		 return new TaskIterator(new examole( manager));
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return true;
	}



}
