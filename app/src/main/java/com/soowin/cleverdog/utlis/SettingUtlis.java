package com.soowin.cleverdog.utlis;

import android.util.Log;

/**
 * Created by hxt on 2017/9/14.
 */

public class SettingUtlis {
    /**
     * 设置违章播报类型
     */
    public static void setWZType(String str) {
        PublicApplication.settingInfo.edit().putBoolean("crr", true).apply();
        PublicApplication.settingInfo.edit().putBoolean("ysx", true).apply();
        PublicApplication.settingInfo.edit().putBoolean("ygd", true).apply();
        PublicApplication.settingInfo.edit().putBoolean("md", true).apply();
        PublicApplication.settingInfo.edit().putBoolean("dt", true).apply();
        PublicApplication.settingInfo.edit().putBoolean("hwg", true).apply();
        PublicApplication.settingInfo.edit().putBoolean("jt", true).apply();
        if (!StrUtils.isEmpty(str)) {
            String[] typelist = str.split(",");
            for (int i = 0; i < typelist.length; i++) {
                if (!StrUtils.isEmpty(typelist[i]))
                    setIsPlay(Integer.parseInt(typelist[i]));
            }
        }
    }

    public static void setIsPlay(int i) {
        switch (i) {
            case 1:
                PublicApplication.settingInfo.edit().putBoolean("crr", false).apply();
                break;
            case 2:
                PublicApplication.settingInfo.edit().putBoolean("ysx", false).apply();
                break;
            case 3:
                PublicApplication.settingInfo.edit().putBoolean("hwg", false).apply();
                break;
            case 4:
                PublicApplication.settingInfo.edit().putBoolean("dt", false).apply();
                break;
            case 5:
                PublicApplication.settingInfo.edit().putBoolean("md", false).apply();
                break;
            case 6:
                PublicApplication.settingInfo.edit().putBoolean("ygd", false).apply();
                break;
            case 7:
                PublicApplication.settingInfo.edit().putBoolean("jt", false).apply();
                break;
        }
    }
}
