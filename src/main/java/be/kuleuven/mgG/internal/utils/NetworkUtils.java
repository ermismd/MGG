/*
 * package be.kuleuven.mgG.internal.utils;
 * 
 * import org.cytoscape.model.CyEdge; import org.cytoscape.model.CyNetwork;
 * import org.cytoscape.model.CyNetworkFactory; import
 * org.cytoscape.model.CyNode; import org.json.simple.JSONArray; import
 * org.json.simple.JSONObject; import org.json.simple.parser.JSONParser;
 * 
 * import be.kuleuven.mgG.internal.model.MGGManager;
 * 
 * 
 * public class NetworkUtils { private CyNetworkFactory networkFactory;
 * 
 * 
 * 
 * 
 * public static CyNetwork createNetworkFromJson(MGGManager mggmanager,
 * JSONObject jsonResponse) {
 * 
 * 
 * 
 * JSONObject elements = (JSONObject) jsonResponse.get("elements");
 * 
 * // Create a new network CyNetwork network = networkFactory.createNetwork();
 * 
 * // Add nodes JSONArray nodes = (JSONArray) elements.get("nodes"); for (Object
 * nodeObj : nodes) { JSONObject nodeData = (JSONObject) ((JSONObject)
 * nodeObj).get("data"); String id = (String) nodeData.get("id"); CyNode node =
 * network.addNode(); network.getRow(node).set(CyNetwork.NAME, id);
 * network.getRow(node).set("alias", ((JSONArray) nodeData.get("alias")));
 * network.getRow(node).set("SUID", (Long) nodeData.get("SUID"));
 * network.getRow(node).set("shared_name", (String)
 * nodeData.get("shared_name")); network.getRow(node).set("selected", (Boolean)
 * nodeData.get("selected")); }
 * 
 * // Add edges JSONArray edges = (JSONArray) elements.get("edges"); for (Object
 * edgeObj : edges) { JSONObject edgeData = (JSONObject) ((JSONObject)
 * edgeObj).get("data"); String sourceId = (String) edgeData.get("source");
 * String targetId = (String) edgeData.get("target"); CyNode sourceNode =
 * getNodeById(network, sourceId); CyNode targetNode = getNodeById(network,
 * targetId); if (sourceNode != null && targetNode != null) { CyEdge edge =
 * network.addEdge(sourceNode, targetNode, false);
 * network.getRow(edge).set(CyNetwork.NAME, (String) edgeData.get("id"));
 * network.getRow(edge).set("shared_interaction", (String)
 * edgeData.get("shared_interaction")); network.getRow(edge).set("interaction",
 * (String) edgeData.get("interaction"));
 * network.getRow(edge).set("shared_name", (String)
 * edgeData.get("shared_name")); network.getRow(edge).set("source", (String)
 * edgeData.get("source")); network.getRow(edge).set("target", (String)
 * edgeData.get("target")); network.getRow(edge).set("selected", (Boolean)
 * edgeData.get("selected")); } }
 * 
 * return network; }
 * 
 * private CyNode getNodeById(CyNetwork network, String id) { for (CyNode node :
 * network.getNodeList()) { if (network.getRow(node).get(CyNetwork.NAME,
 * String.class).equals(id)) { return node; } } return null; } }
 */