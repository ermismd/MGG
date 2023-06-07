package uni.kul.rega.mgG.internal.sources.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import uni.kul.rega.mgG.internal.api.Category;
import uni.kul.rega.mgG.internal.api.Experiment;
import uni.kul.rega.mgG.internal.api.Matrix;
import uni.kul.rega.mgG.internal.api.Metadata;
import uni.kul.rega.mgG.internal.api.Source;
import uni.kul.rega.mgG.internal.model.ScNVManager;
import uni.kul.rega.mgG.internal.utils.CSVReader;
import uni.kul.rega.mgG.internal.utils.CSVWriter;
import uni.kul.rega.mgG.internal.utils.FileUtils;
import uni.kul.rega.mgG.internal.utils.HTTPUtils;

import org.cytoscape.application.CyUserLog;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskMonitor;

public class FileExperimentOne implements Experiment {
    final Logger logger;

    String accession = null;
    List<String[]> rowTable = null;
    List<String[]> colTable = null;
    // MatrixMarket mtx = null; // Remove the matrix instance
    ScNVManager scNVManager;
    FileExperimentOne fileExperiment;
    FileSource source;
    FileMetadata fileMetadata;
    FileExperimentTableModel tableModel = null;

    int rowIndexKey = 1;
    int columnIndexKey = 1;

    public static String SERVICES_URI = "https://webservices.rbvi.ucsf.edu/scnetviz/api/v2/save/File/%s";

    public FileExperimentOne(ScNVManager manager, FileSource source, FileMetadata metadata) {
        this.scNVManager = manager;
        logger = Logger.getLogger(CyUserLog.NAME);
        this.fileExperiment = this;
        this.source = source;
        this.fileMetadata = metadata;
        this.accession = metadata.get(Metadata.ACCESSION).toString();
    }

    public String getAccession() {
        return accession;
    }

    public List<String[]> getColumnLabels() {
        return colTable;
    }

    public List<String[]> getRowLabels() {
        return rowTable;
    }

    public FileExperimentTableModel getTableModel() {
        if (tableModel == null)
            tableModel = new FileExperimentTableModel(scNVManager, this);
        return tableModel;
    }

    public Metadata getMetadata() {
        return fileMetadata;
    }

    public Source getSource() {
        return source;
    }

    public String getSpecies() {
        return (String) fileMetadata.get(Metadata.SPECIES);
    }

    public void readCSV(final TaskMonitor monitor, boolean skipFirst) {
        // Initialize
        rowTable = null;
        colTable = null;

        // Get the CSV file
        File csvFile = (File) fileMetadata.get(FileMetadata.FILE);

        try {
            InputStream stream = new FileInputStream(csvFile);
            rowTable = CSVReader.readCSV(monitor, stream, csvFile.getName());
            if (skipFirst && rowTable.size() > 1) {
                rowTable.remove(0);
            }
        } catch (FileNotFoundException e) {
            monitor.showMessage(TaskMonitor.Level.ERROR, "No such file: '" + csvFile.getName() + "'");
            return;
        } catch (IOException e) {
            monitor.showMessage(TaskMonitor.Level.ERROR,
                    "Error reading file: '" + csvFile.getName() + "': " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Process column labels if available
        if (rowTable != null && rowTable.size() > 0) {
            int numColumns = rowTable.get(0).length;
            colTable = new ArrayList<>();
            for (int i = 0; i < numColumns; i++) {
                String[] columnLabel = new String[] { "Column " + (i + 1) };
                colTable.add(columnLabel);
            }
        }

        // Create the matrix instance if required
        // mtx = new MatrixMarket(scNVManager, null, null);
        // if (rowTable != null)
        //     mtx.setRowTable(rowTable, rowIndexKey);
        // if (colTable != null)
        //     mtx.setColumnTable(colTable, columnIndexKey);

        // scNVManager.addExperiment(accession, this);

        // Cache the file on the server
        new Thread(new CacheExperimentThread(accession)).start();

        System.out.println("rowTable has " + rowTable.size() + " rows");
    }

    public String toString() {
        return getAccession();
    }

    public String toHTML() {
        return fileMetadata.toHTML();
    }

    public String toJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"source\": \"" + getSource().toString() + "\",\n");
        builder.append("\"source name\": \"" + getSource().getName() + "\",\n");
        builder.append("\"metadata\": " + fileMetadata.toJSON() + ",\n");
        builder.append("\"rows\": " + (rowTable != null ? rowTable.size() : 0) + ",\n");
        builder.append("\"columns\": " + (colTable != null ? colTable.size() : 0) + "\n");
        builder.append("}");
        return builder.toString();
    }

    private int getColumnIndex(List<String[]> table, String name) {
        if (table.size() > 0 && table.get(0).length == 1 && name.contains("barcodes"))
            return 0;
        return 1;
    }

    private int getRowIndex(List<String[]> table, String name) {
        if (table.size() > 0 && name.contains("features"))
            return 0;
        return 1;
    }

    class CacheExperimentThread implements Runnable {
        final String accession;

        public CacheExperimentThread(String acc) {
            this.accession = acc;
        }

        @Override
        public void run() {
            String postString = String.format(SERVICES_URI, accession);
            try {
                File expFile = (File) fileMetadata.get(FileMetadata.FILE);
                HTTPUtils.postFile(postString, expFile, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}