package com.hong.parallel.sync;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Ticket implements Runnable {

	private Random random;
	
	private int inventory = 20;
	private String name = "";
	
	private Lock lock;
	private Object object;
	
	Ticket(String pName) {
		this.name = pName;
		this.lock = new ReentrantLock();
		this.object = new Object();
		this.random = new Random();
	}
	
	public void setLock(Lock pLock) {
		this.lock = pLock;
	}
	
	@Override
	public void run() {
		while(true) {

/*
 * Use Lock
 */
//			lock.lock();
//			if(isDeliveriable()) {
//				delivery();
//				System.out.println(Thread.currentThread().getName() + " sold a ticket of " + this.name + ". Currently inventory is " + inventory);
//			}
//			else {
//				break;
//			}
//			lock.unlock();
			
/*
 * User Keyword synchronized
 */
			synchronized (object) {
				if(isDeliveriable()) {
					delivery();
					System.out.println(Thread.currentThread().getName() + " sold a ticket of " + this.name + ". Currently inventory is " + inventory);
				}
				else {
					break;
				}
			}
		}
	}
	
	protected void delivery() {	
		try {
			Thread.sleep(random.nextInt(2000));
			inventory--;
		}
		catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}			
	}
	
	protected boolean isDeliveriable() {
		return inventory > 0 ? true : false;
	}
}
