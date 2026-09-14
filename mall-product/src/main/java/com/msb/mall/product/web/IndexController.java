package com.msb.mall.product.web;

import com.msb.mall.product.entity.CategoryEntity;
import com.msb.mall.product.service.CategoryService;
import com.msb.mall.product.vo.Catalog2VO;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.hibernate.validator.constraints.br.TituloEleitoral;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Trace
    @GetMapping({"/","/home","/index"})
    public String index(Model model){

        // 查询出所有的一级分类的信息
        List<CategoryEntity> list = categoryService.getLeve1Category();
        model.addAttribute("categorys",list);
        // classPath:/templates/
        // .html
        return "index";
    }

    // index/catalog.json
    @Trace
    @ResponseBody
    @RequestMapping("/index/catalog.json")
    public Map<String, List<Catalog2VO>> getCatalog2JSON(){
        Map<String, List<Catalog2VO>> map = categoryService.getCatelog2JSON();
        return map;
    }

    /**
     * 1.锁会自动续期，如果业务时间超长，运行期间Redisson会自动给锁重新添加30s，不用担心业务时间，锁自动过去而造成的数据安全问题
     * 2.加锁的业务只要执行完成， 那么就不会给当前的锁续期，即使我们不去主动的释放锁，锁在默认30s之后也会自动的删除
     *
     * 如果我们指定了锁的过期时间，那么在源码中会直接帮我们创建一个过期时间是指定值的锁，时间到期后就会直接把该锁给删除
     * 如果我们没有指定过期时间，那么在执行的时候首先会创建一把锁且过期时间是30s然后会创建异步任务，每个10s执行一次任务来续期
     * @return
     */
    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        RLock myLock = redissonClient.getLock("myLock");
        // 加锁
       // myLock.lock();
        // 获取锁，并且给定的过期时间是10s 问题？ 业务如果时间超过了10s，会不会自动续期？
        // 通过效果演示我们可以发现，指定了过期时间后那么自动续期就不会生效了，这时我们就需要注意设置的过期时间一定要满足我们的业务场景
        // 实际开发中我们最好指定过期时间-->性能角度考虑
        myLock.lock(10, TimeUnit.SECONDS);
        try {
            System.out.println("加锁成功...业务处理....." + Thread.currentThread().getName());
            Thread.sleep(30000);
        }catch (Exception e){

        }finally {
            System.out.println("释放锁成功..." +  Thread.currentThread().getName());
            // 释放锁
            myLock.unlock();
        }
        return "hello";
    }

    @GetMapping("/writer")
    @ResponseBody
    public String writerValue(){
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        // 加写锁
        RLock rLock = readWriteLock.writeLock();
        String s = null;
        rLock.lock(); // 加写锁
        try {
            System.out.println("加写锁成功....");
            s = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set("msg",s);
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }

    /**
     * 读 读 ：相对于没有加锁
     * 写 读 ：需要等待写锁释放
     * 写 写 ： 阻塞的方式
     * 读 写 ：读数据的时候也会添加锁，那么写的行为也会阻塞
     * @return
     */
    @GetMapping("/reader")
    @ResponseBody
    public String readValue(){
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        // 加读锁
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        String s = null;
        try {
            System.out.println("加读锁成功....");
            s = stringRedisTemplate.opsForValue().get("msg");
            Thread.sleep(30000);
        }catch (Exception e){

        }finally {
            rLock.unlock();
        }

        return s;
    }

    @GetMapping("/lockDoor")
    @ResponseBody
    public String lockDoor(){
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(5);
        try {
            door.await(); // 等待数量降低到0
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "关门熄灯...";
    }

    @GetMapping("/goHome/{id}")
    @ResponseBody
    public String goHome(@PathVariable Long id){
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown(); // 递减的操作
        return id + "下班走人";
    }


    @GetMapping("/park")
    @ResponseBody
    public String park(){
        RSemaphore park = redissonClient.getSemaphore("park");
        boolean b = true;
        try {
            // park.acquire(); // 获取信号 阻塞到获取成功
            b = park.tryAcquire();// 返回获取成功还是失败
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "停车是否成功:" + b;
    }

    @GetMapping("/release")
    @ResponseBody
    public String release(){
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release();
        return "释放了一个车位";
    }


}
