package com.example.shane_kruse.habbithub;

import java.util.ArrayList;

public class PieChartVars {

    public static ArrayList<String> xData;
    public static ArrayList<Float> yData;
    public static ArrayList<Integer> colors;

    public static void setColors(ArrayList<Integer> colors) {
        PieChartVars.colors = colors;
    }

    public static void setxData(ArrayList<String> xData) {
        PieChartVars.xData = xData;
    }

    public static void setyData(ArrayList<Float> yData) {
        PieChartVars.yData = yData;
    }

    public static float[] floatVals(ArrayList<Float> arr) {
        float[] f = new float[arr.size()];
        int i = 0;
        for (float e : arr) {
            f[i] = e;
            i++;
        }
        return f;
    }
}
