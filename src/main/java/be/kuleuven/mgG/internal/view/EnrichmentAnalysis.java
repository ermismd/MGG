	package be.kuleuven.mgG.internal.view;
	
	import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
	import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JFrame;
	import javax.swing.JOptionPane;
	import javax.swing.SwingUtilities;
	
	import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
	import org.cytoscape.model.CyNode;
	import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
	import org.cytoscape.work.TaskMonitor;
	import org.cytoscape.work.Tunable;
	import org.cytoscape.work.TaskMonitor.Level;
	import org.cytoscape.work.util.ListSingleSelection;
	import org.hipparchus.distribution.discrete.HypergeometricDistribution;
	
	import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.tasks.GetTermsFronNetworkEnrichment;
import be.kuleuven.mgG.internal.utils.AnalysisContext;
import be.kuleuven.mgG.internal.utils.EnrichmentResult;
import be.kuleuven.mgG.internal.utils.Mutils;
	
	public class EnrichmentAnalysis extends AbstractTask {
		private final MGGManager mggManager ;
		
		private Set<String> uniqueAttributeNames = new HashSet<>();
		
	    public static String ENRICHED="Enrichment";
	    public static String DEPLETED="Depletion";
	    
	    private List<Double> allPValuesEnriched = new ArrayList<>();
	    private List<Double> allPValuesDepleted = new ArrayList<>();
	    private List<AnalysisContext> EnrichedContexts = new ArrayList<>();
	    private List<AnalysisContext> DepletedContexts = new ArrayList<>();
	    		 	
		 private List<EnrichmentResult> enrichmentResults;
		 
		 	 	 
		 @Tunable(description="Choose the method for FDR correction (Bonferroni or Benjamini-Hochberg)",groups={"Enrichment Parameters"},
		            longDescription="Select whether to use (Bonferroni or Benjamini-Hochberg for significance testing.",
		            exampleStringValue="Bonferroni ",tooltip="Choose between Bonferroni or Benjamini-Hochberg for Significance",
		            gravity=1.0,
		            required=true)
		  public ListSingleSelection<String> cutoffType = new ListSingleSelection<>("Bonferroni","Benjamini_Hochberg");
		   
		 @Tunable(description=" Cutoff value ",groups={" Enrichment Parameters "},
		              longDescription="Specify the False Discovery Rate (FDR) cutoff to consider a result significant.",
		              exampleStringValue="0,05",tooltip="Choose the FDR Cutoff for Significance",
		              gravity=2.0)
		  public Double Cutoff = 0.05;
		    
		    
	
	    public EnrichmentAnalysis(MGGManager mggManager) {
	    	 this.mggManager = mggManager;            
	         this.enrichmentResults = new ArrayList<>();	         
	    }
	
	
	    	    	 	 	  	   	    
	    @Override
	    public void run(TaskMonitor taskMonitor) throws Exception {
	    	 uniqueAttributeNames.clear();
	    	try {
	    		
	    		
	    		
	    		CyNetwork currentNetwork=mggManager.getCurrentNetwork();
	    		
	    		// Retrieve all Faprotax and Phendb terms for the network
	            List<String> faprotaxTerms = Mutils.getFaprotaxAttributes(currentNetwork);
	            List<String> phendbTerms = Mutils.getPhendbAttributes(currentNetwork); 
	            
	            
	           // String selectedTypeCutoff= cutoffType.getSelectedValue();

	            // Combine all terms into a single list 
	            List<String> allTerms = new ArrayList<>();
	            allTerms.addAll(faprotaxTerms);
	            allTerms.addAll(phendbTerms);
	            
	            
	            CyTable nodeTable = currentNetwork.getDefaultNodeTable();
	           // CyColumn mantaClusterColumn = nodeTable.getColumn("manta::cluster");
	            CyColumn microbetagClusterColumn = nodeTable.getColumn("microbetag::cluster");
	            
	            // Check if manta::cluster or microbetag::cluster columns exists.If they dont exist enrichment doesnt proceed
	            
	            if (microbetagClusterColumn == null) {	           
	                taskMonitor.setStatusMessage("There is no microbetag::cluster column, "
	                		+ "cannot perform enrichment/depletion analysis.");
	                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "There is no microbetag::cluster column, cannot perform "
	                		+ "enrichment/depletion analysis.", "Information", JOptionPane.INFORMATION_MESSAGE));
	                return; 
	            }
	            
	            //If manta::cluster or microbetag::cluster column exists then proceed to cehck for the number of clusters.
	            // with 1 cluster in total the enrichment doesnt proceed so
	            // check for cluster count to ensure there are multiple clusters
	            int totalClusters = getTotalClusterCount(currentNetwork);
	            if (totalClusters <= 1) {
	                taskMonitor.setStatusMessage("Only one cluster present, cannot perform enrichment/depletion analysis.");
	                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Enrichment/depletion analysis requires multiple clusters. Only one cluster present, cannot proceed.", "Information", JOptionPane.INFORMATION_MESSAGE));
	                return; 
	            }

	            

	            
	       for (String term : allTerms) {
	              String attributeName = determineAttributeName(term);
	              if (attributeName != null) {
	                  uniqueAttributeNames.add(attributeName); 
	                  performAnalysisForTerm(currentNetwork, attributeName, term, taskMonitor);
	             } else {
	                  taskMonitor.setStatusMessage("The provided term " + term + " does not match any known attributes. ");
	             		}
	        }
	               
	       
	       
	   	       //if cutoff type is bonferroni, do bonferroni correction else do BH
	       
	            if ("Bonferroni".equals(cutoffType.getSelectedValue())) {
	                applyBonferroniCorrection();
	            } else if ("Benjamini_Hochberg".equals(cutoffType.getSelectedValue())) {
	            	applyBenjaminiHochbergCorrection();
	            }

	       
	        showResultsInPanel();
	            
	                        
	        } catch (Exception e) {
	            taskMonitor.setStatusMessage("An error occurred during the enrichment analysis: " + e.getMessage());
	            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
	        }
	    }
	    		
	    		

	    private void performAnalysisForTerm(CyNetwork currentNetwork, String attributeName, String term, TaskMonitor taskMonitor) {
	    	
	        int totalNodes = currentNetwork.getNodeCount();
	        Map<Integer, Set<Long>> clusterToSuids = new HashMap<>();
	        Map<Integer, List<String>> clusterToNodeNamesWithTerm = new HashMap<>();
	        Map<Integer, Integer> termClusterCounts = new HashMap<>();
	        
	        //Check again for the existance of manta::cluster or microbetag::cluster---> this is redudantO remove the redundancy
	        //TODO remove redundancy
	        
	        CyTable nodeTable = currentNetwork.getDefaultNodeTable();
	        CyColumn mantaClusterColumn = nodeTable.getColumn("manta::cluster");
	        CyColumn microbetagClusterColumn = nodeTable.getColumn("microbetag::cluster");
	                        
	        for (CyNode node : currentNetwork.getNodeList()) {
	            CyRow row = currentNetwork.getRow(node);
	            Boolean hasAttribute = row.get(attributeName, Boolean.class);
	            Integer mantaCluster = null;
	            
	            //If manta::cluster column exists use the manta::cluster column ,if doesnt exist look for microbetag::cluster column
	            //if both doesnt exist, mantaCluster is null,only 1 cluster will exist
	            
	            if (mantaClusterColumn != null && mantaClusterColumn.getType() == Integer.class) {
	                mantaCluster = row.get("manta::cluster", Integer.class, -1);
	            } else if (microbetagClusterColumn != null && microbetagClusterColumn.getType() == Integer.class) {
	                mantaCluster = row.get("microbetag::cluster", Integer.class, -1);
	            }
	            //Integer mantaCluster = row.get("mantaCluster", Integer.class, -1);

	            clusterToSuids.computeIfAbsent(mantaCluster, k -> new HashSet<>()).add(node.getSUID());

	            if (Boolean.TRUE.equals(hasAttribute)) {
	                termClusterCounts.merge(mantaCluster, 1, Integer::sum);
	                clusterToNodeNamesWithTerm.computeIfAbsent(mantaCluster, k -> 
	                									new ArrayList<>()).add(row.get(CyNetwork.NAME, String.class));
	            }
	        }

	        int totalClusters = clusterToSuids.keySet().size();


	        for (Integer mantaCluster : clusterToSuids.keySet()) {
	        	int totalNodesInCluster = clusterToSuids.get(mantaCluster).size();
	        	
	        	int totalAttributesTested = uniqueAttributeNames.size();
	        	          
	            
	            //int total = samples; //  = number of environments
	            int nodesWithPropertyXInCluster = termClusterCounts.getOrDefault(mantaCluster, 0);
	            int totalNodesWithPropertyX = getTotalNodesWithPropertyX(currentNetwork, attributeName);

	                            
	           HypergeometricDistribution distribution = new HypergeometricDistribution(totalNodes, totalNodesWithPropertyX, totalNodesInCluster);

	          
	           
	           // For enrichment analysis (Upper Tail, False)
	           double pValForEnrichment = 1 - distribution.cumulativeProbability(nodesWithPropertyXInCluster - 1);
	           	                 
	           // For depletion analysis (Lower Tail, True)
	           double pValForDepletion = distribution.cumulativeProbability(nodesWithPropertyXInCluster);
	           
	         
	           
	           
	           List<String> nodeNamesWithTerm = clusterToNodeNamesWithTerm.getOrDefault(mantaCluster, new ArrayList<>());
               List<Long> suidsWithTerm = new ArrayList<>(clusterToSuids.get(mantaCluster).stream()
                                                           .filter(suid -> nodeNamesWithTerm.contains(currentNetwork.getRow(currentNetwork.getNode(suid)).get(CyNetwork.NAME, String.class)))
                                                           .collect(Collectors.toList()));
               
               
               allPValuesEnriched.add(pValForEnrichment);
               EnrichedContexts.add(new AnalysisContext(ENRICHED, mantaCluster, term, suidsWithTerm, nodeNamesWithTerm,pValForEnrichment,
                       nodesWithPropertyXInCluster, totalNodesInCluster, totalNodes, totalNodesWithPropertyX));

               allPValuesDepleted.add(pValForDepletion);
               DepletedContexts.add(new AnalysisContext(DEPLETED, mantaCluster, term, suidsWithTerm, nodeNamesWithTerm,pValForDepletion,
                       nodesWithPropertyXInCluster, totalNodesInCluster, totalNodes, totalNodesWithPropertyX));
	           
	        	        
	        
	    }}

	    
	    private String determineAttributeName(String term) {
	        CyNetwork currentNetwork = mggManager.getCurrentNetwork();
	        String attributeName = "faprotax::" + term;
	        if (currentNetwork.getDefaultNodeTable().getColumn(attributeName) == null) {
	            attributeName = "phendb::" + term;
	            if (currentNetwork.getDefaultNodeTable().getColumn(attributeName) == null) {
	                return null; // Term not found
	            }
	        }
	        return attributeName;
	    }
	    
	    
	    
	    private void addEnrichmentResult(AnalysisContext context, double originalPValue, double adjustedPValue) {
	        EnrichmentResult result = new EnrichmentResult(context.getType(), context.getMantaCluster(),
	                context.getTerm(), context.getSuids(), originalPValue, adjustedPValue, context.getNodeNames(),
	                context.getNodesWithPropertyXInCluster(), context.getTotalNodesInCluster(),
	                context.getTotalNodes(), context.getTotalNodesWithPropertyX());
	        enrichmentResults.add(result);
	    }
	    
	    private void applyBonferroniCorrection() {
	        int totalTestsEnriched = allPValuesEnriched.size();
	        int totalTestsDepleted = allPValuesDepleted.size();


	        for (int i = 0; i < allPValuesEnriched.size(); i++) {
	            double pValueEnriched = allPValuesEnriched.get(i);
	            double bonferroniAdjustedCutoffEnriched =Math.min(1, pValueEnriched * totalTestsEnriched);
	            if (bonferroniAdjustedCutoffEnriched <= Cutoff) {
	                addEnrichmentResult(EnrichedContexts.get(i), pValueEnriched, bonferroniAdjustedCutoffEnriched);
	            }
	        }

	        for (int i = 0; i < allPValuesDepleted.size(); i++) {
	            double pValueDepleted = allPValuesDepleted.get(i);
	            double bonferroniAdjustedCutoffDepleted=Math.min(1, pValueDepleted  *totalTestsDepleted);
	            if (bonferroniAdjustedCutoffDepleted <= Cutoff) {
	                addEnrichmentResult(DepletedContexts.get(i), pValueDepleted, bonferroniAdjustedCutoffDepleted);
	            }
	        }
	    }
	    
	    
	    private void applyBenjaminiHochbergCorrection() {
	    	
	        int totalTestsEnriched = allPValuesEnriched.size();
	        int totalTestsDepleted = allPValuesDepleted.size();

	        List<Double> sortedPValuesEnriched = new ArrayList<>(allPValuesEnriched);
	        List<Double> sortedPValuesDepleted = new ArrayList<>(allPValuesDepleted);

	        // Sort p-values in ascending order
	        sortedPValuesEnriched.sort(Double::compareTo);
	        sortedPValuesDepleted.sort(Double::compareTo);

	        double[] adjustedPValuesEnriched = new double[totalTestsEnriched];
	        double[] adjustedPValuesDepleted = new double[totalTestsDepleted];

	     
	        for (int i = 0; i < totalTestsEnriched; i++) {
	            //double originalPValue = sortedPValuesEnriched.get(i);
	            double adjustedPValue = (Cutoff*(i+1))/ (double) totalTestsEnriched ;
	            adjustedPValuesEnriched[i] = adjustedPValue;
	        }
	        
	        // Calculate Benjamini-Hochberg adjusted p-values for depleted results
	        for (int i = 0; i < totalTestsDepleted; i++) {
	           //double originalPValue = sortedPValuesDepleted.get(i);
	            double adjustedPValue = (Cutoff*(i+1))/ (double) totalTestsDepleted ;
	            adjustedPValuesDepleted[i] = adjustedPValue;
	        }

	                
	        // Find the largest p-value that is smaller than the critical value
	        double largestPValueEnriched = 0.0;
	        double largestPValueDepleted = 0.0;

	        for (int i = totalTestsEnriched - 1; i >= 0; i--) {
	            double pValueEnriched = sortedPValuesEnriched.get(i);
	            double adjustedPValue = adjustedPValuesEnriched[i];

	            if (pValueEnriched <= adjustedPValue) {
	                largestPValueEnriched = pValueEnriched;
	                break;
	            }
	        }

	        for (int i = totalTestsDepleted - 1; i >= 0; i--) {
	            double pValueDepleted = sortedPValuesDepleted.get(i);
	            double adjustedPValue = adjustedPValuesDepleted[i];

	            if (pValueDepleted <= adjustedPValue) {
	                largestPValueDepleted = pValueDepleted;
	                break;
	            }
	        }

	        // Mark all original p-values smaller than or equal to the largest significant p-value as significant
	        for (int i = 0; i < allPValuesEnriched.size(); i++) {
	            double pValueEnriched = allPValuesEnriched.get(i);
	            if (pValueEnriched <= largestPValueEnriched) {
	                double adjustedPValue = adjustedPValuesEnriched[i];
	                addEnrichmentResult(EnrichedContexts.get(i), pValueEnriched, adjustedPValue);
	            }
	        }

	        for (int i = 0; i < allPValuesDepleted.size(); i++) {
	            double pValueDepleted = allPValuesDepleted.get(i);
	            if (pValueDepleted <= largestPValueDepleted) {
	                double adjustedPValue = adjustedPValuesDepleted[i];
	                addEnrichmentResult(DepletedContexts.get(i), pValueDepleted, adjustedPValue);
	            }
	        }
	    }
	    
	    private int getTotalNodesWithPropertyX(CyNetwork currentNetwork, String attributeName) {
	        int totalNodesWithPropertyX = 0;
	        for (CyNode node : currentNetwork.getNodeList()) {
	            Boolean hasPropertyX = currentNetwork.getRow(node).get(attributeName, Boolean.class);
	            if (Boolean.TRUE.equals(hasPropertyX)) {
	                totalNodesWithPropertyX++;
	            }
	        }
	        return totalNodesWithPropertyX;
	    }
	    
	    public List<EnrichmentResult> getEnrichmentResults() {
	        return enrichmentResults;
	    }
	
	   	    
