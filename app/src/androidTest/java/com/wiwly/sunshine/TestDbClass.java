package com.wiwly.sunshine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.wiwly.sunshine.data.WeatherContract;
import com.wiwly.sunshine.data.WeatherDbHelper;

/**
 * Created by nikunj on 8/28/14.
 */
public class TestDbClass extends AndroidTestCase {

    public static final String LOG_TAG = TestDbClass.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase database = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, database.isOpen());
        database.close();
    }

    public void testInsertReadDb() {
        String city = "North Pole";
        String locationSetting = "99705";
        double lat = 64.772;
        double lon = -147.355;
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, city);
        values.put(WeatherContract.LocationEntry.LOCATION_SETTING, locationSetting);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);

        long locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New Row ID : " + locationRowId);

        String[] columns = {
                WeatherContract.LocationEntry._ID,
                WeatherContract.LocationEntry.LOCATION_SETTING,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME,
                WeatherContract.LocationEntry.COLUMN_COORD_LAT,
                WeatherContract.LocationEntry.COLUMN_COORD_LONG
        };

        // cursor primary interface to query results
        Cursor cursor = db.query(
                WeatherContract.LocationEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            String location = getString(cursor, WeatherContract.LocationEntry.LOCATION_SETTING);
            String name = getString(cursor, WeatherContract.LocationEntry.COLUMN_CITY_NAME);
            double latitude = getDouble(cursor, WeatherContract.LocationEntry.COLUMN_COORD_LAT);
            double longitude = getDouble(cursor, WeatherContract.LocationEntry.COLUMN_COORD_LONG);
            assertEquals(city, name);
            assertEquals(locationSetting, location);
            assertEquals(lat, latitude);
            assertEquals(lon, longitude);

            String dateText = "20141205";
            double degrees = 1.1;
            double humidity = 1.2;
            double pressure = 1.3;
            double maxTemp = 75;
            double minTemp = 65;
            String shortDesc = "Asteroids";
            double windSpeed = 5.5;
            double weatherId = 321;
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
            long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);
            Log.d(LOG_TAG, weatherRowId + "");
            assertTrue(weatherRowId != -1);

            String[] weatherColumns = {
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

            Cursor weatherCursor = db.query(WeatherContract.WeatherEntry.TABLE_NAME, weatherColumns, null, null, null, null, null);
            if (weatherCursor.moveToFirst()) {

                assertEquals(locationRowId, getLong(weatherCursor, WeatherContract.WeatherEntry.COLUMN_LOC_KEY));
                assertEquals(dateText, getString(weatherCursor, WeatherContract.WeatherEntry.COLUMN_DATETEXT));
                assertEquals(degrees, getDouble(weatherCursor, WeatherContract.WeatherEntry.COLUMN_DEGREES));
                assertEquals(humidity, getDouble(weatherCursor, WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
                assertEquals(pressure, getDouble(weatherCursor, WeatherContract.WeatherEntry.COLUMN_PRESSURE));
                assertEquals(maxTemp, getDouble(weatherCursor, WeatherContract.WeatherEntry.COLUMN_MAX_TEMP));
                assertEquals(minTemp, getDouble(weatherCursor, WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));
                assertEquals(shortDesc, getString(weatherCursor, WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
                assertEquals(windSpeed, getDouble(weatherCursor, WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
                assertEquals(weatherId, getInt(weatherCursor, WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));
            } else {
                fail("No values returned");
            }
        } else {
            fail(" No values returned");
        }
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
}




















