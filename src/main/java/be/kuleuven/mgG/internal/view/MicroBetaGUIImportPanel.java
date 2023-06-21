package be.kuleuven.mgG.internal.view;
//package uni.kul.rega.mgG.internal.view;
//
//import java.awt.BorderLayout;
//import java.util.Set;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//
//import java.awt.BorderLayout;
//import java.util.Set;
//
//public class MicroBetaGUIImportPanel extends JPanel {
//	private JTable dataTable;
//
//	public MicroBetaGUIImportPanel(JSONArray jsonArray) {
//		setLayout(new BorderLayout());
//
//		// Create the table model
//		DefaultTableModel tableModel = new DefaultTableModel();
//		dataTable = new JTable(tableModel);
//
//		// Set the column names
//		String[] headers = getColumnNames(jsonArray);
//		tableModel.setColumnIdentifiers(headers);
//
//		// Add the data to the table model
//		addDataToTableModel(jsonArray, tableModel);
//
//		// Create a scroll pane and add the table to it
//		JScrollPane scrollPane = new JScrollPane(dataTable);
//		add(scrollPane, BorderLayout.CENTER);
//	}
//
//	private String[] getColumnNames(JSONArray jsonArray) {
//		JSONObject firstObject = (JSONObject) jsonArray.get(0);
//		Set<String> keySet = firstObject.keySet();
//		String[] columnNames = keySet.toArray(new String[keySet.size()]);
//		return columnNames;
//	}
//
//	private void addDataToTableModel(JSONArray jsonArray, DefaultTableModel tableModel) {
//		for (Object obj : jsonArray) {
//			JSONObject jsonObject = (JSONObject) obj;
//			Object[] rowData = jsonObject.values().toArray();
//			tableModel.addRow(rowData);
//		}
//	}
//}