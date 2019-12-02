package towerdefense;

import java.awt.*;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.*;

import javax.swing.*;


public class Screen extends JPanel implements Runnable {
	public Thread thread = new Thread(this);	

	public static Image[] tileset_ground = new Image[100];	//Chứa ảnh cỏ và đường đi
	public static Image[] tileset_air = new Image[100];	// Chứa hình ảnh cổng kết thúc, thùng rác, tháp
	public static Image[] tileset_res = new Image[100];	//Chứa hình ảnh	nền ô cửa hàng, gold, trái tim
	public static Image[] tileset_mob = new Image[100];	//Chứa hình ảnh enemy

	public static int myWidth, myHeight;
	public static int coinage = 10, health = 5; //Lượng gold ban đầu và máu của người chơi
	public static int killed = 0, killsToWin = 0, level = 1, maxlevel = 3;
	public static int winTime = 4000, winFrame = 0; 

	public static boolean isFirst = true;
	public static boolean isDebug = false;
	public static boolean isWin = false;	// đã thắng chưa

	public static Point mse = new Point(0, 0);

	public static Room room;
	public static Save save;
	public static Store store;
	public static Mob[] mobs = new Mob[100];	//Chứa các enemy

	int index = 0;
	
	public Screen(Frame frame) {
		frame.addMouseListener(new KeyHandel());
		frame.addMouseMotionListener(new KeyHandel());
		thread.start();
	}

	public static void hasWon() {
		if (killed == killsToWin) {
			isWin = true;
			killed = 0;
		}
	}

	public void define() {
		room = new Room();
		save = new Save();
		store = new Store();
		
		coinage = 20;
		health =100;

		for (int i = 0; i < tileset_ground.length; i++) {
			tileset_ground[i] = new ImageIcon("res/tileset_ground1.png").getImage();
			tileset_ground[i] = createImage(
					new FilteredImageSource(tileset_ground[i].getSource(), new CropImageFilter(0, 250 * i, 250, 250)));
		}

		for (int i = 0; i < tileset_air.length; i++) {
			tileset_air[i] = new ImageIcon("res/tileset_air.png").getImage();
			tileset_air[i] = createImage(
					new FilteredImageSource(tileset_air[i].getSource(), new CropImageFilter(0, 250 * i, 250, 250)));
		}

		tileset_res[0] = new ImageIcon("res/cell.png").getImage();
		tileset_res[1] = new ImageIcon("res/heart.png").getImage();
		tileset_res[2] = new ImageIcon("res/coin.png").getImage();
		
		for (int i = 0; i < tileset_mob.length; i++) {
			tileset_mob[i] = new ImageIcon("res/tileset_enemy.png").getImage();
			tileset_mob[i] = createImage(
					new FilteredImageSource(tileset_mob[i].getSource(), new CropImageFilter(0, 250 * i, 250, 250)));
		}

		save.loadSave(new File("save/map" + level));

		for (int i = 0; i < mobs.length; i++) {
			mobs[i] = new Mob();
		}
	}

	public void paintComponent(Graphics g) {
		if (isFirst) {
			myWidth = getWidth();
			myHeight = getHeight();
			define();

			isFirst = false;
		}

		g.setColor(new Color(70, 70, 70));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(new Color(0, 0, 0));
		g.drawLine(room.block[0][0].x - 1, 0, room.block[0][0].x - 1,
				room.block[room.worldHeight - 1][0].y + room.blockSize);
		g.drawLine(room.block[0][room.worldWidth - 1].x + room.blockSize, 0,
				room.block[0][room.worldWidth - 1].x + room.blockSize,
				room.block[room.worldHeight - 1][0].y + room.blockSize);
		g.drawLine(room.block[0][0].x, room.block[room.worldHeight - 1][0].y + room.blockSize,
				room.block[0][room.worldWidth - 1].x + room.blockSize,
				room.block[room.worldHeight - 1][0].y + room.blockSize);

		room.draw(g);

		for (int i = 0; i < mobs.length; i++) {
			if (mobs[i].inGame) {
				mobs[i].draw(g);
			}
		}

		store.draw(g);

		if (health < 1) {
			g.setColor(new Color(240, 20, 20));
			g.fillRect(0, 0, myWidth, myHeight);
			g.setColor(new Color(255, 255, 255));
			g.setFont(new Font("Courier New", Font.BOLD, 14));
			g.drawString("Bạn đã thua!", 10, 20);
		}

		if (isWin) {
			g.setColor(new Color(240, 20, 20));
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(new Color(0, 0, 0));
			g.setFont(new Font("Courier New", Font.BOLD, 14));
			
			if (level == maxlevel) {
				g.drawString("Chúc mừng bạn đã chiến thắng game!", 10, 20);
			} else {
				g.drawString("Chúc mừng bạn đã qua level!", 10, 20);
			}
		}
	}

	public int spawnTime = 2400, spawnFrame = 0;

	//Sinh ra enemy: không sinh ngẫu nhiên
	//Sinh enemy bằng cơ chế: nếu phần tử mảng mobs khác 0 và chia hết cho 3 thì sinh ra fastEnemy
	//nếu chỉ số khác không và chia hết cho 5 thì sinh ra bossEnemy
	//còn lại thì sinh ra mobGreeny
	public void mobSpawner() {
		if (spawnFrame >= spawnTime) {

			for (int i = 0; i < mobs.length; i++) {
				if (!mobs[index].inGame) {
					if(index!=0 && index%3==0) {
						mobs[index].spawnMob(Value.fastEnemy);
						break;
					}else if(index!=0 && index%5==0) {
						mobs[index].spawnMob(Value.bossEnemy);
						break;
					}else {
					mobs[index].spawnMob(Value.mobGreeny);
					break;
					}
				}
			}

			index +=1 ;
			spawnFrame = 0;
		} else {
			spawnFrame += 1;
		}
	}

	public void run() {
		while (true) {
			if (!isFirst && health > 0 && !isWin) {
				room.physic();
				mobSpawner();
				for (int i = 0; i < mobs.length; i++) {
					if (mobs[i].inGame) {
						mobs[i].physic();
					}
				}
			}else {
				if(isWin) {
					if(winFrame >= winTime) {
						if(level > maxlevel) {
							System.exit(0);;
						}else {
							level += 1;
							define();
							isWin = false;
							
						}
						winFrame = 0;
					}else {
						winFrame += 1;
					}
				}
			}

			repaint();

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
