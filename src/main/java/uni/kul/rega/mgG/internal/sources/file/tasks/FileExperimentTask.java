package uni.kul.rega.mgG.internal.sources.file.tasks;

import java.io.File;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import uni.kul.rega.mgG.internal.api.Experiment;
import uni.kul.rega.mgG.internal.api.Metadata;
import uni.kul.rega.mgG.internal.model.ScNVManager;
//import uni.kul.rega.mgG.internal.model.Species;
import uni.kul.rega.mgG.internal.sources.file.FileExperiment;
import uni.kul.rega.mgG.internal.sources.file.FileMetadata;
import uni.kul.rega.mgG.internal.sources.file.FileSource;
//import uni.kul.rega.mgG.internal.tasks.ShowExperimentTableTask;

public class FileExperimentTask extends AbstractTask {
	final ScNVManager scManager;
	final FileSource fileSource;

	//@Tunable (description="Species", required=true, 
	     //     tooltip="Species information is required for network generation")
	//public ListSingleSelection<Species> species = null;
	// public String species = "Homo sapiens";

	@Tunable (description="File or directory with MTX matrix and headers",params="input=true")
	public File file;

	@Tunable (description="Skip first line of header files")
	public boolean skipFirst = false;

	@Tunable (description="Show experiment table after loading",context="nogui")
	public boolean showTable = true;

	public FileExperimentTask(final ScNVManager scManager, final FileSource fileSource) {
		super();
		this.scManager = scManager;
		this.fileSource = fileSource;
	//	species = new ListSingleSelection<Species>(Species.getSpecies());
		// Set Human as the default
		//for (Species s: Species.getSpecies()) {
			//if (s.toString().equals("Homo sapiens")) {
				//species.setSelectedValue(s);
				//break;
			//}
		//}

	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		taskMonitor.setTitle(getTitle());
		taskMonitor.setStatusMessage("Reading mtx file");
		FileMetadata metadata = new FileMetadata(file);
		//metadata.put(Metadata.SPECIES, species.getSelectedValue().getName());
		Experiment experiment = fileSource.getExperiment(metadata, taskMonitor, skipFirst);

		if (showTable) {
			// Show the experiment
			//insertTasksAfterCurrentTask(new ShowExperimentTableTask(scManager, experiment));
		}
	}

	@ProvidesTitle
	public String getTitle() {return "Read mtx experiment file";}
}