/*
 * package be.kuleuven.mgG.internal.tasks;
 * 
 * import org.cytoscape.model.CyNetworkFactory; import
 * org.cytoscape.work.TaskFactory; import org.cytoscape.work.TaskIterator;
 * import org.json.simple.JSONArray;
 * 
 * public static class CreateNetworkTaskFactory implements TaskFactory { private
 * final CyNetworkFactory networkFactory; private final JSONArray jsonResponse;
 * 
 * public CreateNetworkTaskFactory(CyNetworkFactory networkFactory, JSONArray
 * jsonResponse) { this.networkFactory = networkFactory; this.jsonResponse =
 * jsonResponse; }
 * 
 * @Override public TaskIterator createTaskIterator() { return new
 * TaskIterator(new CreateNetworkTask(networkFactory, jsonResponse)); }
 * 
 * @Override public boolean isReady() { return true; } }
 */