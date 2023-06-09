## INFO WEEK1 AND WEEK2



#### Created 2 classes that allows users to import a CSV/TSV file ,process it, display the JSON data in a panel and send the JSON data to a server.

 * * *

 
 
<html>
<head>
  <style>
   .panel {
      display: none;
      background-color: #f1f1f1;
      padding: 10px;
      margin-top: 10px;
      font-size: 10px; /* Increase the font size as needed */
      width: 800px; /* Increase the width as needed */
    }

    h2 {
      font-size: 14px; /* Decrease the font size of the headers */
    }

    .panel-button {
      margin-bottom: 50px; /* Add space between each panel button */
    }
  </style>
</head>
<body>
  <h2>Class 1</h2>
  <button onclick="showMicroBetaGUIImportAction()">MicroBetaGUIImportAction</button>
  <div class="panel" id="MicroBetaGUIImportAction">
    <pre>
      /*
 * 
 * The MicroBetaGUIImportAction represents an action in the mgG application + 
 * that allows users to import a CSV file ,process it, display the JSON data in a panel, +
 * and send the JSON data to a server
 * 
*/

public class MicroBetaGUIImportAction extends AbstractCyAction {

	private final CySwingApplication swingApplication;
	private final CytoPanel cytoPanelWest;
	private final CyApplicationManager cyApplicationManager;  
	    
	    
	public MicroBetaGUIImportAction(CySwingApplication cytoscapeDesktopService,
	CyApplicationManager cyApplicationManager2) {
	
	   super("Import CSV File");
	   this.swingApplication = cytoscapeDesktopService;
	   this.cytoPanelWest = swingApplication.getCytoPanel(CytoPanelName.WEST);
	   this.cyApplicationManager = cyApplicationManager2;

	    setPreferredMenu("Apps.MicroBetaGUI");
	    setMenuGravity(1.0f);
	             
	    }

	  
		@Override
	    public void actionPerformed(ActionEvent e) {
	        //  dialog to choose the CSV 
	        JFileChooser fileChooser = new JFileChooser();
	        int option = fileChooser.showOpenDialog(null);
	        if (option == JFileChooser.APPROVE_OPTION) {
	            //  user selects a file, perform the import  here
	            File selectedFile = fileChooser.getSelectedFile();
	            String filePath = selectedFile.getAbsolutePath();
	            // Call the method to process the CSV 
	            processCSVFile(null, filePath);
	        }
	    }

		private void processCSVFile(final TaskMonitor monitor,String filePath) {
		    try {
		    // Call CSVReader from Utils to parse the TSV/CSV file with tab delimiter
		       List<String[]> csvData = CSVReader.readCSV(monitor, filePath);

		   // Find the headers(the first row that has more than 1 columns)
			     String[] headers = null;
			      for (String[] row : csvData) {
			         if (row.length > 1) {
			                headers = row;
			                break;
			            }
			        }

			        // Create JSONArray to hold the JSONObjects
			        JSONArray jsonArray = new JSONArray();

			        // Iterate each row of CSV 
			        for (String[] values : csvData) {
			            // Skip rows with only one column
			            if (values.length <= 1) {
			                continue;
			            }

			            // Create a JSONObject for each row of CSV 
			            JSONObject jsonObject = new JSONObject();
			            for (int j = 0; j < headers.length; j++) {
			                if (j < values.length) {
			                    jsonObject.put(headers[j], values[j]);
			                }
			            }

			            // Add the JSONObject to the JSONArray
			            jsonArray.add(jsonObject);
			        }

		        // Write  JSON data to a file
		        String jsonFilePath = filePath + ".json";
		        FileWriter writer = new FileWriter(jsonFilePath);
		        writer.write(jsonArray.toJSONString());
		        writer.close();

		        // Show the JSON data in a panel
		        showDataInPanel(jsonArray);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
	    }
		
		
		/*
		 * showDataInPanel method  displays the JSON data passed as a JSONArray. 
		 *It creates a new  JSONDisplayPanel from +
		 * the internal.view package
		 */
		
		private void showDataInPanel(JSONArray jsonArray) {
		    JSONDisplayPanel panel = new JSONDisplayPanel(jsonArray);

		    JFrame frame = new JFrame("JSON Data");
		    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    frame.getContentPane().add(panel);
		    frame.pack();
		    frame.setVisible(true);
		}
		
		
		  private void sendJSONDataToServer(JSONArray jsonArray) {
		        try {
		            // Convert the JSONArray to a JSON string
		            String jsonQuery = jsonArray.toJSONString();

		            // Set the server URL
		            String serverURL = "https://example.com/api/endpoint"; 

		            // Create an instance of CloseableHttpClient
		            CloseableHttpClient httpclient = HttpClients.createDefault();

		            // Send the JSON data to the server
		            JSONObject jsonResponse = HTTPUtils.postJSON(serverURL, httpclient, jsonQuery, null);

		            // Process the server response if needed
		            if (jsonResponse != null) {
		            	
		                // Process the response here
		            	
		            	String response = jsonResponse.toJSONString();
		                // Do something with the response
		            	
		            }
		            

		            // Close the HttpClient
		            httpclient.close();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }

		

		
}
    </pre>
  </div>






  <h2>Class 2</h2>
  <button onclick="showJSONDisplayPanel()">JSONDisplayPanel</button>
  <div class="panel" id="JSONDisplayPanel">
    <pre>
 
public class JSONDisplayPanel extends JPanel {
    private JTable table;

    public JSONDisplayPanel(JSONArray jsonArray) {
        super(new BorderLayout());
        createTable(jsonArray);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createTable(JSONArray jsonArray) {
        DefaultTableModel tableModel = new DefaultTableModel();
        table = new JTable(tableModel);

        // Set the column names
        JSONObject firstObject = (JSONObject) jsonArray.get(0);
        for (Object key : firstObject.keySet()) {
            tableModel.addColumn(key.toString());
        }

        // Add the data to the table model
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            Object[] rowData = new Object[tableModel.getColumnCount()];
            int columnIndex = 0;
            for (Object value : jsonObject.values()) {
                rowData[columnIndex] = value;
                columnIndex++;
            }
            tableModel.addRow(rowData);
        }
    }
}
      
    </pre>
  </div>

  <script>
    function showMicroBetaGUIImportAction() {
      var panel = document.getElementById("MicroBetaGUIImportAction");
      if (panel.style.display === "none") {
        panel.style.display = "block";
      } else {
        panel.style.display = "none";
      }
    }
    
    function showJSONDisplayPanel() {
      var panel = document.getElementById("JSONDisplayPanel");
      if (panel.style.display === "none") {
        panel.style.display = "block";
      } else {
        panel.style.display = "none";
      }
    }
  </script>
</body>
</html>

* * *
* * *


[back](./)
