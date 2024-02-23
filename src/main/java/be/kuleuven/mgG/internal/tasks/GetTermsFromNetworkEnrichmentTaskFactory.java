package be.kuleuven.mgG.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.utils.Mutils;
import be.kuleuven.mgG.internal.view.EnrichmentAnalysis;

public class GetTermsFromNetworkEnrichmentTaskFactory implements TaskFactory{
	
	private final  MGGManager mggManager;
	
	public GetTermsFromNetworkEnrichmentTaskFactory(MGGManager mggManager) {
		this.mggManager=mggManager;
	}

	@Override
	public TaskIterator createTaskIterator() {
		
		return new TaskIterator(new GetTermsFronNetworkEnrichment(mggManager));
				//new EnrichmentAnalysis(mggManager));
	}

	@Override
	public boolean isReady() {
		
		CyNetwork currentNetwork = mggManager.getCurrentNetwork();
	    return currentNetwork != null && Mutils.isMGGNetwork(currentNetwork);
	    		
		
	}

	
	
}
