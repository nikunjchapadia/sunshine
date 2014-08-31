package com.wiwly.sunshine;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.wiwly.sunshine.data.WeatherContract;
import com.wiwly.sunshine.data.WeatherDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by nikunj on 8/29/14.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();
    public String CITY_NAME = "North Pole";
    public String TEST_DATE = "20140829";

    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    public void testGetType(){
        String type;

        type = mContext.getContentResolver().getType(WeatherContract.LocationEntry.CONTENT_URI);
        assertEquals(WeatherContract.LocationEntry.CONTENT_TYPE, type);

        //String type = null;
        type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.CONTENT_URI);
        assertEquals(WeatherContract.WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "94074";
        type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherLocation(testLocation));
        assertEquals(WeatherContract.WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20140829";
        type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(testLocation,testDate));
        assertEquals(WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE, type);


        type = mContext.getContentResolver().getType(WeatherContract.LocationEntry.buildLocationUri(1L));
        assertEquals(WeatherContract.LocationEntry.CONTENT_ITEM_TYPE, type);

    }

    public void testInsertReadProvider() {

        ContentValues values = getLocationContentValues();
        Uri insertLocationUri = mContext.getContentResolver().insert(WeatherContract.LocationEntry.CONTENT_URI,values);
        long locationRowId = ContentUris.parseId(insertLocationUri);
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New Row ID : " + locationRowId);

        ContentValues values1 = new ContentValues(values);
        values1.put(WeatherContract.LocationEntry._ID, locationRowId);
        values1.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "Santa's Village");
        int count = mContext.getContentResolver().update(
                WeatherContract.WeatherEntry.CONTENT_URI,
                values1,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME,
                new String[]{Long.toString(locationRowId)});
        assertEquals(count,1);

        // cursor primary interface to query results
        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                null,  //columns,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            validateCursor(values, cursor);

            ContentValues weatherValues = getWeatherContentValues(locationRowId);
            Uri insertUri = mContext.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, weatherValues);
            long weatherRowId = ContentUris.parseId(insertUri);
            //long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);
            Log.d(LOG_TAG, weatherRowId + "");
            assertTrue(weatherRowId != -1);

            Cursor weatherCursor = mContext.getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI,
                    null,  //weatherColumns,
                    null,
                    null,
                    null
            );
            if (weatherCursor.moveToFirst()) {
                validateCursor(weatherValues, weatherCursor);

            } else {
                fail("No values returned");
            }
            weatherCursor.close();

            weatherCursor = mContext.getContentResolver().query(
                    WeatherContract.WeatherEntry.buildWeatherLocation(CITY_NAME),
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null  // sort order
            );
            weatherCursor.close();

            weatherCursor = mContext.getContentResolver().query(
                    WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(CITY_NAME, TEST_DATE),
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null  // sort order
            );
            weatherCursor.close();

            weatherCursor = mContext.getContentResolver().query(
                    WeatherContract.WeatherEntry.buildWeatherLocationWithDate(CITY_NAME, TEST_DATE),
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null  // sort order
            );
            weatherCursor.close();

            Cursor locationCursor = mContext.getContentResolver().query(
                    WeatherContract.LocationEntry.buildLocationUri(locationRowId),
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null  // sort order
            );

        } else {
            fail(" No values returned");
        }
    }

    public void testDeleteAllRecords(){
        // delete all records
//        mContext.getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI, null, null);
//        mContext.getContentResolver().delete(WeatherContract.LocationEntry.CONTENT_URI, null, null);

        // make sure we deleted all records
        Cursor cursor = mContext.getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI, null, null, null, null);
        assertEquals(cursor.getCount(), 0);
        cursor.close();

        cursor = mContext.getContentResolver().query(WeatherContract.LocationEntry.CONTENT_URI, null, null, null, null);
        assertEquals(cursor.getCount(), 0);
        cursor.close();

    }

    public String getString(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.getString(index);
    }

    public double getDouble(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.getDouble(index);
    }

    public double getInt(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.getInt(index);
    }

    public long getLong(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.getLong(index);
    }

    ContentValues getLocationContentValues(){
        String locationSetting = "99705";
        double lat = 64.772;
        double lon = -147.355;

        ContentValues values = new ContentValues();
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, CITY_NAME);
        values.put(WeatherContract.LocationEntry.LOCATION_SETTING, locationSetting);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);
        return values;
    }

    ContentValues getWeatherContentValues(long locationRowId){
        String dateText = "20141205";
        double degrees = 1.1;
        double humidity = 1.2;
        double pressure = 1.3;
        Integer maxTemp = 75;
        Integer minTemp = 65;
        String shortDesc = "Asteroids";
        double windSpeed = 5.5;
        Integer weatherId = 321;
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, dateText);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, degrees);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, maxTemp);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, minTemp);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, shortDesc);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);
        return weatherValues;
    }

    public static void validateCursor(ContentValues values, Cursor cursor){
        Set<Map.Entry<String, Object>> valueSet = values.valueSet();
        for(Map.Entry<String, Object> entry : valueSet){
            String columnName = entry.getKey();
            int idx = cursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            Log.i(LOG_TAG, "VALUE : Expected == Actual ===> Result : " + expectedValue + "  ==  " + cursor.getString(idx) + " ===> " + expectedValue.equals(cursor.getString(idx)));
            assertEquals(expectedValue, cursor.getString(idx));
        }
    }

    public String[] getLocationColumns() {
        String[] columns = {
                WeatherContract.LocationEntry._ID,
                WeatherContract.LocationEntry.LOCATION_SETTING,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME,
                WeatherContract.LocationEntry.COLUMN_COORD_LAT,
                WeatherContract.LocationEntry.COLUMN_COORD_LONG
        };
        return columns;
    }

    public String[] getWeatherColumns() {
        String[] columns = {
                WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY,
                WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                WeatherContract.WeatherEntry.COLUMN_DEGREES,
                WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
                WeatherContract.WeatherEntry.COLUMN_PRESSURE,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
        };
        return columns;
    }

}
