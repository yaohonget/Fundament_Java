package com.hong.parallel;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.hong.parallel.basic.BaseCallable;
import com.hong.parallel.basic.BaseRunnable;
import com.hong.parallel.basic.BaseThread;

public class ThreadTest {
	public static void main(String[] args) {
		/*
		 * Thread
		 */
		Thread t1 = new BaseThread();
		t1.setName("Test Thread 1");
		t1.setPriority(Thread.MAX_PRIORITY);
		// t1.setDaemon(true);
		t1.start();
		
		/*
		 * Runnable
		 */
		Runnable r1 = new BaseRunnable();
		new Thread(r1, "Test Thread 2").start();

		/*
		 * Executor framework + Callable + Future
		 */
		ExecutorService es = Executors.newCachedThreadPool();
		Callable<Integer> task = new BaseCallable();
		Future<Integer> future = es.submit(task);
		es.shutdown();
		try {
			Thread.sleep(2000);
			if (future.get() != null) {
				System.out.println("result:" + future.get());
			} else {
				System.out.println("get nothing.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * Sequence : join & yield
		 */
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName() + " start.");
				try {
					Thread.yield();
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName() + " end.");
			}
		}, "thread 2");
		
		Thread t3 = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName() + " start.");
				try {
					t2.join();
					Thread.sleep(5000);					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName() + " end.");
			}
		}, "thread 3");
		
		t3.start();
		t2.start();
		
	}
}
