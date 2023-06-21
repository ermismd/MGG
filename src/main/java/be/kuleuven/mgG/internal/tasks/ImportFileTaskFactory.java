package be.kuleuven.mgG.internal.tasks;

import java.io.File;

import javax.swing.JFileChooser;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ImportFileTaskFactory implements TaskFactory {
    private final CySwingApplication swingApplication;
    private final CyApplicationManager cyApplicationManager;

    public ImportFileTaskFactory(CySwingApplication cytoscapeDesktopService, CyApplicationManager cyApplicationManager) {
        this.swingApplication = cytoscapeDesktopService;
        this.cyApplicationManager = cyApplicationManager;
    }

    @Override
    public TaskIterator createTaskIterator() {
        // Use a JFileChooser to get the file path
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();

            return new TaskIterator(new ImportFileTask(swingApplication, cyApplicationManager, filePath));
        } else {
            // If no file was selected, return an empty TaskIterator
            return new TaskIterator();
        }
    }

    @Override
    public boolean isReady() {
        // This task factory is always ready to run
        return true;
    }
}