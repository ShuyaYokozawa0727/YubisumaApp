package com.example.yubisumaapp.utility;

public class YubiSumaUtility {
    public static String[] createNumberLabel(int labelSize) {
        String[] labels = new String[labelSize+1];
        for(int index=0; index<=labelSize; index++) {
            labels[index] = ""+index;
        }
        return labels;
    }
    public static String[] createRangeLabel(int minCount, int maxCount) {
        if(minCount < maxCount) {
            String[] labels = new String[maxCount-minCount+1];
            int index = 0;
            for(int loop=minCount; loop<=maxCount; loop++) {
                labels[index] = ""+loop;
                index++;
            }
            return labels;
        } else {
            return new String[0];
        }
    }
}
