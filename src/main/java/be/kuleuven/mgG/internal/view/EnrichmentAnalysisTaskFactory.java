package be.kuleuven.mgG.internal.view;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.utils.Mutils;

public class EnrichmentAnalysisTaskFactory implements TaskFactory {

	private final  MGGManager mggManager;

    public EnrichmentAnalysisTaskFactory(MGGManager mggManager) {
    	this.mggManager = mggManager;
    }

    @Override
    public TaskIterator createTaskIterator() {
        // Obtain the current network
        CyNetwork currentNetwork = mggManager.getCurrentNetwork();
        if (currentNetwork != null) {
            // Create and return a new TaskIterator with the EnrichmentAnalysis task
            return new TaskIterator(new EnrichmentAnalysis(mggManager));
        } else {
            // If there's no current network, we can't create the task, so return an empty TaskIterator
            return new TaskIterator();
        }
    }

    @Override
    public boolean isReady() {
        CyNetwork currentNetwork = mggManager.getCurrentNetwork();
	    return currentNetwork != null && Mutils.isMGGNetwork(currentNetwork);
    }

   
}