package com.yw.butterknife;

import android.app.Activity;

import java.util.HashMap;

/**
 * create by yangwei
 * on 2020-01-04 23:09
 */
public class ButterKnife {
    public static void bind(Activity activity) {
        //获取包名+类名
        String className = activity.getClass().getName();
        try {
            //利用反射创建一个实例对象
            Class<?> newClass = Class.forName(className + "_ViewBinding");
            newClass.getConstructor(activity.getClass()).newInstance(activity);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
