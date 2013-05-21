package com.anna.sent.soft.womancyc.adapters;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.data.DataKeeper;
import com.anna.sent.soft.womancyc.utils.DateUtils;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public class MonthCalendarViewAdapter extends BaseAdapter {
	private final Context mContext;
	private final List<Calendar> mMonthCalendarValues = new ArrayList<Calendar>();
	private List<Integer> mDayOfWeekValues = new ArrayList<Integer>();
	private String[] mDayOfWeekNames;
	protected int mMonth, mYear;
	private Calendar mSelectedDate, mToday;

	private DataKeeper mDataKeeper = null;

	public MonthCalendarViewAdapter(Context context, DataKeeper dataKeeper) {
		super();
		mContext = context;
		mDataKeeper = dataKeeper;
		mToday = Calendar.getInstance();
		if (mToday.getFirstDayOfWeek() == Calendar.SUNDAY) {
			mDayOfWeekValues.add(Calendar.SUNDAY);
			mDayOfWeekValues.add(Calendar.MONDAY);
			mDayOfWeekValues.add(Calendar.TUESDAY);
			mDayOfWeekValues.add(Calendar.WEDNESDAY);
			mDayOfWeekValues.add(Calendar.THURSDAY);
			mDayOfWeekValues.add(Calendar.FRIDAY);
			mDayOfWeekValues.add(Calendar.SATURDAY);
		} else {
			mDayOfWeekValues.add(Calendar.MONDAY);
			mDayOfWeekValues.add(Calendar.TUESDAY);
			mDayOfWeekValues.add(Calendar.WEDNESDAY);
			mDayOfWeekValues.add(Calendar.THURSDAY);
			mDayOfWeekValues.add(Calendar.FRIDAY);
			mDayOfWeekValues.add(Calendar.SATURDAY);
			mDayOfWeekValues.add(Calendar.SUNDAY);
		}

		DateFormatSymbols symbols = new DateFormatSymbols();
		mDayOfWeekNames = symbols.getShortWeekdays();

		mSelectedDate = (Calendar) mToday.clone();
		int month = mToday.get(Calendar.MONTH);
		int year = mToday.get(Calendar.YEAR);
		initMonthCalendar(month, year);
	}

	public void setSelectedDate(Calendar value) {
		int year = value.get(Calendar.YEAR);
		int month = value.get(Calendar.MONTH);
		int day = value.get(Calendar.DAY_OF_MONTH);
		mSelectedDate.set(Calendar.YEAR, year);
		mSelectedDate.set(Calendar.MONTH, month);
		mSelectedDate.set(Calendar.DAY_OF_MONTH, day);
		if (mYear != year || mMonth != month) {
			initMonthCalendar(month, year);
		}

		notifyDataSetChanged();
	}

	public Calendar getSelectedDate() {
		return mSelectedDate;
	}

	@Override
	public Object getItem(int position) {
		if (position >= mDayOfWeekValues.size()) {
			return mMonthCalendarValues.get(position - mDayOfWeekValues.size());
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return mMonthCalendarValues.size() + mDayOfWeekValues.size();
	}

	private void initMonthCalendar(int month, int year) {
		mYear = year;
		mMonth = month;
		mMonthCalendarValues.clear();

		Calendar current = Calendar.getInstance();
		current.set(year, month, 1);

		Calendar prevMonth = (Calendar) current.clone();
		prevMonth.add(Calendar.MONTH, -1);

		Calendar nextMonth = (Calendar) current.clone();
		nextMonth.add(Calendar.MONTH, 1);

		int trailing = mDayOfWeekValues.indexOf(current
				.get(Calendar.DAY_OF_WEEK));
		int daysInPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
		for (int i = 1; i <= trailing; ++i) {
			Calendar item = Calendar.getInstance();
			item.set(prevMonth.get(Calendar.YEAR),
					prevMonth.get(Calendar.MONTH), daysInPrevMonth - trailing
							+ i);
			mMonthCalendarValues.add(item);
		}

		int daysInCurrentMonth = current
				.getActualMaximum(Calendar.DAY_OF_MONTH);
		for (int i = 1; i <= daysInCurrentMonth; ++i) {
			Calendar item = Calendar.getInstance();
			item.set(year, month, i);
			mMonthCalendarValues.add(item);
		}

		int leading = mDayOfWeekValues.size() * 6 - mMonthCalendarValues.size();
		for (int i = 1; i <= leading; ++i) {
			Calendar item = Calendar.getInstance();
			item.set(nextMonth.get(Calendar.YEAR),
					nextMonth.get(Calendar.MONTH), i);
			mMonthCalendarValues.add(item);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View cell = null;

		if (position < mDayOfWeekValues.size()) {
			if (convertView != null
					&& convertView.getId() == getDayOfWeekViewId()) {
				cell = convertView;
			}

			if (cell == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				cell = inflater.inflate(getDayOfWeekLayoutResource(), parent,
						false);
			}

			initDayOfWeekItem(cell, position);
		} else if (mMonthCalendarValues.size() > 0) {
			if (convertView != null
					&& convertView.getId() == getDayOfMonthViewId()) {
				cell = convertView;
			}

			if (cell == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				cell = inflater.inflate(getDayOfMonthLayoutResource(), parent,
						false);
			}

			Calendar item = mMonthCalendarValues.get(position
					- mDayOfWeekValues.size());
			initDayOfMonth(cell, position, item);
		}

		return cell;
	}

	private int getDayOfWeekLayoutResource() {
		return R.layout.day_of_week;
	}

	private int getDayOfWeekViewId() {
		return R.id.dayOfWeek;
	}

	private void initDayOfWeekItem(View cell, int position) {
		TextView dayOfWeekTextView = (TextView) cell
				.findViewById(R.id.dayOfWeekTextView);
		dayOfWeekTextView.setText(mDayOfWeekNames[mDayOfWeekValues
				.get(position)]);
	}

	protected int getDayOfMonthLayoutResource() {
		return R.layout.day_of_month;
	}

	protected int getDayOfMonthViewId() {
		return R.id.dayOfMonth;
	}

	protected void initDayOfMonth(View cell, int position, Calendar item) {
		List<CalendarData> data = mDataKeeper.getData();

		Calendar begin = Calendar.getInstance();
		begin.set(2013, Calendar.MAY, 1);
		int dayOfCycle = DateUtils.getDifferenceInDays(item, begin);

		View view = cell.findViewById(R.id.dayOfMonth);
		view.setTag(item);

		TextView dayOfCycleTextView = (TextView) cell
				.findViewById(R.id.dayOfCycleTextView);
		dayOfCycleTextView.setText(String.valueOf(dayOfCycle));

		TextView dayOfMonthTextView = (TextView) cell
				.findViewById(R.id.dayOfMonthTextView);
		dayOfMonthTextView.setText(String.valueOf(item
				.get(Calendar.DAY_OF_MONTH)));

		if (item.get(Calendar.MONTH) != mMonth) {
			dayOfCycleTextView.setTextColor(Color.rgb(0xff, 0xaa, 0x00));
			if (ThemeUtils.getThemeId(mContext) == ThemeUtils.DARK_THEME) {
				dayOfMonthTextView.setTextColor(Color.DKGRAY);
			} else {
				dayOfMonthTextView.setTextColor(Color.LTGRAY);
			}
		} else {
			dayOfCycleTextView.setTextColor(Color.rgb(0xff, 0x66, 0x00));
			if (ThemeUtils.getThemeId(mContext) == ThemeUtils.DARK_THEME) {
				dayOfMonthTextView.setTextColor(Color.WHITE);
			} else {
				dayOfMonthTextView.setTextColor(Color.BLACK);
			}
		}

		int index = new DateUtils().indexOf(data, item);
		if (DateUtils.datesAreEqual(item, mSelectedDate)) {
			cell.setBackgroundColor(mContext.getResources().getColor(
					R.color.blue));
		} else if (index >= 0) {
			cell.setBackgroundColor(Color.RED);
		} else {
			cell.setBackgroundColor(0);
		}

		if (DateUtils.datesAreEqual(item, mToday)) {
			dayOfMonthTextView.setTextColor(Color.BLUE);
		}

	}
}