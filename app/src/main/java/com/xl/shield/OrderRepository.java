package com.xl.shield;

import android.util.Log;

import com.xl.shieldlib.annotation.ShieldAnnotation;

/**
 * @Author : wyl
 * @Date : 2023/5/7
 * Desc :
 */
public enum OrderRepository {
    INSTANCE;

    private static final String TAG = "OrderRepository";

    @ShieldAnnotation(key = "getOrderInfo",time = 1000)
    public void getOrderInfo() {
        Log.e(TAG, "获取订单数据");
    }

//    @ShieldAnnotation(key = "calculateOrderInfo",time = 2000)
    public void calculateOrderInfo(){
        Log.e(TAG, "计算订单数据");
    }

}
