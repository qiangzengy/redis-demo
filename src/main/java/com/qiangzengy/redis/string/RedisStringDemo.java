package com.qiangzengy.redis.string;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.List;

/**
 * @author qiangzengy@gmail.com
 * @date 2021/4/17
 */
public class RedisStringDemo {

    public static void main(String[] args) {

        Jedis jedis = new Jedis("192.168.1.118");

        // 1.简单的缓存
        jedis.set("key1","value1");
        String key1 = jedis.get("key1");
        System.out.println(key1);

        // 2.基于nx实现分布式锁
        jedis.del("lock_test");
        // 2.1 第一次加锁结果
        String result = jedis.set("lock_test", "value_test", SetParams.setParams().nx());
        System.out.println("第一次加锁结果:"+result);
        // 2.2 第二次加锁结果
        String result2 = jedis.set("lock_test", "value_test", SetParams.setParams().nx());
        System.out.println("第二次加锁结果:"+result2);

        // 3.批量操作
        // 3.1 批量添加
        Long msetnx = jedis.msetnx("k1", "v1",
                "k2", "v2",
                "k3", "v3");
        System.out.println("批量添加结果1："+msetnx);


        msetnx = jedis.msetnx("k1", "v1",
                "k2", "v2",
                "k3", "v3");
        System.out.println("批量添加结果2："+msetnx);
        // 3.2 批量查询
        List<String> mget = jedis.mget("k1", "k2");
        System.out.println("批量查询结果："+mget);

        // 3.3 批量修改
        jedis.mset("k1","v11","k3","v33");
        mget = jedis.mget("k1", "k3");
        System.out.println("批量查询结果："+mget);

        // 3.4 批量删除
        jedis.del("k1","k2","k3");
        mget = jedis.mget("k1", "k2","k3");
        System.out.println("批量查询结果："+mget);

        // 4. 唯一ID生成器
        jedis.del("only_id");
        Long onlyId =0L;
        for (int i = 0; i < 10; i++){
            onlyId = jedis.incr("only_id");
            System.out.println("生成的第"+i+"个id,ID:"+onlyId);
        }

        // 点赞计数器
        System.out.println("点赞计数："+onlyId);

        // 取消点赞
        Long only_id = jedis.decr("only_id");
        System.out.println("取消点赞后，点赞数："+only_id);

    }

}
