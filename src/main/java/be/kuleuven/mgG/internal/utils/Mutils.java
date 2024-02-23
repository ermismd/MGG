package be.kuleuven.mgG.internal.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.customgraphics.CyCustomGraphics;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.view.MGGNodePanel;




public class Mutils {

    // Namespaces
    public static String MY_NAMESPACE = "MGGid";
    public static String MY_ATTRIBUTE = "id";
    public static String PhenDbSc_NAMESPACE = "phendbScore";
    public static String PhenDb_NAMESPACE = "phendb";
    public static String Weight_NAMESPACE = "microbetag";
    public static String Seed_NAMESPACE = "seed";
    public static String Faprotax_NAMESPACE = "faprotax";


    public static boolean isMGGNetwork(CyNetwork network) {
        if (network == null) return false; //this is new
        CyTable nodeTable = network.getDefaultNodeTable();
//        if (nodeTable.getColumn("@id") == null) {
//            return false; //  if @id column is missing in node table
//        }
        CyTable edgeTable = network.getDefaultEdgeTable();
      
        
        for (CyColumn column : nodeTable.getColumns()) {
            String namespace = column.getNamespace();
            if (namespace != null && namespace.equals("microbetag")) {
                return true;
            }}


      

        
        if (edgeTable.getColumn("microbetag::weight") == null) {
            return false; 
        }

        return true; 
    }
    
    public static boolean isMGGNetworkMicrobetagDB(CyNetwork network) {
		
    	// This is a MGG network only if we have microbetag network in the name column in the network table,
    	
		//if (network == null || network.getRow(network).get("database", String.class) == null)
		//	return false;
		//return isMGGNetwork(network);

    	
    	if (network==null) {
    		return false;
    	}
        
        String nameValue = network.getRow(network).get("name", String.class);

        // Does the "name" contains "microbetag network" or any other variation
        if ( nameValue != null && (nameValue.contains("microbetag network") || nameValue.matches(".*microbetag network\\(\\d+\\).*"))) {
            //  the "name" contains "microbetag network" or similar variations 
                return true;
            }
        
        return isMGGNetwork(network);
        
    }
    	
    	
    	
	
    
    

    // This method will tell us if we have the new side panel functionality (i.e. namespaces)
    public static boolean ifHaveMGG(CyNetwork network) {
        if (network == null) return false;
        CyRow netRow = network.getRow(network);
        Collection < CyColumn > columns = network.getDefaultNodeTable().getColumns("@id");
        if (columns != null && columns.size() > 0)
            return true;
        return false;          
    }

    
    public static void hideSingletons(CyNetworkView view, boolean show) {
		CyNetwork net = view.getModel();
		for (View<CyNode> nv: view.getNodeViews()) {
			CyNode node = nv.getModel();
			List<CyEdge> edges = net.getAdjacentEdgeList(node, CyEdge.Type.ANY);
			if (edges != null && edges.size() > 0) continue;
			if (!show)
				nv.setLockedValue(BasicVisualLexicon.NODE_VISIBLE,false);
			else
				nv.clearValueLock(BasicVisualLexicon.NODE_VISIBLE);
		}
	}
    
    public static void doShowMspecies(CyNetworkView view, boolean show, boolean showSingletons,boolean phendbselected) {
    	
   	   	
    	
    	if (phendbselected) {
            return;
        }
    
        //CyNetworkView view = manager.getCurrentNetworkView();
        CyNetwork net = view.getModel();

        String columnName = "microbetag::ncbi-tax-level";
        //String targetValue = "mspecies";

        // Check if the column exists
       if (net.getDefaultNodeTable().getColumn(columnName) == null) {
           return;
        }
       
       

       // for (CyNode node : net.getNodeList()) {
          //  View<CyNode> nodeView = view.getNodeView(node);
        for (View<CyNode> nodeView:view.getNodeViews()) {
            if (nodeView == null) continue;
            CyNode node=nodeView.getModel();
            if (show) {
                CyRow nodeRow = net.getRow(node);
                String attributeValue = nodeRow.get(columnName, String.class);
               boolean isVisible = "mspecies".equals(attributeValue);
               
                nodeView.setLockedValue(BasicVisualLexicon.NODE_VISIBLE, isVisible);}
//            else
//            	nodeView.clearValueLock(BasicVisualLexicon.NODE_VISIBLE);
//            } //else {
//                // If 'show' is false, set all nodes to visible
//               //nodeView.clearValueLock(BasicVisualLexicon.NODE_VISIBLE);
//            }   
            else {
                // When show is false
                if (showSingletons==false) {
                    // Hide singletons
                    List<CyEdge> edges = net.getAdjacentEdgeList(node, CyEdge.Type.ANY);
                    boolean isSingleton = edges == null || edges.isEmpty();
                    nodeView.setLockedValue(BasicVisualLexicon.NODE_VISIBLE, !isSingleton);
                } else {
                    // If hide singletons is not enabled, clear visibility lock
                    nodeView.clearValueLock(BasicVisualLexicon.NODE_VISIBLE);
                }
            }
        }
    }
          
    	
    
    

