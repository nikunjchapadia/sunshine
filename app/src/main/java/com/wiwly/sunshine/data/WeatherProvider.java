package com.wiwly.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.wiwly.sunshine.data.WeatherContract.WeatherEntry;
import com.wiwly.sunshine.data.WeatherContract.LocationEntry;
/**
 * Created by nikunj on 8/29/14.
 */
public class WeatherProvider extends ContentProvider {

    private final String LOG_TAG = WeatherContract.class.getSimpleName();
    private static final int WEATHER = 100;
    private static final int WEATHER_WITH_LOCATION = 101;
    private static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    private static final int LOCATION = 300;
    private static final int LOCATION_ID = 301;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public WeatherDbHelper mOpenHelper;
    public static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static {
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        LocationEntry.TABLE_NAME +
                        " ON " + WeatherEntry.TABLE_NAME +
                        "." + WeatherEntry.COLUMN_LOC_KEY +
                        " = " + LocationEntry.TABLE_NAME +
                        "." + LocationEntry._ID);
    }

    public static final String sLocationSettingSelection =
            LocationEntry.TABLE_NAME + "." + LocationEntry.LOCATION_SETTING + " = ?";

    public static final String sLocationSettingWithStartDateSelection =
            LocationEntry.TABLE_NAME + "." + LocationEntry.LOCATION_SETTING + " = ? AND " +
                    WeatherEntry.COLUMN_DATETEXT + " >= ?";

    public static final String sLocationSettingWithDaySelection =
            LocationEntry.TABLE_NAME + "." + LocationEntry.LOCATION_SETTING + " = ? AND " +
                    WeatherEntry.COLUMN_DATETEXT + " = ?";


    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder){
        String locationSetting = WeatherEntry.getLocationSettingFromUri(uri);
        String startDate = WeatherEntry.getStartDateFromUri(uri);
        String selection;
        String[] selectionArgs;

        if (startDate == null) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        } else {
            selection = sLocationSettingWithStartDateSelection;
            selectionArgs = new String[]{locationSetting, startDate};
        }
        return sWeatherByLocationSettingQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null, null, sortOrder);
    }

    private Cursor getWeatherByLocationSettingWithDate(Uri uri, String[] projection, String sortOrder){
        String date = WeatherEntry.getDateFromUri(uri);
        String locationSetting = WeatherEntry.getLocationSettingFromUri(uri);
        return sWeatherByLocationSettingQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingWithDaySelection,
                new String[]{locationSetting,date},
                null,null,sortOrder);
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new WeatherDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        Log.i(LOG_TAG, "QUERY Uri : " + uri);
        int match = sUriMatcher.match(uri);
        Log.i(LOG_TAG, "Query - sUriMatcher " + match);
        switch (match) {
            case WEATHER_WITH_LOCATION_AND_DATE: {
                cursor = getWeatherByLocationSettingWithDate(uri, projection, sortOrder);
                break;
            }
            case WEATHER_WITH_LOCATION: {
                cursor = getWeatherByLocationSetting(uri, projection, sortOrder);
                break;
            }
            case WEATHER: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case LOCATION_ID: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        LocationEntry.TABLE_NAME,
                        projection,
                        LocationEntry._ID + " = " + ContentUris.parseId(uri),
                        selectionArgs,
                        null,null,sortOrder);
                break;
            }
            case LOCATION: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,null,sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI : " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherEntry.CONTENT_ITEM_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherEntry.CONTENT_TYPE;
            case WEATHER:
                return WeatherEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return LocationEntry.CONTENT_ITEM_TYPE;
            case LOCATION:
                return LocationEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Log.v(LOG_TAG, "Insert : " + match);
        switch (match){
            case WEATHER: {
                long _id = db.insert(WeatherEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = WeatherEntry.buildWeatherUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into  " + uri);
                }
                break;
            }
            case LOCATION : {
                long _id = db.insert(LocationEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = LocationEntry.buildLocationUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into  " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI : " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Log.i(LOG_TAG, "Insert URI : " + returnUri);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int rowDeleted;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (match){
            case WEATHER: {
                rowDeleted = db.delete(WeatherEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case LOCATION : {
                rowDeleted = db.delete(LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI : " + uri);
        }
        if (null == selection || 0 != rowDeleted){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.i(LOG_TAG, "Deleted Rows : " + rowDeleted);
        return rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowUpdated;
        switch (match){
            case WEATHER: {
                rowUpdated = db.update(WeatherEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case LOCATION : {
                rowUpdated = db.update(LocationEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI : " + uri);
        }
        if (0 != rowUpdated) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.i(LOG_TAG, "Updated Rows : " + rowUpdated);
        return rowUpdated;
    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/*", WEATHER_WITH_LOCATION_AND_DATE);

        matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);
        matcher.addURI(authority, WeatherContract.PATH_LOCATION + "/#", LOCATION_ID);
        return matcher;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match){
            case WEATHER :{
                Log.i(LOG_TAG, "Bulk Insert Weather : " + match);
                db.beginTransaction();
                try {
                    for(ContentValues value : values){
                        long _id = db.insert(WeatherEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                Log.d(LOG_TAG, "Bulk Insert Weather :- Match :" + match + " Count : " + returnCount);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            }
            case LOCATION :{
                Log.i(LOG_TAG, "Bulk Insert Location : " + match);
                db.beginTransaction();
                try {
                    for(ContentValues value : values){
                        long _id = db.insert(LocationEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                Log.d(LOG_TAG, "Bulk Insert Location :- Match :" + match + " Count : " + returnCount);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            }
            default:{
                Log.d(LOG_TAG, "Bulk Insert Default :" + match);
                return super.bulkInsert(uri, values);
            }
        }
        return 0;
    }
}
