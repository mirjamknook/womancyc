package com.anna.sent.soft.womancyc.widget;

import java.util.Calendar;

import android.content.Context;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.adapters.Calculator;
import com.anna.sent.soft.womancyc.database.DataKeeper;

public class BuilderSmall extends Builder {
	@Override
	protected String getResult(Context context, DataKeeper dataKeeper) {
		Calculator calc = new Calculator(dataKeeper);
		Calendar today = Calendar.getInstance();
		int dayOfCycle = calc.getDayOfCycle(today);
		return dayOfCycle > 0 ? String.valueOf(dayOfCycle) : context
				.getString(R.string.noData);
	}
}
