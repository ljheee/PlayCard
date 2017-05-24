package com;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Card extends JLabel implements MouseListener{

	Main main;//Main类的引用
	String name;//图片url名字
	boolean up;//是否正反面
	boolean canClick=false;//是否可被点击
	boolean clicked=false;//是否点击过
	
	public Card(Main m,String name,boolean up){
		this.main=m;
		this.name=name;
		this.up=up;
	    if(this.up)
	    	this.turnFront();
	    else {
			this.turnRear();
		}
		this.setSize(71, 96);
		this.setVisible(true);
		this.addMouseListener(this);
	}
	/**
	 * 正面
	 */
	public void turnFront() {
		this.setIcon(new ImageIcon("images/" + name + ".gif"));
		this.up = true;
	}
	
	/**
	 * 反面
	 */
	public void turnRear() {
		this.setIcon(new ImageIcon("images/rear.gif"));
		this.up = false;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	public void mousePressed(MouseEvent e) {
		if(canClick){
			Point from=this.getLocation();
			int step; //移动的距离
			if(clicked)
				step=-20;
			else {
				step=20;
			}
			clicked=!clicked; //反向
			//当被选中的时候，向前移动一步/后退一步
			Common.move(this,from,new Point(from.x,from.y-step));
		}
	}
	

}
