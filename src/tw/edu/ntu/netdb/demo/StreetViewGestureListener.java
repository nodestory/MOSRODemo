package tw.edu.ntu.netdb.demo;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class StreetViewGestureListener extends GestureDetector.SimpleOnGestureListener {
	private static final int SWIPE_THRESHOLD = 100;
	private static final int SWIPE_VELOCITY_THRESHOLD = 100;

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();

		Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");

		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		boolean result = false;
		try {
			float diffY = e2.getY() - e1.getY();
			float diffX = e2.getX() - e1.getX();
			if (Math.abs(diffX) > Math.abs(diffY)) {
				if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
					if (diffX > 0) {
						onSwipeRight();
					} else {
						onSwipeLeft();
					}
				}
			} else {
				if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
					if (diffY > 0) {
						onSwipeBottom();
					} else {
						onSwipeTop();
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return result;
	}

	public void onSwipeRight() {
	}

	public void onSwipeLeft() {
	}

	public void onSwipeTop() {
	}

	public void onSwipeBottom() {
	}
}

// public class StreetViewGestureListener implements OnTouchListener {
//
// private final GestureDetector gestureDetector = new GestureDetector(new
// StreetViewGestureListener());
//
// public boolean onTouch(final View view, final MotionEvent motionEvent) {
// return gestureDetector.onTouchEvent(motionEvent);
// }
//
// private final class StreetViewGestureListener extends SimpleOnGestureListener
// {
//
// private static final int SWIPE_THRESHOLD = 100;
// private static final int SWIPE_VELOCITY_THRESHOLD = 100;
//
// @Override
// public boolean onDown(MotionEvent e) {
// return true;
// }
//
// @Override
// public boolean onDoubleTap(MotionEvent e) {
// tapped = !tapped;
// if (tapped) {
// } else {
// }
//
// return true;
// }
//
// @Override
// public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float
// velocityY) {
// boolean result = false;
// try {
// float diffY = e2.getY() - e1.getY();
// float diffX = e2.getX() - e1.getX();
// if (Math.abs(diffX) > Math.abs(diffY)) {
// if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) >
// SWIPE_VELOCITY_THRESHOLD) {
// if (diffX > 0) {
// onSwipeRight();
// } else {
// onSwipeLeft();
// }
// }
// } else {
// if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) >
// SWIPE_VELOCITY_THRESHOLD) {
// if (diffY > 0) {
// onSwipeBottom();
// } else {
// onSwipeTop();
// }
// }
// }
// } catch (Exception exception) {
// exception.printStackTrace();
// }
// return result;
// }
// }
//
// public void onSwipeRight() {
// }
//
// public void onSwipeLeft() {
// }
//
// public void onSwipeTop() {
// }
//
// public void onSwipeBottom() {
// }
// }