package com.xl.shieldlib;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author : wyl
 * @Date : 2023/5/6
 * Desc :
 */
public class ShieldUtils {

    private final ConcurrentHashMap<String, Long> concurrentHashMap = new ConcurrentHashMap<>();

    private ShieldUtils() {

    }

    private static class Holder {
        static ShieldUtils shieldUtils = new ShieldUtils();
    }


    public static ShieldUtils getInstance() {
        return Holder.shieldUtils;
    }


    public boolean isShield(String key, int time) {
        Long lastTime = concurrentHashMap.get(key);
        if (lastTime == null) {
            concurrentHashMap.put(key, System.currentTimeMillis());
            return false;
        }
        long nowTime = System.currentTimeMillis();
        if (nowTime - lastTime < time) {
            System.err.println("事件「" + key + "」被拦截");
            return true;
        }
        concurrentHashMap.put(key, System.currentTimeMillis());
        return false;
    }

}
