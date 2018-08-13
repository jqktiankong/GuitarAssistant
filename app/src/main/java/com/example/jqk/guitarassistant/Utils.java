package com.example.jqk.guitarassistant;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<String> lyricTransform(String lyrics) {
        String str = lyrics;
        str = str.replace("，", ",");
        str = str.replace(" ", "");
        List<String> result = new ArrayList<String>();
        Log.d("123", "str = " + str);
        StringBuffer stringBuffer = new StringBuffer(str);
        for (int i = 0; i < stringBuffer.length(); i++) {
            if ((stringBuffer.charAt(i) + "").equals(",")) {
                int last = result.size() - 1;
                result.set(last, result.get(result.size() - 1) + stringBuffer.charAt(i));
            } else {
                result.add(stringBuffer.charAt(i) + "");
            }
        }
        return result;
    }
}
