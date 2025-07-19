package com.wenqi.example.lock.livelock;

/**
 * 活锁(Livelock)示例
 * 
 * 活锁是指线程持续执行，但无法取得进展的情况。
 * 与死锁不同，活锁中的线程并没有被阻塞，而是不断地相互响应对方的行为，
 * 但都无法完成自己的工作。
 */
public class LivelockDemo {
    
    static class Resource {
        private String name;
        private Worker owner;
        
        public Resource(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public synchronized Worker getOwner() {
            return owner;
        }
        
        public synchronized void setOwner(Worker owner) {
            this.owner = owner;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    static class Worker {
        private String name;
        private boolean active;
        
        public Worker(String name) {
            this.name = name;
            this.active = true;
        }
        
        public String getName() {
            return name;
        }
        
        public boolean isActive() {
            return active;
        }
        
        /**
         * 尝试处理资源，但如果发现另一个工作者需要该资源，就礼让地放弃
         */
        public void work(Resource resource, Worker otherWorker, Resource otherResource) {
            while (active) {
                // 检查资源是否属于自己或无主
                if (resource.getOwner() == null || resource.getOwner() == this) {
                    // 如果另一个工作者也在等待这个资源
                    if (otherWorker.isActive() && 
                            otherResource.getOwner() != otherWorker) {
                        System.out.println(name + " 礼让地放弃 " + 
                                resource.getName() + " 给 " + otherWorker.getName());
                        resource.setOwner(null);
                        try {
                            Thread.sleep(100); // 模拟工作时间
                        } catch (InterruptedException e) {
                            // 忽略
                        }
                        continue;
                    }
                    
                    // 没有人等待，可以使用资源
                    resource.setOwner(this);
                    System.out.println(name + " 正在使用 " + resource.getName());
                    active = false;
                    System.out.println(name + " 完成工作");
                } else {
                    // 等待资源可用
                    System.out.println(name + " 正在等待 " + resource.getName());
                    try {
                        Thread.sleep(100); // 模拟等待时间
                    } catch (InterruptedException e) {
                        // 忽略
                    }
                }
            }
        }
    }
    
    public static void main(String[] args) {
        final Resource resourceA = new Resource("资源A");
        final Resource resourceB = new Resource("资源B");
        
        final Worker worker1 = new Worker("工作者1");
        final Worker worker2 = new Worker("工作者2");
        
        // 启动两个线程，每个工作者需要使用两个资源来完成任务
        new Thread(() -> {
            worker1.work(resourceA, worker2, resourceB);
        }).start();
        
        new Thread(() -> {
            worker2.work(resourceB, worker1, resourceA);
        }).start();
    }
} 