    public static void clearHighlight(MGGManager manager, CyNetworkView view) {
        // if (node == null) return;
        // View<CyNode> nodeView = view.getNodeView(node);
        if (view == null) return;

        VisualLexicon lex = manager.getService(RenderingEngineManager.class).getDefaultVisualLexicon();
        VisualProperty customGraphics1 = lex.lookup(CyNode.class, "NODE_CUSTOMGRAPHICS_1");
        VisualProperty customGraphics2 = lex.lookup(CyNode.class, "NODE_CUSTOMGRAPHICS_2");
        VisualProperty customGraphics3 = lex.lookup(CyNode.class, "NODE_CUSTOMGRAPHICS_3");

        for (View < CyNode > nv: view.getNodeViews()) {
            nv.clearValueLock(customGraphics1);
            nv.clearValueLock(customGraphics2);
            nv.clearValueLock(customGraphics3);
            nv.clearValueLock(BasicVisualLexicon.NODE_TRANSPARENCY);
        }

        for (View < CyEdge > ev: view.getEdgeViews()) {
            ev.clearValueLock(BasicVisualLexicon.EDGE_TRANSPARENCY);
        }
    }


    public static void highlight(MGGManager manager, CyNetworkView view, List < CyNode > nodes) {
        CyNetwork net = view.getModel();

        List < CyEdge > edgeList = new ArrayList < CyEdge > ();
        List < CyNode > nodeList = new ArrayList < CyNode > ();
        for (CyNode node: nodes) {
            edgeList.addAll(net.getAdjacentEdgeList(node, CyEdge.Type.ANY));
            nodeList.addAll(net.getNeighborList(node, CyEdge.Type.ANY));
        }


        VisualLexicon lex = manager.getService(RenderingEngineManager.class).getDefaultVisualLexicon();
        VisualProperty customGraphics1 = lex.lookup(CyNode.class, "NODE_CUSTOMGRAPHICS_1");
        VisualProperty customGraphics2 = lex.lookup(CyNode.class, "NODE_CUSTOMGRAPHICS_2");
        VisualProperty customGraphics3 = lex.lookup(CyNode.class, "NODE_CUSTOMGRAPHICS_3");

        CyCustomGraphics cg = new EmptyCustomGraphics();

        // Override our current style through overrides
        for (View < CyNode > nv: view.getNodeViews()) {
            if (nodeList.contains(nv.getModel()) || nodes.contains(nv.getModel())) {
                nv.setLockedValue(BasicVisualLexicon.NODE_TRANSPARENCY, 255);
            } else {
                nv.setLockedValue(customGraphics1, cg);
                nv.setLockedValue(customGraphics2, cg);
                nv.setLockedValue(customGraphics3, cg);
                nv.setLockedValue(BasicVisualLexicon.NODE_TRANSPARENCY, 20);
            }
        }
        for (View < CyEdge > ev: view.getEdgeViews()) {
            if (edgeList.contains(ev.getModel())) {
                ev.setLockedValue(BasicVisualLexicon.EDGE_TRANSPARENCY, 255);
            } else {
                ev.setLockedValue(BasicVisualLexicon.EDGE_TRANSPARENCY, 20);
            }
        }
    }


    
    public static CyNetworkView getNetworkView(MGGManager manager, CyNetwork network) {
        Collection < CyNetworkView > views =
            manager.getService(CyNetworkViewManager.class).getNetworkViews(network);

        // At some point, figure out a better way to do this
        for (CyNetworkView view: views) {
            return view;
        }
        return null;
    }

    
    
    
    public static List < String > getPhenDbScList(CyNetwork network) {
        List < String > phendbScores = new ArrayList < > ();
        if (network == null) {
            // System.out.println("network is null");
            return phendbScores;
        }
        Collection < CyColumn > columns = network.getDefaultNodeTable().getColumns(PhenDbSc_NAMESPACE);
        if (columns == null || columns.isEmpty()) {
            return phendbScores;
        }
        
        for (CyColumn col : columns) {
        String columnName = col.getName();
        // Check if the column name ends with 'Score'
        if (columnName.endsWith("Score")) {
            // Remove the PhenDbSc_NAMESPACE prefix and add to the list
            String adjustedName = columnName.replace(PhenDbSc_NAMESPACE + "::", "");
            phendbScores.add(adjustedName);
        }
    }

    return phendbScores;
    }


    
    
