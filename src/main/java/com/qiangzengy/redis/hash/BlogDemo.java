package com.qiangzengy.redis.hash;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qiangzengy@gmail.com
 * @date 2021/4/17
 */
public class BlogDemo {

    private Jedis jedis = new Jedis("192.168.1.118");

    /**
     * 获取博客id
     * @return
     */
    public Long getBlogId(){
        return jedis.incr("blog_id");
    }


    /**
     * 模拟发表博客
     */
    public boolean publishBlog(Long id, Map<String,String> map){
        jedis.hmset("blog"+id,map);
        return true;
    }

    /**
     * 模拟查看博客
     */
    public Map<String,String> content(Long id){
        Map<String, String> map = jedis.hgetAll("blog" + id);
        // 增加浏览次数
        incrementBlogViewCount(id);
        return map;
    }

    /**
     * 增加浏览次数
     */
    public void incrementBlogViewCount(Long id){
        jedis.hincrBy("blog"+id,"view_count", 1);
    }

    /**
     * 点赞
     */
    public void incrementBlogLikeCount(Long id) {
        jedis.hincrBy("blog" + id, "like_count", 1);
    }

    /**
     * 取消点赞
     */
    public void canBlogLikeCount(Long id) {
        jedis.hincrBy("blog" + id, "like_count", -1);
    }


    /**
     * 更新博客
     */
    public boolean updateBlog(Long id,Map<String,String> map){
        jedis.hmset("blog"+id,map);
        return true;
    }

    /**
     * 删除博客
     */
    public boolean deleteBlog(Long id){
        jedis.del("blog"+id);
        return true;
    }

    public static void main(String[] args){
        BlogDemo demo = new BlogDemo();
        // 获取ID
        Long blogId = demo.getBlogId();
        // 发表博客
        Map<String,String> map = new HashMap<String, String>();
        map.put("title","java技术博客");
        map.put("content","学习redis的一次笔记");
        map.put("anthor","qiangzneg");
        demo.publishBlog(blogId,map);

        // 查看博客
        Map<String, String> content = demo.content(blogId);
        System.out.println(content);

        // 修改博客

        map.put("content","学习redis的一次笔记,进阶技术");
        demo.updateBlog(blogId,map);
        content = demo.content(blogId);
        System.out.println(content);

        // 点赞
        demo.incrementBlogLikeCount(blogId);
        content = demo.content(blogId);
        System.out.println(content);

        // 取消点赞
        demo.canBlogLikeCount(blogId);
        content = demo.content(blogId);
        System.out.println(content);

        // 删除博客
        demo.deleteBlog(blogId);
    }


}
