package com.hong.parallel.sync;

import java.util.concurrent.locks.ReentrantLock;

public class Event {

	public static void main(String[] args) {
		Runnable tickets1 = new Ticket("concert1");
				
		Thread seller1 = new Seller(tickets1, "Seller1");
		Thread seller2 = new Seller(tickets1, "Seller2");
		
		seller1.start();
		seller2.start();

	}

}
