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
		  // Use a JFileChooser to get the file path
	    JFileChooser fileChooser = new JFileChooser();
	    int option = fileChooser.showOpenDialog(null);
	    if (option == JFileChooser.APPROVE_OPTION) {
	        File selectedFile = fileChooser.getSelectedFile();
	        String filePath = selectedFile.getAbsolutePath();

	        return new TaskIterator(new ImportFileTask(filePath, mggManager));
	    } else if (option == JFileChooser.CANCEL_OPTION) {
	        // User cancelled the file selection, return an empty TaskIterator
	        return new TaskIterator();
	    } else {
	        // An error occurred or no file was selected
	        String errorMessage = "Error selecting file";
	        // You can display an error message or handle the error in any other way appropriate for your application
	        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
	        // Return an empty TaskIterator or any other appropriate error handling
	        return new TaskIterator();
	    }
	}
	

    @Override
    public boolean isReady() {  
        return true;
    }
}