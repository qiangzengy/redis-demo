package com.qiangzengy.redis.list;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    public long getBlogId() {
        return jedis.incr("blog_id_counter");
    }

    /**
     * 发表一篇博客
     */
    public boolean publishBlog(long id, Map<String, String> blog) {
        if(jedis.hexists("blog:" + id, "title")) {
            return false;
        }
        blog.put("content_length", String.valueOf(blog.get("content").length()));

        jedis.hmset("blog:" + id, blog);
        jedis.lpush("blog_list", String.valueOf(id));

        return true;
    }

    /**
     * 查看一篇博客
     * @param id
     * @return
     */
    public Map<String, String> findBlogById(long id) {
        Map<String, String> blog = jedis.hgetAll("blog:" + id);
        incrementBlogViewCount(id);
        return blog;
    }

    /**
     * 更新一篇博客
     */
    public void updateBlog(long id, Map<String, String> updatedBlog) {
        String updatedContent = updatedBlog.get("content");
        if(updatedContent != null && !"".equals(updatedContent)) {
            updatedBlog.put("content_length", String.valueOf(updatedContent.length()));
        }

        jedis.hmset("blog:" + id, updatedBlog);
    }

    /**
     * 对博客进行点赞
     * @param id
     */
    public void incrementBlogLikeCount(long id) {
        jedis.hincrBy("blog:" + id, "like_count", 1);
    }

    /**
     * 增加博客浏览次数
     * @param id
     */
    public void incrementBlogViewCount(long id) {
        jedis.hincrBy("blog:" + id, "view_count", 1);
    }

    /**
     * 分页查询博客
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<String> findBlogByPage(int pageNo, int pageSize) {
        int startIndex = (pageNo - 1) * pageSize;
        int endIndex = pageNo * pageSize - 1;
        return jedis.lrange("blog_list", startIndex, endIndex);
    }

    public static void main(String[] args) {
        BlogDemo demo = new BlogDemo();

        // 发表一篇博客
        long id = demo.getBlogId();

        Map<String, String> map = new HashMap<String, String>();
        map.put("title","java技术博客");
        map.put("content","学习redis的一次笔记");
        map.put("anthor","qiangzneg");

        demo.publishBlog(id, map);

        // 更新一篇博客
        Map<String, String> updatedBlog = new HashMap<String, String>();
        updatedBlog.put("title", "我特别的喜欢学习Redis");
        updatedBlog.put("content", "我平时喜欢到官方网站上去学习Redis");

        demo.updateBlog(id, updatedBlog);

        // 构造20篇博客数据
        for(int i = 0; i < 20; i++) {
            id = demo.getBlogId();

            map = new HashMap<String, String>();
            map.put("id", String.valueOf(id));
            map.put("title", "第" + (i + 1) + "篇博客");
            map.put("content", "学习第" + (i + 1) + "篇博客，是一件很有意思的事情");
            map.put("author", "qiangzengy");
            map.put("time", "2020-01-01 10:00:00");

            demo.publishBlog(id, map);
        }

        // 有人分页浏览所有的博客，先浏览第一页
        int pageNo = 1;
        int pageSize = 10;

        List<String> blogPage = demo.findBlogByPage(pageNo, pageSize);
        System.out.println("展示第一页的博客......");
        for(String blogId : blogPage) {
            map = demo.findBlogById(Long.valueOf(blogId));
            System.out.println(map);
        }

        pageNo = 2;

        blogPage = demo.findBlogByPage(pageNo, pageSize);
        System.out.println("展示第二页的博客......");
        for(String blogId : blogPage) {
            map = demo.findBlogById(Long.valueOf(blogId));
            System.out.println(map);
        }

        // 有别人点击进去查看你的博客的详细内容，并且进行点赞
        Random random = new Random();
        int blogIndex = random.nextInt(blogPage.size());
        String blogId = blogPage.get(blogIndex);

        Map<String, String> blogResult = demo.findBlogById(Long.valueOf(blogId));
        System.out.println("查看博客的详细内容：" + blogResult);
        demo.incrementBlogLikeCount(Long.valueOf(blogId));

        // 你自己去查看自己的博客，看看浏览次数和点赞次数
        blogResult = demo.findBlogById(Long.valueOf(blogId));
        System.out.println("自己查看博客的详细内容：" + blogResult);
    }


}
