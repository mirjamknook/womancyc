package com.anna.sent.soft.womancyc;

import java.util.Calendar;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.anna.sent.soft.womancyc.fragments.DayViewFragment;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.superclasses.DialogActivity;
import com.anna.sent.soft.womancyc.utils.DateUtils;

public class DayViewActivity extends DialogActivity implements
		DayViewFragment.Listener {
	private static final String TAG = "moo";
	private static final boolean DEBUG = true;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

	private void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private boolean mIsLargeLayout;
	private Calendar mDateToShow;
	private DayViewFragment mDayView;

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);

		if (fragment instanceof DayViewFragment) {
			mDayView = (DayViewFragment) fragment;
			mDayView.setListener(this);
		}
	}

	@Override
	public void setViews(Bundle savedInstanceState) {
		super.setViews(savedInstanceState);

		mIsLargeLayout = getResources().getBoolean(R.bool.isLargeLayout);

		if (savedInstanceState == null) {
			mDateToShow = (Calendar) getIntent().getSerializableExtra(
					Shared.DATE_TO_SHOW);
			if (mDateToShow == null) {
				mDateToShow = Calendar.getInstance();
			} else {
				log("got from intent " + DateUtils.toString(this, mDateToShow));
			}
		} else {
			mDateToShow = (Calendar) savedInstanceState
					.getSerializable(Shared.DATE_TO_SHOW);
			log("restore " + DateUtils.toString(this, mDateToShow));
		}

		setResult();

		if (mIsLargeLayout) {
			finish();
			return;
		}

		if (savedInstanceState == null) {
			Bundle args = new Bundle();
			args.putSerializable(Shared.DATE_TO_SHOW, mDateToShow);

			Fragment newFragment = new DayViewFragment();
			newFragment.setArguments(args);

			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, newFragment).commit();
		}
	}

	@Override
	protected void dataChanged() {
		mDayView.update();
	}

	@Override
	protected void saveActivityState(Bundle state) {
		state.putSerializable(Shared.DATE_TO_SHOW, mDateToShow);
		log("save " + DateUtils.toString(this, mDateToShow));
	}

	@Override
	public void onDayViewItemChangedByUser(Calendar date) {
		mDateToShow = date;
		setResult();
		log("put to result " + DateUtils.toString(this, date));
	}

	private void setResult() {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(Shared.DATE_TO_SHOW, mDateToShow);
		setResult(RESULT_OK, resultIntent);
	}

	@Override
	protected void onDestroy() {
		try {
			if (getIntent().getExtras().containsKey("setResult")) {
				Intent data = new Intent(this, MainActivity.class);
				data.putExtra(Shared.DATE_TO_SHOW, mDateToShow);
				PendingIntent.getActivity(this, 0, data,
						PendingIntent.FLAG_UPDATE_CURRENT).send();
			}
		} catch (CanceledException e) {
			e.printStackTrace();
		}

		super.onDestroy();
	}
}
