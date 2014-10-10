package com.netsdo.denguetracker;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Info {
    private static String TAG = "Info";

    private Long rowid;
    private String iwho;
    private String iwhen;
    private String iwhere;
    private String ihow;
    private String iwhat;
    private String iwhy;

    public void setRowid(Long rowid) {
        this.rowid = rowid;
    }

    public void setIwho(String iwho) {
        this.iwho = iwho;
    }

    public void setIwhen(String iwhen) {
        this.iwhen = iwhen;
    }

    public void setIwhere(String iwhere) {
        this.iwhere = iwhere;
    }

    public void setIhow(String ihow) {
        this.ihow = ihow;
    }

    public void setIwhat(String iwhat) {
        this.iwhat = iwhat;
    }

    public void setIwhy(String iwhy) {
        this.iwhy = iwhy;
    }

    public Long getRowid() {
        return rowid;
    }

    public String getIwho() {
        return iwho;
    }

    public String getIwhen() {
        return iwhen;
    }

    public String getIwhere() {
        return iwhere;
    }

    public String getIhow() {
        return ihow;
    }

    public String getIwhat() {
        return iwhat;
    }

    public String getIwhy() {
        return iwhy;
    }

    public boolean setInfo(String jInfo) {
        Log.d(TAG, "setInfo, jInfo:" + jInfo);

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

            return false;
        }

    }

    public String getInfo() {
        try {
            JSONObject lObj = new JSONObject();
            JSONArray lInfo = new JSONArray();
            JSONObject lInfoRec = new JSONObject();

            lObj.put("norec", 1);
            lInfoRec.put("position", 0);
            lInfoRec.put("rowid", rowid);
            lInfoRec.put("iwho", iwho);
            lInfoRec.put("iwhen", iwhen);
            lInfoRec.put("iwhere", (new JSONObject(iwhere)).toString());
            lInfoRec.put("ihow", ihow);
            lInfoRec.put("iwhat", iwhat);
            lInfoRec.put("iwhy", iwhy);
            lInfo.put(lInfoRec);
            lObj.put("info", lInfo);

            Log.d(TAG, "getInfo, jInfo:" + lObj.toString());
            return lObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }
    }
}