//	    private int getTotalClusterCount(CyNetwork currentNetwork) {
//	        Set<Integer> clusters = new HashSet<>();
//	        for (CyNode node : currentNetwork.getNodeList()) {
//	            Integer mantaCluster = currentNetwork.getRow(node).get("mantaCluster", Integer.class, -1);
//	            if (mantaCluster != -1) {
//	                clusters.add(mantaCluster);
//	            }
//	        }
//	        return clusters.size();
//	    }
	    private int getTotalClusterCount(CyNetwork currentNetwork) {
	        Set<Integer> clusters = new HashSet<>();
	        CyTable nodeTable = currentNetwork.getDefaultNodeTable();
            //CyColumn mantaClusterColumn = nodeTable.getColumn("manta::cluster");
            CyColumn microbetagClusterColumn = nodeTable.getColumn("microbetag::cluster");

//            if (mantaClusterColumn != null) {
//                for (CyNode node : currentNetwork.getNodeList()) {
//                    Integer mantaCluster = currentNetwork.getRow(node).get("manta::cluster", Integer.class);
//                    if (mantaCluster == null) {
//                        // If mantaCluster is null, NaN, or empty, consider it as cluster -1
//                        mantaCluster = -1;
//                    }
//                    clusters.add(mantaCluster);
//                }}
             if (microbetagClusterColumn != null) {
                for (CyNode node : currentNetwork.getNodeList()) {
                    Integer microbetagCluster = currentNetwork.getRow(node).get("microbetag::cluster", Integer.class);
                    if (microbetagCluster == null) {
                        // If microbetagCluster is null, NaN, or empty, consider it as cluster -1
                        microbetagCluster = -1;
                    }
                    clusters.add(microbetagCluster);
                }
            }

            return clusters.size();
        }
	    
	      
	    private void showResultsInPanel() {
	        SwingUtilities.invokeLater(() -> {
	            // Create the JFrame to display the results
	            JFrame frame = new JFrame("Enrichment Analysis Results");
	            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	            // Initialize the panel with the manager and update it with the results
	            EnrichmentResultPanel resultPanel = new EnrichmentResultPanel(mggManager);
	            resultPanel.updateResults(enrichmentResults);

	            // Set the content of the JFrame to your result panel
	            frame.setContentPane(resultPanel);

	            // Configure the JFrame size, location, and visibility
	            frame.pack(); // Adjusts frame size to fit its content
	            frame.setLocationRelativeTo(null); // Centers the frame on the screen
	            frame.setVisible(true); // Makes the frame visible
	        });
	    }
	}
	    

	    
	    
    
