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

	        return new TaskIterator(new ImportFileTask(filePath, mggManager));
	    } else if (option == JFileChooser.CANCEL_OPTION) {
	        
	        return new TaskIterator();
	    } else {
	        
	        String errorMessage = "Error selecting file";
	        
	        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
	       
	        return new TaskIterator();
	    }
	}
	

    @Override
    public boolean isReady() {  
        return true;
    }
}