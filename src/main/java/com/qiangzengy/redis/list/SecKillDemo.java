package com.qiangzengy.redis.list;

import redis.clients.jedis.Jedis;

/**
 * @author qiangzengy@gmail.com
 * @date 2021/4/17
 */
public class SecKillDemo {

    private Jedis jedis = new Jedis("192.168.1.118");

    /**
     * 秒杀抢购请求入队
     * @param secKillRequest
     */
    public void enqueueSecKillRequest(String secKillRequest) {
        jedis.lpush("sec_kill_request_queue", secKillRequest);
    }

    /**
     * 秒杀抢购请求出队
     * @return
     */
    public String dequeueSecKillRequest() {
        return jedis.rpop("sec_kill_request_queue");
    }

    public static void main(String[] args) throws Exception {
        SecKillDemo demo = new SecKillDemo();

        for(int i = 0; i < 10; i++) {
            demo.enqueueSecKillRequest("第" + (i + 1) + "个秒杀请求");
        }

        while(true) {
            String secKillRequest = demo.dequeueSecKillRequest();

            if(secKillRequest == null
                    || "".equals(secKillRequest)) {
                break;
            }

            System.out.println(secKillRequest);
        }
    }

}
