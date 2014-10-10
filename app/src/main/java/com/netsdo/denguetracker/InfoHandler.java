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

    private Context mContext;
    private SQLiteDatabase mDB;
    private Cursor mCursor; // cursor used by selectInfo,
    private String mSQL;
    private String mDateTime;
    private int mNoRec;

    public InfoHandler(Context context) {
        String lSQL;

        mContext = context;

        mDB = context.openOrCreateDatabase("InfoDB", context.MODE_PRIVATE, null);

        lSQL = "CREATE TABLE IF NOT EXISTS Info(rowid INTEGER PRIMARY KEY, iwho TEXT, iwhen TEXT, iwhere TEXT, ihow TEXT, iwhat TEXT, iwhy);";
//        lSQL = "DROP TABLE Info;";
        Log.d(TAG, lSQL);
        mDB.execSQL(lSQL);
    }

    public long insertInfo(String iwho, String iwhen, String iwhere, String ihow, String iwhat, String iwhy) {
        String lSQL;

        lSQL = "INSERT INTO Info(iwho, iwhen, iwhere, ihow, iwhat, iwhy) VALUES('" + iwho + "','" + iwhen + "','" + iwhere + "','" + ihow + "','" + iwhat + "','" + iwhy + "');";
        Log.d(TAG, lSQL);
        mDB.execSQL(lSQL);

        //todo, to return rowid if inserted, to return -1 if failed
        return 1;
    }

    public int updateInfo(Long rowid, String iwho, String iwhen, String iwhere, String ihow, String iwhat, String iwhy) {
        String lSQL;

        lSQL = "UPDATE Info SET iwho = '" + iwho + "', iwhen = '" + iwhen + "', iwhere = '" + iwhere + "', ihow = '" + ihow + "', iwhat = '" + iwhat + "', iwhy = '" + iwhy + "' WHERE rowid = " + rowid + ";";
        Log.d(TAG, lSQL);
        mDB.execSQL(lSQL);

        //todo, to return number of records deleted, to return 0 if no record deleted, to return -1 if failed
        return 1;
    }

    public int deleteInfo(Long rowid) {
        String lSQL;

        lSQL = "DELETE FROM Info WHERE rowid = " + rowid + ";";
        mDB.execSQL("DELETE FROM Info WHERE rowid = " + rowid + ";");

        //todo, to return 1 if successful, to return 0 if no record deleted, to return -1 if failed
        return 1;
    }

    public int openSelectInfo(Long rowid, String iwho, String iwhen, String iwhere, String ihow, String iwhat, String iwhy) {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }

        mSQL = "SELECT rowid, iwho, iwhen, iwhere, ihow, iwhat, iwhy FROM Info WHERE 1 = 1 ";
        if (rowid != null) {
            mSQL += "AND rowid = " + rowid;
        } else {
            if (iwho != null) {
                mSQL += "AND iwho  = '" + iwho + "'";
            }
            if (iwhen != null) {
                mSQL += "AND iwhen  = '" + iwhen + "'";
            }
            if (iwhere != null) {
                mSQL += "AND iwhere = '" + iwhere + "'";
            }
            if (ihow != null) {
                mSQL += "AND ihow   = '" + ihow + "'";
            }
            if (iwhat != null) {
                mSQL += "AND iwhat  = '" + iwhat + "'";
            }
            if (iwhy != null) {
                mSQL += "AND iwhy  = '" + iwhy + "'";
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
                lObj.put("datetime", mDateTime);
                lInfoRec.put("position", mCursor.getPosition());
                lInfoRec.put("rowid", mCursor.getInt(0));
                lInfoRec.put("iwho", mCursor.getString(1));
                lInfoRec.put("iwhen", mCursor.getString(2));
                lInfoRec.put("iwhere", (new JSONObject(mCursor.getString(3))).toString());
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

    public String selectAllInfo() {
        String lSQL;

        lSQL = "SELECT rowid, iwho, iwhen, iwhere, ihow, iwhat, iwhy FROM Info ORDER BY iwhen DESC;";

        try {
            JSONObject lObj = new JSONObject();
            lObj.put("sql", lSQL);

            return selectInfo(lSQL.toString());
        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }
    }

    public String selectInfo(String jSQL) {
        // assume the query includes all columns in original sequence. SELECT rowid, iwho, iwhen, iwhere, ihow, iwhat, iwhy FROM Info;
        Cursor lCursor;
        String lSQL;
        Integer lNoRec;
        String lDateTime;

        lDateTime = new SimpleDateFormat(mContext.getString(R.string.iso6301)).format(new Date());

        Log.d(TAG, "selectInfo, DATE:" + lDateTime + ", JSON:" + jSQL);

        try {
            JSONObject mObj;
            mObj = new JSONObject(jSQL);
            lSQL = mObj.getString("sql");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        lCursor = mDB.rawQuery(lSQL, null);
        lNoRec = lCursor.getCount();
        if (lNoRec == 0) {
            lCursor.close();

            return null;
        } else {
            try {
                JSONObject mObj = new JSONObject();
                JSONArray mInfoObj = new JSONArray();

                mObj.put("sql", lSQL);
                mObj.put("norec", lNoRec);
                mObj.put("datetime", lDateTime);
                while (lCursor.moveToNext()) {
                    JSONObject mInfoRec = new JSONObject();
                    mInfoRec.put("position", lCursor.getPosition());
                    mInfoRec.put("rowid", lCursor.getInt(0));
                    mInfoRec.put("iwho", lCursor.getString(1));
                    mInfoRec.put("iwhen", lCursor.getString(2));
                    mInfoRec.put("iwhere", (new JSONObject(lCursor.getString(3))).toString());
                    mInfoRec.put("ihow", lCursor.getString(4));
                    mInfoRec.put("iwhat", lCursor.getString(5));
                    mInfoRec.put("iwhy", lCursor.getString(6));
                    mInfoObj.put(mInfoRec);
                }
                lCursor.close();
                mObj.put("info", mInfoObj);
                Log.d(TAG, "selectAllInfo(String), JSON:" + mObj.toString());

                return mObj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                lCursor.close();

                return null;
            }
        }
    }

    public int execSQL(String SQL) {

        Log.d(TAG, SQL);
        mDB.execSQL(SQL);

        //todo, to return number of records affected, to return 0 if no record deleted, to return -1 if failed
        return 1;
    }

    public int truncateInfo() {
        String lSQL;

        lSQL = "DELETE FROM Info; VACUUM;";
        Log.d(TAG, lSQL);
        mDB.execSQL(lSQL);

        //todo, to return number of records truncated, to return -1 if failed
        return 1;
    }
}
