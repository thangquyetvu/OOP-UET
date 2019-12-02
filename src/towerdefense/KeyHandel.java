package towerdefense;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class KeyHandel implements MouseMotionListener, MouseListener {

	public void mouseClicked(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}
	
	//Được triệu hồi khi con trỏ chuột nhấn trên 1 thành phần
	public void mousePressed(MouseEvent e) {
		Screen.store.click(e.getButton());
	}

	public void mouseReleased(MouseEvent e) {

	}

	//Được triệu hồi khi một nút chuột được nhấn sau đó được kéo
	public void mouseDragged(MouseEvent e) {
		Screen.mse = new Point((e.getX()) - ((Frame.size.width - Screen.myWidth) / 2),
				(e.getY()) - (Frame.size.height - (Screen.myHeight)) - (Frame.size.width-Screen.myWidth)/2);
	}

	//Được triệu hồi khi con trỏ chuột đã được di chuyển và không có nút nào được nhấn
	public void mouseMoved(MouseEvent e) {
		Screen.mse = new Point((e.getX()) - ((Frame.size.width - Screen.myWidth) / 2),
				(e.getY()) - (Frame.size.height - (Screen.myHeight)) - (Frame.size.width-Screen.myWidth)/2);
	}

}
