package com.netsdo;

import com.netsdo.denguetracker.MainActivity;

public class Temperature {
    public final static float HUMAN_MAX = 45;
    public final static float HUMAN_MIN = 30;
    private final static float KELVIN_BASE = (float) 273.15;
    float mTemp = 0; // in Celsius only

    private static float convertFtoC(float fahrenheit) {
        return ((fahrenheit - 32) * 5 / 9);
    }

    private static float convertCtoF(float celsius) {
        return (((celsius * 9) / 5) + 32);
    }

    private static float convertCtoK(float celsius) {
        return (celsius - KELVIN_BASE);
    }

    private static float convertKtoC(float kelvin) {
        return (kelvin + KELVIN_BASE);
    }

    public static String toString(float celsius) {
        if (MainActivity.mStringDisplay.getDisplay("TempUnit") == "C") {
            return String.format("%.2f C", celsius);
        } else if (MainActivity.mStringDisplay.getDisplay("TempUnit") == "F") {
            return String.format("%.2f F", convertCtoF(celsius));
        } else if (MainActivity.mStringDisplay.getDisplay("TempUnit") == "K") {
            return String.format("%.2f K", convertCtoK(celsius));
        } else {
            return String.format("%.2f C", celsius); // use Celsius as default unit
        }
    }

    public String toString() {
        if (MainActivity.mStringDisplay.getDisplay("TempUnit") == "C") {
            return String.format("%.2f C", mTemp);
        } else if (MainActivity.mStringDisplay.getDisplay("TempUnit") == "F") {
            return String.format("%.2f F", convertCtoF(mTemp));
        } else if (MainActivity.mStringDisplay.getDisplay("TempUnit") == "K") {
            return String.format("%.2f K", convertCtoK(mTemp));
        } else {
            return String.format("%.2f C", mTemp); // use Celsius as default unit
        }
    }

    public float getTempC() {
        return mTemp;
    }

    public void setTempC(float celsius) {
        mTemp = celsius;
    }

    public float getTempF() {
        return convertCtoF(mTemp);
    }

    public void setTempF(float fahrenheit) {
        mTemp = convertFtoC(fahrenheit);
    }

    public float getTempK() {
        return convertCtoK(mTemp);
    }

    public void setTempK(float kelvin) {
        mTemp = convertKtoC(kelvin);
    }
}
