package com.netsdo.denguetracker;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Info {
    public final static long NULLLONG = -1; // reserved to indicate null Long
    private long rowid = NULLLONG;
    public final static long ZEROLONG = 0; // reserved to indicate new record
    public final static String NULLSTRING = "null"; // reserved to indicate null String, used for data output only.
    private String iwho = NULLSTRING;
    private String iwhen = NULLSTRING;
    private String ihow = NULLSTRING;
    private String iwhat = NULLSTRING;
    private String iwhy = NULLSTRING;
    public final static String NULLWHERE = "{\"time\":\"2000-01-01T00:00:00.000+0000\",\"longitude\":0,\"latitude\":0,\"accuracy\":0,\"altitude\":0,\"speed\":0}"; // reserved to indicate null iwhere
    private String iwhere = NULLWHERE;
    public final static String NULLWHEN = "2000-01-01T00:00:00.000+0000"; // reserved to indicate null iwhen, used for data output only.
    public final static String iwhatListMosBite[] = {"Head", "Body", "RightArm", "RightHand", "RightLeg", "RightFoot", "LeftArm", "LeftHand", "LeftLeg", "LeftFoot"};
    public final static String ihowList[] = {"MosBite"};
    private static String TAG = "Info";

    public void setrowid(long rowid) {
        this.rowid = rowid;
    }

    public void setiwho(String iwho) {
        this.iwho = (iwho == null ? NULLSTRING : iwho);
    }

    public void setiwhen(String iwhen) {
        this.iwhen = (iwhen == null ? NULLSTRING : iwhen);
    }

    public void setiwhere(String iwhere) {
        this.iwhere = (iwhere == null ? NULLSTRING : iwhere);
    }

    public void setihow(String ihow) {
        this.ihow = (ihow == null ? NULLSTRING : ihow);
    }

    public void setiwhat(String iwhat) {
        this.iwhat = (iwhat == null ? NULLSTRING : iwhat);
    }

    public void setiwhy(String iwhy) {
        this.iwhy = (iwhy == null ? NULLSTRING : iwhy);
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
            return String.format(MainActivity.mStringDisplay.getDisplay("iwhere-format" + format),
                    lObj.getString("latitude"), // %1$s
                    lObj.getString("longitude"), // %2$s
                    lObj.getString("altitude"), // %3$s
                    getihow("0"), // %4$s
                    getiwhat("0") // %5$s
            );
        } catch (JSONException e) {
            e.printStackTrace();

            return iwhere;
        }
    }

    public String getihow() {
        // return original value
        return ihow;
    }

    public String getihow(String format) {
        // if format = 0, return direct translation without formatting
        // if format = other, return translation with format specified
        if (format == "0") {
            return MainActivity.mStringDisplay.getDisplay(ihow);
        } else {
            return String.format(MainActivity.mStringDisplay.getDisplay(ihow + "-format" + format),
                    ihow // %1$s
            );
        }
    }

    public String getiwhat() {
        // return original value
        return iwhat;
    }

    public String getiwhat(String format) {
        // if format = 0, return direct translation without formatting
        // if format = other, return translation with format specified
        if (format.equals("0")) {
            return MainActivity.mStringDisplay.getDisplay(iwhat);
        } else {
            return String.format(MainActivity.mStringDisplay.getDisplay(iwhat + "-format" + format),
                    iwhat //%1$s
            );
        }
    }

    public String[] getlistiwhat() {
        // return original value
        return iwhatListMosBite;
    }

    public String[] getlistiwhat(String format) {
        // if format = 0, return direct translation
        // if format = other, return translation with format specified
        String[] lList = new String[iwhatListMosBite.length];

        if (format.equals("0")) {
            for (int i = 0; i < iwhatListMosBite.length; i++) {
                lList[i] = MainActivity.mStringDisplay.getDisplay(iwhatListMosBite[i]);
            }
        } else {
            for (int i = 0; i < iwhatListMosBite.length; i++) {
                lList[i] = String.format(MainActivity.mStringDisplay.getDisplay(iwhatListMosBite[i]) + "-format" + format,
                        iwhat //%1$s
                );
            }
        }
        return lList;
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
            iwhen = NULLWHEN;
            iwhere = NULLWHERE;
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

            //todo # add sql and datetime to provide complete format of JSON
            lObj.put("norec", 1);
            lInfoRec.put("position", 0);
            lInfoRec.put("rowid", rowid);
            lInfoRec.put("iwho", iwho);
            lInfoRec.put("iwhen", iwhen);
            lInfoRec.put("iwhere", new JSONObject(iwhere));
            lInfoRec.put("ihow", ihow);
            lInfoRec.put("iwhat", iwhat);
            lInfoRec.put("iwhy", iwhy);
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
