package com.anna.sent.soft.womancyc.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;

public class OnSwipeTouchListener implements OnTouchListener {
	public OnSwipeTouchListener(Context context) {
		gestureDetector = new GestureDetector(context.getApplicationContext(),
				new GestureListener());
	}

	private final GestureDetector gestureDetector;

	@Override
	public boolean onTouch(final View v, final MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			return v.performClick();
		}

		return gestureDetector.onTouchEvent(event);
	}

	private final class GestureListener extends SimpleOnGestureListener {

		private static final int SWIPE_THRESHOLD = 100;
		private static final int SWIPE_VELOCITY_THRESHOLD = 100;

		@Override
		public boolean onDown(MotionEvent e) {
			return super.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			boolean result = false;
			try {
				float diffY = e2.getY() - e1.getY();
				float diffX = e2.getX() - e1.getX();
				if (Math.abs(diffX) > Math.abs(diffY)) {
					if (Math.abs(diffX) > SWIPE_THRESHOLD
							&& Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffX > 0) {
							result = onSwipeRight();
						} else {
							result = onSwipeLeft();
						}
					}
				} else {
					if (Math.abs(diffY) > SWIPE_THRESHOLD
							&& Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffY > 0) {
							result = onSwipeBottom();
						} else {
							result = onSwipeTop();
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			return result;
		}
	}

	public boolean onSwipeRight() {
		return false;
	}

	public boolean onSwipeLeft() {
		return false;
	}

	public boolean onSwipeTop() {
		return false;
	}

	public boolean onSwipeBottom() {
		return false;
	}
}