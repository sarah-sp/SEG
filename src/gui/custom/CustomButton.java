package gui.custom;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.border.LineBorder;

import gui.BorderInterface;
import model.Theme;


public class CustomButton extends JButton {

	private Color hoverColor, bg;
	private boolean change;
	
	public CustomButton(String text) {
		
		hoverColor = Theme.ACTIVE_HOVER;
		bg = Theme.ACTIVE_BG;
		
		this.setText(text);
		this.setBackground(bg);
		this.setFocusPainted(false);
		this.setFont(new Font("Courier", Font.BOLD, 30));

		this.setOpaque(true);
		this.setBorder(new LineBorder(new Color(220,220,220), 1));
		
		this.setPreferredSize(new Dimension(400,75));

		
		this.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				if(isEnabled()){
					setBackground(hoverColor);	
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if(isEnabled()){
					setBackground(bg);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if(isEnabled()) {
					setBackground(hoverColor);
				}
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setBackground(bg);
			}
			
		});
		
	}
	
	public boolean getChange(){
		return change;
	}
	
	public void changeHover(Color bg, Color hoverColor) {
		this.bg = bg;
		this.hoverColor = hoverColor;
		this.setBorder(new LineBorder(hoverColor,1));
	}
	
	public void setFontSize(int size){
		this.setFont(new Font("Courier", Font.BOLD, size));
	}
	
	public void refreshBorder(Color c){
		this.setBorder(new LineBorder(c, 1));
	}
	
	


}
