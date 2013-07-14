package com.anna.sent.soft.womancyc.fragments;

import java.util.Calendar;

import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.anna.sent.soft.womancyc.database.DataKeeper;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.superclasses.DataKeeperClient;
import com.anna.sent.soft.womancyc.utils.DateUtils;
import com.anna.sent.soft.womancyc.utils.OnSwipeTouchListener;

public class MonthViewFragment extends Fragment implements OnItemClickListener,
		OnItemLongClickListener, OnClickListener, OnDateSetListener,
		DataKeeperClient {
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

	public interface Listener {
		public void onMonthViewItemChangedByUser(Calendar date);

		public void onMonthViewItemLongClick(Calendar date);
	}

	private Listener mListener = null;

	public void setListener(Listener listener) {
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
	private static final String CURRENT_MONTH_TEMPLATE = "MMMM yyyy";

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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		log("onActivityCreated");
		adapter = new MonthViewAdapter(getActivity(), mDataKeeper);

		Button prevMonth = (Button) getActivity().findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (Button) getActivity().findViewById(R.id.currentMonth);
		currentMonth.setOnClickListener(this);

		Button nextMonth = (Button) getActivity().findViewById(R.id.nextMonth);
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

	public Calendar getSelectedDate() {
		return adapter.getSelectedDate();
	}

	public void setSelectedDate(Calendar date) {
		log("set selected date to " + DateUtils.toString(getActivity(), date));
		adapter.setSelectedDate(date);
		currentMonth.setText(DateFormat.format(CURRENT_MONTH_TEMPLATE,
				date.getTime()));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Object item = adapter.getItem(position);
		if (item != null) {
			Calendar date = (Calendar) item;
			setSelectedDate(date);

			if (mListener != null) {
				mListener.onMonthViewItemChangedByUser(date);
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		Object item = adapter.getItem(position);
		if (item != null) {
			Calendar date = (Calendar) item;
			setSelectedDate(date);

			if (mListener != null) {
				mListener.onMonthViewItemChangedByUser(date);
			}

			if (mListener != null) {
				mListener.onMonthViewItemLongClick(date);
			}
		}

		return true;
	}

	public void update() {
		adapter.update();
	}

	private void toPrevMonth() {
		Calendar dateToShow = (Calendar) adapter.getSelectedDate().clone();
		dateToShow.set(Calendar.DAY_OF_MONTH, 1);
		dateToShow.add(Calendar.MONTH, -1);
		setSelectedDate(dateToShow);
		if (mListener != null) {
			mListener.onMonthViewItemChangedByUser(dateToShow);
		}
	}

	private void toNextMonth() {
		Calendar dateToShow = (Calendar) adapter.getSelectedDate().clone();
		dateToShow.set(Calendar.DAY_OF_MONTH, 1);
		dateToShow.add(Calendar.MONTH, 1);
		setSelectedDate(dateToShow);
		if (mListener != null) {
			mListener.onMonthViewItemChangedByUser(dateToShow);
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
			Bundle args = new Bundle();
			args.putSerializable(Shared.DATE_TO_SHOW, adapter.getSelectedDate());
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
		if (mListener != null) {
			mListener.onMonthViewItemChangedByUser(dateToShow);
		}
	}
}
