package com.netsdo.denguetracker;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class StringDisplay {
    private static String TAG = "StringDisplay";
    private static String mStringData;
    private static JSONObject mJSONData = new JSONObject();
    private static Locale mLocale = Locale.getDefault(); // device or user chosen locale.
    private static Locale mDefaultLocale = Locale.US; // App Default Locale.

    public StringDisplay() {
//        mLocale = Locale.getDefault();
//        mDefaultLocale = Locale.US;
//        mJSONData = new JSONObject();

        loadData();
    }

    public void setLocale(Locale locale) {
        mLocale = locale;
    }

    public String getDisplay(String Str) {
        String lDisplay;
        JSONObject a;

        try {
            return mJSONData.getString(Str + "-" + mLocale.toString());
        } catch (JSONException e) {
            try {
                return mJSONData.getString(Str + "-" + mDefaultLocale.toString());
            } catch (JSONException e1) {
                return Str; // return original keyword if everything not there.
            }
        }
    }

    public boolean loadData() {
        try {
            // system format
            mJSONData.put("iwhere-format0-" + mDefaultLocale.toString(), "geo:0,0?q=%1$s,%2$s (%4$s %5$s)&z=20"); //hardcode zoom level to 20
            mJSONData.put("iwhere-format1-" + mDefaultLocale.toString(), "Latitude:%1$s, Longitude:%2$s");
            mJSONData.put("iwhere-format2-" + mDefaultLocale.toString(), "Latitude:%1$s, Longitude:%2$s, Altitude:%3$s");

            // display to user
            mJSONData.put("MosBite-" + mDefaultLocale.toString(), "Mosquito Bite On");
            mJSONData.put("MosBite-" + Locale.CHINESE.toString(), "蚊子咬");
            mJSONData.put("Head-" + mDefaultLocale.toString(), "Head");
            mJSONData.put("Head-" + Locale.CHINESE.toString(), "头");
            mJSONData.put("Body-" + mDefaultLocale.toString(), "Body");
            mJSONData.put("Body-" + Locale.CHINESE.toString(), "身体");
            mJSONData.put("RightArm-" + mDefaultLocale.toString(), "Right Arm");
            mJSONData.put("RightArm-" + Locale.CHINESE.toString(), "右臂");
            mJSONData.put("RightHand-" + mDefaultLocale.toString(), "Right Hand");
            mJSONData.put("RightHand-" + Locale.CHINESE.toString(), "右手");
            mJSONData.put("RightLeg-" + mDefaultLocale.toString(), "Right Leg");
            mJSONData.put("RightLeg-" + Locale.CHINESE.toString(), "右腿");
            mJSONData.put("RightFoot-" + mDefaultLocale.toString(), "Right Foot");
            mJSONData.put("RightFoot-" + Locale.CHINESE.toString(), "右脚");
            mJSONData.put("LeftArm-" + mDefaultLocale.toString(), "Left Arm");
            mJSONData.put("LeftArm-" + Locale.CHINESE.toString(), "左臂");
            mJSONData.put("LeftHand-" + mDefaultLocale.toString(), "Left Hand");
            mJSONData.put("LeftHand-" + Locale.CHINESE.toString(), "左手");
            mJSONData.put("LeftLeg-" + mDefaultLocale.toString(), "Left Leg");
            mJSONData.put("LeftLeg-" + Locale.CHINESE.toString(), "左腿");
            mJSONData.put("LeftFoot-" + mDefaultLocale.toString(), "Left Foot");
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
