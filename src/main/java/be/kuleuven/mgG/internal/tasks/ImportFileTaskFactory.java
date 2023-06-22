package be.kuleuven.mgG.internal.tasks;

import java.io.File;

import javax.swing.JFileChooser;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import be.kuleuven.mgG.internal.model.MGGManager;

public class ImportFileTaskFactory implements TaskFactory {
    private final CySwingApplication swingApplication;
    private final CyApplicationManager cyApplicationManager;
    private final MGGManager mggManager;
    
    
    public ImportFileTaskFactory(CySwingApplication cytoscapeDesktopService, CyApplicationManager cyApplicationManager,MGGManager mggManager) {
        this.swingApplication = cytoscapeDesktopService;
        this.cyApplicationManager = cyApplicationManager;
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

            return new TaskIterator(new ImportFileTask(swingApplication, cyApplicationManager, filePath, mggManager));
        } else {
            // If no file was selected, return an empty TaskIterator
            return new TaskIterator();
        }
    }

    @Override
    public boolean isReady() {  
        return true;
    }
}