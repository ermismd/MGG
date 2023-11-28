package be.kuleuven.mgG.internal.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class CollapsablePanel extends JPanel {
	private static String RIGHT_ARROW = "\uF0DA";
	private static String DOWN_ARROW = "\uF0D7";
	Font awesomeFont;

	JPanel contentPanel_;
	HeaderPanel headerPanel_;

	private class HeaderPanel extends JPanel implements ActionListener {
		Font font;
		JButton expandButton;
		JLabel label;
		boolean expanded = false;

		public HeaderPanel(Font iconFont, String text, boolean collapsed, int fontSize) {
			font = new Font("Arial", Font.BOLD, fontSize);

			this.setLayout(new GridBagLayout());
			this.expanded = !collapsed;

			EasyGBC c = new EasyGBC();

//			if (collapsed)
//				expandButton = new JButton(RIGHT_ARROW);
//				
//			else
//				expandButton = new JButton(DOWN_ARROW);
//			expandButton.addActionListener(this);
//			expandButton.setBorderPainted(false);
//			expandButton.setContentAreaFilled(false);
//			expandButton.setOpaque(false);
//			expandButton.setFocusPainted(false);
//			expandButton.setFont(iconFont);
//			this.add(expandButton, c.anchor("west").noExpand());
			
			 if (collapsed) {
	                expandButton = new JButton(RIGHT_ARROW);
	                expandButton.setToolTipText("Click to expand");
	            } else {
	                expandButton = new JButton(DOWN_ARROW);
	                expandButton.setToolTipText("Click to collapse");
	            }
	            expandButton.addActionListener(this);
	            expandButton.setBorderPainted(false);
	            expandButton.setContentAreaFilled(false);
	            expandButton.setOpaque(false);
	            expandButton.setFocusPainted(false);
	            expandButton.setFont(iconFont);
	            expandButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand pointer
	            this.add(expandButton, c.anchor("west").noExpand());
			
			label = new JLabel(text);
			label.setFont(font);
			//label.setForeground(Color.BLUE);
			this.add(label, c.right().expandHoriz());
			this.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
			// this.setBorder(BorderFactory.createEtchedBorder());
			this.setBackground(Color.LIGHT_GRAY);
			setPreferredSize(new Dimension(200, 20));
		}

		public void setText(String text) {
			// System.out.println("Setting label text to: "+text);
			label.setText("<html>"+text+"</html>");
		}

		public void actionPerformed(ActionEvent e) {
			toggleSelection();
		}

		public void setButton(String buttonState) {
			expandButton.setText(buttonState);
		}

	}

	public CollapsablePanel(Font iconFont, String text, JPanel panel, boolean collapsed) {
		this(iconFont, text, panel, collapsed, 14);
	}

	public CollapsablePanel(Font iconFont, String text, JPanel panel, boolean collapsed, int fontSize) {
		super();
		// setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setLayout(new GridBagLayout());
		EasyGBC c = new EasyGBC();

		headerPanel_ = new HeaderPanel(iconFont, text, collapsed, fontSize);

		setBackground(new Color(200, 200, 220));
		contentPanel_ = panel;
		// panel.setBorder(BorderFactory.createEtchedBorder());

		add(headerPanel_, c.anchor("northwest").down().expandHoriz());
		add(contentPanel_, c.anchor("west").down().expandBoth());
		contentPanel_.setVisible(!collapsed);
	}

	public void setLabel(String label) {
		headerPanel_.setText(label);
	}

	public void addContent(JComponent panel) {
		contentPanel_.add(panel);
	}

	public JPanel getContent() {
		return contentPanel_;
	}

	public void toggleSelection() {
		if (contentPanel_.isShowing()) {
			headerPanel_.setButton(RIGHT_ARROW);
			headerPanel_.expandButton.setToolTipText("Click to expand"); // Update tooltip
			contentPanel_.setVisible(false);
		} else {
			contentPanel_.setVisible(true);
			headerPanel_.setButton(DOWN_ARROW);
			 headerPanel_.expandButton.setToolTipText("Click to collapse"); // Update tooltip
		}

		validate();

		headerPanel_.repaint();
	}

	public void collapse() {
		if (contentPanel_.isShowing())
			toggleSelection();
	}

	public void expand() {
		if (!contentPanel_.isShowing())
			toggleSelection();
	}

}