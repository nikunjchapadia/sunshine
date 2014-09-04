package com.wiwly.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wiwly.sunshine.data.WeatherContract.LocationEntry;
import com.wiwly.sunshine.data.WeatherContract.WeatherEntry;

/**
 * Created by nikunj on 9/4/14.
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HT = "#SunshineApp";
    public static final String LOCATION_KEY = "location";
    public static final String DATE_KEY = "date";

    public static final int DETAIL_LOADER = 0;

    private String mForecastStr;
    private String mLocation;
    private ImageView mIconView;
    private TextView mDateView;
    private TextView mFriendlyDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null != savedInstanceState) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }

//        Intent intent = getActivity().getIntent();
//        if (intent != null && intent.hasExtra(DATE_KEY)){
//            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
//        }

        Bundle args = getArguments();
        if(args !=null && args.containsKey(DATE_KEY)){
            getLoaderManager().initLoader(DETAIL_LOADER,null,this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = (ImageView)rootView.findViewById(R.id.detail_item_icon);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_text_view);
        mFriendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_text_view);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_text_view);
        mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_text_view);
        mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_text_view);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_text_view);
        mWindView = (TextView) rootView.findViewById(R.id.detail_pressure_text_view);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_wind_text_view);
//        Intent intent = getActivity().getIntent();
//            if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
//                mForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
//                ((TextView) rootView.findViewById(R.id.detail_text)).setText(mForecastStr);
//            }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null ?");
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HT);
        return shareIntent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (null != mLocation) {
            outState.putString(LOCATION_KEY, mLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        if (args != null && args.containsKey(DATE_KEY) && mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
//        Intent intent = getActivity().getIntent();
//        if (intent != null && intent.hasExtra(DATE_KEY) &&
//                mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
//            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
//        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] FORECAST_COLUMNS = {
                WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
                WeatherEntry.COLUMN_DATETEXT,
                WeatherEntry.COLUMN_SHORT_DESC,
                WeatherEntry.COLUMN_MAX_TEMP,
                WeatherEntry.COLUMN_MIN_TEMP,
                WeatherEntry.COLUMN_HUMIDITY,
                WeatherEntry.COLUMN_PRESSURE,
                WeatherEntry.COLUMN_WIND_SPEED,
                WeatherEntry.COLUMN_DEGREES,
                WeatherEntry.COLUMN_WEATHER_ID,
                LocationEntry.LOCATION_SETTING
        };

        String date = getArguments().getString(DATE_KEY);
        mLocation = Utility.getPreferredLocation(getActivity());

        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null || !intent.hasExtra(DATE_KEY)) {
            return null;
        }
        String forecastDate = intent.getStringExtra(DATE_KEY);
        // Sort order:  Ascending, by date.
        String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";

        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithDate(mLocation, date);
        Log.v(LOG_TAG, weatherForLocationUri.toString());

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) {
            return;
        }
        int weatherId = data.getInt(data.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID));

        //mIconView.setImageResource(R.drawable.ic_launcher);
        mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        String date = data.getString(data.getColumnIndex(WeatherEntry.COLUMN_DATETEXT));
        String friendlyDateText = Utility.getDayName(getActivity(), date);
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);
        mFriendlyDateView.setText(friendlyDateText);
        mDateView.setText(dateText);

        String description = data.getString(data.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC));
        mDescriptionView.setText(description);

        boolean isMetric = Utility.isMetric(getActivity());

        double high = data.getDouble(data.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP));
        String highString = Utility.formatTemperature(getActivity(), high, isMetric);
        mHighTempView.setText(highString);
        double low = data.getDouble(data.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP));
        String lowString = Utility.formatTemperature(getActivity(), low, isMetric);
        mLowTempView.setText(lowString);

        float humidity = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY));
        mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

        float windSpeedStr = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED));
        float windDirStr = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_DEGREES));
        mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

        float pressure = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_PRESSURE));
        mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

        //TextView dateView = (TextView) getView().findViewById(R.id.detail_date_text_view);
        //TextView forecastView = (TextView) getView().findViewById(R.id.detail_forecast_text_view);
        //TextView highView = (TextView) getView().findViewById(R.id.detail_high_text_view);
        //TextView lowView = (TextView) getView().findViewById(R.id.detail_low_text_view);
        //dateView.setText(dateText);
        //forecastView.setText(description);
        //highView.setText(Utility.formatTemperature(high, isMetric) + "\u00B0");
        //lowView.setText(Utility.formatTemperature(low, isMetric) + "\u00B0");

        mForecastStr = String.format("%s - %s - %s/%s", dateText, description, highString, lowString);
        Log.v(LOG_TAG, "Forecast String: " + mForecastStr);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }
}

