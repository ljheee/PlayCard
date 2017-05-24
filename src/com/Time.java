package com;

import java.awt.Point;
import java.util.Vector;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Time extends Thread {
	Main main;
	boolean isRun = true;
	int i = 10;

	public Time(Main m, int i) {
		this.main = m;
		this.i = i;
	}

	@Override
	public void run() {

		while (i > -1 && isRun) {
			main.time[1].setText("倒计时:" + i--);
			second(1);// 等一秒
		}
		if (i == -1)// 正常终结，说明超时
			main.time[1].setText("不抢");
		main.landlord[0].setVisible(false);
		main.landlord[1].setVisible(false);
		for (Card card2 : main.playerList[1])
			card2.canClick = true;// 可被点击
		// 如果自己抢到地主
		if (main.time[1].getText().equals("抢地主")) {
			// 得到地主牌
			main.playerList[1].addAll(main.lordList);
			openlord(true);
			second(2);// 等待五秒
			Common.order(main.playerList[1]);
			Common.rePosition(main, main.playerList[1], 1);
			main.publishCard[1].setEnabled(false);
			setlord(1);
		} else {
			// 电脑选地主
			if (Common.getScore(main.playerList[0]) < Common
					.getScore(main.playerList[2])) {
				main.time[2].setText("抢地主");
				main.time[2].setVisible(true);
				setlord(2);// 设定地主
				openlord(true);
				second(3);
				main.playerList[2].addAll(main.lordList);
				Common.order(main.playerList[2]);
				Common.rePosition(main, main.playerList[2], 2);
				openlord(false);

			} else {
				main.time[0].setText("抢地主");
				main.time[0].setVisible(true);
				setlord(0);// 设定地主
				openlord(true);
				second(3);
				main.playerList[0].addAll(main.lordList);
				Common.order(main.playerList[0]);
				Common.rePosition(main, main.playerList[0], 0);
				openlord(false);

			}
		}
		// 选完地主后 关闭地主按钮
		main.landlord[0].setVisible(false);
		main.landlord[1].setVisible(false);
		turnOn(false);
		for (int i = 0; i < 3; i++) {
			main.time[i].setText("不要");
			main.time[i].setVisible(false);
		}
		// 开始游戏 根据地主不同顺序不同
		main.turn = main.dizhuFlag;
		while (true) {

			if (main.turn == 1) // 我
			{
				// 如果我主动出牌关闭不要按钮
				if (main.time[0].getText().equals("不要")
						&& main.time[2].getText().equals("不要"))
					main.publishCard[1].setEnabled(false);
				else {
					main.publishCard[1].setEnabled(true);
				}
				turnOn(true);// 出牌按钮 --我出牌
				timeWait(30, 1);// 我自己的定时器
				turnOn(false);// 选完关闭
				main.turn = (main.turn + 1) % 3;
				if (win())// 判断输赢
					break;
			}
			if (main.turn == 0) {
				computer0();
				main.turn = (main.turn + 1) % 3;
				if (win())// 判断输赢
					break;
			}
			if (main.turn == 2) {
				computer2();
				main.turn = (main.turn + 1) % 3;
				if (win())// 判断输赢
					break;
			}
		}
	}

	// 等待i秒
	public void second(int i) {
		try {
			Thread.sleep(i * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 地主牌翻看
	public void openlord(boolean is) {
		for (int i = 0; i < 3; i++) {
			if (is)
				main.lordList.get(i).turnFront(); // 地主牌翻看
			else {
				main.lordList.get(i).turnRear(); // 地主牌闭合
			}
			main.lordList.get(i).canClick = true;// 可被点击
		}
	}

	// 设定地主
	public void setlord(int i) {
		Point point = new Point();
		if (i == 1)// 我是地主
		{
			point.x = 80;
			point.y = 430;
			main.dizhuFlag = 1;// 设定地主
		}
		if (i == 0) {
			point.x = 80;
			point.y = 20;
			main.dizhuFlag = 0;
		}
		if (i == 2) {
			point.x = 700;
			point.y = 20;
			main.dizhuFlag = 2;
		}
		main.dizhu.setLocation(point);
		main.dizhu.setVisible(true);
	}

	// 打开出牌按钮
	public void turnOn(boolean flag) {
		main.publishCard[0].setVisible(flag);
		main.publishCard[1].setVisible(flag);
	}

	// 电脑0走牌(我代表1)
	public void computer0() {
		timeWait(1, 0); // 定时
		ShowCard(0); // 出牌

	}

	// 电脑2走牌(我代表1)
	public void computer2() {
		timeWait(1, 2); // 定时
		ShowCard(2); // 出牌

	}

	// 走牌
	public void ShowCard(int role) {
		int orders[] = new int[] { 4, 3, 2, 1, 5 };
		Model model = Common.getModel(main.playerList[role], orders);
		// 待走的牌
		List<String> list = new Vector();
		// 如果是主动出牌
		if (main.time[(role + 1) % 3].getText().equals("不要")
				&& main.time[(role + 2) % 3].getText().equals("不要")) {
			// 有顺子出顺子
			if (model.a123.size() > 0) {
				list.add(model.a123.get(model.a123.size() - 1));
			}// 有3带就出3带，没有就出光3
			else if (model.a3.size() > 0) {
				// 3带单,且非关键时刻不能带王，2
				if (model.a1.size() > 0) {
					list.add(model.a1.get(model.a1.size() - 1));
				}// 3带对
				else if (model.a2.size() > 0) {
					list.add(model.a2.get(model.a2.size() - 1));
				}
				list.add(model.a3.get(model.a3.size() - 1));
			}// 有双顺出双顺
			else if (model.a112233.size() > 0) {
				list.add(model.a112233.get(model.a112233.size() - 1));
			}// 有飞机出飞机
			else if (model.a111222.size() > 0) {
				String name[] = model.a111222.get(0).split(",");
				// 带单
				if (name.length / 3 <= model.a1.size()) {
					list.add(model.a111222.get(model.a111222.size() - 1));
					for (int i = 0; i < name.length / 3; i++)
						list.add(model.a1.get(i));
				} else if (name.length / 3 <= model.a2.size())// 带双
				{
					list.add(model.a111222.get(model.a111222.size() - 1));
					for (int i = 0; i < name.length / 3; i++)
						list.add(model.a2.get(i));
				}
				
			}// 有对子出对子 (除开3带，飞机)
			else if (model.a2.size() > (model.a111222.size() * 2 + model.a3
					.size())) {
				list.add(model.a2.get(model.a2.size() - 1));
			}
			// 有单出单 (除开3带，飞机能带的单牌)
			else if(model.a1.size() > (model.a111222.size() * 2 + model.a3.size())) {
				list.add(model.a1.get(model.a1.size() - 1));
			}// 有炸弹出炸弹
			else if (model.a4.size() > 0) {
				// 4带2,1
				int sizea1 = model.a1.size();
				int sizea2 = model.a2.size();
				if (sizea1 >= 2) {
					list.add(model.a1.get(sizea1 - 1));
					list.add(model.a1.get(sizea1 - 2));
					list.add(model.a4.get(0));

				} else if (sizea2 >= 2) {
					list.add(model.a2.get(sizea1 - 1));
					list.add(model.a2.get(sizea1 - 2));
					list.add(model.a4.get(0));

				} else {// 直接炸
					list.add(model.a4.get(0));

				}

			}
		}// 如果是跟牌
		else {
			//如果队友牌大则不出
			
			if(role!=main.dizhuFlag)
			{
				int f=0;
				if(main.time[main.dizhuFlag].getText().equals("不要"))
				{
					f=1;
				}//如果我的下家是地主，且我的上家出的比较大了
				if((role+1)%3==main.dizhuFlag)
				{
					if((Common.jugdeType(main.currentList[(role+2)%3])!=CardType.c1||
							Common.jugdeType(main.currentList[(role+2)%3])!=CardType.c2)
						&&main.currentList[main.dizhuFlag].size()<1)
						f=1;
					if(main.currentList[(role+2)%3].size()>0&&Common.getValue(main.currentList[(role+2)%3].get(0))>13)
						f=1;
				}
				if(f==1){
					main.time[role].setVisible(true);
					main.time[role].setText("不要");
					return;
				}
			}
			// 拆牌打
			int can = 0;
			if (role == main.dizhuFlag)// 如果自己是地主
			{// 当对手牌小于5张的时候炸
				if (main.playerList[(role + 1) % 3].size() <= 5
						|| main.playerList[(role + 2) % 3].size() <= 5)
					can = 1;
			} else {// 当地主牌小于5张的时候炸
				if (main.playerList[main.dizhuFlag].size() <= 5)
					can = 1;
			}
			// 如果can=1就要拆牌model得重新来
			List<Card> player;
			if(main.time[(role + 2) % 3].getText().equals("不要"))
				player=main.currentList[(role + 1) % 3];
			else
				player= main.currentList[(role + 2) % 3];
					

			CardType cType = Common.jugdeType(player);
			// 如果是单牌
			if (cType == CardType.c1) {
				if (can == 1)
					model = Common.getModel(main.playerList[role], new int[] {
							1, 4, 3, 2, 5 });
				AI_1(model.a1, player, list, role);
			}// 如果是对子
			else if (cType == CardType.c2) {
				if (can == 1)
					model = Common.getModel(main.playerList[role], new int[] {
							2, 4, 3, 5, 1 });
				AI_1(model.a2, player, list, role);
			}// 3带
			else if (cType == CardType.c3) {
				AI_1(model.a3, player, list, role);
			}// 炸弹
			else if (cType == CardType.c4) {
				AI_1(model.a4, player, list, role);
			}// 如果是3带1
			else if (cType == CardType.c31) {
				if (can == 1)
					model = Common.getModel(main.playerList[role], new int[] {
							3, 1, 4, 2, 5 });
				AI_2(model.a3, model.a1, player, list, role);
			}// 如果是3带2
			else if (cType == CardType.c32) {
				if (can == 1)
					model = Common.getModel(main.playerList[role], new int[] {
							3, 2, 4, 5, 1 });
				AI_2(model.a3, model.a2, player, list, role);
			}// 如果是4带11
			else if (cType == CardType.c411) {
				AI_5(model.a4, model.a1, player, list, role);
			}
			// 如果是4带22
			else if (cType == CardType.c422) {
				AI_5(model.a4, model.a2, player, list, role);
			}
			// 顺子
			else if (cType == CardType.c123) {
				if (can == 1)
					model = Common.getModel(main.playerList[role], new int[] {
							5, 3, 2, 4, 1 });
				AI_3(model.a123, player, list, role);
			}
			// 双顺
			else if (cType == CardType.c1122) {
				if (can == 1)
					model = Common.getModel(main.playerList[role], new int[] {
							2, 4, 3, 5, 1 });
				AI_3(model.a112233, player, list, role);
			}
			// 飞机带单
			else if (cType == CardType.c11122234) {
				AI_4(model.a111222, model.a1, player, list, role);
			}
			// 飞机带对
			else if (cType == CardType.c1112223344) {
				AI_4(model.a111222, model.a2, player, list, role);
			}
			if (list.size()==0&&can == 1) {
				int len4 = model.a4.size();
				if (len4 > 0)
					list.add(model.a4.get(len4 - 1));
			}
			
		}

		// 定位出牌
		main.currentList[role].clear();
		if (list.size() > 0) {
			Point point = new Point();
			if (role == 0)
				point.x = 200;
			if (role == 2)
				point.x = 550;
			if (role == 1) {
				point.x = (770 / 2) - (main.currentList[1].size() + 1) * 15 / 2;
				point.y = 300;
			}
			point.y = (400 / 2) - (list.size() + 1) * 15 / 2;// 屏幕中部
			// 将name转换成Card
			//把要出的牌放到里面
			List<Card> temp=new Vector<Card>();
			for (int i = 0, len = list.size(); i < len; i++) {
				List<Card> cards = getCardByName(main.playerList[role],
						list.get(i));
				for (Card card : cards) {
					temp.add(card);
				}
			}
			temp=Common.getOrder2(temp);
			for (Card card : temp) {
				Common.move(card, card.getLocation(), point);
				point.y += 15;
				main.container.setComponentZOrder(card, 0);
				main.currentList[role].add(card);
				main.playerList[role].remove(card);
			}
			Common.rePosition(main, main.playerList[role], role);
		} else {
			main.time[role].setVisible(true);
			main.time[role].setText("不要");
		}
		for (Card card : main.currentList[role])
			card.turnFront();
	}

	// 按name获得Card，方便从Model取出
	public List getCardByName(List<Card> list, String n) {
		String[] name = n.split(",");
		List cardsList = new Vector<Card>();
		int j = 0;
		for (int i = 0, len = list.size(); i < len; i++) {
			if (j < name.length && list.get(i).name.equals(name[j])) {
				cardsList.add(list.get(i));
				i = 0;
				j++;
			}
		}
		return cardsList;
	}

	// 顺子
	public void AI_3(List<String> model, List<Card> player, List<String> list,
			int role) {

		for (int i = 0, len = model.size(); i < len; i++) {
			String[] s = model.get(i).split(",");
			if (s.length == player.size()
					&& getValueInt(model.get(i)) > Common.getValue(player
							.get(0))) {
				list.add(model.get(i));
				return;
			}
		}
	}

	// 飞机带单，双
	public void AI_4(List<String> model1, List<String> model2,
			List<Card> player, List<String> list, int role) {
		// 排序按重复数
		player = Common.getOrder2(player);
		int len1 = model1.size();
		int len2 = model2.size();

		if (len1 < 1 || len2 < 1)
			return;
		for (int i = 0; i < len1; i++) {
			String[] s = model1.get(i).split(",");
			String[] s2 = model2.get(0).split(",");
			if ((s.length / 3 <= len2)
					&& (s.length * (3 + s2.length) == player.size())
					&& getValueInt(model1.get(i)) > Common.getValue(player
							.get(0))) {
				list.add(model1.get(i));
				for (int j = 1; j <= s.length / 3; j++)
					list.add(model2.get(len2 - j));
			}
		}
	}

	// 4带1，2
	public void AI_5(List<String> model1, List<String> model2,
			List<Card> player, List<String> list, int role) {
		// 排序按重复数
		player = Common.getOrder2(player);
		int len1 = model1.size();
		int len2 = model2.size();

		if (len1 < 1 || len2 < 2)
			return;
		for (int i = 0; i < len1; i++) {
			if (getValueInt(model1.get(i)) > Common.getValue(player.get(0))) {
				list.add(model1.get(i));
				for (int j = 1; j <= 2; j++)
					list.add(model2.get(len2 - j));
			}
		}
	}

	// 单牌，对子，3个，4个,通用
	public void AI_1(List<String> model, List<Card> player, List<String> list,
			int role) {

		for (int len = model.size(), i = len - 1; i >= 0; i--) {
			if (getValueInt(model.get(i)) > Common.getValue(player.get(0))) {
				list.add(model.get(i));
				break;
			}
		}

	}

	// 3带1,2,4带1,2
	public void AI_2(List<String> model1, List<String> model2,
			List<Card> player, List<String> list, int role) {
		// model1是主牌,model2是带牌,player是玩家出的牌,,list是准备回的牌
		// 排序按重复数
		player = Common.getOrder2(player);
		int len1 = model1.size();
		int len2 = model2.size();
		// 如果有王直接炸了
		if (len1 > 0 && model1.get(0).length() < 10) {
			list.add(model1.get(0));
			System.out.println("王炸");
			return;
		}
		if (len1 < 1 || len2 < 1)
			return;
		for (int len = len1, i = len - 1; i >= 0; i--) {
			if (getValueInt(model1.get(i)) > Common.getValue(player.get(0))) {
				list.add(model1.get(i));
				break;
			}
		}
		list.add(model2.get(len2 - 1));
		if (list.size() < 2)
			list.clear();
	}

	// 延时，模拟时钟
	public void timeWait(int n, int player) {

		if (main.currentList[player].size() > 0)
			Common.hideCards(main.currentList[player]);
		if (player == 1)// 如果是我，10秒到后直接下一家出牌
		{
			int i = n;

			while (main.nextPlayer == false && i >= 0) {
				// main.container.setComponentZOrder(main.time[player], 0);
				main.time[player].setText("倒计时:" + i);
				main.time[player].setVisible(true);
				second(1);
				i--;
			}
			if (i == -1 && player == 1) {
				// 如果我超时
				ShowCard(1);
			}
			main.nextPlayer = false;
		} else {
			for (int i = n; i >= 0; i--) {
				second(1);
				// main.container.setComponentZOrder(main.time[player], 0);
				main.time[player].setText("倒计时:" + i);
				main.time[player].setVisible(true);
			}
		}
		main.time[player].setVisible(false);
	}

	// 通过name估值
	public int getValueInt(String n) {
		String name[] = n.split(",");
		String s = name[0];
		int i = Integer.parseInt(s.substring(2, s.length()));
		if (s.substring(0, 1).equals("5"))
			i += 3;
		if (s.substring(2, s.length()).equals("1")
				|| s.substring(2, s.length()).equals("2"))
			i += 13;
		return i;
	}

	/**
	 * 判断输赢
	 * @return
	 */
	public boolean win() {
		for (int i = 0; i < 3; i++) {
			if (main.playerList[i].size() == 0) {
				String s;
				if (i == 1) {
					s = "恭喜你，胜利了!";
				} else {
					s = "恭喜电脑" + i + ",赢了! 你的智商有待提高哦";
				}
				for (int j = 0; j < main.playerList[(i + 1) % 3].size(); j++)
					main.playerList[(i + 1) % 3].get(j).turnFront();
				for (int j = 0; j < main.playerList[(i + 2) % 3].size(); j++)
					main.playerList[(i + 2) % 3].get(j).turnFront();
				JOptionPane.showMessageDialog(main, s);
				return true;
			}
		}
		return false;
	}
}
