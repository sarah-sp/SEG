package gui.custom;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JRadioButton;

import model.Theme;

/**
 * arrow and question mark buttons
 * @author Sarah
 *
 */
public class PictureButton extends JRadioButton {
	
	private BufferedImage staticImage;
	private BufferedImage hoverImage;
	private ImageIcon staticIcon, hoverIcon;
	private String name;

	public PictureButton(String name, String tooltip){
		
		this.name = name;
		staticImage = null;
		hoverImage = null;
		
		try {
			staticImage = ImageIO.read(new File("img/" + name + "_def.png"));
			hoverImage = ImageIO.read(new File("img/" + name + "_def_hover.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		staticIcon = new ImageIcon(staticImage);
		hoverIcon = new ImageIcon(hoverImage);
		
		
		this.setIcon(staticIcon);
		this.setBorderPainted(false);
		this.setToolTipText(tooltip);
		
		this.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				if(hoverIcon != null){
					setIcon(hoverIcon);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				setIcon(staticIcon);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if(hoverIcon != null){
					setIcon(hoverIcon);
				} 	
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setIcon(staticIcon);
			}
			
		});
	
	}
	
	public void changeTheme(Color fg, Color hover) throws IOException{
		
		if(fg.equals(Color.GREEN) && hover.equals(Color.BLUE)){
			
			changeImage("pro");		 
		} else if (fg.equals(Theme.HUSKY_FG) && hover.equals(Theme.HUSKY_HOVER)){
			changeImage("husky");
			
		} else if (fg.equals(Theme.SOFT_FG) && hover.equals(Theme.SOFT_HOVER)){
			changeImage("soft");
			
		} else if(fg.equals(Color.BLACK) && hover.equals(Color.LIGHT_GRAY)){
			
			changeImage("def");
		} else {
			
			staticIcon.setImage(changeColor(staticImage, fg));
			hoverIcon.setImage(changeColor(hoverImage, hover));
		}
	}
	
	public void changeImage(String theme) throws IOException{
		
		staticImage = ImageIO.read(new File("img/" + name + "_" + theme +".png"));
		hoverImage = ImageIO.read(new File("img/" + name + "_" + theme +"_hover.png"));
		
		staticIcon.setImage(staticImage);
		hoverIcon.setImage(hoverImage);
	}
	
	/*
	 * changing coloring of the buffered image
	 */
	public BufferedImage changeColor(BufferedImage image, Color color){
		for(int x = 0; x < image.getWidth(); x++) {
			for(int y = 0; y < image.getHeight(); y++) {
				
				int alpha = (image.getRGB(x, y) >>24)&0xFF;
				if(alpha > 20) {
					image.setRGB(x, y, color.getRGB());
				}
			}
		}
		return image;
	}
	
}
