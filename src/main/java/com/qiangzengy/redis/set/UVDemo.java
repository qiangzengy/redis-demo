package com.qiangzengy.redis.set;

import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qiangzengy@gmail.com
 * @date 2021/4/18
 */
public class UVDemo {

    private Jedis jedis = new Jedis("192.168.1.118");

    /**
     * 添加一次用户访问记录
     */
    public void addUserAccess(long userId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        jedis.sadd("user_access::" + today, String.valueOf(userId));
    }

    /**
     * 获取当天的网站uv的值
     * @return
     */
    public long getUV() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        return jedis.scard("user_access::" + today);
    }

    public static void main(String[] args) throws Exception {
        UVDemo demo = new UVDemo();

        for(int i = 0; i < 100; i++) {
            long userId = i + 1;

            for(int j = 0; j < 10; j++) {
                demo.addUserAccess(userId);
            }
        }

        long uv = demo.getUV();
        System.out.println("当日uv为：" + uv);
    }

}
