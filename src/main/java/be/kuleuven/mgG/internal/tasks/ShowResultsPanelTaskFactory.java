package be.kuleuven.mgG.internal.tasks;

import java.util.Properties;

import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.ENABLE_FOR;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_BEFORE;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import be.kuleuven.mgG.internal.model.MGGManager;


public class ShowResultsPanelTaskFactory extends AbstractTaskFactory {
	final MGGManager manager;
	boolean show = false;

	public ShowResultsPanelTaskFactory(final MGGManager manager) {
		this.manager = manager;
	}

	public TaskIterator createTaskIterator() {
		return new TaskIterator(new ShowResultsPanelTask(manager, this, show));
	}


	public boolean isReady() {
		// We always want to be able to shut it off
	    if (!show) return true;

	    CyNetwork net = manager.getCurrentNetwork();
	    if (net == null) return false;

	    //* Check for the existence of the 'flashweave-score' column in the edge table
	   boolean hasFlashweaveScore = net.getRow(net).get("flashweave-score",Double.class) != null; 

	    // Implement other checks if necessary
	    // boolean hasIdColumn = net.getDefaultNodeTable().getColumn("@id") != null;
	    // boolean hasScoreColumn = net.getDefaultEdgeTable().getColumn("score") != null;

	    //* Return true if the column exists, otherwise return false
	   return hasFlashweaveScore;
	   
	    
	    
	}
}

