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
	public static final int MAXFLOOR = 20;
	public static final int NUM = 5;
	public static final Color ON = new Color(250, 200, 150);
	public static final Color OFF = new Color(250, 250, 250);
	
	
	// GUI
	JFrame frame;
	JPanel buttonPanel;
	JButton exteriorButton[][];
	JButton interiorButton[][];
	JLabel floorLabel[];
	JLabel directionLabel[];
	
	public int state[];
	public int floor[];
	public int door[];
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
			while(!queue[liftNo].isEmpty()) {
				try {
					Thread.sleep(1000);    // 模拟电梯运行
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				int nextFloor = floor[liftNo] + (state[liftNo] == UP ? 1 : -1);
				if (queue[liftNo].get(0).floor == nextFloor) {
					exteriorButton[nextFloor][queue[liftNo].get(0).direction].setBackground(OFF);
					interiorButton[nextFloor][liftNo].setBackground(OFF);
					door[liftNo] = OPEN;
					floorLabel[liftNo].setForeground(Color.blue);
					floor[liftNo] = nextFloor;
					floorLabel[liftNo].setText("" + floor[liftNo]);
					state[liftNo] = queue[liftNo].remove(0).direction;
					directionLabel[liftNo].setText(state[liftNo] == UP ? "▲" : "▼");
					try {
						Thread.sleep(3000);    // 模拟电梯上下客
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					door[liftNo] = CLOSED;
					floorLabel[liftNo].setForeground(Color.black);
					if (!queue[liftNo].isEmpty()) {
						if (queue[liftNo].get(0).floor == floor[liftNo]) {  // 到达队列中最高层或最低层的情形
							exteriorButton[queue[liftNo].get(0).floor][queue[liftNo].get(0).direction].setBackground(OFF);
							queue[liftNo].remove(0);
						}
						if (!queue[liftNo].isEmpty()) {
							state[liftNo] = queue[liftNo].get(0).floor > floor[liftNo] ? UP : DOWN;
							directionLabel[liftNo].setText(state[liftNo] == UP ? "▲" : "▼");
						}
					}
				}
				else {
					floor[liftNo] = nextFloor;
					floorLabel[liftNo].setText("" + floor[liftNo]);
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
		queue = new ArrayList[NUM];
		for (int i = 0; i < NUM; ++i) {
			state[i] = IDLE;
			floor[i] = 1;
			door[i] = CLOSED;
			queue[i] = new ArrayList<Process>();
		}
		frame = new JFrame("Lift Simulator");
		
		//Buttons
		exteriorButton = new JButton[MAXFLOOR + 1][2];
		interiorButton = new JButton[MAXFLOOR + 1][NUM];
		
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
		buttonPanel.setLayout(new GridLayout(22,7,30,8));
		
		floorLabel = new JLabel[NUM];
		directionLabel = new JLabel[NUM];
		for (int j = 0; j < NUM; ++j) {
			floorLabel[j] = new JLabel(""+1,JLabel.CENTER);
			floorLabel[j].setFont(new Font("Arial",Font.BOLD,20));
			directionLabel[j] = new JLabel("",JLabel.CENTER);
       	}
		
		buttonPanel.add(new JLabel("",JLabel.CENTER));
		buttonPanel.add(new JLabel("",JLabel.CENTER));
		for (int j = 0; j < NUM; ++j) {
			buttonPanel.add(floorLabel[j]);
       	}
		
		buttonPanel.add(new JLabel("",JLabel.CENTER));
		buttonPanel.add(new JLabel("",JLabel.CENTER));
		for (int j = 0; j < NUM; ++j) {
			buttonPanel.add(directionLabel[j]);
       	}
		
		for (int i = MAXFLOOR; i > 0; --i) {
			for (int j = 0; j < 2; ++j) {
				buttonPanel.add(exteriorButton[i][j]);
				final int I = i, J = j;
				exteriorButton[i][j].addActionListener(
						new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								exteriorButton[I][J].setBackground(ON);
								allocateProcess(I, J);
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
		int l = 0, minWaitingTime = getWaitingTime(0, flr, ptype);
		for (int i = 1; i < NUM; ++i) {
			if (getWaitingTime(i, flr, ptype) < minWaitingTime) {
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
				}
				else {
					exteriorButton[flr][ptype].setBackground(OFF);
				}
			}
			else if (flr > floor[liftNo]) {
				state[liftNo] = UP;
				if (ptype == INTERIOR) {
					queue[liftNo].add(new Process(flr, UP));
				}
				else {
					queue[liftNo].add(new Process(flr, ptype));
				}
				directionLabel[liftNo].setText("▲");
				LiftThread thrd = new LiftThread(liftNo);
				thrd.start();
			}
			else {
				state[liftNo] = DOWN;
				if (ptype == INTERIOR) {
					queue[liftNo].add(new Process(flr, DOWN));
				}
				else {
					queue[liftNo].add(new Process(flr, ptype));
				}
				directionLabel[liftNo].setText("▼");
				LiftThread thrd = new LiftThread(liftNo);
				thrd.start();
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