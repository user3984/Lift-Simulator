package Lift;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Process{
	public int floor;
	public int direction;
	
	public Process(int flr, int dir) {
		floor = flr;
		direction = dir;
	}
}

public class LiftController{
	
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int IDLE = 2;
	public static final int INTERIOR = 3;
	public static final int CLOSED = 0;
	public static final int OPEN = 1;
	public static final int CLOSING = 2;
	public static final int MAXFLOOR = 20;
	public static final int NUM = 5;
	public static final Color ON = new Color(250, 200, 150);
	public static final Color OFF = new Color(250, 250, 250);
	
	// GUI
	JFrame frame;
	JPanel buttonPanel;
	JButton exteriorButton[][];
	JButton interiorButton[][];
	JButton openButton[];
	JButton closeButton[];
	JButton alert[];
	JLabel floorLabel[];
	JLabel directionLabel[];
	
	public int state[];
	public int floor[];
	public int door[];
	public boolean available[];
	ArrayList<Process> queue[];
	
	public static int abs(int n) {
		return n > 0 ? n : -n; 
	}
	
	class LiftThread extends Thread{
		int liftNo;
		public LiftThread(int liftNo) {
			this.liftNo = liftNo;
		}
		
		@Override
		public void run() {
			if (door[liftNo] == OPEN) {
				for (int t = 0; t < 30 && door[liftNo] == OPEN; ++t) {
					try {
						Thread.sleep(100);    // 模拟电梯上下客
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				floorLabel[liftNo].setForeground(Color.black);
				door[liftNo] = CLOSED;
			}
			while(!queue[liftNo].isEmpty()) {
				if (queue[liftNo].get(0).floor == floor[liftNo]) {
					exteriorButton[queue[liftNo].get(0).floor][queue[liftNo].get(0).direction].setBackground(OFF);
					queue[liftNo].remove(0);
					if (queue[liftNo].isEmpty()) {
						break;
					}
				}
				state[liftNo] = queue[liftNo].get(0).floor > floor[liftNo] ? UP : DOWN;
				directionLabel[liftNo].setText(state[liftNo] == UP ? "▲" : "▼");
				try {
					Thread.sleep(1000);    // 模拟电梯运行
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				floor[liftNo] = floor[liftNo] + (state[liftNo] == UP ? 1 : -1);
				floorLabel[liftNo].setText("" + floor[liftNo]);
				if (queue[liftNo].get(0).floor == floor[liftNo]) {
					door[liftNo] = OPEN;
					floorLabel[liftNo].setForeground(Color.blue);
					state[liftNo] = queue[liftNo].get(0).direction;
					interiorButton[floor[liftNo]][liftNo].setBackground(OFF);
					exteriorButton[floor[liftNo]][state[liftNo]].setBackground(OFF);
					directionLabel[liftNo].setText(state[liftNo] == UP ? "▲" : "▼");
					for (int t = 0; t < 30 && door[liftNo] == OPEN; ++t) {
						try {
							Thread.sleep(100);    // 模拟电梯上下客
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					door[liftNo] = CLOSED;
					queue[liftNo].remove(0);
					floorLabel[liftNo].setForeground(available[liftNo] ? Color.black : Color.red);
				}
				
			}
			state[liftNo] = IDLE;
			directionLabel[liftNo].setText("");
		}
	}
		
	public LiftController(){
		state = new int[NUM];
		floor = new int[NUM];
		door = new int[NUM];
		available = new boolean[NUM];
		queue = new ArrayList[NUM];
		for (int i = 0; i < NUM; ++i) {
			state[i] = IDLE;
			floor[i] = 1;
			door[i] = CLOSED;
			queue[i] = new ArrayList<Process>();
			available[i] = true;
		}
		frame = new JFrame("Lift Simulator");
		
		//Buttons
		exteriorButton = new JButton[MAXFLOOR + 1][2];
		interiorButton = new JButton[MAXFLOOR + 1][NUM];
		alert = new JButton[NUM];
		openButton = new JButton[NUM];
		closeButton = new JButton[NUM];
		
		for (int i = 0; i < MAXFLOOR + 1; ++i) {
			exteriorButton[i][UP] = new JButton("▲");
			exteriorButton[i][UP].setBackground(OFF);
			exteriorButton[i][DOWN] = new JButton("▼");
			exteriorButton[i][DOWN].setBackground(OFF);
		}
		
		for (int i = MAXFLOOR; i > 0; --i) {
			for (int j = 0; j < NUM; ++j) {
				interiorButton[i][j] = new JButton(""+i);
				interiorButton[i][j].setBackground(OFF);
	       	}
        }
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(MAXFLOOR+5,7,30,5));
		
		floorLabel = new JLabel[NUM];
		directionLabel = new JLabel[NUM];
		for (int j = 0; j < NUM; ++j) {
			floorLabel[j] = new JLabel(""+1,JLabel.CENTER);
			floorLabel[j].setFont(new Font("Arial",Font.BOLD,20));
			alert[j] = new JButton("Alert");
			alert[j].setBackground(OFF);
			openButton[j] = new JButton("◀▶");
			openButton[j].setBackground(OFF);
			closeButton[j] = new JButton("▶◀");
			closeButton[j].setBackground(OFF);
			directionLabel[j] = new JLabel("",JLabel.CENTER);
       	}
		
		JLabel lb1 = new JLabel("故障",JLabel.CENTER);
		lb1.setForeground(Color.red);
		lb1.setFont(new Font("微软雅黑",Font.BOLD,18));
		JLabel lb2 = new JLabel("开门",JLabel.CENTER);
		lb2.setForeground(Color.blue);
		lb2.setFont(new Font("微软雅黑",Font.BOLD,18));
		
		buttonPanel.add(lb1);
		buttonPanel.add(new JLabel("",JLabel.CENTER));
		for (int j = 0; j < NUM; ++j) {
			buttonPanel.add(floorLabel[j]);
       	}
		
		buttonPanel.add(lb2);
		buttonPanel.add(new JLabel("",JLabel.CENTER));
		for (int j = 0; j < NUM; ++j) {
			buttonPanel.add(directionLabel[j]);
       	}
		
		buttonPanel.add(new JLabel("",JLabel.CENTER));
		buttonPanel.add(new JLabel("",JLabel.CENTER));
		for (int j = 0; j < NUM; ++j) {
			buttonPanel.add(alert[j]);
			final int J = j;
			alert[J].addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (available[J] == true) {
								available[J] = false;
								alert[J].setBackground(ON);
								floorLabel[J].setForeground(Color.red);
							}
							else {
								available[J] = true;
								alert[J].setBackground(OFF);
								floorLabel[J].setForeground(Color.black);
							}
						}
					});
		}
		
		// 最高层
		buttonPanel.add(new JLabel(""));
		buttonPanel.add(exteriorButton[MAXFLOOR][DOWN]);
		exteriorButton[MAXFLOOR][DOWN].addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						boolean avail = false;
						for (int i = 0; i < NUM; ++i) {
							avail = avail || available[i];
						}
						if (avail) {
							exteriorButton[MAXFLOOR][DOWN].setBackground(ON);
							allocateProcess(MAXFLOOR, DOWN);
						}
					}
				});
		for (int j = 0; j < NUM; ++j) {
			buttonPanel.add(interiorButton[MAXFLOOR][j]);
			final int J = j;
			interiorButton[MAXFLOOR][J].addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							interiorButton[MAXFLOOR][J].setBackground(ON);
							addProcess(J, MAXFLOOR, INTERIOR);
						}
					});
       	}
		for (int i = MAXFLOOR - 1; i > 1; --i) {
			for (int j = 0; j < 2; ++j) {
				buttonPanel.add(exteriorButton[i][j]);
				final int I = i, J = j;
				exteriorButton[i][j].addActionListener(
						new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								boolean avail = false;
								for (int i = 0; i < NUM; ++i) {
									avail = avail || available[i];
								}
								if (avail) {
									exteriorButton[I][J].setBackground(ON);
									allocateProcess(I, J);
								}
							}
						});
	       	}
			for (int j = 0; j < NUM; ++j) {
				buttonPanel.add(interiorButton[i][j]);
				final int I = i, J = j;
				interiorButton[i][j].addActionListener(
						new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								interiorButton[I][J].setBackground(ON);
								addProcess(J, I, INTERIOR);
							}
						});
	       	}
        }
		
		// 最低层
		exteriorButton[1][UP].addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						boolean avail = false;
						for (int i = 0; i < NUM; ++i) {
							avail = avail || available[i];
						}
						if (avail) {
							exteriorButton[1][UP].setBackground(ON);
							allocateProcess(1, UP);
						}
					}
				});
		buttonPanel.add(exteriorButton[1][UP]);
		buttonPanel.add(new JLabel(""));
		for (int j = 0; j < NUM; ++j) {
			buttonPanel.add(interiorButton[1][j]);
			final int J = j;
			interiorButton[1][J].addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							interiorButton[1][J].setBackground(ON);
							addProcess(J, 1, INTERIOR);
						}
					});
       	}
		
		buttonPanel.add(new JLabel(""));
		buttonPanel.add(new JLabel(""));
		for (int j = 0; j < NUM; ++j) {
			buttonPanel.add(openButton[j]);
			final int J = j;
			openButton[J].addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (state[J] == IDLE) {
								addProcess(J, floor[J], INTERIOR);
							}
						}
					});
		}
		
		buttonPanel.add(new JLabel(""));
		buttonPanel.add(new JLabel(""));
		for (int j = 0; j < NUM; ++j) {
			buttonPanel.add(closeButton[j]);
			final int J = j;
			closeButton[J].addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							door[J] = CLOSING;
						}
					});
		}
		
		frame.add(buttonPanel);
		frame.pack();
		frame.setVisible(true);
	}
	
