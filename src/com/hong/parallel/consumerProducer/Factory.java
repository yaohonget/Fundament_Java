package com.hong.parallel.consumerProducer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Factory implements Runnable {
	
	@Override
	public void run() {
		while(produce()) {
			
		}		
	}
	
	protected boolean produce() {
		if(TVProductLine.lock.tryLock()) {
			boolean flag = TVProductLine.build(Thread.currentThread().getName());
			TVProductLine.lock.unlock();
			return flag;
		}
		else {
			return true;
		}		
	}

}
