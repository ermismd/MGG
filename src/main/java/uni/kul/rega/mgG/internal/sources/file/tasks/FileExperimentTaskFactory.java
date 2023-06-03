package uni.kul.rega.mgG.internal.sources.file.tasks;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import uni.kul.rega.mgG.internal.model.ScNVManager;
import uni.kul.rega.mgG.internal.sources.file.FileSource;

public class FileExperimentTaskFactory extends AbstractTaskFactory {
	final ScNVManager scManager;
	final FileSource fileSource;

	public FileExperimentTaskFactory(final ScNVManager scManager, final FileSource fileSource) {
		super();
		this.scManager = scManager;
		this.fileSource = fileSource;
	}

	public TaskIterator createTaskIterator() {
		return new TaskIterator(new FileExperimentTask(scManager, fileSource));
	}
}
