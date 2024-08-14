package be.kuleuven.mgG.internal.tasks;


import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import be.kuleuven.mgG.internal.model.MGGManager;


public class ImportFileTaskFactory implements TaskFactory {
    
    private final MGGManager mggManager;
    
    
    public ImportFileTaskFactory(MGGManager mggManager) {
      
        this.mggManager=mggManager;
    }

    
	@Override
    public TaskIterator createTaskIterator() {
		 
	    JFileChooser fileChooser = new JFileChooser();
	    int option = fileChooser.showOpenDialog(null);
	    if (option == JFileChooser.APPROVE_OPTION) {
	        File selectedFile = fileChooser.getSelectedFile();
	        String filePath = selectedFile.getAbsolutePath();

	        // Check if file has valid extension (.csv or .tsv)
            if (filePath.endsWith(".csv") || filePath.endsWith(".tsv")) {
                return new TaskIterator(new ImportFileTask(filePath, mggManager));
            } else {
                // Show error message if it's not .csv or .tsv
                String errorMessage = "Invalid file type. Please select a .csv or .tsv file.";
                JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                // Return a dummy task to avoid "hasNext() is false" error
                return new TaskIterator(new DummyErrorTask(errorMessage));
            }
        } else if (option == JFileChooser.CANCEL_OPTION) {
            // cancellation
            String errorMessage = "File selection was canceled.";
            JOptionPane.showMessageDialog(null, errorMessage, "Canceled", JOptionPane.WARNING_MESSAGE);
            // Return a dummy task to avoid "hasNext() is false" error
            return new TaskIterator(new DummyErrorTask(errorMessage));
        } else {
            String errorMessage = "Error selecting file";
            JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            // Return a dummy task to avoid "hasNext() is false" error
            return new TaskIterator(new DummyErrorTask(errorMessage));
        }
    }



	

    @Override
    public boolean isReady() {  
        return true;
    }
}