package com;

import java.awt.Color;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Main extends JFrame implements ActionListener {

	public Container container = null;// 定义容器
	JMenuItem start, exit, about;// 定义菜单按钮
	JButton landlord[] = new JButton[2];// 抢地主按钮
	JButton publishCard[] = new JButton[2];// 出牌按钮
	int dizhuFlag;// 地主标志
	int turn;
	JLabel dizhu; // 地主图标
	List<Card> currentList[] = new Vector[3]; // 当前的出牌
	List<Card> playerList[] = new Vector[3]; // 3个玩家(列表)
	
	List<Card> lordList;// 地主牌
	
	Card card[] = new Card[56]; // 定义54张牌
	JTextField time[] = new JTextField[3]; // 计时器
	Time t; // 定时器（线程）
	boolean nextPlayer = false; // 转换角色

	public Main() {

		Init();// 初始化窗体
		SetMenu();// 创建菜单 按钮(抢地主，发牌,计时器)
		this.setVisible(true);
		
		CardInit();// 发牌
		getLord(); // 发完牌开始抢地主
		time[1].setVisible(true);
		// 线程安全性,把非主线程的UI控制放到里面
		t = new Time(this, 10);// 从10开始倒计时
		t.start();
	}

	/**
	 * 初始化窗体
	 */
	public void Init() {

		this.setTitle("斗地主游戏---by 小柒,qq361106306");
		this.setSize(830, 620);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setLocationRelativeTo(getOwner()); // 屏幕居中

		container = this.getContentPane();
		container.setLayout(null);
		container.setBackground(new Color(0, 112, 26)); // 背景为绿色
	}

	/**
	 * 创建菜单 功能按钮
	 */
	public void SetMenu() {
		JMenuBar jMenuBar = new JMenuBar();
		JMenu game = new JMenu("游戏");
		JMenu help = new JMenu("帮助");

		start = new JMenuItem("新游戏");
		exit = new JMenuItem("退出");
		about = new JMenuItem("关于");

		start.addActionListener(this);
		exit.addActionListener(this);
		about.addActionListener(this);

		game.add(start);
		game.add(exit);
		help.add(about);

		jMenuBar.add(game);
		jMenuBar.add(help);
		this.setJMenuBar(jMenuBar);

		landlord[0] = new JButton("抢地主");
		landlord[1] = new JButton("不     抢");
		publishCard[0] = new JButton("出牌");
		publishCard[1] = new JButton("不要");
		for (int i = 0; i < 2; i++) {
			publishCard[i].setBounds(320 + i * 100, 400, 60, 20);
			landlord[i].setBounds(320 + i * 100, 400, 75, 20);
			container.add(landlord[i]);
			landlord[i].addActionListener(this);
			landlord[i].setVisible(false);
			container.add(publishCard[i]);
			publishCard[i].setVisible(false);
			publishCard[i].addActionListener(this);
		}
		for (int i = 0; i < 3; i++) {
			time[i] = new JTextField("倒计时:");
			time[i].setEditable(false);
			time[i].setVisible(false);
			container.add(time[i]);
		}
		time[0].setBounds(140, 230, 60, 20);
		time[1].setBounds(374, 360, 60, 20);
		time[2].setBounds(620, 230, 60, 20);

		for (int i = 0; i < 3; i++) {
			currentList[i] = new Vector<Card>();
		}
	}

	/**
	 * 洗牌发牌
	 */
	public void CardInit() {

		int count = 1;
		// 初始化牌
		for (int i = 1; i <= 5; i++) {
			for (int j = 1; j <= 13; j++) {
				if ((i == 5) && (j > 2))
					break;
				else {
					card[count] = new Card(this, i + "-" + j, false);
					card[count].setLocation(350, 50);
					container.add(card[count]);
					count++;
				}
			}
		}
		
		Random random = new Random();
		// 打乱顺序
		for (int i = 0; i < 100; i++) {
			int a = random.nextInt(54) + 1;
			int b = random.nextInt(54) + 1;
			
			Card k = card[a];//2牌互换
			card[a] = card[b];
			card[b] = k;
		}
		
		// 开始发牌
		for (int i = 0; i < 3; i++)
			playerList[i] = new Vector<Card>(); // 玩家牌
		
		lordList = new Vector<Card>();// 地主牌三张
		int t = 0;
		for (int i = 1; i <= 54; i++) {
			if (i >= 52){ // 地主牌
				Common.move(card[i], card[i].getLocation(), new Point(300 + (i - 52) * 80, 10));
				lordList.add(card[i]);
				continue;
			}
			
			switch ((t++) % 3) {
			case 0:
				// 左边玩家
				Common.move(card[i], card[i].getLocation(), new Point(50, 60 + i * 5));
				playerList[0].add(card[i]);
				break;
			case 1:
				// 我
				Common.move(card[i], card[i].getLocation(), new Point(180 + i * 7, 450));
				playerList[1].add(card[i]);
				card[i].turnFront(); // 显示正面
				break;
			case 2:
				// 右边玩家
				Common.move(card[i], card[i].getLocation(), new Point(700, 60 + i * 5));
				playerList[2].add(card[i]);
				break;
			}
			// card[i].turnFront(); //显示正面
			container.setComponentZOrder(card[i], 0);
		}
		// 发完牌排序，从大到小
		for (int i = 0; i < 3; i++) {
			Common.order(playerList[i]);
			Common.rePosition(this, playerList[i], i);// 重新定位
		}
		dizhu = new JLabel(new ImageIcon("images/dizhu.gif"));
		dizhu.setVisible(false);
		dizhu.setSize(40, 40);
		container.add(dizhu);
	}

	/**
	 * 抢地主
	 */
	public void getLord() {
		for (int i = 0; i < 2; i++)
			landlord[i].setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == exit) {
			this.dispose();
			System.exit(0);
		}
		if (e.getSource() == about) {
			JOptionPane.showMessageDialog(this, "QQ361106306,小柒");
		}
		if (e.getSource() == start) {
//			 this.restart();
			this.dispose();
			new Main();
		}
		if (e.getSource() == landlord[0]) {
			time[1].setText("抢地主");
			t.isRun = false; // 时钟终结
		}
		if (e.getSource() == landlord[1]) {
			time[1].setText("不抢");
			t.isRun = false; // 时钟终结
		}
		// 如果是不要
		if (e.getSource() == publishCard[1]) {
			this.nextPlayer = true;
			currentList[1].clear();
			time[1].setText("不要");
		}
		// 如果是出牌按钮
		if (e.getSource() == publishCard[0]) {
			List<Card> c = new Vector<Card>();
			// 点选出牌
			for (int i = 0; i < playerList[1].size(); i++) {
				Card card = playerList[1].get(i);
				if (card.clicked) {
					c.add(card);
				}
			}
			int flag = 0;

			// 如果我主动出牌
			if (time[0].getText().equals("不要") && time[2].getText().equals("不要")) {
				if (Common.jugdeType(c) != CardType.c0)
					flag = 1;// 表示可以出牌
			} else {// 如果我跟牌
				flag = Common.checkCards(c, currentList, this);
			}
			
			// 判断是否符合出牌
			if (flag == 1) {
				currentList[1] = c;
				playerList[1].removeAll(currentList[1]);// 移除走的牌
				// 定位出牌
				Point point = new Point();
				point.x = (770 / 2) - (currentList[1].size() + 1) * 15 / 2;
				point.y = 300;
				for (int i = 0, len = currentList[1].size(); i < len; i++) {
					Card card = currentList[1].get(i);
					Common.move(card, card.getLocation(), point);
					point.x += 15;
				}
				
				// 抽完牌后重新整理牌
				Common.rePosition(this, playerList[1], 1);
				time[1].setVisible(false);
				this.nextPlayer = true;
			}

		}
	}

	public static void main(String args[]) {
		new Main();
	}

}
