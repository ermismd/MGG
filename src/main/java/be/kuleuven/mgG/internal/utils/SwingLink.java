package be.kuleuven.mgG.internal.utils;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;

import javax.swing.JLabel;

import org.cytoscape.util.swing.OpenBrowser;




public class SwingLink extends JLabel {
  private static final long serialVersionUID = 8273875024682878518L;
  private String text;
  private URI uri;
  private final OpenBrowser openBrowser;
 

  public SwingLink(final String text, final URI uri, final OpenBrowser openBrowser){
    super();
		this.openBrowser = openBrowser;
    setup(text,uri);
  }

  public SwingLink(String text, String uri, final OpenBrowser openBrowser){
    super();
		this.openBrowser = openBrowser;
    setup(text,URI.create(uri));
  }

  public void setup(String t, URI u){
    text = t;
    uri = u;
    setText(text, true);
    setToolTipText(uri.toString());
    addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        open1(uri);
        //open(uri);
      }
      public void mouseEntered(MouseEvent e) {
        // setText(text,false);
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }
      public void mouseExited(MouseEvent e) {
        // setText(text,true);
				setCursor(Cursor.getDefaultCursor());
      }
    });
  }

  @Override
  public void setText(String text){
    setText(text,true);
  }

  public void setText(String text, boolean ul){
    String link = ul ? "<u>"+text+"</u>" : text;
    super.setText("<html><span style=\"color: #000099;\">"+
    link+"</span></html>");
    this.text = text;
  }

  
  public URI getURI() {
	    return uri;
	}
  
  public String getRawText(){
    return text;
  }

  public void open1(URI uri) {
	    if (Desktop.isDesktopSupported()) {
	        Desktop desktop = Desktop.getDesktop();
	        if (desktop.isSupported(Desktop.Action.BROWSE)) {
	            try {
	                desktop.browse(uri);
	            } catch (IOException e) {
	                e.printStackTrace(); 
	            }
	        }
	    }
	}
  
 public void open(URI uri) {
		openBrowser.openURL(uri.toString());
		
  }
}