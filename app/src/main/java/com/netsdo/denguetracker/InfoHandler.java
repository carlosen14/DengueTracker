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
    private SQLiteDatabase db;
    private Cursor sc; // cursor used by selectInfo
    private String selectCriteria;
    private Integer noRec;
    private String selectTime;

    public InfoHandler(Context context) {
        mContext = context;
        db = context.openOrCreateDatabase("InfoDB", context.MODE_PRIVATE, null);
        Log.d(TAG, "CREATE TABLE IF NOT EXISTS Info(rowid INTEGER PRIMARY KEY, iwhen TEXT, iwhere TEXT, ihow TEXT, iwhat TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS Info(rowid INTEGER PRIMARY KEY, iwhen TEXT, iwhere TEXT, ihow TEXT, iwhat TEXT);");
    }

    public boolean insertInfo (String iwhen, String iwhere, String ihow, String iwhat) {
        Log.d(TAG, "INSERT INTO Info(iwhen, iwhere, ihow, iwhat) VALUES('" + iwhen + "','" + iwhere + "','" + ihow + "','" + iwhat + "');");
        db.execSQL("INSERT INTO Info(iwhen, iwhere, ihow, iwhat) VALUES('" + iwhen + "','" + iwhere + "','" + ihow + "','" + iwhat + "');");
        return true;
    }

    public boolean deleteInfo (Integer rowid) {
        Log.d(TAG,             "SELECT rowid FROM Info WHERE rowid = " + rowid + ";");
        Cursor c = db.rawQuery("SELECT rowid FROM Info WHERE rowid = " + rowid + ";", null);
        if (c.moveToFirst()) {
            if (c != null && !c.isClosed()) {
                c.close();
            }
            Log.d(TAG, "DELETE FROM Info WHERE rowid = " + rowid + ";");
            db.execSQL("DELETE FROM Info WHERE rowid = " + rowid + ";");
            return true;
        } else {
            if (c != null && !c.isClosed()) {
                c.close();
            }
            return false;
        }
    }

    public boolean deleteAllInfo () {
        Log.d(TAG, "DELETE FROM Info;");
        db.execSQL("DELETE FROM Info;");
        return true;
    }

    public boolean updateInfo (Integer rowid, String iwhen, String iwhere, String ihow, String iwhat) {
        Log.d(TAG,             "SELECT rowid FROM Info WHERE rowid = " + rowid + ";");
        Cursor c = db.rawQuery("SELECT rowid FROM Info WHERE rowid = " + rowid + ";", null);
        if (c.moveToFirst()) {
            Log.d(TAG, "UPDATE Info SET iwhen = '" + iwhen + "', iwhere = '" + iwhere + "', ihow = '" + ihow + "', iwhat = '" + iwhat + "' WHERE rowid = " + rowid + ";");
            db.execSQL("UPDATE Info SET iwhen = '" + iwhen + "', iwhere = '" + iwhere + "', ihow = '" + ihow + "', iwhat = '" + iwhat + "' WHERE rowid = " + rowid + ";");
            if (c != null && !c.isClosed()) {
                c.close();
            }
            return true;
        } else {
            return false;
        }
    }

    public Integer openSelectInfo (Integer rowid, String iwhen, String iwhere, String ihow, String iwhat) {
        if (sc != null && !sc.isClosed()) {
            sc.close();
        }

        selectCriteria = "SELECT rowid, iwhen, iwhere, ihow, iwhat FROM Info WHERE 1 = 1 ";
        if (rowid != null) {
            selectCriteria += "AND rowid = " + rowid;
        } else {
            if (iwhen != null) {
                selectCriteria += "AND iwhen  = '" + iwhen  + "'";
            }
            if (iwhere != null) {
                selectCriteria += "AND iwhere = '" + iwhere + "'";
            }
            if (ihow != null) {
                selectCriteria += "AND ihow   = '" + ihow   + "'";
            }
            if (iwhat != null) {
                selectCriteria += "AND iwhat  = '" + iwhat  + "'";
            }
        }
        selectCriteria += ";";
        Log.d(TAG,       selectCriteria);

        selectTime = new SimpleDateFormat(mContext.getString(R.string.iso6301)).format(new Date());
        sc = db.rawQuery(selectCriteria, null);
        noRec = sc.getCount();
        if (noRec == 0) {
            if (sc != null && !sc.isClosed()) {
                sc.close();
            }
            return 0;
        } else {
            return noRec;
        }
    }

    public void closeSelectInfo () {
        if (sc != null && !sc.isClosed()) {
            sc.close();
        }
    }

    public String selectNextInfo () {
        if (sc.moveToNext()) {
            try {
                JSONObject mObj = new JSONObject();
                JSONArray mInfo = new JSONArray();
                JSONObject mInfoRec = new JSONObject();
                JSONObject mwhere;

                mObj.put("sql", selectCriteria);
                mObj.put("norec", noRec);
                mObj.put("datetime", selectTime);
                mInfoRec.put("position", sc.getPosition());
                mInfoRec.put("rowid",    sc.getInt(0));
                mInfoRec.put("iwhen",    sc.getString(1));
                mInfoRec.put("ihow",     sc.getString(3));
                mInfoRec.put("iwhat",    sc.getString(4));
                mwhere = new JSONObject(sc.getString(2));
                mInfoRec.put("iwhere", mwhere);
                mInfo.put(mInfoRec);
                mObj.put("info", mInfo);
                return mObj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                if (sc != null && !sc.isClosed()) {
                    sc.close();
                }
                return null;
            }
        } else {
            if (sc != null && !sc.isClosed()) {
                sc.close();
            }
            return null;
        }
    }

    public String selectAllInfo () {
        if (sc != null && !sc.isClosed()) {
            sc.close();
        }

        selectCriteria = "SELECT rowid, iwhen, iwhere, ihow, iwhat FROM Info;";
        Log.d(TAG,       selectCriteria);

        selectTime = new SimpleDateFormat(mContext.getString(R.string.iso6301)).format(new Date());
        sc = db.rawQuery(selectCriteria, null);
        noRec = sc.getCount();
        if (noRec == 0) {
            if (sc != null && !sc.isClosed()) {
                sc.close();
            }
            return null;
        } else {
            try {
                JSONObject mObj = new JSONObject();
                JSONArray mInfo = new JSONArray();

                mObj.put("sql", selectCriteria);
                mObj.put("norec", noRec);
                mObj.put("datetime", selectTime);
                while (sc.moveToNext()) {
                    JSONObject mInfoRec = new JSONObject();
                    JSONObject mwhere;
                    mInfoRec.put("position", sc.getPosition());
                    mInfoRec.put("rowid", sc.getInt(0));
                    mInfoRec.put("iwhen", sc.getString(1));
                    mInfoRec.put("ihow", sc.getString(3));
                    mInfoRec.put("iwhat", sc.getString(4));
                    mwhere = new JSONObject(sc.getString(2));
                    mInfoRec.put("iwhere", mwhere);
                    mInfo.put(mInfoRec);
                }

                if (sc != null && !sc.isClosed()) {
                    sc.close();
                }

                mObj.put("info", mInfo);
                return mObj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                if (sc != null && !sc.isClosed()) {
                    sc.close();
                }
                return null;
            }
        }
    }

}