    public static List < String > getPhendbAttributes(CyNetwork network) {
        List < String > phendbAttributes = new ArrayList < > ();
        if (network == null) {
            return phendbAttributes;
        }


        Collection < CyColumn > columns = network.getDefaultNodeTable().getColumns(PhenDb_NAMESPACE);
        if (columns == null || columns.size() == 0) return phendbAttributes;
        for (CyColumn col: columns) {
            phendbAttributes.add(col.getNameOnly());
        }
        
        return phendbAttributes;
    }
    
    
    
    
    public static List < String > getFaprotaxAttributes(CyNetwork network) {
        List < String > faprotaxAttributes = new ArrayList < > ();
        if (network == null) {
            return faprotaxAttributes;
        }


        Collection < CyColumn > columns = network.getDefaultNodeTable().getColumns(Faprotax_NAMESPACE);
        if (columns == null || columns.size() == 0) return faprotaxAttributes;
        for (CyColumn col: columns) {
        	faprotaxAttributes.add(col.getNameOnly());
        }
        
        return faprotaxAttributes;
    }

    
    
    
    public static List < String > getWeightList(CyNetwork network) {
        List < String > weight = new ArrayList < > ();
        if (network == null) {
            // System.out.println("network is null");
            return weight;
        }
        Collection < CyColumn > columns = network.getDefaultEdgeTable().getColumns("microbetag");
        if (columns == null || columns.size() == 0) return weight;
        for (CyColumn col: columns) {
            weight.add(col.getNameOnly());
        }
        return weight;
    }

    
    
    
	public static List<String> getSeedList(CyNetwork network) {
		List<String> seed = new ArrayList<>();
		if (network == null) return seed;
		Collection<CyColumn> columns = network.getDefaultEdgeTable().getColumns(Seed_NAMESPACE);
		if (columns == null || columns.size() == 0) return seed;
		for (CyColumn col: columns) {
			if (col.getNameOnly().equals("competition-std") ||col.getNameOnly().equals("cooperation-std")|| !col.getType().equals(Double.class))
				continue;
			seed.add(col.getNameOnly());
		}
		return seed;
	}

	
	
	
    public static List < String > getphenDbList(CyNetwork network) {
        List < String > phenDb = new ArrayList < > ();
        if (network == null) return phenDb;
        Collection < CyColumn > columns = network.getDefaultEdgeTable().getColumns(PhenDb_NAMESPACE);
        if (columns == null || columns.size() == 0) return phenDb;
        for (CyColumn col: columns) {
            if (!col.getType().equals(Boolean.class))
                continue;
            phenDb.add(col.getNameOnly());
        }
        return phenDb;
    }


    

    
    
    
 
    
    
