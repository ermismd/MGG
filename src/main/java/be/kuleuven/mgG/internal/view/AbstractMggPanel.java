package be.kuleuven.mgG.internal.view;



import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.util.swing.IconManager;
import org.cytoscape.util.swing.OpenBrowser;

import be.kuleuven.mgG.internal.model.MGGManager;



public abstract class AbstractMggPanel extends JPanel {

	protected final MGGManager manager;
	protected final OpenBrowser openBrowser;
	protected final Font iconFont;
	protected final Font labelFont;
	protected final Font textFont;
	protected CyNetwork currentNetwork;
	protected Map<CyNetwork, Map<String,Map<String, Double>>> filters;
	
	

	public AbstractMggPanel(final MGGManager manager) {
		this.manager = manager;
		this.openBrowser = manager.getService(OpenBrowser.class);
		this.currentNetwork = manager.getCurrentNetwork();
		IconManager iconManager = manager.getService(IconManager.class);
		iconFont = iconManager.getIconFont(17.0f);
		labelFont = new Font("Arial", Font.BOLD, 10);
		textFont = new Font("Arial", Font.PLAIN, 10);
		filters = new HashMap<>();
		filters.put(currentNetwork, new HashMap<>());
		
		
		
	
		
	}

	abstract void doFilter(String type);
	
	abstract void undoFilters();
	
	abstract double initFilter(String type, String text);
	
	abstract double initFilterSeed(String type, String text);

	protected JComponent createFilterSlider(String type, String text, CyNetwork network, boolean labels, double max) {
		double value = 0.0;
		if (filters.containsKey(network) && 
		    filters.get(network).containsKey(type) && 
		    filters.get(network).get(type).containsKey(text)) {
			value = filters.get(network).get(type).get(text);
			// System.out.println("value = "+value);
		} else {
			value = initFilter(type, text);
		}
		Box box = Box.createHorizontalBox();
		if (labels) {
			JLabel label = new JLabel(text);
			label.setFont(labelFont);
			label.setPreferredSize(new Dimension(100,20));
			box.add(Box.createRigidArea(new Dimension(10,0)));
			box.add(label);
			box.add(Box.createHorizontalGlue());
		}
		 // Assume max is positive and represents the maximum value for the slider.
	    // Slider's range is from -100 to max * 100, assuming max is also a double.
	    int sliderMax = (int)(max );
	    JSlider slider = new JSlider(-100, sliderMax, (int)(value * 100)); // value should be between -1 and max
	    slider.setToolTipText("Filter ranges between -1.0 and " + max);
	    slider.setPreferredSize(new Dimension(150, 20));
	    box.add(slider);
	    
	 // Adjust the displayed value in the text field
	    JTextField textField = new JTextField(String.format("%.2f", value), 4); // value is between -1 and max
		textField.setPreferredSize(new Dimension(30,20));
		textField.setMaximumSize(new Dimension(30,20));
		textField.setFont(textFont);
		box.add(textField);
		// Hook it up
		addChangeListeners(type, text, slider, textField, max);
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		return box;
	}
	
	protected JComponent createFilterSlider2(String type, String text, CyNetwork network, boolean labels, double max) {
		double value = 0.0;
		if (filters.containsKey(network) && 
		    filters.get(network).containsKey(type) && 
		    filters.get(network).get(type).containsKey(text)) {
			value = filters.get(network).get(type).get(text);
			// System.out.println("value = "+value);
		} else {
			value = initFilter(type, text);
		}
		Box box = Box.createHorizontalBox();
		if (labels) {
			JLabel label = new JLabel(text);
			label.setFont(labelFont);
			label.setPreferredSize(new Dimension(100,20));
			box.add(Box.createRigidArea(new Dimension(10,0)));
			box.add(label);
			box.add(Box.createHorizontalGlue());
		}
		 // Assume max is positive and represents the maximum value for the slider.
	    // Slider's range is from -100 to max * 100, assuming max is also a double.
		JSlider slider;
		slider = new JSlider(0,(int)max,(int)(value*100));
		slider.setToolTipText("Filter ranges between 0.0 and " + max/100);
		slider.setPreferredSize(new Dimension(150,20));
		box.add(slider);
		// box.add(Box.createHorizontalGlue());
		JTextField textField;
		textField = new JTextField(String.format("%.2f",value),4);
		textField.setPreferredSize(new Dimension(30,20));
		textField.setMaximumSize(new Dimension(30,20));
		textField.setFont(textFont);
		box.add(textField);
		// Hook it up
		addChangeListeners(type, text, slider, textField, max);
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		return box;
	}

	protected JComponent createFilterSlider3(String type, String text, CyNetwork network, boolean labels, double max) {
		double value = 0.0;
		if (filters.containsKey(network) && 
		    filters.get(network).containsKey(type) && 
		    filters.get(network).get(type).containsKey(text)) {
			value = filters.get(network).get(type).get(text);
			// System.out.println("value = "+value);
		} else {
			value = initFilterSeed(type, text);
		}
		Box box = Box.createHorizontalBox();
		if (labels) {
			JLabel label = new JLabel(text);
			label.setFont(labelFont);
			label.setPreferredSize(new Dimension(100,20));
			box.add(Box.createRigidArea(new Dimension(10,0)));
			box.add(label);
			box.add(Box.createHorizontalGlue());
		}
		 // Assume max is positive and represents the maximum value for the slider.
	    // Slider's range is from -100 to max * 100, assuming max is also a double.
		JSlider slider;
		slider = new JSlider(0,(int)max,(int)(value*100));
		slider.setToolTipText("Filter ranges between 0.0 and " + max/100);
		slider.setPreferredSize(new Dimension(150,20));
		box.add(slider);
		// box.add(Box.createHorizontalGlue());
		JTextField textField;
		textField = new JTextField(String.format("%.2f",value),4);
		textField.setPreferredSize(new Dimension(30,20));
		textField.setMaximumSize(new Dimension(30,20));
		textField.setFont(textFont);
		box.add(textField);
		// Hook it up
		addChangeListeners(type, text, slider, textField, max);
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		return box;
	}

	
	
	
	
	protected void addChangeListeners(String type, String label, JSlider slider, 
	                                  JTextField textField, double max) {
//		slider.addChangeListener(new ChangeListener() {
//			public void stateChanged(ChangeEvent e) {
//				JSlider sl = (JSlider)e.getSource();
//				int value = sl.getValue();
//				double v = ((double)value)/100.0;
//				textField.setText(String.format("%.2f",v));
//				addFilter(type, label, v);
//				doFilter(type);
//			}
//		});
		slider.addChangeListener(new ChangeListener() {
		   
		    public void stateChanged(ChangeEvent e) {
		        JSlider source = (JSlider) e.getSource();
		            int weightThreshold = source.getValue();
		            double v=((double)weightThreshold)/100.0;
		            textField.setText(String.format("%.2f",v));
		            addFilter(type, label, v);
		            doFilter(type);  
		            
		        }
		    
		});
	
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextField field = (JTextField)e.getSource();
				String text = field.getText();
				slider.setValue((int)(Double.parseDouble(text)*100.0));
			}
		});
		
		
	}

	

	
	
	protected void addFilter(String type, String label, double value) {
		Map<String,Double> filter = filters.get(currentNetwork).get(type);
		filter.put(label, value);

		if (value == 0)
			filter.remove(label);
	}

	protected void removeFilters(CyNetwork network) {
		if (network != null && filters.containsKey(network))
			filters.remove(network);
	}

}
