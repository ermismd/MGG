package be.kuleuven.mgG.internal.tasks;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.json.simple.JSONObject;

import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.view.JSONDisplayPanel;

public class CheckAbudanceFileTask extends AbstractTask {
	
	
	 private final MGGManager mggManager;
	 
	
	 public CheckAbudanceFileTask(MGGManager mggManager) {
	    	
	    
	        
			this.mggManager = mggManager;
	               
	    }
	    

	
	 @Override
	    public void run(TaskMonitor taskMonitor) throws Exception {
	        JSONObject dataObject = mggManager.getJsonObject();
	        taskMonitor.setStatusMessage("Data Send " + dataObject.toJSONString());
	        
	        if (dataObject != null && dataObject.containsKey("data")) {
	            SwingUtilities.invokeLater(() -> {
	                JSONDisplayPanel panel = new JSONDisplayPanel(mggManager, dataObject);
	                displayPanelInFrame(panel, "Imported Abudance Data");
	            });
	        } else {
	            
	            taskMonitor.setStatusMessage("No Abudance Data Imported");
	        
	        }}
	 
	    

	    private void displayPanelInFrame(JSONDisplayPanel panel, String title) {
	        JFrame frame = new JFrame(title);
	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        frame.getContentPane().add(panel);
	        frame.pack();
	        frame.setVisible(true);
	   
	    }
	    

}
