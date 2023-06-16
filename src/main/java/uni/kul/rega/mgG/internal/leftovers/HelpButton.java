package uni.kul.rega.mgG.internal.leftovers;
//package uni.kul.rega.mgG.internal.view;
//
//import java.awt.Font;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.swing.JButton;
//
//import uni.kul.rega.mgG.internal.model.ScNVManager;
//
//public class HelpButton extends JButton {
//	final ScNVManager scManager;
//	final String subPart;
//
//	public HelpButton(final ScNVManager scManager, String subPart)
//	{
//		super("Help");
//		this.scManager = scManager;
//		this.subPart = subPart;
//
//		setFont(new Font("SansSerif", Font.PLAIN, 10));
//		addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				Map<String, Object> args = new HashMap<>();
//				args.put("id","mgG");
//				args.put("title", "mgG Help");
//				if (subPart != null) {
//					args.put("url", "https://github.com/ermismd/MGG/tree/MGG#"+subPart);
//				} else {
//					args.put("url", "https://github.com/ermismd/MGG/tree/MGG");
//				}
//				scManager.executeCommand("cybrowser", "dialog", args, false);
//			}
//		});
//	}
//}
