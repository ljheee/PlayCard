package com;

import java.awt.Point;
import java.util.Vector;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Delayed;

public class Common {

	/**
	 * 判断牌型
	 * @param list
	 * @return
	 */
	public static CardType jugdeType(List<Card> list) {
		// 因为之前排序过所以比较好判断
		int len = list.size();
		// 单牌,对子，3不带，4个一样炸弹
		if (len <= 4) { // 如果第一个和最后个相同，说明全部相同
			if (list.size() > 0
					&& Common.getValue(list.get(0)) == Common.getValue(list
							.get(len - 1))) {
				switch (len) {
				case 1:
					return CardType.c1;
				case 2:
					return CardType.c2;
				case 3:
					return CardType.c3;
				case 4:
					return CardType.c4;
				}
			}
			// 双王,化为对子返回
			if (len == 2 && Common.getColor(list.get(1)) == 5)
				return CardType.c2;
			// 当第一个和最后个不同时,3带1
			if (len == 4
					&& ((Common.getValue(list.get(0)) == Common.getValue(list
							.get(len - 2))) || Common.getValue(list.get(1)) == Common
							.getValue(list.get(len - 1))))
				return CardType.c31;
			else {
				return CardType.c0;
			}
		}
		// 当5张以上时，连字，3带2，飞机，2顺，4带2等等
		if (len >= 5) {// 现在按相同数字最大出现次数
			Card_index card_index = new Card_index();
			for (int i = 0; i < 4; i++)
				card_index.a[i] = new Vector<Integer>();
			// 求出各种数字出现频率
			Common.getMax(card_index, list); // a[0,1,2,3]分别表示重复1,2,3,4次的牌
			// 3带2 -----必含重复3次的牌
			if (card_index.a[2].size() == 1 && card_index.a[1].size() == 1
					&& len == 5)
				return CardType.c32;
			// 4带2(单,双)
			if (card_index.a[3].size() == 1 && len == 6)
				return CardType.c411;
			if (card_index.a[3].size() == 1 && card_index.a[1].size() == 2
					&& len == 8)
				return CardType.c422;
			// 单连,保证不存在王
			if ((Common.getColor(list.get(0)) != 5)
					&& (card_index.a[0].size() == len)
					&& (Common.getValue(list.get(0))
							- Common.getValue(list.get(len - 1)) == len - 1))
				return CardType.c123;
			// 连队
			if (card_index.a[1].size() == len / 2
					&& len % 2 == 0
					&& len / 2 >= 3
					&& (Common.getValue(list.get(0))
							- Common.getValue(list.get(len - 1)) == (len / 2 - 1)))
				return CardType.c1122;
			// 飞机
			if (card_index.a[2].size() == len / 3
					&& (len % 3 == 0)
					&& (Common.getValue(list.get(0))
							- Common.getValue(list.get(len - 1)) == (len / 3 - 1)))
				return CardType.c111222;
			// 飞机带n单,n/2对
			if (card_index.a[2].size() == len / 4
					&& ((Integer) (card_index.a[2].get(len / 4 - 1))
							- (Integer) (card_index.a[2].get(0)) == len / 4 - 1))
				return CardType.c11122234;

			// 飞机带n双
			if (card_index.a[2].size() == len / 5
					&& card_index.a[2].size() == len / 5
					&& ((Integer) (card_index.a[2].get(len / 5 - 1))
							- (Integer) (card_index.a[2].get(0)) == len / 5 - 1))
				return CardType.c1112223344;

		}
		return CardType.c0;
	}