    // Method to determine the category for an attribute
    public static String getCategoryForAttribute(String attribute) {
    	
    	 // Map of attribute to its category
        Map<String, String> attributeCategoryMap = new HashMap<>();

        // Add mappings for each attribute to its category
    

        // Lifestyle
        String[] lifestyleAttributes = {
            "aerobe","anaerobe","aSaccharolytic", "autoCo2", "fermentative", "Aerobe","Anaerobe", "halophilic","methanotroph","methanotrophy","nonFermentative",
            "phototrophy","psychrophilic","saccharolytic","symbiont","thermophilic","methylotrophy","chitinolysis","knallgas bacteria",
            "cellulolysis","xylanolysis","plant pathogen","ligninolysis","fermentation","aerobic chemoheterotrophy","invertebrate parasites",
            "human pathogens septicemia","intracellular parasites","predatory or exoparasitic","human pathogens pneumonia","human pathogens nosocomia",
            "human pathogens meningitis","human pathogens gastroenteritis","human pathogens diarrhea","nonphotosynthetic cyanobacteria","human pathogens all",
            "photosynthetic cyanobacteria","fish parasites","aerobic anoxygenic phototrophy","anoxygenic photoautotrophy H2 oxidizing",
            "anoxygenic photoautotrophy S oxidizing","anoxygenic photoautotrophy Fe oxidizing","anoxygenic photoautotrophy",
            "human gut","human associated","mammal gut","chemoheterotrophy","animal parasites or symbionts","oxygenic photoautotrophy",
            "photoautotrophy","phototrophy","photoheterotrophy","dGlucose"
        };
        
        
        
        for (String attr : lifestyleAttributes) {
            attributeCategoryMap.put(attr, "Lifestyle");
        }

        // Subgroups of Lifestyle
        String[] energySourceAttributes = {
            "phototrophy", "aerobic chemoheterotrophy", "nonphotosynthetic cyanobacteria",       
            "photosynthetic cyanobacteria","aerobic anoxygenic phototrophy","anoxygenic photoautotrophy H2 oxidizing",
            "anoxygenic photoautotrophy S oxidizing","anoxygenic photoautotrophy Fe oxidizing","anoxygenic photoautotrophy",
            "chemoheterotrophy","oxygenic photoautotrophy","photoautotrophy","phototrophy","photoheterotrophy","dGlucose" };
        
        for (String attr : energySourceAttributes) {
            attributeCategoryMap.put(attr, "Lifestyle: Energy Source");
        }
        
        String[] carbonSourceAttributes = {"autoCo2","aSaccharolytic","fermentative","methanotroph","methanotrophy","nonFermentative",
        		"phototrophy","saccharolytic","methylotrophy","chitinolysis","cellulolysis","xylanolysis","ligninolysis","fermentation",
        		"aerobic chemoheterotrophy","nonphotosynthetic cyanobacteria","photosynthetic cyanobacteria","aerobic anoxygenic phototrophy",
        		"anoxygenic photoautotrophy H2 oxidizing","anoxygenic photoautotrophy S oxidizing","anoxygenic photoautotrophy Fe oxidizing",
        		"anoxygenic photoautotrophy","chemoheterotrophy","oxygenic photoautotrophy","photoautotrophy","photoheterotrophy",
        		"dGlucose"};
        
        for (String attr : carbonSourceAttributes) {
            attributeCategoryMap.put(attr, "Lifestyle: Carbon Source");
        }
        
        
        String[] host_associatedSourceAttributes= {"symbiont","plant pathogen","invertebrate parasites","human pathogens septicemia",
        		"intracellular parasites","predatory or exoparasitic","human pathogens pneumonia","human pathogens nosocomia",
        		"human pathogens meningitis","human pathogens gastroenteritis","human pathogens diarrhea","human pathogens all",
        		"fish parasites","human gut","human associated","mammal gut","animal parasites or symbionts" };


        for (String attr : host_associatedSourceAttributes) {
            attributeCategoryMap.put(attr, "Lifestyle: Host - Associated");
        }		
        
        
        String[] biogeochemical_processesAttributes = { "NOB","nitrogen fixation","aob","dark sulfite oxidation","fixingN2",
        		"nitrate ammonification","sulfateReducer","sulfite respiration","arsenate detoxification","nitrite ammonification",
        		"acetoclastic methanogenesis","thiosulfate respiration","arsenate respiration","nitrite respiration",
        		"methanogenesis by disproportionation of methyl groups","respiration of sulfur compounds","dissimilatory arsenate reduction",
        		"dark sulfide oxidation","methanogenesis using formate","oil bioremediation","arsenite oxidation detoxification",
        		"dark sulfur oxidation","methanogenesis by CO2 reduction with H2","aromatic hydrocarbon degradation","arsenite oxidation energy yielding",
        		"dark thiosulfate oxidation","methanogenesis by reduction of methyl compounds with H2","aromatic compound degradation",
        		"dissimilatory arsenite oxidation","dark oxidation of sulfur compounds","hydrogenotrophic methanogenesis",
        		"aliphatic non methane hydrocarbon degradation","anammox","manganese oxidation","methanogenesis","hydrocarbon degradation",
        		"nitrate denitrification","fumarate respiration","methanol oxidation","manganese respiration","nitrite denitrification",
        		"dark iron oxidation","nitrous oxide denitrification","nitrate respiration","aerobic ammonia oxidation",
        		"nitrate reduction","denitrification","nitrogen respiration","aerobic nitrite oxidation","chlorate reducers",
        		"sulfate respiration","nitrification","dark hydrogen oxidation","iron respiration","sulfur respiration","plastic degradation",
        		"reductive acetogenesis","ureolysis"     };

        
        for (String attr : biogeochemical_processesAttributes) {
            attributeCategoryMap.put(attr, "Biogeochemical processes");
        }		
        
        
        String[] carbonCycleAttributes= {"acetoclastic methanogenesis","methanogenesis by disproportionation of methyl groups",
        		"methanogenesis using formate","methanogenesis by CO2 reduction with H2",
        		"methanogenesis by reduction of methyl compounds with H2","hydrogenotrophic methanogenesis",
        		"methanogenesis","methanol oxidation","oil bioremediation","aromatic hydrocarbon degradation",
        		"aromatic compound degradation","aliphatic non methane hydrocarbon degradation","hydrocarbon degradation",
        		"fumarate respiration","plastic degradation","reductive acetogenesis"  };

        
        		
        for (String attr : carbonCycleAttributes) {
            attributeCategoryMap.put(attr, "Biogeochemical processes: Carbon Cycle");
        }	
        
        
        String[] nitrogenCycleattributes = {"NOB","aob","fixingN2","anammox","nitrate denitrification","nitrite denitrification",
        		"nitrous oxide denitrification","aerobic ammonia oxidation","denitrification","aerobic nitrite oxidation",
        		"nitrogen fixation","nitrate ammonification","nitrite ammonification","nitrite respiration","nitrate respiration",
        		"nitrate reduction","nitrogen respiration","nitrification","ureolysis" };

        		
        for (String attr : nitrogenCycleattributes) {
            attributeCategoryMap.put(attr, "Biogeochemical processes: Nitrogen Cycle");
        }	
        
        String[] sulfurCycleAttributes  = {"sulfateReducer","sulfate respiration","sulfur respiration","dark sulfite oxidation",
        		"sulfite respiration","thiosulfate respiration","respiration of sulfur compounds","dark sulfide oxidation",
        		"dark sulfur oxidation","dark thiosulfate oxidation","dark oxidation of sulfur compounds" };

        
        for (String attr : sulfurCycleAttributes) {
            attributeCategoryMap.put(attr, "Biogeochemical processes: Sulfur Cycle");
        }	
        
        
        String[] arsenicCycleAttributes= {"arsenate detoxification","arsenate respiration","dissimilatory arsenate reduction",
        		"arsenite oxidation detoxification","arsenite oxidation energy yielding","dissimilatory arsenite oxidation"
        };
       
   
        for (String attr : arsenicCycleAttributes) {
            attributeCategoryMap.put(attr, "Biogeochemical processes: Arsenic Cycle");
        }	
            
        	
        
        String[] metaboliteProducedAttributes= {"aceticAcid","butanol","butyricAcid","dLacticAcid","ethanol",
        		"hydrogen","indole","isobutyricAcid","isovalericAcid","lLacticAcid","formicAcid","rAcetoin","succinicAcid"};

        
        for (String attr : metaboliteProducedAttributes) {
            attributeCategoryMap.put(attr, "Metabolites Produced");
        }	

        
        String[] miscAttributes= {"chloroplasts","T3SS","T6SS"};

        for (String attr : miscAttributes) {
            attributeCategoryMap.put(attr, "Misc");
        }	
    		 
     

        // Return the category for the given attribute
        return attributeCategoryMap.getOrDefault(attribute, "Unknown");
    }
  
    // to get all categories
    public static List<String> getCategories() {
    	return Arrays.asList(
    	        "Lifestyle",
    	        "Lifestyle: Energy Source",
    	        "Lifestyle: Carbon Source",
    	        "Lifestyle: Host - Associated",
    	        "Biogeochemical processes",
    	        "Biogeochemical processes: Carbon Cycle",
    	        "Biogeochemical processes: Nitrogen Cycle",
    	        "Biogeochemical processes: Sulfur Cycle",
    	        "Biogeochemical processes: Arsenic Cycle",
    	        "Metabolites Produced",
    	        "Misc"
    	        
    	        
    	    );
    }

}