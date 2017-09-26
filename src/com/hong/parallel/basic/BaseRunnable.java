package com.hong.parallel.basic;

public class BaseRunnable implements Runnable {

	@Override
	public void run() {
		System.out.println("::Type - " + this.getClass().getName() + " ::Name - " + Thread.currentThread().getName()+" ::ID - "+Thread.currentThread().getId()+" ::Prio - "+Thread.currentThread().getPriority());
	}

}
