package towerdefense;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import org.omg.CORBA.portable.ValueBase;

public class Store {
	public static int shopWidth = 8;	//Số ô cửa hàng
	public static int buttonSize = 52;	//Kích thước của 1 ô của cửa hàng
	public static int cellSpace = 2;	//Khoảng cách giữa 2 ô
	public static int iconSize = 20;	//Kích thước của icon
	public static int iconSpace = 3;	
	public static int iconTextY = 15;
	public static int itemIn = 4;
	public static int heldID = -1;
	public static int realID = -1;
	public static int[] buttonID = { Value.airTowerLaser, Value.airTowerSlow, Value.airTowerFar, Value.airAir, Value.airAir,
			Value.airAir, Value.airAir, Value.airTrashCan };
	public static int[] buttonPrice = { 10, 15, 20, 0, 0, 0, 0, 0 };	//Mảng lưu giá tiền của tháp

	public Rectangle[] button = new Rectangle[shopWidth];	//Tạo 1 mảng Rectangle có 8 phần tử
	public Rectangle buttonHealth;	//Tạo 1 Rctangle chứa hình trái tim
	public Rectangle buttonCoins;	//Tạo 1 Rectangle chứa hình gold

	public boolean holdsItem = false;	//Chuột có đang giữ item không

	public Store() {
		define();
	}

	public void click(int mouseButton) {
		if (mouseButton == 1) {
			for (int i = 0; i < button.length; i++) {
				if (button[i].contains(Screen.mse)) {
					if (buttonID[i] != Value.airAir) {
						if (buttonID[i] == Value.airTrashCan) {
							holdsItem = false;
							heldID = Value.airAir;
						} else {
							heldID = buttonID[i];
							realID = i;
							holdsItem = true;
						}
					}
				}
			}
		}
		//mua item
		if(holdsItem) {
			if(Screen.coinage >= buttonPrice[realID]) {
				for (int y = 0; y < Screen.room.block.length; y++) {
					for (int x = 0; x < Screen.room.block[0].length; x++) {
						if(Screen.room.block[y][x].contains(Screen.mse)) {
							if(Screen.room.block[y][x].groundId != Value.groundRoad && Screen.room.block[y][x].airID == Value.airAir) {
								Screen.room.block[y][x].airID = heldID;
								Screen.coinage -= buttonPrice[realID];
							}
						}
					}
				}
			}
		}
	}

	public void define() {
		//Khởi tạo các thành phần icon trên các Rec để vẽ
		for (int i = 0; i < button.length; i++) {
			button[i] = new Rectangle(
					(Screen.myWidth / 2) - (shopWidth * (buttonSize + cellSpace)) / 2 + (buttonSize + cellSpace) * i,
					Screen.room.block[Screen.room.worldHeight - 1][0].y + Screen.room.blockSize + cellSpace, buttonSize,
					buttonSize);
		}

		buttonHealth = new Rectangle(Screen.room.block[0][0].x - 1, button[0].y, iconSize, iconSize);
		buttonCoins = new Rectangle(Screen.room.block[0][0].x - 1, button[0].y + button[0].height - iconSize, iconSize,
				iconSize);
	}

	public void draw(Graphics g) {

		for (int i = 0; i < button.length; i++) {
			if (button[i].contains(Screen.mse)) {
				g.setColor(new Color(225, 225, 225, 100));
				g.fillRect(button[i].x, button[i].y, button[i].width, button[i].height);
			}
			g.drawImage(Screen.tileset_res[0], button[i].x, button[i].y, button[i].width, button[i].height, null);
			if (buttonID[i] != Value.airAir)
				g.drawImage(Screen.tileset_air[buttonID[i]], button[i].x + itemIn, button[i].y + itemIn,
						button[i].width - (itemIn * 2), button[i].height - (itemIn * 2), null);

			if (buttonPrice[i] > 0) {
				g.setColor(new Color(249,244,0));
				g.setFont(new Font("Courier New", Font.BOLD, 14));
				g.drawString(buttonPrice[i] + "$", button[i].x + itemIn + 10, button[i].y + itemIn +25);
			}
		}
		g.drawImage(Screen.tileset_res[1], buttonHealth.x, buttonHealth.y, buttonHealth.width, buttonHealth.height,
				null);
		g.drawImage(Screen.tileset_res[2], buttonCoins.x, buttonCoins.y, buttonCoins.width, buttonCoins.height, null);
		g.setFont(new Font("Courier New", Font.BOLD, 14));
		g.setColor(new Color(255, 255, 255));
		g.drawString("" + Screen.health, buttonHealth.x + buttonHealth.width + iconSpace, buttonHealth.y + iconTextY);
		g.drawString("" + Screen.coinage, buttonCoins.x + buttonCoins.width + iconSpace, buttonCoins.y + iconTextY);

		if (holdsItem) {
			g.drawImage(Screen.tileset_air[heldID], Screen.mse.x - (button[0].width - (itemIn * 2)) / 2 + itemIn,
					Screen.mse.y - (button[0].width - (itemIn * 2)) / 2 + itemIn, button[0].width - (itemIn * 2),
					button[0].height - (itemIn * 2), null);
		}
	}
}
