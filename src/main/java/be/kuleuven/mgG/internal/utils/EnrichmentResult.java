	package be.kuleuven.mgG.internal.utils;
	
	import java.util.List;
	
	public class EnrichmentResult {
		  	
		  	private    String termName;
		  	private    double pValue;
		  	private    double fdrValue;
		 	private    int numberOfNodesInCluster;
			private    List<String> nodeNames;
			private    int totalNodesInCluster;
			private    int mantaCluster;
			private    List<Long> suid;
			private int totalNodes;
			private int totalNodeswithpropertyx;
			private String enr_Dep;
			
			
			public static final String colChartColor = "chart color";
			public static final String colEnrichmentTermsNames = "enrichmentTermsNames";
			public static final String colEnrichmentTermsIntegers = "enrichmentTermsIntegers";
			public static final String colEnrichmentPassthrough = "enrichmentPassthrough";
			
		 
	    // Constructor
	    public EnrichmentResult(String enr_Dep, int mantaCluster, String termName, List <Long> suid, double pValue, double fdrValue,
	    		List<String>nodeNames, int numberOfNodesInCluster, int totalNodesInCluster ,int totalNodes,int totalNodeswithpropertyx) {
	    	this.enr_Dep=enr_Dep;
	        this.nodeNames = nodeNames;
	        this.termName = termName;
	        this.mantaCluster = mantaCluster;
	        this.suid = suid;
	        this.pValue = pValue;
	        this.fdrValue = fdrValue;
	        this.numberOfNodesInCluster = numberOfNodesInCluster; 
	        this.totalNodesInCluster = totalNodesInCluster;
	        this.totalNodes=totalNodes;
	        this.totalNodeswithpropertyx=totalNodeswithpropertyx;
	    }
	    
	  
	
	    // Getters
	    public String getEnr_Dep() {return enr_Dep;}
	    public List<String> getNodeName() { return nodeNames; }
	    public String getTerm() { return termName; }
	    public int getMantaCluster() { return mantaCluster; }
	    public List<Long> getSuid() { return suid; }
	    public double getPValue() { return pValue; }
	    public double getFdrValue() { return fdrValue; }
	    public int getNumberOfNodesWithTermInCluster() { return numberOfNodesInCluster; }
	    public int getTotalNodesInCluster() {return totalNodesInCluster;}
	    public int getTotalNodes() {return totalNodes;}
	    public int getTotalNodeswithTerm() {return totalNodeswithpropertyx;}
	    
	}