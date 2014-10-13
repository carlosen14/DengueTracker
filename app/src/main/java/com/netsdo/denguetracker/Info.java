package com.netsdo.denguetracker;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Info {
    private static String TAG = "Info";

    public final static long NULLLONG = -1;

    private long rowid = NULLLONG; // -1 reserved for no data stored.
    private String iwho;
    private String iwhen;
    private String iwhere;
    private String ihow;
    private String iwhat;
    private String iwhy;

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

    public String getiwhere(int format) {
        try {
            JSONObject lObj = new JSONObject(iwhere);
            switch (format) {
                case 0: // return uri geo format, "geo:0,0?q=1.3569602420177331,103.88373221520939(Mosquito Bite on Right Leg)&z=20"
                    return "geo:0,0?q=" + lObj.getString("latitude") + "," + lObj.getString("longitude") + " (" + getihow(1) + " " + getiwhat(1) + ")&z=20"; //hardcode zoom level to 20
                case 1:
                    return "Latitude: " + lObj.getString("latitude") + ", Longitude: " + lObj.getString("longitude");
                case 2:
                    return "Latitude: " + lObj.getString("latitude") + ", Longitude: " + lObj.getString("longitude") + ", Altitude: " + lObj.getString("altitude");
                default:
                    return iwhere;
            }
        } catch (JSONException e) {
            e.printStackTrace();

            return "No Location Available.";
        }
    }

    public String getihow(int format) {
        // todo, use JSON for transaction from internal code to user readable string
        String lihow;
        if (ihow.equals("MosBite")) {
            lihow = "Mosquito Bite On ";
        } else {
            lihow = "Unknown How ";
        }

        return lihow;
    }

    public String getiwhat(int format) {
        String liwhat;
        if (iwhat.equals("Head")) {
            liwhat = "Head";
        } else if (iwhat.equals("Body")) {
            liwhat = "Body";
        } else if (iwhat.equals("RightArm")) {
            liwhat = "Right Arm";
        } else if (iwhat.equals("RightHand")) {
            liwhat = "Right Hand";
        } else if (iwhat.equals("RightLeg")) {
            liwhat = "Right Leg";
        } else if (iwhat.equals("RightFoot")) {
            liwhat = "Right Foot";
        } else if (iwhat.equals("LeftArm")) {
            liwhat = "Left Arm";
        } else if (iwhat.equals("LeftHand")) {
            liwhat = "Left Hand";
        } else if (iwhat.equals("LeftLeg")) {
            liwhat = "Left Leg";
        } else if (iwhat.equals("LeftFoot")) {
            liwhat = "Left Foot";
        } else {
            liwhat = "Unknown What";
        }

        return liwhat;
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

            rowid = -1;
            iwho = null;
            iwhen = null;
            iwhere = null;
            ihow = null;
            iwhat = null;
            iwhy = null;

            return false;
        }
    }

    public String getInfo() {
        if (rowid == NULLLONG) {
            return null;
        }

        try {
            JSONObject lObj = new JSONObject();
            JSONArray lInfo = new JSONArray();
            JSONObject lInfoRec = new JSONObject();

            lObj.put("norec", 1);
            lInfoRec.put("position", 0);
            lInfoRec.put("rowid", rowid);
            lInfoRec.put("iwho", iwho);
            lInfoRec.put("iwhen", iwhen);
            lInfoRec.put("iwhere", (new JSONObject(iwhere)));
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
