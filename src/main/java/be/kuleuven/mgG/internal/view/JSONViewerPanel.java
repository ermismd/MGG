package be.kuleuven.mgG.internal.view;


import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONObject;

import java.awt.*;

public class JSONViewerPanel extends JPanel {

    private JTextArea textArea;

    public JSONViewerPanel(JSONObject jsonObject) {
        initializeUI();
        displayJSON(jsonObject);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void displayJSON(JSONObject jsonObject) {
        String jsonText = jsonObject.toString(); // 4 is the indentation level for pretty printing
        textArea.setText(jsonText);
    }
}