package com.netsdo.denguetracker;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Info {
    private static String TAG = "Info";

    public final static long NULLLONG = -1; // reserved to indicate null Long
    public final static long ZEROLONG = 0; // reserved to indicate new record
    public final static String NULLSTRING = "null"; // reserved to indicate null String, used for data output only.

    private long rowid = NULLLONG;
    private String iwho = NULLSTRING;
    private String iwhen = NULLSTRING;
    private String iwhere = NULLSTRING;
    private String ihow = NULLSTRING;
    private String iwhat = NULLSTRING;
    private String iwhy = NULLSTRING;

    public void setrowid(long rowid) {
        this.rowid = rowid;
    }

    public void setiwho(String iwho) {
        this.iwho = iwho;
    }

    public void setiwhen(String iwhen) {
        this.iwhen = iwhen;
    }

    public void setiwhere(String iwhere) {
        this.iwhere = iwhere;
    }

    public void setihow(String ihow) {
        this.ihow = ihow;
    }

    public void setiwhat(String iwhat) {
        this.iwhat = iwhat;
    }

    public void setiwhy(String iwhy) {
        this.iwhy = iwhy;
    }

    public long getrowid() {
        return rowid;
    }

    public String getiwho() {
        return iwho;
    }

    public String getiwhen() {
        return iwhen;
    }

    public String getiwhere() {
        return iwhere;
    }

    public String getiwhere(String format) {
        try {
            JSONObject lObj = new JSONObject(iwhere);
            return String.format(MainActivity.mStringDisplay.getDisplay("iwhere-format" + format), lObj.getString("latitude"), lObj.getString("longitude"), lObj.getString("altitude"), getihow("0"), getiwhat("0"));
        } catch (JSONException e) {
            e.printStackTrace();

            return iwhere;
        }
    }

    public String getihow(String format) {
        return MainActivity.mStringDisplay.getDisplay(ihow);
    }

    public String getiwhat(String format) {
        return MainActivity.mStringDisplay.getDisplay(iwhat);
    }

    public String getihow() {
        return ihow;
    }

    public String getiwhat() {
        return iwhat;
    }

    public String getiwhy() {
        return iwhy;
    }

    public boolean setInfo(String jInfo) {
        try {
            JSONObject lObj = new JSONObject(jInfo);
            JSONArray lInfoObj = lObj.getJSONArray("info");
            JSONObject lInfoRec = lInfoObj.getJSONObject(0); // to get first record only.

            rowid = lInfoRec.getLong("rowid");
            iwho = lInfoRec.getString("iwho");
            iwhen = lInfoRec.getString("iwhen");
            iwhere = lInfoRec.getString("iwhere");
            ihow = lInfoRec.getString("ihow");
            iwhat = lInfoRec.getString("iwhat");
            iwhy = lInfoRec.getString("iwhy");

            return true;
        } catch (JSONException e) {
            e.printStackTrace();

            rowid = NULLLONG;
            iwho = NULLSTRING;
            iwhen = NULLSTRING;
            iwhere = NULLSTRING;
            ihow = NULLSTRING;
            iwhat = NULLSTRING;
            iwhy = NULLSTRING;

            return false;
        }
    }

    public String getInfo() {
        if (rowid == NULLLONG) { // check rowid only to determine the record is
            return NULLSTRING;
        }

        try {
            JSONObject lObj = new JSONObject();
            JSONArray lInfo = new JSONArray();
            JSONObject lInfoRec = new JSONObject();

            //todo, add sql and datetime to provide complete format of JSON
            lObj.put("norec", 1);
            lInfoRec.put("position", 0);
            lInfoRec.put("rowid", rowid);
            lInfoRec.put("iwho", iwho == null ? NULLSTRING : iwho);
            lInfoRec.put("iwhen", iwhen == null ? NULLSTRING : iwhen);
            lInfoRec.put("iwhere", iwhere == null ? NULLSTRING : new JSONObject(iwhere));
            lInfoRec.put("ihow", ihow == null ? NULLSTRING : ihow);
            lInfoRec.put("iwhat", iwhat == null ? NULLSTRING : iwhat);
            lInfoRec.put("iwhy", iwhy == null ? NULLSTRING : iwhy);
            lInfo.put(lInfoRec);
            lObj.put("info", lInfo);
            Log.d(TAG, "getInfo, jInfo:" + lObj.toString());

            return lObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();

            return NULLSTRING;
        }
    }
}
