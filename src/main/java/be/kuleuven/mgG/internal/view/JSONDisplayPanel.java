package be.kuleuven.mgG.internal.view;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class JSONDisplayPanel extends JPanel {
    private JTable table;

    public JSONDisplayPanel(JSONArray jsonArray) {
        super(new BorderLayout());
        createTable(jsonArray);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createTable(JSONArray jsonArray) {
        DefaultTableModel tableModel = new DefaultTableModel();
        table = new JTable(tableModel);

        // Set the column names
        JSONObject firstObject = (JSONObject) jsonArray.get(0);
        for (Object key : firstObject.keySet()) {
            tableModel.addColumn(key.toString());
        }

        // Add the data to the table model
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            Object[] rowData = new Object[tableModel.getColumnCount()];
            int columnIndex = 0;
            for (Object value : jsonObject.values()) {
                rowData[columnIndex] = value;
                columnIndex++;
            }
            tableModel.addRow(rowData);
        }
    }
}