package net.itgoo.uidatepicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.itgoo.uidatepicker.R;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

public class UIDatePicker extends PopupWindow {
    //年月日
    public static final int DATE_PICKER_MODE_DATE = 0;
    //时分
    public static final int DATE_PICKER_MODE_TIME = 1;
    //年月
    public static final int DATE_PICKER_MODE_YEAR_MONTH = 2;
    //年
    public static final int DATE_PICKER_MODE_YEAR = 3;
    //默认最小日期
    private static final int DEFAULT_MIN_YEAR = 1900;
    //默认最大日期
    private static final int DEFAULT_MAX_YEAR = 2099;
    private int mMode = DATE_PICKER_MODE_DATE;
    //最大显示日期
    private Date mMaxDate;
    //最小显示日期
    private Date mMinDate;
    //当前显示日期
    private Date mCurrentDate;
    private Calendar mCalendar;
    private Context mContext;
    private UIDatePickerListener mDatePickerListener;
    private LinearLayout mWheelParentView;
    private WheelView mWheelView1;
    private WheelView mWheelView2;
    private WheelView mWheelView3;
    private Button mCancelButton;
    private Button mDoneButton;
    private boolean mIs24hourFormat = true;
    private boolean mIsRememberLastSelectDate = false;

    public UIDatePicker(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    private void init() {
        initDateValue();
        setDateFormat();

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.UIDatePickerAnimStyle);
        setBackgroundDrawable(new BitmapDrawable());

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.ui_date_picker_view_picker, null);
        mWheelParentView = (LinearLayout) view.findViewById(R.id.ui_date_picker_view_picker_content_layout);
        mCancelButton = (Button) view.findViewById(R.id.ui_date_picker_view_picker_left_button);
        mDoneButton = (Button) view.findViewById(R.id.ui_date_picker_view_picker_right_button);
        mCancelButton.setOnClickListener(mCancelClickListener);
        mDoneButton.setOnClickListener(mDoneClickListener);
        setContentView(view);
    }

    /**
     * 设置当前日期
     *
     * @param date
     */
    public void setDate(Date date) {
        mCurrentDate = date;
    }

    /**
     * 设置最大显示日期
     *
     * @param date
     */
    public void setMaximumDate(Date date) {
        mMaxDate = date;
    }

    /**
     * 设置最小显示日期
     *
     * @param date
     */
    public void setMinimumDate(Date date) {
        mMinDate = date;
    }

    /**
     * 设置类型
     *
     * @param mode
     */
    public void setDatePickerMode(int mode) {
        mMode = mode;
    }

    /**
     * 是否记住最后选择的日期
     * @param b
     */
    public void setIsRememberLastSelectDate(boolean b) {
        mIsRememberLastSelectDate = b;
    }

    /**
     * 设置监听
     *
     * @param datePickerListener
     */
    public void setDatePickerListener(UIDatePickerListener datePickerListener) {
        mDatePickerListener = datePickerListener;
    }

    /**
     * @param parentId
     */
    public void show(int parentId) {
        View parentView = LayoutInflater.from(mContext).inflate(parentId, null);
        show(parentView);
    }

    /**
     * @param parent
     */
    public void show(View parent) {
        setDatePicker();
        showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 初始化日期变量
     */
    private void initDateValue() {
        mCalendar = Calendar.getInstance();

        mCurrentDate = new Date();
        mCalendar.set(DEFAULT_MAX_YEAR, 12, 31);
        mMaxDate = mCalendar.getTime();

        mCalendar.set(DEFAULT_MIN_YEAR, 1, 1);
        mMinDate = mCalendar.getTime();
    }

    /**
     * 设置日期格式
     */
    private void setDateFormat() {
        //判断是否24小时格式
        mIs24hourFormat = DateFormat.is24HourFormat(mContext);
    }

    /**
     * 设置DatePicker
     */
    private void setDatePicker() {
        if (mMode == DATE_PICKER_MODE_DATE) {
            setDatePickerModeDate();
        } else if (mMode == DATE_PICKER_MODE_TIME) {
            setDatePickerModeTime();
        } else if (mMode == DATE_PICKER_MODE_YEAR_MONTH) {
            setDatePickerModeDate();
            mWheelView3.setVisibility(View.GONE);
        } else if (mMode == DATE_PICKER_MODE_YEAR) {
            setDatePickerModeDate();
            mWheelView2.setVisibility(View.GONE);
            mWheelView3.setVisibility(View.GONE);
        }
    }

    /******************************************************************************************
     * 日期型
     *************************************************************/

    /**
     * 设置日期型DatePicker
     */
    private void setDatePickerModeDate() {
        mWheelParentView.removeAllViews();

        mWheelView1 = new WheelView(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2.0f);
        mWheelView1.setLayoutParams(lp);
        mWheelView2 = new WheelView(mContext);
        lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        mWheelView2.setLayoutParams(lp);
        mWheelView3 = new WheelView(mContext);
        lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        mWheelView3.setLayoutParams(lp);

        mWheelParentView.addView(mWheelView1);
        mWheelParentView.addView(mWheelView2);
        mWheelParentView.addView(mWheelView3);

        //年
        mCalendar.setTime(mMinDate);
        int minYear = mCalendar.get(Calendar.YEAR);
        mCalendar.setTime(mCurrentDate);
        int currentYear = mCalendar.get(Calendar.YEAR);
        DatePickerAdapter yearAdapter = new DatePickerAdapter(mContext, getDateYearArray(), -1);
        yearAdapter.mSuffix = mContext.getString(R.string.ui_date_picker_year);
        mWheelView1.setViewAdapter(yearAdapter);
        mWheelView1.setCyclic(true);
        mWheelView1.setCurrentItem(currentYear - minYear);
        mWheelView1.addChangingListener(mDateWheelChangedListener);
        yearAdapter.mCurrentItem = mWheelView1.getCurrentItem();

        //月
        mCalendar.setTime(mCurrentDate);
        int currentMonth = mCalendar.get(Calendar.MONTH);
        DatePickerAdapter monthAdapter = new DatePickerAdapter(mContext, getDateMonthArray(), -1);
        monthAdapter.mSuffix = mContext.getString(R.string.ui_date_picker_month);;
        mWheelView2.setViewAdapter(monthAdapter);
        mWheelView2.setCyclic(true);
        mWheelView2.setCurrentItem(currentMonth);
        mWheelView2.addChangingListener(mDateWheelChangedListener);
        monthAdapter.mCurrentItem = mWheelView2.getCurrentItem();

        //日
        mCalendar.setTime(mCurrentDate);
        int currentDay = mCalendar.get(Calendar.DATE);
        int maxDays = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        DatePickerAdapter dayAdapter = new DatePickerAdapter(mContext, getDateDayArray(31), maxDays - 1);
        dayAdapter.mSuffix = mContext.getString(R.string.ui_date_picker_day);;
        mWheelView3.setViewAdapter(dayAdapter);
        mWheelView3.setCyclic(true);
        mWheelView3.setCurrentItem(currentDay - 1);
        mWheelView3.addChangingListener(mDateWheelChangedListener);
        dayAdapter.mCurrentItem = mWheelView3.getCurrentItem();
    }

    /**
     * 更新
     */
    private void updateDateModeDatePicker() {
        mCalendar.setTime(mMinDate);
        mCalendar.set(Calendar.YEAR, mCalendar.get(Calendar.YEAR) + mWheelView1.getCurrentItem());
        mCalendar.set(Calendar.MONTH, mWheelView2.getCurrentItem());

        //获取某年某月的最大天数,并更新
        int maxDays = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        DatePickerAdapter adapter = (DatePickerAdapter) mWheelView3.getViewAdapter();
        adapter.mValidMaxValue = maxDays - 1;
        if (maxDays - 1 < mWheelView3.getCurrentItem()) {
            mWheelView3.setCurrentItem(maxDays - 1, true);
        }
        mWheelView3.invalidateWheel(true);
    }

    /**
     * 获取年份字符串数组
     *
     * @return
     */
    private String[] getDateYearArray() {
        ArrayList<String> list = new ArrayList<String>();
        mCalendar.setTime(mMinDate);
        int minYear = mCalendar.get(Calendar.YEAR);
        mCalendar.setTime(mMaxDate);
        int maxYear = mCalendar.get(Calendar.YEAR);

        for (int i = minYear; i < maxYear; i++) {
            list.add(String.format("%d", i));
        }

        return list.toArray(new String[list.size()]);
    }

    /**
     * 获取月份字符串
     *
     * @return
     */
    private String[] getDateMonthArray() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 1; i <= 12; i++) {
            list.add(String.format("%d", i));
        }

        return list.toArray(new String[list.size()]);
    }

    /**
     * 获取天字符串
     *
     * @return
     */
    private String[] getDateDayArray(int maxDay) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 1; i <= maxDay; i++) {
            list.add(String.format("%d", i));
        }

        return list.toArray(new String[list.size()]);
    }

    //日期变更监听
    private OnWheelChangedListener mDateWheelChangedListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            DatePickerAdapter adapter = (DatePickerAdapter) wheel.getViewAdapter();
            adapter.mCurrentItem = newValue;
            wheel.invalidateWheel(true);
            if (wheel != mWheelView3) {
                updateDateModeDatePicker();
            }
        }
    };

    /******************************************************************************************
     * 时间型
     *************************************************************/

    /**
     * 设置时间型DatePicker
     */
    private void setDatePickerModeTime() {
        mWheelParentView.removeAllViews();

        LinearLayout.LayoutParams lp = null;
        if (mIs24hourFormat == false) {
            mWheelView1 = new WheelView(mContext);
            lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2.0f);
            mWheelView1.setLayoutParams(lp);
            mWheelParentView.addView(mWheelView1);
        }
        mWheelView2 = new WheelView(mContext);
        lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        mWheelView2.setLayoutParams(lp);
        mWheelView3 = new WheelView(mContext);
        lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        mWheelView3.setLayoutParams(lp);

        mWheelParentView.addView(mWheelView2);
        mWheelParentView.addView(mWheelView3);

        //小时
        mCalendar.setTime(mCurrentDate);
        int currentHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        DatePickerAdapter hourAdapter = new DatePickerAdapter(mContext, getDateHourArray(), -1);
        mWheelView2.setViewAdapter(hourAdapter);
        mWheelView2.setCyclic(true);
        mWheelView2.setCurrentItem((mIs24hourFormat == true ? currentHour : (currentHour - 1)));
        mWheelView2.addChangingListener(mTimeWheelChangedListener);
        hourAdapter.mCurrentItem = mWheelView2.getCurrentItem();

        //分
        int currentMinute = mCalendar.get(Calendar.MINUTE);
        DatePickerAdapter minuteAdapter = new DatePickerAdapter(mContext, getDateMinuteArray(), -1);
        mWheelView3.setViewAdapter(minuteAdapter);
        mWheelView3.setCyclic(true);
        mWheelView3.setCurrentItem(currentMinute);
        mWheelView3.addChangingListener(mTimeWheelChangedListener);
        minuteAdapter.mCurrentItem = mWheelView3.getCurrentItem();

        //上午下午
        if (mIs24hourFormat == false) {
            DatePickerAdapter amPmAdapter = new DatePickerAdapter(mContext, getAmPmArray(), -1);
            mWheelView1.setViewAdapter(amPmAdapter);
            mWheelView1.setCyclic(false);
            mWheelView1.setCurrentItem((currentHour < 12 ? 0 : 1));
            mWheelView1.addChangingListener(mTimeWheelChangedListener);
            amPmAdapter.mCurrentItem = mWheelView1.getCurrentItem();
        }
    }

    /**
     * 获取小时字符串
     *
     * @return
     */
    private String[] getDateHourArray() {
        ArrayList<String> list = new ArrayList<String>();
        if (mIs24hourFormat) {
            for (int i = 0; i <= 23; i++) {
                if (i < 10) {
                    list.add(String.format("0%d", i));
                } else {
                    list.add(String.format("%d", i));
                }
            }
            return list.toArray(new String[list.size()]);
        } else {
            for (int i = 1; i <= 12; i++) {
                list.add(String.format("%d", i));
            }
            return list.toArray(new String[list.size()]);
        }
    }

    /**
     * 获取分字符串
     *
     * @return
     */
    private String[] getDateMinuteArray() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i <= 59; i++) {
            if (i < 10) {
                list.add(String.format("0%d", i));
            } else {
                list.add(String.format("%d", i));
            }
        }

        return list.toArray(new String[list.size()]);
    }

    /**
     * 获取上午下午字符串
     *
     * @return
     */
    private String[] getAmPmArray() {
        return new String[]{"上午", "下午"};
    }

    //时间变更监听
    OnWheelChangedListener mTimeWheelChangedListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            DatePickerAdapter adapter = (DatePickerAdapter) wheel.getViewAdapter();
            adapter.mCurrentItem = newValue;
            wheel.invalidateWheel(true);
            if (mIs24hourFormat == false && wheel == mWheelView2) {
                //判断变更上午下午
                if ((oldValue == 11 && newValue == 0) || (oldValue == 0 && newValue == 11)) {
                    adapter = (DatePickerAdapter) mWheelView1.getViewAdapter();
                    mWheelView1.setCurrentItem((mWheelView1.getCurrentItem() == 0 ? 1 : 0), true);
                    mWheelView1.invalidateWheel(true);
                    adapter.mCurrentItem = mWheelView1.getCurrentItem();
                }
            }
        }
    };

    /******************************************************************************************
     *
     *************************************************************/
    private class DatePickerAdapter extends ArrayWheelAdapter<String> {
        //当前选中项
        int mCurrentItem;
        //可用最大值
        int mValidMaxValue;
        LayoutInflater mInflater;
        String[] mItems;
        String mSuffix;

        public DatePickerAdapter(Context context, String[] items, int validMaxValue) {
            super(context, items);
            mCurrentItem = 0;
            mValidMaxValue = validMaxValue;
            mInflater = LayoutInflater.from(context);
            mItems = items;
            mSuffix = "";
        }

        @Override
        public View getItem(int index, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.ui_date_picker_item_picker, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.ui_date_picker_item_picker_tv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            viewHolder.textView.setText(mItems[index] + mSuffix);
            if ((mValidMaxValue != -1 && index > mValidMaxValue)) {
                viewHolder.textView.setTextColor(Color.GRAY);
            } else {
                viewHolder.textView.setTextColor(Color.BLACK);
            }

            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }

    //取消点击
    private OnClickListener mCancelClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    //确定点击
    private OnClickListener mDoneClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mDatePickerListener != null) {
                Calendar calendar = Calendar.getInstance();
                if (mMode == DATE_PICKER_MODE_DATE || mMode == DATE_PICKER_MODE_YEAR_MONTH || mMode == DATE_PICKER_MODE_YEAR) {
                    DatePickerAdapter adapter = (DatePickerAdapter) mWheelView1.getViewAdapter();
                    String year = adapter.mItems[adapter.mCurrentItem];
                    adapter = (DatePickerAdapter) mWheelView2.getViewAdapter();
                    String month = adapter.mItems[adapter.mCurrentItem];
                    adapter = (DatePickerAdapter) mWheelView3.getViewAdapter();
                    String day = adapter.mItems[adapter.mCurrentItem];

                    calendar.set(Integer.valueOf(year), Integer.valueOf(month) - 1, Integer.valueOf(day));
                } else if (mMode == DATE_PICKER_MODE_TIME) {
                    DatePickerAdapter adapter = (DatePickerAdapter) mWheelView2.getViewAdapter();
                    int hour = Integer.valueOf(adapter.mItems[adapter.mCurrentItem]);
                    adapter = (DatePickerAdapter) mWheelView3.getViewAdapter();
                    int minute = Integer.valueOf(adapter.mItems[adapter.mCurrentItem]);
                    if (mIs24hourFormat == false) {
                        adapter = (DatePickerAdapter) mWheelView1.getViewAdapter();
                        if (adapter.mCurrentItem == 1) {
                            hour += 12;
                        }
                    }
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                }
                mDatePickerListener.onDatePickerComplete(UIDatePicker.this, calendar.getTime());
                if (mIsRememberLastSelectDate) {
                    mCurrentDate = calendar.getTime();
                }
            }
            dismiss();
        }
    };

    public interface UIDatePickerListener {
        void onDatePickerComplete(UIDatePicker datePicker, Date date);
    }
}

