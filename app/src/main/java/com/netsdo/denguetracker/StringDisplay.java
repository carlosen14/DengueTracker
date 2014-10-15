package com.netsdo.denguetracker;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class StringDisplay {
    private final static String TAG = "StringDisplay";

    private final static String mStringData = new String();
    private final static JSONObject mJSONData = new JSONObject();
    private final static Locale mAppLocale = Locale.US; // Locale used by App

    private static Locale mLocale; // Locale chosen by device or user

    public StringDisplay() {
        mLocale = Locale.getDefault();

        loadData();
    }

    public static void setLocale(Locale locale) {
        mLocale = locale;
    }

    public static String getDisplay(String Str) {
        try {
            return mJSONData.getString(Str + "-" + mLocale.toString());
        } catch (JSONException e) {
            try {
                return mJSONData.getString(Str + "-" + mAppLocale.toString());
            } catch (JSONException e1) {
                return Str; // return original keyword if can't find any display string.
            }
        }
    }

    public static String getAppDisplay(String Str) {
        // faster fetching if get value of default locale only
        try {
            return mJSONData.getString(Str + "-" + mAppLocale.toString());
        } catch (JSONException e1) {
            return Str; // return original keyword if can't find any display string.
        }
    }

    private boolean loadData() {
        try {
            // string format
            mJSONData.put("iwhere-format1-" + mAppLocale.toString(), "geo:0,0?q=%1$s,%2$s (%4$s %5$s)&z=20"); //hardcode zoom level to 20
            mJSONData.put("iwhere-format2-" + mAppLocale.toString(), "Latitude:%1$s, Longitude:%2$s");
            mJSONData.put("iwhere-format3-" + mAppLocale.toString(), "Latitude:%1$s, Longitude:%2$s, Altitude:%3$s");
            mJSONData.put("iwhere-format3-" + Locale.CHINESE.toString(), "经度:%2$s, 纬度:%1$s, 高度:%3$s");

            // display index used by MosEditFragment
            mJSONData.put("Head-format1-" + mAppLocale.toString(), "0");
            mJSONData.put("Body-format1-" + mAppLocale.toString(), "1");
            mJSONData.put("RightArm-format1-" + mAppLocale.toString(), "2");
            mJSONData.put("RightHand-format1-" + mAppLocale.toString(), "3");
            mJSONData.put("RightLeg-format1-" + mAppLocale.toString(), "4");
            mJSONData.put("RightFoot-format1-" + mAppLocale.toString(), "5");
            mJSONData.put("LeftArm-format1-" + mAppLocale.toString(), "6");
            mJSONData.put("LeftHand-format1-" + mAppLocale.toString(), "7");
            mJSONData.put("LeftLeg-format1-" + mAppLocale.toString(), "8");
            mJSONData.put("LeftFoot-format1-" + mAppLocale.toString(), "9");

            // general string
            mJSONData.put("When-" + mAppLocale.toString(), "When: ");
            mJSONData.put("When-" + Locale.CHINESE.toString(), "时间");
            mJSONData.put("Where-" + mAppLocale.toString(), "Where:");
            mJSONData.put("Where-" + Locale.CHINESE.toString(), "地点");

            // fixed string
            mJSONData.put("MosBite-" + mAppLocale.toString(), "Mosquito Bite On");
            mJSONData.put("MosBite-" + Locale.CHINESE.toString(), "蚊子咬了");
            mJSONData.put("Head-" + mAppLocale.toString(), "Head");
            mJSONData.put("Head-" + Locale.CHINESE.toString(), "头");
            mJSONData.put("Body-" + mAppLocale.toString(), "Body");
            mJSONData.put("Body-" + Locale.CHINESE.toString(), "身体");
            mJSONData.put("RightArm-" + mAppLocale.toString(), "Right Arm");
            mJSONData.put("RightArm-" + Locale.CHINESE.toString(), "右臂");
            mJSONData.put("RightHand-" + mAppLocale.toString(), "Right Hand");
            mJSONData.put("RightHand-" + Locale.CHINESE.toString(), "右手");
            mJSONData.put("RightLeg-" + mAppLocale.toString(), "Right Leg");
            mJSONData.put("RightLeg-" + Locale.CHINESE.toString(), "右腿");
            mJSONData.put("RightFoot-" + mAppLocale.toString(), "Right Foot");
            mJSONData.put("RightFoot-" + Locale.CHINESE.toString(), "右脚");
            mJSONData.put("LeftArm-" + mAppLocale.toString(), "Left Arm");
            mJSONData.put("LeftArm-" + Locale.CHINESE.toString(), "左臂");
            mJSONData.put("LeftHand-" + mAppLocale.toString(), "Left Hand");
            mJSONData.put("LeftHand-" + Locale.CHINESE.toString(), "左手");
            mJSONData.put("LeftLeg-" + mAppLocale.toString(), "Left Leg");
            mJSONData.put("LeftLeg-" + Locale.CHINESE.toString(), "左腿");
            mJSONData.put("LeftFoot-" + mAppLocale.toString(), "Left Foot");
            mJSONData.put("LeftFoot-" + Locale.CHINESE.toString(), "左脚");

            return true;
        } catch (JSONException e) {
            e.printStackTrace();

            return false;
        }
    }

    public String getLocaleList() throws JSONException {
        // provide a list of supported locals.
        JSONObject lObj = new JSONObject();
        JSONArray lLocale = new JSONArray();
        {
            JSONObject lLocaleRec = new JSONObject();
            lLocaleRec.put("locale", Locale.CHINESE.toString());
            lLocaleRec.put("name", Locale.CHINESE.getDisplayName());
            lLocale.put(lLocaleRec);
        }
        {
            JSONObject lLocaleRec = new JSONObject();
            lLocaleRec.put("locale", Locale.ENGLISH.toString());
            lLocaleRec.put("name", Locale.ENGLISH.getDisplayName());
            lLocale.put(lLocaleRec);
        }
        {
            Locale lloc = new Locale("ms");
            JSONObject lLocaleRec = new JSONObject();
            lLocaleRec.put("locale", lloc.toString());
            lLocaleRec.put("name", lloc.getDisplayName());
            lLocale.put(lLocaleRec);
        }
        lObj.put("locale", lLocale);
        Log.d(TAG, lObj.toString());

        return lObj.toString();
    }
}
