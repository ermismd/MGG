package be.kuleuven.mgG.internal.tasks;

import java.awt.Component;
import java.util.Properties;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;

import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.view.MGGCytoPanel;



public class ShowResultsPanelTask extends AbstractTask {
    final MGGManager manager;
    final ShowResultsPanelTaskFactory factory;
    final boolean show;

    public ShowResultsPanelTask(final MGGManager manager,
        final ShowResultsPanelTaskFactory factory, boolean show) {
        this.manager = manager;
        this.factory = factory;
        this.show = show;
    }

    public void run(TaskMonitor monitor) {
        monitor.setTitle("Show/hide results panel");

        CySwingApplication swingApplication = manager.getService(CySwingApplication.class);
        CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.EAST);

        // If the panel is not already registered, create it
        if (cytoPanel.indexOfComponent("be.kuleuven.mgG.internal.MGG") < 0) {
            CytoPanelComponent2 panel = new MGGCytoPanel(manager);

            // Register it
            manager.registerService(panel, CytoPanelComponent.class, new Properties());

            if (cytoPanel.getState() == CytoPanelState.HIDE)
                cytoPanel.setState(CytoPanelState.DOCK);

        } else {
            int compIndex = cytoPanel.indexOfComponent("be.kuleuven.mgG.internal.MGG");
            Component panel = cytoPanel.getComponentAt(compIndex);
            if (panel instanceof CytoPanelComponent2) {
                // Unregister it
                manager.unregisterService(panel, CytoPanelComponent.class);
                manager.setCytoPanel(null);
            }
        }

        // factory.reregister();
    }

    public static boolean isPanelRegistered(MGGManager manager) {
        CySwingApplication swingApplication = manager.getService(CySwingApplication.class);
        CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.EAST);

        if (cytoPanel.indexOfComponent("be.kuleuven.mgG.internal.MGG") >= 0)
            return true;

        return false;
    }
}
