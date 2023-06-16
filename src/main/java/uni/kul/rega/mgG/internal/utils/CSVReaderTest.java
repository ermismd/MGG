package uni.kul.rega.mgG.internal.utils;



import uni.kul.rega.mgG.internal.utils.CSVReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import junit.framework.Assert;

public class CSVReaderTest {
    @Test
    public void testCSVReader() {
        String filePath = "C:/Users/herme/Desktop/qiime2_use_case.tsv";

        try {
            List<String[]> csvData = CSVReader.readCSV(null, filePath);

            // Assert that the CSV data is not null and has at least one row
            assertNotNull(csvData);
            assertFalse(csvData.isEmpty());

            // Get the header row
            String[] headers = csvData.get(0);

            // Assert the number of columns in each row
            int expectedNumColumns = headers.length;
            for (String[] row : csvData) {
                assertEquals(expectedNumColumns, row.length);
            }

            // Assert the column positions
            for (int i = 0; i < headers.length; i++) {
                String expectedHeader = headers[i];
                assertEquals(expectedHeader, csvData.get(0)[i]);
            }

            // Add more specific assertions or testing logic as needed

        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Exception occurred while reading CSV file");
    }
}}




