package com.anna.sent.soft.womancyc.fragments;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.adapters.MonthViewAdapter;
import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.data.DataKeeper;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.superclasses.StateSaver;
import com.anna.sent.soft.womancyc.superclasses.StateSaverFragment;
import com.anna.sent.soft.womancyc.utils.DateUtils;
import com.anna.sent.soft.womancyc.utils.OnSwipeTouchListener;

public class MonthViewFragment extends StateSaverFragment implements
		OnItemClickListener, OnItemLongClickListener, StateSaver,
		OnClickListener, OnDateSetListener {
	private static final String TAG = "moo";
	private static final boolean DEBUG = false;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

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

	private Button currentMonth;
	private Button prevMonth;
	private Button nextMonth;
	private GridView calendarView;
	private MonthViewAdapter adapter;
	private static final String CURRENT_MONTH_TEMPLATE = "MMMM yyyy";
	private boolean mIsLargeLayout;
	private Calendar mDateToShow = null;

	public MonthViewFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.view_month, null);
		return v;
	}

	@Override
	public void setViews(Bundle savedInstanceState) {
		log("onActivityCreated");
		mIsLargeLayout = getResources().getBoolean(R.bool.isLargeLayout);

		adapter = new MonthViewAdapter(getActivity(), mDataKeeper);

		prevMonth = (Button) getActivity().findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (Button) getActivity().findViewById(R.id.currentMonth);
		currentMonth.setOnClickListener(this);

		nextMonth = (Button) getActivity().findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(this);

		calendarView = (GridView) getActivity().findViewById(
				R.id.calendarGridView);
		calendarView.setAdapter(adapter);

		calendarView.setOnItemClickListener(this);
		calendarView.setOnItemLongClickListener(this);
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

	@Override
	public void onStart() {
		log("onStart");
		super.onStart();
		setSelectedDate(mDateToShow == null ? adapter.getSelectedDate()
				: mDateToShow);
		mDateToShow = null;
	}

	@Override
	public void restoreState(Bundle state) {
		mDateToShow = (Calendar) state.getSerializable(Shared.DATE_TO_SHOW);
		log("restore " + DateUtils.toString(getActivity(), mDateToShow), true);
	}

	@Override
	public void saveState(Bundle state) {
		log("save "
				+ DateUtils.toString(getActivity(), adapter.getSelectedDate()),
				true);
		state.putSerializable(Shared.DATE_TO_SHOW, adapter.getSelectedDate());
	}

	private Calendar getSelectedDate() {
		return adapter.getSelectedDate();
	}

	private void setSelectedDate(Calendar dateToShow) {
		adapter.setSelectedDate(dateToShow);
		currentMonth.setText(DateFormat.format(CURRENT_MONTH_TEMPLATE,
				dateToShow.getTime()));
		if (mIsLargeLayout) {
			showAsEmbeddedFragment(dateToShow);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Object item = adapter.getItem(position);
		if (item != null) {
			Calendar date = (Calendar) item;
			setSelectedDate(date);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		Object item = adapter.getItem(position);
		if (item != null) {
			Calendar date = (Calendar) item;
			setSelectedDate(date);
			if (!mIsLargeLayout) {
				showAsDialogFragment(date);
			}
		}

		return true;
	}

	private void showAsDialogFragment(Calendar date) {
		FragmentManager fragmentManager = getFragmentManager();
		DialogFragment newFragment = createDayView(date);
		newFragment.show(fragmentManager, newFragment.getClass()
				.getSimpleName());
	}

	private void showAsEmbeddedFragment(Calendar date) {
		FragmentManager fragmentManager = getFragmentManager();

		Fragment dayView = fragmentManager.findFragmentById(R.id.dayView);
		if (dayView != null) {
			fragmentManager.beginTransaction().remove(dayView).commit();
		}

		fragmentManager.beginTransaction()
				.add(R.id.dayView, createDayView(date)).commit();
	}

	private DialogFragment createDayView(Calendar date) {
		int index = new DateUtils().indexOf(mDataKeeper.getData(), date);
		CalendarData value;
		if (index >= 0) {
			value = mDataKeeper.getData().get(index);
		} else {
			value = new CalendarData(date);
		}

		Bundle args = new Bundle();
		args.putSerializable(value.getClass().getSimpleName(), value);

		DayViewFragment newFragment = new DayViewFragment();
		newFragment.setArguments(args);

		return newFragment;
	}

	private DataKeeper mDataKeeper = null;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof DataKeeper) {
			mDataKeeper = (DataKeeper) activity;
		}
	}

	public void update() {
		adapter.notifyDataSetChanged();
	}

	private void toPrevMonth() {
		Calendar dateToShow = (Calendar) getSelectedDate().clone();
		dateToShow.set(Calendar.DAY_OF_MONTH, 1);
		dateToShow.add(Calendar.MONTH, -1);
		setSelectedDate(dateToShow);
	}

	private void toNextMonth() {
		Calendar dateToShow = (Calendar) getSelectedDate().clone();
		dateToShow.set(Calendar.DAY_OF_MONTH, 1);
		dateToShow.add(Calendar.MONTH, 1);
		setSelectedDate(dateToShow);
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
			Bundle args = new Bundle();
			args.putSerializable(Shared.DATE_TO_SHOW, getSelectedDate());
			DatePickerDialogFragment dialog = new DatePickerDialogFragment();
			dialog.setArguments(args);
			dialog.setOnDateSetListener(this);
			dialog.show(getFragmentManager(), dialog.getClass().getSimpleName());
			break;
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		Calendar dateToShow = Calendar.getInstance();
		dateToShow.set(year, month, day);
		setSelectedDate(dateToShow);
	}
}