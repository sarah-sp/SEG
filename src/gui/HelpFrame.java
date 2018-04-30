package gui;

import java.awt.Container;
import java.io.File;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class HelpFrame extends JFrame {
	
	private String panelName;

	public HelpFrame(String panelName) {
		super("Help for " + panelName + " section");
		this.panelName = panelName;
	}
	
	public void init(){
		
		this.setSize(500, 700);
		
		Container c = this.getContentPane();
		
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		editorPane.setEditable(false);
		editorPane.setEnabled(true);
		
		editorPane.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {

					editorPane.scrollToReference(e.getURL().getRef());
			        
			    }
			}
		});
		
		try {
			File file = new File("html/"+panelName+".html");
			editorPane.setPage(file.toURI().toURL());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JScrollPane scroll = new JScrollPane(editorPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		c.add(scroll);
		
		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}


}
