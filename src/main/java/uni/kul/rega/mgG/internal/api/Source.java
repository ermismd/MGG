package uni.kul.rega.mgG.internal.api;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;

//import uni.kul.rega.mgG.internal.model.DifferentialExpression;

public interface Source {
	public static String SOURCENAME = "name";
	public String getName();
	public List<String> getAccessions();
	public List<Metadata> getMetadata();
	//public Experiment getExperiment(String accession);
	//public Experiment loadExperimentFromSession(JSONObject jsonExperiment, Map<String,File> fileMap);
	
}
