package be.kuleuven.mgG.internal.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class EnrichmentsTableModel extends AbstractTableModel {
	
    private final String[] columnNames = {"Enrichement/Depletion","Cluster","Enriched Term","P-value", "FDR Value", "Node Names", "Nodes with Term in Cluster", 
    		"Total Nodes in Cluster","Total Nodes with Term "};
    
    private List<EnrichmentResult> results;

    public EnrichmentsTableModel(List<EnrichmentResult> results) {
        this.results = results;
    }

    @Override
    public int getRowCount() {
        return results.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        EnrichmentResult result = results.get(rowIndex);
        switch (columnIndex) {
        	case 0:return result.getEnr_Dep();
            case 1: return result.getMantaCluster();
            case 2: return result.getTerm();
            case 3: return result.getPValue();
            case 4: return result.getFdrValue();
            case 5: return String.join(", ", result.getNodeName()); // Display node names as a string
            case 6: return result.getNumberOfNodesWithTermInCluster();
            case 7: return result.getTotalNodesInCluster(); 
            case 8: return result.getTotalNodeswithTerm();
            default: return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public void setResults(List<EnrichmentResult> results) {
        this.results = results;
        sortResultsByPValue();
        fireTableDataChanged(); 
    }

    private void sortResultsByPValue() {
        Collections.sort(results, new Comparator<EnrichmentResult>() {
            @Override
            public int compare(EnrichmentResult o1, EnrichmentResult o2) {
                int termComparison = o1.getTerm().compareTo(o2.getTerm());
                if (termComparison != 0) {
                    return termComparison; // sort by term
                } else {
                    return Double.compare(o1.getPValue(), o2.getPValue()); // sort by P-value
                }
            }
        });
    }
}
