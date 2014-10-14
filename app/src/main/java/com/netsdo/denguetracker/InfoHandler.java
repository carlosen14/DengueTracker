package com.netsdo.denguetracker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InfoHandler {
    private final static String TAG = "InfoHandler";

    public final static long NULLLONG = -1; //reserved for null value

    private Context mContext;
    private SQLiteDatabase mDB;

    // variable used by batch selection in method openSelectInfo, selectNextInfo, closeSelectInfo
    private Cursor mCursor;
    private String mSQL;
    private String mDateTime;
    private long mNoRec;

    public InfoHandler(Context context) {
        mContext = context;

        mDB = mContext.openOrCreateDatabase("InfoDB", Context.MODE_PRIVATE, null);

        createInfoDB();
    }

    public long insertInfo(String jInfo) {
        // return x if number of records inserted,
        // return 0 if no record inserted
        // return NULLLONG if failed
        Log.d(TAG, "insertInto, JSON:" + jInfo);
        String lSQL;

        try {
            JSONObject lInfoRec = (new JSONObject(jInfo)).getJSONArray("info").getJSONObject(0); // to handle first record at the moment.
            lSQL = "INSERT INTO Info(iwho, iwhen, iwhere, ihow, iwhat, iwhy) VALUES('" + lInfoRec.getString("iwho") + "', '" + lInfoRec.getString("iwhen") + "', '" + lInfoRec.getString("iwhere") + "', '" + lInfoRec.getString("ihow") + "', '" + lInfoRec.getString("iwhat") + "', '" + lInfoRec.getString("iwhy") + "');";
            Log.d(TAG, lSQL);
            mDB.execSQL(lSQL);

            return 1; //todo, hardcode
        } catch (JSONException e) {
            e.printStackTrace();

            return NULLLONG;
        }
    }

    public long updateInfo(String jInfo) {
        // return x if number of records inserted,
        // return 0 if no record inserted
        // return NULLLONG if failed
        Log.d(TAG, "updateInfo, JSON:" + jInfo);
        String lSQL;

        try {
            JSONObject lInfoRec = (new JSONObject(jInfo)).getJSONArray("info").getJSONObject(0); // to handle first record at the moment.
            lSQL = "UPDATE Info SET iwho = '" + lInfoRec.getString("iwho") + "', iwhen = '" + lInfoRec.getString("iwhen") + "', iwhere = '" + lInfoRec.getString("iwhere") + "', ihow = '" + lInfoRec.getString("ihow") + "', iwhat = '" + lInfoRec.getString("iwhat") + "', iwhy = '" + lInfoRec.getString("iwhy") + "' WHERE rowid = " + lInfoRec.getLong("rowid") + ";";
            Log.d(TAG, lSQL);
            mDB.execSQL(lSQL);

            return 1; //todo, hardcode
        } catch (JSONException e) {
            e.printStackTrace();

            return NULLLONG;
        }
    }

    public long deleteInfo(long rowid) {
        // return x if number of records inserted,
        // return 0 if no record inserted
        // return NULLLONG if failed
        String lSQL;

        lSQL = "DELETE FROM Info WHERE rowid = " + rowid + ";";
        Log.d(TAG, lSQL);
        mDB.execSQL(lSQL);

        return 1; //todo, hardcode
    }

    public long openSelectInfo(String jSQL) {
        // assume the query includes all columns in original sequence. SELECT rowid, iwho, iwhen, iwhere, ihow, iwhat, iwhy FROM Info;
        Log.d(TAG, "openSelectInfo, JSON:" + jSQL);

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }

        try {
            mSQL = (new JSONObject(jSQL)).getString("sql");
        } catch (JSONException e) {
            e.printStackTrace();

            return NULLLONG;
        }

        mDateTime = new SimpleDateFormat(mContext.getString(R.string.iso6301)).format(new Date());
        Log.d(TAG, mSQL);
        mCursor = mDB.rawQuery(mSQL, null);
        mNoRec = mCursor.getCount();
        if (mNoRec == 0) {
            if (!mCursor.isClosed()) {
                mCursor.close();
            }
        }

        return mNoRec;
    }

    public String selectNextInfo() {
        if (mCursor == null || mCursor.isClosed()) {
            return null;
        }

        if (mCursor.moveToNext()) {
            try {
                JSONObject lObj = new JSONObject();
                JSONArray lInfo = new JSONArray();
                JSONObject lInfoRec = new JSONObject();

                lObj.put("sql", mSQL);
                lObj.put("norec", mNoRec);
                lObj.put("datetime", mDateTime); // SQL initialization date time
                lInfoRec.put("position", mCursor.getPosition());
                lInfoRec.put("rowid", mCursor.getLong(0));
                lInfoRec.put("iwho", mCursor.getString(1));
                lInfoRec.put("iwhen", mCursor.getString(2));
                lInfoRec.put("iwhere", (new JSONObject(mCursor.getString(3)))); // iwhere is JSON format
                lInfoRec.put("ihow", mCursor.getString(4));
                lInfoRec.put("iwhat", mCursor.getString(5));
                lInfoRec.put("iwhy", mCursor.getString(6));
                lInfo.put(lInfoRec);
                lObj.put("info", lInfo);

                return lObj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                if (mCursor != null && !mCursor.isClosed()) {
                    mCursor.close();
                }

                return null;
            }
        } else {
            if (mCursor != null && !mCursor.isClosed()) {
                mCursor.close();
            }

            return null;
        }
    }

    public void closeSelectInfo() {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
    }

    public String selectInfo(long rowid) {
        String lSQL;

        lSQL = "SELECT rowid, iwho, iwhen, iwhere, ihow, iwhat, iwhy FROM Info WHERE rowid = " + rowid + ";";

        try {
            JSONObject lObj = new JSONObject();
            lObj.put("sql", lSQL);

            return selectInfo(lObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }
    }

    public String selectAllInfo() {
        String lSQL;

        lSQL = "SELECT rowid, iwho, iwhen, iwhere, ihow, iwhat, iwhy FROM Info ORDER BY iwhen DESC;";

        try {
            JSONObject lObj = new JSONObject();
            lObj.put("sql", lSQL);

            return selectInfo(lObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }
    }

    public String selectInfo(String jSQL) {
        // assume the query includes all columns in original sequence. SELECT rowid, iwho, iwhen, iwhere, ihow, iwhat, iwhy FROM Info;
        Log.d(TAG, "selectInfo, JSON:" + jSQL);

        Cursor lCursor;
        String lSQL;
        long lNoRec;

        try {
            lSQL = (new JSONObject(jSQL)).getString("sql");
        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

        Log.d(TAG, lSQL);
        lCursor = mDB.rawQuery(lSQL, null);
        lNoRec = lCursor.getCount();
        if (lNoRec == 0) {
            lCursor.close();

            return null;
        } else {
            try {
                JSONObject lObj = new JSONObject();
                JSONArray lInfoObj = new JSONArray();

                lObj.put("sql", lSQL);
                lObj.put("norec", lNoRec);
                lObj.put("datetime", new SimpleDateFormat(mContext.getString(R.string.iso6301)).format(new Date()));
                Log.d(TAG, "selectAllInfo(String), Partial JSON:" + lObj.toString());
                while (lCursor.moveToNext()) {
                    JSONObject lInfoRec = new JSONObject();
                    lInfoRec.put("position", lCursor.getPosition());
                    lInfoRec.put("rowid", lCursor.getLong(0));
                    lInfoRec.put("iwho", lCursor.getString(1));
                    lInfoRec.put("iwhen", lCursor.getString(2));
                    lInfoRec.put("iwhere", new JSONObject(lCursor.getString(3))); // iwhere is in JSON format
                    lInfoRec.put("ihow", lCursor.getString(4));
                    lInfoRec.put("iwhat", lCursor.getString(5));
                    lInfoRec.put("iwhy", lCursor.getString(6));
                    lInfoObj.put(lInfoRec);
                }
                lCursor.close();
                lObj.put("info", lInfoObj);
                Log.d(TAG, "selectAllInfo(String), Full JSON:" + lObj.toString());

                return lObj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                lCursor.close();

                return null;
            }
        }
    }

    public long truncateInfo() {
        // return x if number of records inserted,
        // return 0 if no record inserted
        // return NULLLONG if failed
        String lSQL;

        lSQL = "DELETE FROM Info; VACUUM;";
        Log.d(TAG, lSQL);
        mDB.execSQL(lSQL);

        return 1; //todo, hardcode
    }

    public void createInfoDB() {
        String lSQL;

        lSQL = "CREATE TABLE IF NOT EXISTS Info(rowid INTEGER PRIMARY KEY, iwho TEXT, iwhen TEXT, iwhere TEXT, ihow TEXT, iwhat TEXT, iwhy);";
        Log.d(TAG, lSQL);
        mDB.execSQL(lSQL);
    }

    public void upgradeInfoDB() {
        String lSQL;

        lSQL = "DROP TABLE Info;";
        Log.d(TAG, lSQL);
        mDB.execSQL(lSQL);

        lSQL = "CREATE TABLE IF NOT EXISTS Info(rowid INTEGER PRIMARY KEY, iwho TEXT, iwhen TEXT, iwhere TEXT, ihow TEXT, iwhat TEXT, iwhy);";
        Log.d(TAG, lSQL);
        mDB.execSQL(lSQL);
    }
}
