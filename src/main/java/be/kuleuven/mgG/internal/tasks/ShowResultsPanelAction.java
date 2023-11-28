package be.kuleuven.mgG.internal.tasks;

import java.awt.event.ActionEvent;

import javax.swing.event.MenuEvent;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.work.TaskManager;

import be.kuleuven.mgG.internal.model.MGGManager;
import be.kuleuven.mgG.internal.utils.Mutils;


@SuppressWarnings("serial")
public class ShowResultsPanelAction extends AbstractCyAction {

	final MGGManager manager;
		
	public ShowResultsPanelAction(String name, MGGManager manager) {
		super(name);

		this.manager = manager;
		setPreferredMenu("Apps.MGG.Show Results panel");
		setMenuGravity(3);
		useCheckBoxMenuItem = true;
		insertSeparatorBefore = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		TaskManager<?, ?> tm = manager.getService(TaskManager.class);
		ShowResultsPanelTaskFactory factory = manager.getShowResultsPanelTaskFactory();
		tm.execute(factory.createTaskIterator());
	}

	@Override
	public void menuSelected(MenuEvent evt) {
		updateEnableState();
		putValue(SELECTED_KEY, ShowResultsPanelTask.isPanelRegistered(manager));
	}
	
	@Override
	public void updateEnableState() {
		setEnabled(Mutils.isMGGNetworkMicrobetagDB(manager.getCurrentNetwork()));
	}
	
}
