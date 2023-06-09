//package uni.kul.rega.mgG.internal.sources.file;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.swing.table.TableModel;
//
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipInputStream;
//import java.util.zip.GZIPInputStream;
//
//import org.apache.log4j.Logger;
//
//import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
//import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
//
//import org.json.simple.JSONObject;
//
////import uni.kul.rega.mgG.internal.api.Category;
//import uni.kul.rega.mgG.internal.api.Experiment;
//import uni.kul.rega.mgG.internal.api.Matrix;
//import uni.kul.rega.mgG.internal.api.Metadata;
//import uni.kul.rega.mgG.internal.api.Source;
////import uni.kul.rega.mgG.internal.model.DifferentialExpression;
//import uni.kul.rega.mgG.internal.model.MatrixMarket;
//import uni.kul.rega.mgG.internal.model.ScNVManager;
//import uni.kul.rega.mgG.internal.utils.CSVReader;
//import uni.kul.rega.mgG.internal.utils.CSVWriter;
//import uni.kul.rega.mgG.internal.utils.FileUtils;
//import uni.kul.rega.mgG.internal.utils.HTTPUtils;
////import uni.kul.rega.mgG.internal.utils.ModelUtils;
//
//import org.cytoscape.application.CyUserLog;
//import org.cytoscape.service.util.CyServiceRegistrar;
//import org.cytoscape.work.TaskMonitor;
//
//public class FileExperiment implements Experiment {
//	final Logger logger;
//
//	String accession = null;
//	List<String[]> rowTable = null;
//	List<String[]> colTable = null;
//	MatrixMarket mtx = null;
//	//final List<Category> categories;
//	//double[][] tSNE;
//	//String plotType = null;
//	// GXACluster fileCluster = null;
//	// GXAIDF fileIDF = null;
//	// GXADesign fileDesign = null;
//
//	final ScNVManager scNVManager;
//	final FileExperiment fileExperiment;
//	final FileSource source;
//	final FileMetadata fileMetadata;
//	//DifferentialExpression diffExp = null;
//	FileExperimentTableModel tableModel = null;
//
//	int rowIndexKey = 1;
//	int columnIndexKey = 1;
//
//	public static String SERVICES_URI = "https://webservices.rbvi.ucsf.edu/scnetviz/api/v2/save/File/%s";
//
//	public FileExperiment (ScNVManager manager, FileSource source, FileMetadata metadata) {
//		this.scNVManager = manager;
//		logger = Logger.getLogger(CyUserLog.NAME);
//		this.fileExperiment = this;
//		this.source = source;
//		this.fileMetadata = metadata;
//		this.accession = metadata.get(Metadata.ACCESSION).toString();
//		//categories = new ArrayList<Category>();
//	}
//
//	public Matrix getMatrix() { return mtx; }
//	public String getAccession() { return accession; }
//
//	public List<String[]> getColumnLabels() { return colTable; }
//	public List<String[]> getRowLabels() { return rowTable; }
//
//	
//
//	public Metadata getMetadata() { return fileMetadata; }
//
//	public Source getSource() { return source; }
//
//	public String getSpecies() { return (String)fileMetadata.get(Metadata.SPECIES); }
//
//	public FileExperimentTableModel getTableModel() { 
//		if (tableModel == null)
//			tableModel = new FileExperimentTableModel(scNVManager, this); 
//		return tableModel;
//	}
//
//	
//
//	public void readMTX (final TaskMonitor monitor, boolean skipFirst) {
//		// Initialize
//		mtx = null;
//		colTable = null;
//		rowTable = null;
//
//		// Get the URI
//		File mtxFile = (File)fileMetadata.get(FileMetadata.FILE);
//
//		// Three possibilities:
//		// 1) Directory
//		// 2) ZIP file
//		// 3) Tar.gz file
//
//		try {
//			if (mtxFile.isDirectory()) {
//				// System.out.println("Directory");
//				for (File f: mtxFile.listFiles()) {
//					readFile(monitor, f, skipFirst);
//				}
//			} else if (FileUtils.isZip(mtxFile.getName())) {
//				// System.out.println("Zip file");
//				ZipInputStream zipStream = FileUtils.getZipInputStream(mtxFile);
//				ZipEntry entry;
//				while ((entry = zipStream.getNextEntry()) != null) {
//					readFile(monitor, entry.getName(), zipStream, skipFirst);
//					zipStream.closeEntry();
//				}
//				zipStream.close();
//			} else if (FileUtils.isTar(mtxFile.getName())) {
//				// System.out.println("Tar file");
//				TarArchiveInputStream tarStream = FileUtils.getTarInputStream(mtxFile);
//				TarArchiveEntry entry;
//				while ((entry = tarStream.getNextTarEntry()) != null) {
//					// System.out.println("Tar entry: "+entry.getName());
//					if (FileUtils.isGzip(entry.getName())) {
//						InputStream stream = FileUtils.getGzipStream(tarStream);
//						readFile(monitor, entry.getName(), stream, skipFirst);
//					} else {
//						readFile(monitor, entry.getName(), tarStream, skipFirst);
//					}
//				}
//				tarStream.close();
//			} else {
//				readFile(monitor, mtxFile, skipFirst);
//			}
//    } catch (FileNotFoundException e) {
//      monitor.showMessage(TaskMonitor.Level.ERROR, "No such file: '"+mtxFile.getName()+"'");
//      return;
//		} catch(IOException e) {
//      monitor.showMessage(TaskMonitor.Level.ERROR, "Error reading file: '"+mtxFile.getName()+"': "+e.getMessage());
//      e.printStackTrace();
//      return;
//    }
//
//		scNVManager.addExperiment(accession, this);
//
//    // Cache the file on the server
//		new Thread(new CacheExperimentThread(accession)).start();
//
//		System.out.println("mtx has "+mtx.getNRows()+" rows and "+mtx.getNCols()+" columns");
//	}
//
//	private void readFile(TaskMonitor monitor, File f, boolean skipFirst) throws IOException {
//		InputStream stream = new FileInputStream(f);
//		// if (FileUtils.isGzip(f.getName())) {
//		// 	stream = FileUtils.getGzipStream(stream);
//		// }
//		readFile(monitor, f.getName(), stream, skipFirst);
//	}
//
//	private void readFile(TaskMonitor monitor, String name, InputStream stream, 
//	                      boolean skipFirst) throws IOException {
//		if (isColumnFile(name)) {
//			// System.out.println("Reading columns from "+name);
//			colTable = CSVReader.readCSV(monitor, stream, name);
//			// System.out.println("colTable has "+colTable.size()+" columns");
//
//			if (skipFirst) {
//				if (colTable.size() > 1)
//					colTable.remove(0);
//				// See if the first line is a header
//				// FileUtils.skipHeader(colTable);
//			}
//
//			columnIndexKey = getColumnIndex(colTable, name);
//			if (mtx != null) {
//				mtx.setColumnTable(colTable, columnIndexKey);
//			}
//		} else if (isRowFile(name)) {
//			rowTable = CSVReader.readCSV(monitor, stream, name);
//			// System.out.println("rowTable has "+rowTable.size()+" rows");
//			if (skipFirst) {
//				if (rowTable.size() > 1)
//					rowTable.remove(0);
//				// See if the first line is a header
//				// FileUtils.skipHeader(rowTable);
//			}
//
//			rowIndexKey = getRowIndex(rowTable, name);
//			if (mtx != null) {
//				mtx.setRowTable(rowTable, rowIndexKey);
//			}
//		} if (isMtxFile(name)) {
//			mtx = new MatrixMarket(scNVManager, null, null);
//			if (rowTable != null)
//				mtx.setRowTable(rowTable, rowIndexKey);
//			if (colTable != null)
//				mtx.setColumnTable(colTable, columnIndexKey);
//
//			mtx.readMTX(monitor, stream, name);
//		}
//	}
//
//	public String toString() {
//		return getAccession();
//	}
//
//	public String toHTML() {
//		return fileMetadata.toHTML();
//	}
//
//	public String toJSON() {
//		StringBuilder builder = new StringBuilder();
//		builder.append("{");
//		builder.append("\"source\": \""+getSource().toString()+"\",\n");
//		builder.append("\"source name\": \""+getSource().getName()+"\",\n");
//		builder.append("\"metadata\": "+fileMetadata.toJSON()+",\n");
//		builder.append("\"rows\": "+getMatrix().getNRows()+",\n");
//		builder.append("\"columns\": "+getMatrix().getNCols()+",\n");
//		//List<Category> categories = getCategories();
//		builder.append("\"categories\": [");
//		//int nCat = categories.size();
//		//for (Category cat: categories) {
//			//builder.append(cat.toJSON());
//			//if (nCat-- > 1) builder.append(",\n");
//		//}
//		builder.append("]");
//		//if (diffExp != null) {
//			//builder.append(",\""+ScNVManager.DIFFEXP+"\":");
//			//builder.append(diffExp.toJSON()+"\n");
//		//}
//		builder.append("}");
//		return builder.toString();
//	}
//
//	public void readSessionFiles(String accession, Map<String, File> fileMap) throws Exception {
//    File mtxFile = null, rowFile = null, colFile = null;
//    for (String fileName: fileMap.keySet()) {
//      if (isMtxFile(fileName))
//        mtxFile = fileMap.get(fileName);
//      else if (isRowFile(fileName))
//        rowFile = fileMap.get(fileName);
//      else if (isColumnFile(fileName))
//        colFile = fileMap.get(fileName);
//    }
//
//    if (mtxFile == null  || rowFile == null || colFile == null) {
//      throw new Exception("Matrix, row, or column file missing from session!");
//    }
//
//    readFile(null, rowFile, false);
//    readFile(null, colFile, false);
//    readFile(null, mtxFile, false);
//    return;
//  }
//
//	public void createSessionFiles(String accession, List<File> files) throws Exception {
//		String tmpDir = System.getProperty("java.io.tmpdir");
//		String expPrefix = source.getName()+"."+accession;
//		try {
//			// Save the Experiment file as an MTX
//			File mtxFile = new File(tmpDir, URLEncoder.encode(expPrefix)+".mtx");
//			mtx.saveFile(mtxFile);
//			files.add(mtxFile);
//			File mtxRowFile = new File(tmpDir, URLEncoder.encode(expPrefix)+".mtx_rows");
//			CSVWriter.writeCSV(mtxRowFile, rowTable);
//			files.add(mtxRowFile);
//
//			File mtxColFile = new File(tmpDir, URLEncoder.encode(expPrefix)+".mtx_cols");
//			CSVWriter.writeCSV(mtxColFile, colTable);
//			files.add(mtxColFile);
//		} catch (Exception e) {
//			logger.error("Unable to save MTX data for "+accession+" in session: "+e.toString());
//				e.printStackTrace();
//			return;
//		}
//
//
//	}
//
//
//
//
//	public boolean isColumnFile(String fileName) {
//		String name = FileUtils.baseName(fileName);
//		if (name.endsWith(".mtx_cols") || name.contains("colLabels") || name.startsWith("barcodes")) {
//			return true;
//		}
//		return false;
//	}
//
//	public boolean isRowFile(String fileName) {
//		String name = FileUtils.baseName(fileName);
//		if (name.endsWith(".mtx_rows") || name.contains("rowLabels") || name.startsWith("features")) {
//			return true;
//		}
//		return false;
//	}
//
//	public boolean isMtxFile(String fileName) {
//		if (fileName.endsWith(".mtx") || fileName.endsWith(".mtx.gz"))
//			return true;
//		return false;
//	}
//
//	private int getColumnIndex(List<String[]> table, String name) {
//		// System.out.println("getColumnIndex of "+name);
//		// System.out.println("getColumnIndex table size = "+table.size());
//		// System.out.println("getColumnIndex table length = "+table.get(0).length);
//		if (table.size() > 0 && table.get(0).length == 1 && name.contains("barcodes"))
//			return 0;
//		return 1;
//	}
//
//	private int getRowIndex(List<String[]> table, String name) {
//		// System.out.println("Row Table size = "+table.size());
//		if (table.size() > 0 && name.contains("features"))
//			return 0;
//		return 1;
//	}
//
//	class CacheExperimentThread implements Runnable {
//    final String accession;
//    public CacheExperimentThread(String acc) { this.accession = acc; }
//		@Override
//		public void run() {
//      String postString = String.format(SERVICES_URI, accession);
//      try {
//        // Create the cache file
//        mtx.createCache("File", accession);
//
//        // Wait until the cache is available
//        while (!mtx.hasCache()) {
//          Thread.sleep(1000);
//        }
//
//        // OK, now send the file to the server
//        File expFile = mtx.getMatrixCache();
//        HTTPUtils.postFile(postString, expFile, null);
//        mtx.cacheSent(true);
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }
//  }
//
//}
