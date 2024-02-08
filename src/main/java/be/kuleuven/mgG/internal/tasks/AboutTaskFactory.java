package be.kuleuven.mgG.internal.tasks;

import javax.swing.JFrame;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class AboutTaskFactory implements TaskFactory{
	
	

	@Override
	public TaskIterator createTaskIterator() {
		
		return  new TaskIterator(new AboutTask());
	}

	@Override
	public boolean isReady() {
		
		return true;
	}

}
