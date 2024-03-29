package com.anna.sent.soft.womancyc.fragments;

import org.joda.time.LocalDate;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.adapters.MonthViewAdapter;
import com.anna.sent.soft.womancyc.base.DataKeeperClient;
import com.anna.sent.soft.womancyc.database.DataKeeper;
import com.anna.sent.soft.womancyc.utils.OnSwipeTouchListener;

public class MonthViewFragment extends Fragment implements
		MonthViewAdapter.Listener, OnClickListener, DataKeeperClient {
	private static final String TAG = "moo";
	private static final boolean DEBUG = false;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

	@SuppressWarnings("unused")
	private void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	@SuppressWarnings("unused")
	private void log(String msg, boolean debug) {
		if (DEBUG && debug) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private CalendarListener mListener = null;

	public void setListener(CalendarListener listener) {
		mListener = listener;
	}

	/**
	 * must be not null! fragment gets it when onAttach() is called by parent
	 * activity
	 */
	private DataKeeper mDataKeeper = null;

	@Override
	public void setDataKeeper(DataKeeper dataKeeper) {
		mDataKeeper = dataKeeper;
	}

	private Button currentMonth;
	private GridView calendarView;
	private MonthViewAdapter adapter;

	public MonthViewFragment() {
		super();
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.view_month, null);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// log("onActivityCreated");
		adapter = new MonthViewAdapter(getActivity(), mDataKeeper, this);

		Button prevMonth = (Button) getActivity().findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (Button) getActivity().findViewById(R.id.currentMonth);
		currentMonth.setOnClickListener(this);

		Button nextMonth = (Button) getActivity().findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(this);

		calendarView = (GridView) getActivity().findViewById(
				R.id.calendarGridView);
		calendarView.setAdapter(adapter);
		calendarView
				.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
					@Override
					public boolean onSwipeRight() {
						toPrevMonth();
						return true;
					}

					@Override
					public boolean onSwipeLeft() {
						toNextMonth();
						return true;
					}
				});
	}

	public LocalDate getSelectedDate() {
		return adapter.getSelectedDate();
	}

	@SuppressLint("SimpleDateFormat")
	public void setSelectedDate(LocalDate date) {
		// log("set selected date to " + DateUtils.toString(date));
		adapter.setSelectedDate(date);

		int month = date.getMonthOfYear();
		int year = date.getYear();
		String[] monthNames = getResources().getStringArray(R.array.MonthNames);

		currentMonth.setText(String
				.format("%s %d", monthNames[month - 1], year));
	}

	@Override
	public void onItemClick() {
		if (mListener != null) {
			mListener.updateDetailedView();
		}
	}

	@Override
	public void onItemLongClick() {
		if (mListener != null) {
			mListener.showDetailedView();
		}
	}

	public void update() {
		adapter.update();
	}

	private void toPrevMonth() {
		if (mListener != null) {
			LocalDate dateToShow = new LocalDate(adapter.getYear(),
					adapter.getMonth(), 1).minusMonths(1);
			mListener.navigateToDate(dateToShow);
		}
	}

	private void toNextMonth() {
		if (mListener != null) {
			LocalDate dateToShow = new LocalDate(adapter.getYear(),
					adapter.getMonth(), 1).plusMonths(1);
			mListener.navigateToDate(dateToShow);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.prevMonth:
			toPrevMonth();
			break;
		case R.id.nextMonth:
			toNextMonth();
			break;
		case R.id.currentMonth:
			if (mListener != null) {
				mListener.showDatePickerToChangeDate();
			}

			break;
		}
	}
}