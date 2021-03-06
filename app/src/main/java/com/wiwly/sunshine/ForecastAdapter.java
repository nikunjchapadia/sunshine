package com.wiwly.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by nikunj on 9/3/14.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    private boolean mUseTodayLayout;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        if (viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        } else {
            layoutId = R.layout.list_item_forcast;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        //return LayoutInflater.from(context).inflate(R.layout.list_item_forcast, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Read weather icon ID from cursor
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        int viewType = getItemViewType(cursor.getPosition());
        switch (viewType){
            case VIEW_TYPE_TODAY:{
                viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
                break;
            }
            case VIEW_TYPE_FUTURE_DAY : {
                viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(weatherId));
                break;
            }
        }
        // Use placeholder image for now
//        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
//        iconView.setImageResource(R.drawable.ic_launcher);
        viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(weatherId));
        // Read date from cursor
        String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it
        //TextView dateView = (TextView) view.findViewById(R.id.list_item_date_text_view);
        //dateView.setText(Utility.getFriendlyDayString(context, dateString));
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));

        // Read weather forecast from cursor
        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        // Find TextView and set weather forecast on it
//        TextView descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_text_view);
//        descriptionView.setText(description);
        viewHolder.descriptionView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        float high = cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);
//        TextView highTextView = (TextView) view.findViewById(R.id.list_item_high_text_view);
//        highTextView.setText(Utility.formatTemperature(high, isMetric));
        viewHolder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));

        // Read low temperature from cursor
        float low = cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);
//        TextView lowTextView = (TextView) view.findViewById(R.id.list_item_low_text_view);
//        lowTextView.setText(Utility.formatTemperature(low, isMetric));
        viewHolder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));
    }

    // who gonna set this , main activity
    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_text_view);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_text_view);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_text_view);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_text_view);
        }
    }

}
