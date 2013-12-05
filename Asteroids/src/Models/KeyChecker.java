package Models;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

public class KeyChecker {
	private char playerDir[] = new char[2];
	
	private boolean playerAccelerating[] = new boolean[2];
	
	private boolean playerShooting[] = new boolean[2];
	
	private boolean escapePressed;
	
	public KeyChecker() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                synchronized (this) {
                    switch (ke.getID()) {
                    // if this event was caused by a key press
                    case KeyEvent.KEY_PRESSED:
                    	switch (ke.getKeyCode()) {
                    	case KeyEvent.VK_LEFT:
                    		playerDir[0] = 'l';
                    		break;
                    	case KeyEvent.VK_RIGHT:
                    		playerDir[0] = 'r';
                    		break;
                    	case KeyEvent.VK_UP:
                    		playerAccelerating[0] = true;
                    		break;
                    	case KeyEvent.VK_PERIOD:
                    		playerShooting[0] = true;
                    		break;
                    	case KeyEvent.VK_A:
                    		playerDir[1] = 'l';
                    		break;
                    	case KeyEvent.VK_D:
                    		playerDir[1] = 'r';
                    		break;
                    	case KeyEvent.VK_W:
                    		playerAccelerating[1] = true;
                    		break;
                    	case KeyEvent.VK_C:
                    		playerShooting[1] = true;
                    		break;
                    	case KeyEvent.VK_ESCAPE:
                    		escapePressed = true;
                    		break;
                    	}
                        break;

                    // if this event was caused by a key release
                    case KeyEvent.KEY_RELEASED:
                    	switch (ke.getKeyCode()) {
                    	case KeyEvent.VK_LEFT:
                    		if (playerDir[0] == 'l') playerDir[0] = 'c';
                    		break;
                    	case KeyEvent.VK_RIGHT:
                    		if (playerDir[0] == 'r') playerDir[0] = 'c';
                    		break;
                    	case KeyEvent.VK_UP:
                    		if (playerAccelerating[0] == true) playerAccelerating[0] = false;
                    		break;
                    	case KeyEvent.VK_PERIOD:
                    		if (playerShooting[0] == true) playerShooting[0] = false;
                    		break;
                    	case KeyEvent.VK_A:
                    		if (playerDir[1] == 'l') playerDir[1] = 'c';
                    		break;
                    	case KeyEvent.VK_D:
                    		if (playerDir[1] == 'r') playerDir[1] = 'c';
                    		break;
                    	case KeyEvent.VK_W:
                    		if (playerAccelerating[1] == true) playerAccelerating[1] = false;
                    		break;
                    	case KeyEvent.VK_C:
                    		if (playerShooting[1] == true) playerShooting[1] = false;
                    		break;
                    	case KeyEvent.VK_ESCAPE:
                    		if (escapePressed == true) escapePressed = false;
                    		break;
                    	}
                        break;
                    }
                    return false;
                }
            }
        });
	}
	
	public char getPlayerDirection(int player) {
		return playerDir[player];
	}
	
	public boolean getPlayerAccelerating(int player) {
		return playerAccelerating[player];
	}
	
	public boolean getPlayerShooting(int player) {
		return playerShooting[player];
	}
	
	public boolean getEscapePressed() {
		return escapePressed;
	}
}
