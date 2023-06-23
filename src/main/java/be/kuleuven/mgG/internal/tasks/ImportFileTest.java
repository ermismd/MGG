package be.kuleuven.mgG.internal.tasks;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cytoscape.work.TaskMonitor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import be.kuleuven.mgG.internal.utils.CSVReader;

public class ImportFileTest {

	
	  @Test
	    public void testReadCSV() {
	        // Define mock CSV data
	        List<String[]> csvData1 = new ArrayList<>();
	        csvData1.add(new String[] {"Header1", "Header2", "Header3"});
	        csvData1.add(new String[] {"Data1", "Data2", "Data3"});
	        csvData1.add(new String[] {"Data4", "Data5", "Data6"});
	        csvData1.add(new String[] {"Data7"});

	        // Find the headers
	        String[] headers = null;
	        for (int i = 0; i < csvData1.size(); i++) {
	            String[] row = csvData1.get(i);
	            if (row.length > 1) {
	                headers = row;
	                csvData1.remove(i);  // remove the header row
	                break;
	            }
	        }

	        String filePath = "C:\\Users\\herme\\git\\MGG\\src\\main\\java\\be\\kuleuven\\mgG\\internal\\tasks\\qiime2_use_case.tsv";

	        // Mock TaskMonitor (you might want to use a real one or a mock framework like Mockito)
	        TaskMonitor taskMonitor = null;

	        try {
	            // Call the method
	            List<String[]> csvData2 = CSVReader.readCSV(taskMonitor, filePath);
	            if (csvData2.size() > 1) {
	                String[] secondLine = csvData2.get(1);
	                System.out.println(Arrays.toString(secondLine));
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	       
	    }}