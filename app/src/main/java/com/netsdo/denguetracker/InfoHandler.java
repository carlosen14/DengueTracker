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

    private static String TAG = "InfoHandler";

    Context mContext;
    private SQLiteDatabase mDB;
    private Cursor mCursor; // cursor used by selectInfo
    private String mSQL;
    private String mDateTime;
    private Integer mNoRec;

    public InfoHandler(Context context) {
        mContext = context;
        mDB = context.openOrCreateDatabase("InfoDB", context.MODE_PRIVATE, null);
        Log.d(TAG, "CREATE TABLE IF NOT EXISTS Info(rowid INTEGER PRIMARY KEY, iwhen TEXT, iwhere TEXT, ihow TEXT, iwhat TEXT);");
        mDB.execSQL("CREATE TABLE IF NOT EXISTS Info(rowid INTEGER PRIMARY KEY, iwhen TEXT, iwhere TEXT, ihow TEXT, iwhat TEXT);");
    }

    public Boolean insertInfo (String iwhen, String iwhere, String ihow, String iwhat) {
        Log.d(TAG, "INSERT INTO Info(iwhen, iwhere, ihow, iwhat) VALUES('" + iwhen + "','" + iwhere + "','" + ihow + "','" + iwhat + "');");
        mDB.execSQL("INSERT INTO Info(iwhen, iwhere, ihow, iwhat) VALUES('" + iwhen + "','" + iwhere + "','" + ihow + "','" + iwhat + "');");
        return true;
    }

    public Boolean deleteInfo (Integer rowid) {
        Log.d(TAG,             "SELECT rowid FROM Info WHERE rowid = " + rowid + ";");
        Cursor c = mDB.rawQuery("SELECT rowid FROM Info WHERE rowid = " + rowid + ";", null);
        if (c.moveToFirst()) {
            if (c != null && !c.isClosed()) {
                c.close();
            }
            Log.d(TAG, "DELETE FROM Info WHERE rowid = " + rowid + ";");
            mDB.execSQL("DELETE FROM Info WHERE rowid = " + rowid + ";");
            return true;
        } else {
            if (c != null && !c.isClosed()) {
                c.close();
            }
            return false;
        }
    }

    public Boolean deleteAllInfo () {
        Log.d(TAG, "DELETE FROM Info;");
        mDB.execSQL("DELETE FROM Info;");
        return true;
    }

    public Boolean updateInfo (Integer rowid, String iwhen, String iwhere, String ihow, String iwhat) {
        Log.d(TAG,             "SELECT rowid FROM Info WHERE rowid = " + rowid + ";");
        Cursor c = mDB.rawQuery("SELECT rowid FROM Info WHERE rowid = " + rowid + ";", null);
        if (c.moveToFirst()) {
            Log.d(TAG, "UPDATE Info SET iwhen = '" + iwhen + "', iwhere = '" + iwhere + "', ihow = '" + ihow + "', iwhat = '" + iwhat + "' WHERE rowid = " + rowid + ";");
            mDB.execSQL("UPDATE Info SET iwhen = '" + iwhen + "', iwhere = '" + iwhere + "', ihow = '" + ihow + "', iwhat = '" + iwhat + "' WHERE rowid = " + rowid + ";");
            if (c != null && !c.isClosed()) {
                c.close();
            }
            return true;
        } else {
            return false;
        }
    }

    public Integer openSelectInfo (Integer rowid, String iwhen, String iwhere, String ihow, String iwhat) {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }

        mSQL = "SELECT rowid, iwhen, iwhere, ihow, iwhat FROM Info WHERE 1 = 1 ";
        if (rowid != null) {
            mSQL += "AND rowid = " + rowid;
        } else {
            if (iwhen != null) {
                mSQL += "AND iwhen  = '" + iwhen  + "'";
            }
            if (iwhere != null) {
                mSQL += "AND iwhere = '" + iwhere + "'";
            }
            if (ihow != null) {
                mSQL += "AND ihow   = '" + ihow   + "'";
            }
            if (iwhat != null) {
                mSQL += "AND iwhat  = '" + iwhat  + "'";
            }
        }
        mSQL += ";";
        Log.d(TAG, mSQL);

        mDateTime = new SimpleDateFormat(mContext.getString(R.string.iso6301)).format(new Date());
        mCursor = mDB.rawQuery(mSQL, null);
        mNoRec = mCursor.getCount();

        if (mNoRec == 0) {
            if (mCursor != null && !mCursor.isClosed()) {
                mCursor.close();
            }
            return 0;
        } else {
            return mNoRec;
        }
    }

    public String selectNextInfo () {
        if (mCursor.moveToNext()) {
            try {
                JSONObject mObj = new JSONObject();
                JSONArray mInfo = new JSONArray();
                JSONObject mInfoRec = new JSONObject();
                JSONObject mwhere;

                mObj.put("sql", mSQL);
                mObj.put("norec", mNoRec);
                mObj.put("datetime", mDateTime);
                mInfoRec.put("position", mCursor.getPosition());
                mInfoRec.put("rowid",    mCursor.getInt(0));
                mInfoRec.put("iwhen",    mCursor.getString(1));
                mInfoRec.put("ihow",     mCursor.getString(3));
                mInfoRec.put("iwhat",    mCursor.getString(4));
                mwhere = new JSONObject(mCursor.getString(2));
                mInfoRec.put("iwhere", mwhere);
                mInfo.put(mInfoRec);
                mObj.put("info", mInfo);
                return mObj.toString();
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

    public void closeSelectInfo () {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
    }

    public String selectAllInfo () {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }

        mSQL = "SELECT rowid, iwhen, iwhere, ihow, iwhat FROM Info;";
        Log.d(TAG, mSQL);

        mDateTime = new SimpleDateFormat(mContext.getString(R.string.iso6301)).format(new Date());
        mCursor = mDB.rawQuery(mSQL, null);
        mNoRec = mCursor.getCount();
        if (mNoRec == 0) {
            if (mCursor != null && !mCursor.isClosed()) {
                mCursor.close();
            }

            return null;
        } else {
            try {
                JSONObject mObj = new JSONObject();
                JSONArray mInfoObj = new JSONArray();

                mObj.put("sql", mSQL);
                mObj.put("norec", mNoRec);
                mObj.put("datetime", mDateTime);
                while (mCursor.moveToNext()) {
                    JSONObject mInfoRec = new JSONObject();
                    JSONObject mwhere;
                    mInfoRec.put("position", mCursor.getPosition());
                    mInfoRec.put("rowid", mCursor.getInt(0));
                    mInfoRec.put("iwhen", mCursor.getString(1));
                    mInfoRec.put("ihow", mCursor.getString(3));
                    mInfoRec.put("iwhat", mCursor.getString(4));
                    mwhere = new JSONObject(mCursor.getString(2)); //position is stored in JSON format.
                    mInfoRec.put("iwhere", mwhere);
                    mInfoObj.put(mInfoRec);
                }

                if (mCursor != null && !mCursor.isClosed()) {
                    mCursor.close();
                }

                mObj.put("info", mInfoObj);

                return mObj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                if (mCursor != null && !mCursor.isClosed()) {
                    mCursor.close();
                }
                return null;
            }
        }
    }

}
