package be.kuleuven.mgG.internal.utils;

import java.util.List;

public class AnalysisContext {

	

	    public final String type;
	    public final Integer mantaCluster;
	    public final String term;
	    public final List<Long> suids;
	    public final List<String> nodeNames;
	    public final int nodesWithPropertyXInCluster;
	    public final int totalNodesInCluster;
	    public final int totalNodes;
	    public final int totalNodesWithPropertyX;
	    public final double pvalue;

	    public AnalysisContext(String type, Integer mantaCluster, String term, List<Long> suids, List<String> nodeNames,double pvalue,
	                           int nodesWithPropertyXInCluster, int totalNodesInCluster, int totalNodes, int totalNodesWithPropertyX) {
	        this.type = type;
	        this.mantaCluster = mantaCluster;
	        this.term = term;
	        this.suids = suids;
	        this.nodeNames = nodeNames;
	        this.nodesWithPropertyXInCluster=nodesWithPropertyXInCluster;
	        this.totalNodesInCluster=totalNodesInCluster;
	        this.totalNodes=totalNodes;
	        this.totalNodesWithPropertyX=totalNodesWithPropertyX;
	        this.pvalue=pvalue;     
	
	        
	             
}
	    // Getters
	    public String getType() {return type;}
	    public List<String> getNodeNames() { return nodeNames; }
	    public String getTerm() { return term; }
	    public int getMantaCluster() { return mantaCluster; }
	    public List<Long> getSuids() { return suids; }
	    //public double getPValue() { return pValue; }
	    //public double getFdrValue() { return fdrValue; }
	    public int getNodesWithPropertyXInCluster() { return nodesWithPropertyXInCluster; }
	    public int getTotalNodesInCluster() {return totalNodesInCluster;}
	    public int getTotalNodes() {return totalNodes;}
	    public int getTotalNodesWithPropertyX() {return totalNodesWithPropertyX;} 
	    public double getPvalue() {return pvalue;}
	    
	    
	    
	    
}