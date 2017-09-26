package com.hong.parallel.consumerProducer;

public class Seller implements Runnable {	
	
	@Override
	public void run() {
		while(sell()) {
			
		}
	}
	
	protected boolean sell() {
		if(TVProductLine.lock.tryLock()) {
			boolean flag = TVProductLine.delivery(Thread.currentThread().getName());
			TVProductLine.lock.unlock();
			return flag;
		}
		else {
			return true;
		}
	}

}
