package towerdefense;

import java.awt.*;

public class Block extends Rectangle {
	public Rectangle towerSquare;
	public int towerSquareSize = 130;
	public int groundId;
	public int airID;
	public int loseTime = 100, loseFrame = 0;
	public int shotMob = -1;		//Dùng để xác định xem tháp bắn con nào
	public boolean shoting = false;
	public boolean shotingSlow = false;
	public boolean shotingFar = false;

	
	// khởi tạo 1 block
	public Block(int x, int y, int width, int height, int groundID, int ariID) {
		setBounds(x, y, width, height);
		
		//khởi tạo 1 Rectangle
										//tọa độ x				tọa độ y					chiều rộng
		this.groundId = groundID;
		this.airID = ariID;
		
		towerSquare = new Rectangle(x - (towerSquareSize / 2), y - (towerSquareSize / 2), width + towerSquareSize,
				height + towerSquareSize);
					//chiều cao
	}

	//Vẽ trên Rectagle đó
	public void draw(Graphics g) {
		g.drawImage(Screen.tileset_ground[groundId], x, y, width, height, null);

		if (airID != Value.airAir) {
			g.drawImage(Screen.tileset_air[airID], x, y, width, height, null);
		}
	}

	public void physic() {
//		if(airID == Value.airTowerFar) {
//			this.towerSquareSize = 1000;
//		}else {
//			this.towerSquareSize = 130;
//		}
//							intersets: phát hiện va chạm giữa 2 hình chữ nhật
		
		
		if (shotMob != -1 && towerSquare.intersects(Screen.mobs[shotMob])) {
			if(airID == Value.airTowerLaser) {
				shoting = true;
				shotingSlow = false;
			}
			if(airID == Value.airTowerSlow) {
				shoting = false;
				shotingSlow = true;
			}
			if(airID == Value.airTowerFar) {
				shoting = true;
				shotingSlow = false;
			}
		} 
			else {
				shoting = false;	
				shotingSlow = false;
			}

		if (!shoting && !shotingSlow) {
			if (airID == Value.airTowerLaser) {
				//Duyệt từng con enemy trong mảng mobs
				for (int i = 0; i < Screen.mobs.length; i++) {
					if (Screen.mobs[i].inGame) {
						//Nếu 2 vùng của hình chữ nhật chạm nhau
						if (towerSquare.intersects(Screen.mobs[i])) {
							shoting = true;
							shotingSlow = false;
							shotMob = i;
						}
					}
				}
			}
			
			else if (airID == Value.airTowerSlow) {
				for (int i = 0; i < Screen.mobs.length; i++) {
					if (Screen.mobs[i].inGame) {
						if (towerSquare.intersects(Screen.mobs[i])) {
							shoting = false;
							shotingSlow = true;
							shotMob = i;
						}
//							else {
//							Screen.mobs[i].walkSpeed = 80;
//							shoting = false;
//							shotingSlow = false;
////							shotMob = -1;
//						}
					}
				}
			}
			else if (airID == Value.airTowerFar) {
				//Duyệt từng con enemy trong mảng mobs
				towerSquare = new Rectangle(x - (towerSquareSize*2 / 2), y - (towerSquareSize*2 / 2), width + towerSquareSize*2,
						height + towerSquareSize*2);
				for (int i = 0; i < Screen.mobs.length; i++) {
					if (Screen.mobs[i].inGame) {
						//Nếu 2 vùng của hình chữ nhật chạm nhau
						if (towerSquare.intersects(Screen.mobs[i])) {
							shoting = true;
							shotingSlow = false;
							shotMob = i;
						}
					}
				}
			}
			
		}
		
		//Nếu có thể bắn
		if (shoting) {
			if (loseFrame >= loseTime) {
				Screen.mobs[shotMob].loseHealth(2);
				loseFrame = 0;
			} else {
				loseFrame += 1;
			}

			//
			if (Screen.mobs[shotMob].isDead()) {
				shoting = false;
				shotMob = -1;

				Screen.killed += 1;

				//Gọi tới phương thức hasWon để kiểm tra xem thắng chưa
				Screen.hasWon();
			}
		}

		if (shotingSlow) {
			if (loseFrame >= loseTime) {
				Screen.mobs[shotMob].loseSpeed(20);
				loseFrame = 0;
			} else {
				loseFrame += 1;
			}
			
			if (Screen.mobs[shotMob].isDead()) {
				shotingSlow = false;
				shotMob = -1;				
			}
		}
		
		
	}

	public void getMoney(int mobID) {
		//Cập nhật giá vàng coinage += giá vàng của loại enemy mobID
		Screen.coinage += Value.deadthReward[mobID];
	}

	public void fight(Graphics g) {
		if (Screen.isDebug) {
			if (airID == Value.airTowerLaser || airID == Value.airTowerSlow) {
				g.drawRect(towerSquare.x, towerSquare.y, towerSquare.width, towerSquare.height);
			}
		}

		if (shoting) {
			//Cập nhật màu đồ họa thành 255 255 0
			//Vẽ 1 đường bắn từ tọa độ tâm hình chữ nhật tháp tới tâm hình chữ nhật mob
			g.setColor(new Color(255, 255, 0));
			g.drawLine(x + width / 2, y + height / 2, Screen.mobs[shotMob].x + Screen.mobs[shotMob].width / 2,
					Screen.mobs[shotMob].y + Screen.mobs[shotMob].height / 2);
		}
		if (shotingSlow) {
			g.setColor(new Color(32, 90, 167));
			g.drawLine(x + width / 2, y + height / 2, Screen.mobs[shotMob].x + Screen.mobs[shotMob].width / 2,
					Screen.mobs[shotMob].y + Screen.mobs[shotMob].height / 2);
		}
	}

}
