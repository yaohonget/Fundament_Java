package com.hong.parallel;

import com.hong.parallel.basic.BaseRunnable;
import com.hong.parallel.basic.BaseThread;

public class ThreadTest {
	public static void main(String [] args) {
		Thread t1 = new BaseThread();
		t1.setName("Test Thread 1");
		t1.setPriority(Thread.MAX_PRIORITY);
		//t1.setDaemon(true);
		t1.start();
		
		Runnable r1 = new BaseRunnable();
		new Thread(r1, "Test Thread 2").start();
		
	}
}