	/**
	 * 移动效果的函数,用于发牌
	 * @param card
	 * @param from
	 * @param to
	 */
	public static void move(Card card, Point from, Point to) {
		if (to.x != from.x) {
			double k = (1.0) * (to.y - from.y) / (to.x - from.x);
			double b = to.y - to.x * k;
			int flag = 0;// 判断向左还是向右移动步幅
			if (from.x < to.x)
				flag = 20;
			else {
				flag = -20;
			}
			for (int i = from.x; Math.abs(i - to.x) > 20; i += flag) {
				double y = k * i + b;// 这里主要用的数学中的线性函数

				card.setLocation(i, (int) y);
				try {
					Thread.sleep(5); // 延迟，可自己设置
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		// 位置校准
		card.setLocation(to);
	}

	/**
	 * 对玩家手中的牌 list排序
	 * @param list
	 */
	public static void order(List<Card> list) {
		Collections.sort(list, new Comparator<Card>() {
			@Override
			public int compare(Card o1, Card o2) {
				// TODO Auto-generated method stub
				int a1 = Integer.parseInt(o1.name.substring(0, 1));// 花色
				int a2 = Integer.parseInt(o2.name.substring(0, 1));
				int b1 = Integer.parseInt(o1.name.substring(2, o1.name.length()));// 数值
				int b2 = Integer.parseInt(o2.name.substring(2, o2.name.length()));
				int flag = 0;
				// 如果是王的话
				if (a1 == 5)
					b1 += 100;
				if (a1 == 5 && b1 == 1)
					b1 += 50;
				if (a2 == 5)
					b2 += 100;
				if (a2 == 5 && b2 == 1)
					b2 += 50;
				// 如果是A或者2
				if (b1 == 1)
					b1 += 20;
				if (b2 == 1)
					b2 += 20;
				if (b1 == 2)
					b1 += 30;
				if (b2 == 2)
					b2 += 30;
				flag = b2 - b1;
				if (flag == 0)
					return a2 - a1;
				else {
					return flag;
				}
			}
		});
	}

	/**
	 * 重新定位 
	 * flag代表电脑1 ,2 或者是我
	 * @param m
	 * @param list
	 * @param flag
	 */
	public static void rePosition(Main m, List<Card> list, int flag) {
		Point p = new Point();
		if (flag == 0) {
			p.x = 50;
			p.y = (450 / 2) - (list.size() + 1) * 15 / 2;
		}
		if (flag == 1) {// 我的排序 _y=450 width=830
			p.x = (800 / 2) - (list.size() + 1) * 21 / 2;
			p.y = 450;
		}
		if (flag == 2) {
			p.x = 700;
			p.y = (450 / 2) - (list.size() + 1) * 15 / 2;
		}
		int len = list.size();
		for (int i = 0; i < len; i++) {
			Card card = list.get(i);
			Common.move(card, card.getLocation(), p);
			m.container.setComponentZOrder(card, 0);
			if (flag == 1)
				p.x += 21;
			else
				p.y += 15;
		}
	}

	/**
	 * 地主牌权值，看[电脑玩家]是否抢地主
	 * @param list
	 * @return
	 */
	public static int getScore(List<Card> list) {
		int count = 0;
		for (int i = 0, len = list.size(); i < len; i++) {
			Card card = list.get(i);
			if (card.name.substring(0, 1).equals("5")) {
				// System.out.println(card.name.substring(0, 1));
				count += 5;
			}
			if (card.name.substring(2, card.name.length()).equals("2")) {
				// System.out.println(2);
				count += 2;
			}
		}
		return count;
	}

	/**
	 * 返回花色
	 * @param card
	 * @return
	 */
	public static int getColor(Card card) {
		return Integer.parseInt(card.name.substring(0, 1));
	}

	// 返回值
	public static int getValue(Card card) {
		int i = Integer.parseInt(card.name.substring(2, card.name.length()));
		if (card.name.substring(2, card.name.length()).equals("2"))
			i += 13;
		if (card.name.substring(2, card.name.length()).equals("1"))
			i += 13;
		if (Common.getColor(card) == 5)
			i += 2;// 是王
		return i;
	}

	// 得到最大相同数
	public static void getMax(Card_index card_index, List<Card> list) {
		int count[] = new int[14];// 1-13各算一种,王算第14种
		for (int i = 0; i < 14; i++)
			count[i] = 0;
		
		for (int i = 0, len = list.size(); i < len; i++) {
			if (Common.getColor(list.get(i)) == 5)// 王
				count[13]++;
			else
				count[Common.getValue(list.get(i)) - 1]++;
		}
		for (int i = 0; i < 14; i++) {
			switch (count[i]) {
			case 1:
				card_index.a[0].add(i + 1);
				break;
			case 2:
				card_index.a[1].add(i + 1);
				break;
			case 3:
				card_index.a[2].add(i + 1);
				break;
			case 4:
				card_index.a[3].add(i + 1);
				break;
			}
		}
	}

	// 拆牌
	public static Model getModel(List<Card> list,int[] orders) {
		// 先复制一个list
		List list2 = new Vector<Card>(list);
		Model model = new Model();
		for(int i=0;i<orders.length;i++)
			showOrders(orders[i], list2, model);
		return model;
	}

	// 拆连子
	public static void get123(List<Card> list, Model model) {
		List<Card> del = new Vector<Card>();// 要删除的Cards
		if (list.size() > 0
				&& (Common.getValue(list.get(0)) < 7 || Common.getValue(list
						.get(list.size() - 1)) > 10))
			return;
		if (list.size() < 5)
			return;
		// 先要把所有不重复的牌归为一类，防止3带，对子影响
		List<Card> list2 = new Vector<Card>();
		List<Card> temp = new Vector<Card>();
		List<Integer> integers = new Vector<Integer>();
		for (Card card : list2) {
			if (integers.indexOf(Common.getValue(card)) < 0) {
				integers.add(Common.getValue(card));
				temp.add(card);
			}
		}
		Common.order(temp);
		for (int i = 0, len = temp.size(); i < len; i++) {
			int k = i;
			for (int j = i; j < len; j++) {
				if (Common.getValue(temp.get(i)) - Common.getValue(temp.get(j)) == j
						- i) {
					k = j;
				}
			}
			if (k - i >= 4) {
				String s = "";
				for (int j = i; j < k; j++) {
					s += temp.get(j).name + ",";
					del.add(temp.get(j));
				}
				s += temp.get(k).name;
				del.add(temp.get(k));
				model.a123.add(s);
				i = k;
			}
		}
		list.removeAll(del);
	}

	// 拆双顺
	public static void getTwoTwo(List<Card> list, Model model) {
		List<String> del = new Vector<String>();// 要删除的Cards
		// 从model里面的对子找
		List<String> l = model.a2;
		if (l.size() < 3)
			return;
		Integer s[] = new Integer[l.size()];
		for (int i = 0, len = l.size(); i < len; i++) {
			String[] name = l.get(i).split(",");
			s[i] = Integer.parseInt(name[0].substring(2, name[0].length()));
		}
		// s0,1,2,3,4 13,9,8,7,6
		for (int i = 0, len = l.size(); i < len; i++) {
			int k = i;
			for (int j = i; j < len; j++) {
				if (s[i] - s[j] == j - i)
					k = j;
			}
			if (k - i >= 2)// k=4 i=1
			{// 说明从i到k是连队
				String ss = "";
				for (int j = i; j < k; j++) {
					ss += l.get(j) + ",";
					del.add(l.get(j));
				}
				ss += l.get(k);
				model.a112233.add(ss);
				del.add(l.get(k));
				i = k;
			}
		}
		l.removeAll(del);
	}

	// 拆飞机
	public static void getPlane(List<Card> list, Model model) {
		List<String> del = new Vector<String>();// 要删除的Cards
		// 从model里面的3带找
		List<String> l = model.a3;
		if (l.size() < 2)
			return;
		Integer s[] = new Integer[l.size()];
		for (int i = 0, len = l.size(); i < len; i++) {
			String[] name = l.get(i).split(",");
			s[i] = Integer.parseInt(name[0].substring(2, name[0].length()));
		}
		for (int i = 0, len = l.size(); i < len; i++) {
			int k = i;
			for (int j = i; j < len; j++) {
				if (s[i] - s[j] == j - i)
					k = j;
			}
			if (k != i) {// 说明从i到k是飞机
				String ss = "";
				for (int j = i; j < k; j++) {
					ss += l.get(j) + ",";
					del.add(l.get(j));
				}
				ss += l.get(k);
				model.a111222.add(ss);
				del.add(l.get(k));
				i = k;
			}
		}
		l.removeAll(del);
	}

	// 拆炸弹
	public static void getBoomb(List<Card> list, Model model) {
		List<Card> del = new Vector<Card>();// 要删除的Cards
		if(list.size()<1)
			return;
		// 王炸
		if (list.size() >= 2 && Common.getColor(list.get(0)) == 5
				&& Common.getColor(list.get(1)) == 5) {
			model.a4.add(list.get(0).name + "," + list.get(1).name); // 按名字加入
			del.add(list.get(0));
			del.add(list.get(1));
		}
		// 如果王不构成炸弹咋先拆单
		if (Common.getColor(list.get(0)) == 5
				&& Common.getColor(list.get(1)) != 5) {
			del.add(list.get(0));
			model.a1.add(list.get(0).name);
		}
		list.removeAll(del);
		// 一般的炸弹
		for (int i = 0, len = list.size(); i < len; i++) {
			if (i + 3 < len
					&& Common.getValue(list.get(i)) == Common.getValue(list
							.get(i + 3))) {
				String s = list.get(i).name + ",";
				s += list.get(i + 1).name + ",";
				s += list.get(i + 2).name + ",";
				s += list.get(i + 3).name;
				model.a4.add(s);
				for (int j = i; j <= i + 3; j++)
					del.add(list.get(j));
				i = i + 3;
			}
		}
		list.removeAll(del);
	}

	// 拆3带
	public static void getThree(List<Card> list, Model model) {
		List<Card> del = new Vector<Card>();// 要删除的Cards
		// 连续3张相同
		for (int i = 0, len = list.size(); i < len; i++) {
			if (i + 2 < len
					&& Common.getValue(list.get(i)) == Common.getValue(list
							.get(i + 2))) {
				String s = list.get(i).name + ",";
				s += list.get(i + 1).name + ",";
				s += list.get(i + 2).name;
				model.a3.add(s);
				for (int j = i; j <= i + 2; j++)
					del.add(list.get(j));
				i = i + 2;
			}
		}
		list.removeAll(del);
	}

	// 拆对子
	public static void getTwo(List<Card> list, Model model) {
		List<Card> del = new Vector<Card>();// 要删除的Cards
		// 连续2张相同
		for (int i = 0, len = list.size(); i < len; i++) {
			if (i + 1 < len
					&& Common.getValue(list.get(i)) == Common.getValue(list
							.get(i + 1))) {
				String s = list.get(i).name + ",";
				s += list.get(i + 1).name;
				model.a2.add(s);
				for (int j = i; j <= i + 1; j++)
					del.add(list.get(j));
				i = i + 1;
			}
		}
		list.removeAll(del);
	}

	// 拆单牌
	public static void getSingle(List<Card> list, Model model) {
		List<Card> del = new Vector<Card>();// 要删除的Cards
		// 1
		for (int i = 0, len = list.size(); i < len; i++) {
			model.a1.add(list.get(i).name);
			del.add(list.get(i));
		}
		list.removeAll(del);
	}

	// 隐藏之前出过的牌
	public static void hideCards(List<Card> list) {
		for (int i = 0, len = list.size(); i < len; i++) {
			list.get(i).setVisible(false);
		}
	}

	// 检查牌的是否能出
	public static int checkCards(List<Card> c, List<Card>[] current,Main m) {
		// 找出当前最大的牌是哪个电脑出的,c是点选的牌
		List<Card> currentlist;
		if(m.time[0].getText().equals("不要"))
			currentlist=current[2];
		else
			currentlist=current[0];
		CardType cType = Common.jugdeType(c);
		CardType cType2=Common.jugdeType(currentlist);
		// 如果张数不同直接过滤
		if (cType != CardType.c4 && c.size() != currentlist.size())
			return 0;
		// 比较我的出牌类型
		if (cType != CardType.c4&&Common.jugdeType(c) != Common.jugdeType(currentlist)) {

			return 0;
		}
		// 比较出的牌是否要大
		// 我是炸弹
		if (cType == CardType.c4) {
			if(c.size()==2)
				return 1;
			if(cType2!=CardType.c4)
			{
				return 1;
			}
		}

		// 单牌,对子,3带,4炸弹
		if (cType == CardType.c1 || cType == CardType.c2
				|| cType == CardType.c3 || cType == CardType.c4) {
			if (Common.getValue(c.get(0)) <= Common
					.getValue(currentlist.get(0))) {
				return 0;
			} else {
				return 1;
			}
		}
		// 顺子,连队，飞机裸
		if (cType == CardType.c123 || cType == CardType.c1122
				|| cType == CardType.c111222) {
			if (Common.getValue(c.get(0)) <= Common
					.getValue(currentlist.get(0)))
				return 0;
			else
				return 1;
		}
		// 按重复多少排序
		// 3带1,3带2 ,飞机带单，双,4带1,2,只需比较第一个就行，独一无二的
		if (cType == CardType.c31 || cType == CardType.c32
				|| cType == CardType.c411 || cType == CardType.c422
				|| cType == CardType.c11122234 || cType == CardType.c1112223344) {
			List<Card> a1 = Common.getOrder2(c); // 我出的牌
			List<Card> a2 = Common.getOrder2(currentlist);// 当前最大牌
			if (Common.getValue(a1.get(0)) < Common.getValue(a2.get(0)))
				return 0;
		}
		return 1;
	}

	// 按照重复次数排序
	public static List getOrder2(List<Card> list) {
		List<Card> list2 = new Vector<Card>(list);
		List<Card> list3 = new Vector<Card>();
		List<Integer> list4 = new Vector<Integer>();
		int len = list2.size();
		int a[] = new int[20];
		for (int i = 0; i < 20; i++)
			a[i] = 0;
		for (int i = 0; i < len; i++) {
			a[Common.getValue(list2.get(i))]++;
		}
		int max = 0;
		for (int i = 0; i < 20; i++) {
			max = 0;
			for (int j = 19; j >= 0; j--) {
				if (a[j] > a[max])
					max = j;
			}

			for (int k = 0; k < len; k++) {
				if (Common.getValue(list2.get(k)) == max) {
					list3.add(list2.get(k));
				}
			}
			list2.remove(list3);
			a[max] = 0;
		}
		return list3;
	}
	//拆牌循序
	public static void showOrders(int i, List<Card> list, Model model) {
		switch (i) {
		case 1:
			Common.getSingle(list, model);
			break;
		case 2:
			Common.getTwo(list, model);
			Common.getTwoTwo(list, model);
			break;
		case 3:
			Common.getThree(list, model);
			Common.getPlane(list, model);
			break;
		case 4:
			Common.getBoomb(list, model);
			break;
		case 5:
			Common.get123(list, model);
			break;
		}
	}
}

class Card_index {
	List a[] = new Vector[4];// 单张
}