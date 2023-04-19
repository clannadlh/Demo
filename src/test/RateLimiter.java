package test;

import java.util.concurrent.Semaphore;

public class RateLimiter {
    private final int tps; // TPS协议
    private final int limit; // 每秒交易限制
    private final Semaphore semaphore;

    public RateLimiter(int tps, int limit) {
        this.tps = tps;
        this.limit = limit;
        this.semaphore = new Semaphore(limit);
    }

    public void throttle() {
        while (true) {
            try {
                semaphore.acquire();
                break;
            } catch (InterruptedException e) {
                // ignore
            }
        }
        new Thread(() -> {
            try {
                Thread.sleep(1000 / tps);
            } catch (InterruptedException e) {
                // ignore
            } finally {
                semaphore.release();
            }
        }).start();
    }
}

class Main {
    public static void main(String[] args) {
        // 设置TPS和每秒交易限制
        int tps = 100;
        int perSecondLimit = 10;

        // 初始化速率限制器
        RateLimiter limiter = new RateLimiter(tps, perSecondLimit);

        // 模拟请求的到来
        int requests = 90;
        long arrivalInterval = 10; // 10毫秒

        for (int i = 0; i < requests; i++) {
            // 限制速率
            limiter.throttle();

            // 处理请求
            System.out.println(String.format("Request %d processed at %d", i+1, System.currentTimeMillis()));

            // 模拟请求到来的时间间隔
            try {
                Thread.sleep(arrivalInterval);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }
}
