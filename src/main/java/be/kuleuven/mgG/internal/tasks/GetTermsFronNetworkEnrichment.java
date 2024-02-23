package be.kuleuven.mgG.internal.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TaskMonitor.Level;
import org.cytoscape.work.util.ListSingleSelection;

import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.utils.Mutils;
import be.kuleuven.mgG.internal.view.EnrichmentAnalysis;
import be.kuleuven.mgG.internal.view.EnrichmentAnalysisTaskFactory;

	public class GetTermsFronNetworkEnrichment extends AbstractTask {
		
		
		private final MGGManager manager;
		private ArrayList<String> combinedTerms;
	
		public GetTermsFronNetworkEnrichment(MGGManager manager) {
			
			this.manager=manager;
		}
		
		
		
	
			 @Override
			    public void run(TaskMonitor taskMonitor) throws Exception {
				 
				        CyNetwork currentNetwork = manager.getCurrentNetwork();
				        
				        // Get attributes lists from PhenDb and Faprotax
				        List<String> phendbAttributeList = Mutils.getPhendbAttributes(currentNetwork);
				        List<String> faprotaxAttributeList = Mutils.getFaprotaxAttributes(currentNetwork);
				        
				        
				        taskMonitor.showMessage(Level.INFO, "phendb:"+phendbAttributeList.toString());
				        taskMonitor.showMessage(Level.INFO, "faprotaxAttributeList:"+faprotaxAttributeList.toString());

				        // Combine terms with category prefix
				        combinedTerms = new ArrayList<>(); 
				        for (String term : phendbAttributeList) {
				            combinedTerms.add("PhenDb: " + term);
				        }
				        for (String term : faprotaxAttributeList) {
				            combinedTerms.add("Faprotax: " + term);
				        }

				        
				        String[] options =  combinedTerms.toArray(new String[0]);
				        manager.setEnrichmentAttributeString(options);
				        
				        
				        taskMonitor.showMessage(Level.INFO, "options "+options .toString());
				        insertTasksAfterCurrentTask(this, new EnrichmentAnalysis(manager));
				        taskMonitor.showMessage(Level.INFO, "combined"+combinedTerms.toString());
				            
				            
				      
				        
				        
				    }
			 
	}
