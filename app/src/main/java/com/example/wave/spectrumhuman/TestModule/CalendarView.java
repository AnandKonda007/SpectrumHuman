package com.example.wave.spectrumhuman.TestModule;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.wave.spectrumhuman.DataBase.UrineResultsDataController;
import com.example.wave.spectrumhuman.DataBase.UserDataController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.R;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class CalendarView extends Fragment {
	 ViewSwitcher calendarSwitcher;
	 TextView currentMonth;
	public GregorianCalendar month, itemmonth;
    public ArrayList<String> items;
    public Adapter adapter;
	public Handler handler;
	public  String SelectedDate;
	TextView current_month;
	String selectedMonthVal,tempSelectedMonthVal,selectedYearVal,tempSelectedYearVal;
	//
	ArrayList<String> monthEnglishArray,monthLanuagesArray;
	ArrayList<String> yearsArray;
	SimpleDateFormat dateFormat;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final RelativeLayout calendarLayout = (RelativeLayout)inflater.inflate(R.layout.calendar, null);
		final GridView calendarDayGrid = (GridView)calendarLayout.findViewById(R.id.calendar_days_grid);

        loadMothArray();// load month array
		loadYearArray();//load year array

		final GestureDetector swipeDetector = new GestureDetector(getActivity(), new SwipeGesture(getActivity()));
		final GridView calendarGrid = (GridView)calendarLayout.findViewById(R.id.calendar_grid);
		calendarSwitcher = (ViewSwitcher)calendarLayout.findViewById(R.id.calendar_switcher);
		currentMonth = (TextView)calendarLayout.findViewById(R.id.current_month);
		 setLocaleForCalendar();
		currentMonth.setText(dateFormat.format(month.getTime()));
		currentMonth.setPaintFlags(currentMonth.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

		current_month=(TextView) calendarLayout.findViewById(R.id.current_month);

		itemmonth = (GregorianCalendar) month.clone();
		items = new ArrayList<String>();
		adapter = new Adapter(getActivity(), month);
		calendarGrid.setAdapter(adapter);
		handler = new Handler();
		handler.post(calendarUpdater);
		final TextView nextMonth = (TextView) calendarLayout.findViewById(R.id.next_month);
		nextMonth.setOnClickListener(new NextMonthClickListener());
		final TextView prevMonth = (TextView) calendarLayout.findViewById(R.id.previous_month);
		prevMonth.setOnClickListener(new PreviousMonthClickListener());
		calendarGrid.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return swipeDetector.onTouchEvent(event);

			}
		});
		calendarDayGrid.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.day_item, R.id.tvText, getResources().getStringArray(R.array.days_array)));
		calendarGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				String selected= String.valueOf(adapter.getItem(i));
				Log.e("sele","call"+selected);
				if(items.contains(adapter.getItem(i)))
				{
					Log.e("SelectedDate",""+month.getTime());
					Intent intent = new Intent();
					intent.putExtra("editTextValue", selected);
					getActivity().setResult(RESULT_OK, intent);
				}else {
					Toast.makeText(getActivity(),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DATA_IS_NOT_AVAILABLE_TOAST_KEY),Toast.LENGTH_SHORT).show();
				}
				getActivity().finish();
			}
		});

		current_month.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				loadMonthYearPickers();
			}
		});
        return calendarLayout;
	}
	public void loadMonthYearPickers(){
		Log.e("loadMonthYearPickers","call");
		final Dialog mod = new Dialog(getActivity());
		mod.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mod.setContentView(R.layout.activity_monthyearpicker_alert);
		TextView txtTitle = (TextView) mod.findViewById(R.id.text_info);
		txtTitle.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SELECT_MONTH_AND_YEAR_KEY));
		mod.show();
		mod.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
		mod.setCanceledOnTouchOutside(false);
		Log.e("currentMonth",""+currentMonth.getText().toString());
		final String[] monthArray=currentMonth.getText().toString().split(" ");
		Log.e("currentMonth",""+monthArray[0]);
		Log.e("currentMonth",""+monthArray[1]);

		tempSelectedMonthVal=monthArray[0];
		selectedMonthVal=tempSelectedMonthVal;

		tempSelectedYearVal=monthArray[1];
		selectedYearVal=tempSelectedYearVal;


		//
		final NumberPicker monthPicker = (NumberPicker) mod.findViewById(R.id.number_picker);
		monthPicker.setMinValue(0);
		monthPicker.setMaxValue(monthLanuagesArray.size()-1);
		String[] mStringArray = new String[monthLanuagesArray.size()];

		mStringArray = monthLanuagesArray.toArray(mStringArray);
		monthPicker.setDisplayedValues(mStringArray);
		Log.e("MonthValonth",""+selectedMonthVal);

		int index = monthLanuagesArray.indexOf(selectedMonthVal);
		Log.e("index",""+monthLanuagesArray.indexOf(selectedMonthVal));

		monthPicker.setValue(index);
		monthPicker.setWrapSelectorWheel(true);

		monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

				tempSelectedMonthVal=monthEnglishArray.get(newVal);
				selectedMonthVal=tempSelectedMonthVal;

			}
		});
		////
		//
		final NumberPicker yearPicker = (NumberPicker) mod.findViewById(R.id.number_pickertwo);
		yearPicker.setMinValue(0);
		yearPicker.setMaxValue(yearsArray.size()-1);
		String[] mArray = new String[yearsArray.size()];

		mArray = yearsArray.toArray(mArray);
		yearPicker.setDisplayedValues(mArray);

		int index1 = yearsArray.indexOf(selectedYearVal);
		yearPicker.setValue(index1);
		yearPicker.setWrapSelectorWheel(true);


		yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

				tempSelectedYearVal=yearsArray.get(newVal);
				selectedYearVal=tempSelectedYearVal;

			}
		});
		////
		Button ok=(Button)mod.findViewById(R.id.btn_ok);
		Button cancle=(Button)mod.findViewById(R.id.btn_cancel);
		ok.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
		cancle.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY));

		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mod.dismiss();

				selectedMonthVal=tempSelectedMonthVal;
				selectedYearVal=tempSelectedYearVal;
				String  requiredString =LanguageTextController.getInstance().currentLanguageDictionary.get(selectedMonthVal)+" "+selectedYearVal;
				currentMonth.setText(requiredString);
				month.set(Integer.parseInt(selectedYearVal),monthEnglishArray.indexOf(selectedMonthVal),1);
				refreshCalendar();
			}
		});
		cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mod.dismiss();

				tempSelectedMonthVal=selectedMonthVal;
				tempSelectedYearVal=selectedYearVal;
			}
		});
	}
	public void loadMothArray()
	{
		Log.e("loadMothArray","call");

		monthEnglishArray =new ArrayList<String>();

		monthEnglishArray.add(LanguagesKeys.JANUVARY_KEY);
		monthEnglishArray.add(LanguagesKeys.FEBRUVARY_KEY);
		monthEnglishArray.add(LanguagesKeys.MARCH_KEY);
		monthEnglishArray.add(LanguagesKeys.APRIL_KEY);
		monthEnglishArray.add(LanguagesKeys.MAY_KEY);
		monthEnglishArray.add(LanguagesKeys.JUNE_KEY);
		monthEnglishArray.add(LanguagesKeys.JULY_KEY);
		monthEnglishArray.add(LanguagesKeys.AUGUST_KEY);
		monthEnglishArray.add(LanguagesKeys.SEPTEMBER_KEY);
		monthEnglishArray.add(LanguagesKeys.OCTOBER_KEY);
		monthEnglishArray.add(LanguagesKeys.NOVEMBER_KEY);
		monthEnglishArray.add(LanguagesKeys.DECEMBER_KEY);

		// load Languages Array
		monthLanuagesArray=new ArrayList<String>();

		for (String objRelative: monthEnglishArray)
		{
			monthLanuagesArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(objRelative));
		}
	}
	public void loadYearArray()
	{
		yearsArray=new ArrayList<String>();

		for (int i=1987;i<=2050;i++)
		{
			yearsArray.add(String.valueOf(i));
		}
	}
	protected void updateCurrentMonth() {
		adapter.refreshDays();
		currentMonth.setText(dateFormat.format(month.getTime()));
		Log.e("updateCurrentMonth",""+currentMonth.getText().toString());
	}

	protected final void onNextMonth()
	{
		refreshCalendar();
		if (month.get(GregorianCalendar.MONTH) == month.getActualMaximum(GregorianCalendar.MONTH))
		{
			month.set((month.get(GregorianCalendar.YEAR) + 1),
					month.getActualMinimum(GregorianCalendar.MONTH), 1);

		} else {
			month.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) + 1);
		}
		updateCurrentMonth();
	}
	protected final void onPreviousMonth() {

		refreshCalendar();
		if (month.get(GregorianCalendar.MONTH) == month.getActualMinimum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) - 1), month.getActualMaximum(GregorianCalendar.MONTH), 1);

		} else {
			month.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) - 1);
		}
		updateCurrentMonth();
	}

	private final class NextMonthClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			onNextMonth();
			refreshCalendar();
		}
	}

	private final class PreviousMonthClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			onPreviousMonth();
			refreshCalendar();
		}
	}

	private final class SwipeGesture extends SimpleOnGestureListener {
		private final int swipeMinDistance;
		private final int swipeThresholdVelocity;

		public SwipeGesture(Context context) {
			final ViewConfiguration viewConfig = ViewConfiguration.get(context);
			swipeMinDistance = viewConfig.getScaledTouchSlop();
			swipeThresholdVelocity = viewConfig.getScaledMinimumFlingVelocity();
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (e1.getX() - e2.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
				onNextMonth();
			}  else if (e2.getX() - e1.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
				onPreviousMonth();
			}
			return false;
		}
	}


	public void refreshCalendar() {
		adapter.refreshDays();
		adapter.notifyDataSetChanged();
		handler.post(calendarUpdater); // generate some resultCalendar items
		currentMonth.setText(dateFormat.format(month.getTime()));
		//currentMonth.setText(DateFormat.format("MMMM yyyy", month));
	}

	public Runnable calendarUpdater = new Runnable() {

		@Override
		public void run() {
			items.clear();

			//java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
			for (int i = 0; i < UrineResultsDataController.getInstance().allUrineResults.size(); i++) {
				itemmonth.add(GregorianCalendar.DATE, 1);
				String s=UrineResultsDataController.getInstance().allUrineResults.get(i).getTestedTime();
				long yourmilliseconds = Long.parseLong(s);
				Log.e("yourmilliseconds",""+yourmilliseconds);
				SimpleDateFormat weekFormatter = new SimpleDateFormat("yyyy-MM-dd",Locale.SIMPLIFIED_CHINESE);
				Date resultdate = new Date(yourmilliseconds*1000);
				String weekString = weekFormatter.format(resultdate);
				items.add(weekString);

			}
			adapter.setItems(items);
			adapter.notifyDataSetChanged();
		}
	};
	public void setLocaleForCalendar(){
		if (UserDataController.getInstance().currentUser.getPreferedLanguage().equals("Simplified") )
		{
			month = (GregorianCalendar) GregorianCalendar.getInstance(Locale.SIMPLIFIED_CHINESE);
           dateFormat=new SimpleDateFormat("MMMM yyyy",Locale.SIMPLIFIED_CHINESE);
			Log.e("CHINESE","call");

		}
		else if( UserDataController.getInstance().currentUser.getPreferedLanguage().equals("Traditional"))
		{
			Log.e("CHINESETraditional","call");
			month = (GregorianCalendar) GregorianCalendar.getInstance(Locale.SIMPLIFIED_CHINESE);
			dateFormat=new SimpleDateFormat("MMMM yyyy",Locale.SIMPLIFIED_CHINESE);

		} else
		{
			Log.e("english","call");
			month = (GregorianCalendar) GregorianCalendar.getInstance(Locale.ENGLISH);
			dateFormat=new SimpleDateFormat("MMMM yyyy",Locale.ENGLISH);
		}
	}
}