//	void printQueue() {
//		for (int l = 0; l<NUM; ++l) {
//			System.out.print("# "+l);
//			for (int i = 0; i < queue[l].size(); ++i) {
//				System.out.print("("+queue[l].get(i).floor+","+queue[l].get(i).direction+") ");
//			}
//			System.out.print("\n");
//		}
//		System.out.print("===================\n");
//	}
	
	public int getWaitingTime(int liftNo, int flr, int ptype) {
		if (state[liftNo] == IDLE) {
			return abs(flr - floor[liftNo]);
		}
		else if (state[liftNo] == UP) {
			if (ptype == UP) {
				if (flr > floor[liftNo]) {
					int i = 0;
					while (i < queue[liftNo].size() && queue[liftNo].get(i).direction == UP && queue[liftNo].get(i).floor < flr) {
						++i;
					}
					if (i == 0) {
						return abs(flr - floor[liftNo]);
					}
					else if (i >= queue[liftNo].size() || (!(queue[liftNo].get(i).floor == flr && queue[liftNo].get(i).direction == UP))) {
						int wt = abs(queue[liftNo].get(0).floor - floor[liftNo]) + 3;
						for (int k = 1; k < i; ++k) {
							wt += abs(queue[liftNo].get(k).floor - queue[liftNo].get(k - 1).floor) + 3;
						}
						wt += abs(queue[liftNo].get(i - 1).floor - flr);
						return wt;
					}
					else {
						return 0;   // 任务已在队列中
					}
				}
				else if (flr < floor[liftNo] || (flr == floor[liftNo] && door[liftNo] == CLOSED)) {
					int i = 0;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == UP; ++i)
						;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == DOWN; ++i)
						;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).floor < flr; ++i)
						;
					if (i == 0) {
						return abs(flr - floor[liftNo]);
					}
					else if (i >= queue[liftNo].size() || !(queue[liftNo].get(i).floor == flr && queue[liftNo].get(i).direction == UP)) {
						int wt = abs(queue[liftNo].get(0).floor - floor[liftNo]) + 3;
						for (int k = 1; k < i; ++k) {
							wt += abs(queue[liftNo].get(k).floor - queue[liftNo].get(k - 1).floor) + 3;
						}
						wt += abs(queue[liftNo].get(i - 1).floor - flr);
						return wt;
					}
					else {
						return 0;   // 任务已在队列中
					}
				}
			}
			else if (ptype == DOWN) {
				int i = 0;
				for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == UP; ++i)
					;
				for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == DOWN && queue[liftNo].get(i).floor > flr; ++i)
					;
				if (i == 0) {
					return abs(flr - floor[liftNo]);
				}
				else if (i >= queue[liftNo].size() || !(queue[liftNo].get(i).floor == flr && queue[liftNo].get(i).direction == DOWN)) {
					int wt = abs(queue[liftNo].get(0).floor - floor[liftNo]) + 3;
					for (int k = 1; k < i; ++k) {
						wt += abs(queue[liftNo].get(k).floor - queue[liftNo].get(k - 1).floor) + 3;
					}
					wt += abs(queue[liftNo].get(i - 1).floor - flr);
					return wt;
				}
				else {
					return 0;   // 任务已在队列中
				}
			}
		}
		else {  //state[liftNo] == UP
			if (ptype == DOWN) {
				if (flr < floor[liftNo]) {
					int i = 0;
					while (i < queue[liftNo].size() && queue[liftNo].get(i).direction == DOWN && queue[liftNo].get(i).floor > flr) {
						++i;
					}
					if (i == 0) {
						return abs(flr - floor[liftNo]);
					}
					else if (i >= queue[liftNo].size() || (!(queue[liftNo].get(i).floor == flr && queue[liftNo].get(i).direction == UP))) {
						int wt = abs(queue[liftNo].get(0).floor - floor[liftNo]) + 3;
						for (int k = 1; k < i; ++k) {
							wt += abs(queue[liftNo].get(k).floor - queue[liftNo].get(k - 1).floor) + 3;
						}
						wt += abs(queue[liftNo].get(i - 1).floor - flr);
						return wt;
					}
					else {
						return 0;   // 任务已在队列中
					}
				}
				else if (flr > floor[liftNo] ||(flr == floor[liftNo] && door[liftNo] == CLOSED)) {
					int i = 0;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == DOWN; ++i)
						;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == UP; ++i)
						;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).floor > flr; ++i)
						;
					if (i == 0) {
						return abs(flr - floor[liftNo]);
					}
					else if (i >= queue[liftNo].size() || !(queue[liftNo].get(i).floor == flr && queue[liftNo].get(i).direction == DOWN)) {
						int wt = abs(queue[liftNo].get(0).floor - floor[liftNo]) + 3;
						for (int k = 1; k < i; ++k) {
							wt += abs(queue[liftNo].get(k).floor - queue[liftNo].get(k - 1).floor) + 3;
						}
						wt += abs(queue[liftNo].get(i - 1).floor - flr);
						return wt;
					}
					else {
						return 0;   // 任务已在队列中
					}
				}
			}
			else if (ptype == UP) {
				int i = 0;
				for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == DOWN; ++i)
					;
				for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == UP && queue[liftNo].get(i).floor < flr; ++i)
					;
				if (i == 0) {
					return abs(flr - floor[liftNo]);
				}
				else if (i >= queue[liftNo].size() || !(queue[liftNo].get(i).floor == flr && queue[liftNo].get(i).direction == UP)) {
					int wt = abs(queue[liftNo].get(0).floor - floor[liftNo]) + 3;
					for (int k = 1; k < i; ++k) {
						wt += abs(queue[liftNo].get(k).floor - queue[liftNo].get(k - 1).floor) + 3;
					}
					wt += abs(queue[liftNo].get(i - 1).floor - flr);
					return wt;
				}
				else {
					return 0;   // 任务已在队列中
				}
			}
		}
		return 0;    // 电梯开门
	}

	public void allocateProcess(int flr, int ptype) {   // ptype = up, down
		int l = -1, minWaitingTime = Integer.MAX_VALUE;
		for (int i = 0; i < NUM; ++i) {
			if (available[i] && getWaitingTime(i, flr, ptype) < minWaitingTime) {
				minWaitingTime = getWaitingTime(i, flr, ptype);
				l = i;
			}
		}
		addProcess(l, flr, ptype);
	}
	
	public void addProcess(int liftNo, int flr, int ptype) {
//		System.out.println("addProcess(liftNo="+liftNo+", flr="+flr+", ptype="+ptype);
		if (state[liftNo] == IDLE) {     // ....
			if (flr == floor[liftNo]) {  //按下当前层按钮
				if (ptype == INTERIOR) {
					interiorButton[flr][liftNo].setBackground(OFF);
					if (door[liftNo] == CLOSED) {
						door[liftNo] = OPEN;
						floorLabel[liftNo].setForeground(Color.blue);
						LiftThread thrd = new LiftThread(liftNo);
						thrd.start();
					}
				}
				else {
					exteriorButton[flr][ptype].setBackground(OFF);
					if (available[liftNo]) {
						floorLabel[liftNo].setForeground(Color.blue);
						state[liftNo] = ptype;
						directionLabel[liftNo].setText(ptype == UP ? "▲" : "▼");
						if (door[liftNo] == CLOSED) {
							door[liftNo] = OPEN;
							LiftThread thrd = new LiftThread(liftNo);
							thrd.start();
						}
					}
				}
			}
			else if (flr > floor[liftNo]) {
				state[liftNo] = UP;
				directionLabel[liftNo].setText("▲");
				if (ptype == INTERIOR) {
					queue[liftNo].add(new Process(flr, UP));
				}
				else {
					queue[liftNo].add(new Process(flr, ptype));
				}
				if (door[liftNo] == CLOSED) {
					LiftThread thrd = new LiftThread(liftNo);
					thrd.start();
				}
			}
			else {
				state[liftNo] = DOWN;
				directionLabel[liftNo].setText("▼");
				if (ptype == INTERIOR) {
					queue[liftNo].add(new Process(flr, DOWN));
				}
				else {
					queue[liftNo].add(new Process(flr, ptype));
				}
				if (door[liftNo] == CLOSED) {
					LiftThread thrd = new LiftThread(liftNo);
					thrd.start();
				}
			}
			
		}
		else if (state[liftNo] == UP) {
			if (ptype == UP) {
				if (flr > floor[liftNo]) {
					int i = 0;
					while (i < queue[liftNo].size() && queue[liftNo].get(i).direction == UP && queue[liftNo].get(i).floor < flr) {
						++i;
					}
					if (i >= queue[liftNo].size() || !(queue[liftNo].get(i).floor == flr && queue[liftNo].get(i).direction == UP)) {
						queue[liftNo].add(i, new Process(flr, UP));  //插入队列
					}
				}
				else if (flr < floor[liftNo] || (flr == floor[liftNo] && door[liftNo] == CLOSED)) {
					int i = 0;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == UP; ++i)
						;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == DOWN; ++i)
						;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).floor < flr; ++i)
						;
					if (i >= queue[liftNo].size() || !(queue[liftNo].get(i).floor == flr && queue[liftNo].get(i).direction == UP)) {
						queue[liftNo].add(i, new Process(flr, UP));  //插入队列
					}
				}
				else {
					exteriorButton[flr][ptype].setBackground(OFF);
				}
			}
			else if (ptype == DOWN) {
				int i = 0;
				for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == UP; ++i)
					;
				for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == DOWN && queue[liftNo].get(i).floor > flr; ++i)
					;
				if (i >= queue[liftNo].size() || !(queue[liftNo].get(i).floor == flr && queue[liftNo].get(i).direction == DOWN)) {
					queue[liftNo].add(i, new Process(flr, DOWN));  //插入队列
				}
			}
			else if (ptype == INTERIOR) {
				if (flr > floor[liftNo]) {
					int i = 0;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == UP && queue[liftNo].get(i).floor < flr; ++i)
						;
					if (i >= queue[liftNo].size() || !(queue[liftNo].get(i).floor == flr && queue[liftNo].get(i).direction == UP)) {
						queue[liftNo].add(i, new Process(flr, UP));  //插入队列
					}
				}
				else if (flr < floor[liftNo] || (flr == floor[liftNo] && door[liftNo] == CLOSED)) {
					int i = 0;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == UP; ++i)
						;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == DOWN && queue[liftNo].get(i).floor > flr; ++i)
						;
					if (i >= queue[liftNo].size() || !(queue[liftNo].get(i).floor == flr && queue[liftNo].get(i).direction == DOWN)) {
						queue[liftNo].add(i, new Process(flr, DOWN));  //插入队列
					}
				}
				else {
					interiorButton[flr][liftNo].setBackground(OFF);
				}
			}
		}
		else {  //state[liftNo] == DOWN
			if (ptype == DOWN) {
				if (flr < floor[liftNo]) {
					int i = 0;
					while (i < queue[liftNo].size() && queue[liftNo].get(i).direction == DOWN && queue[liftNo].get(i).floor > flr) {
						++i;
					}
					if (i >= queue[liftNo].size() || (!(queue[liftNo].get(i).floor == flr && queue[liftNo].get(i).direction == DOWN))) {
						queue[liftNo].add(i, new Process(flr, DOWN));  //插入队列
					}
				}
				else if (flr > floor[liftNo] || (flr == floor[liftNo] && door[liftNo] == CLOSED)) {
					int i = 0;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == DOWN; ++i)
						;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == UP; ++i)
						;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).floor > flr; ++i)
						;
					if (i >= queue[liftNo].size() || !(queue[liftNo].get(i).floor == flr && queue[liftNo].get(i).direction == DOWN)) {
						queue[liftNo].add(i, new Process(flr, DOWN));  //插入队列
					}
				}
				else {
					exteriorButton[flr][ptype].setBackground(OFF);
				}
			}
			else if (ptype == UP) {
				int i = 0;
				for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == DOWN; ++i)
					;
				for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == UP && queue[liftNo].get(i).floor < flr; ++i)
					;
				if (i >= queue[liftNo].size() || !(queue[liftNo].get(i).floor == flr && queue[liftNo].get(i).direction == UP)) {
					queue[liftNo].add(i, new Process(flr, UP));  //插入队列
				}
			}
			else if (ptype == INTERIOR) {
				if (flr < floor[liftNo]) {
					int i = 0;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == DOWN && queue[liftNo].get(i).floor > flr; ++i)
						;
					if (i >= queue[liftNo].size() || !(queue[liftNo].get(i).floor == flr && queue[liftNo].get(i).direction == DOWN)) {
						queue[liftNo].add(i, new Process(flr, DOWN));  //插入队列
					}
				}
				else if (flr > floor[liftNo] || (flr == floor[liftNo] && door[liftNo] == CLOSED)) {
					int i = 0;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == DOWN; ++i)
						;
					for (; i < queue[liftNo].size() && queue[liftNo].get(i).direction == UP && queue[liftNo].get(i).floor < flr; ++i)
						;
					if (i >= queue[liftNo].size() || !(queue[liftNo].get(i).floor == flr && queue[liftNo].get(i).direction == UP)) {
						queue[liftNo].add(i, new Process(flr, UP));  //插入队列
					}
				}
				else {
					interiorButton[flr][liftNo].setBackground(OFF);
				}
			}
		}
//		printQueue();
	}
	
	public static void main(String[] args) {
		new LiftController();
	}
}