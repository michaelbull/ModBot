package ch.swingfx.twinkle.manager;

import javax.swing.JWindow;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrimaryNotificationManager {

	private PrimaryNotificationManager() {

	}

	/**
	 * Delay before we show the notification
	 */
	private static final int DELAY = 250;

	/**
	 * our lock for synchronization
	 */
	private static final Lock sLock;

	/**
	 * true if a window is open. guarded by lock
	 */
	private static boolean sWindowOpen = false;

	static {
		sLock = new ReentrantLock(true);
	}

	protected static void showNotification(final JWindow window) {
		if (sWindowOpen) {
			return;
		}
		try {
			sLock.lock();
			window.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					window.removeWindowListener(this);
					sWindowOpen = false;
				}
			});
			Timer delayVisibleTimer = new Timer(DELAY, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final Timer t = (Timer) e.getSource();
					t.stop();
					window.setVisible(true);
					window.getGlassPane().setVisible(true);

				}
			});
			delayVisibleTimer.start();

		} finally {
			sLock.unlock();
		}
	}

}
