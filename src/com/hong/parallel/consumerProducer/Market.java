package com.hong.parallel.consumerProducer;

public class Market {

	public static void main(String[] args) {
		
		new Thread(new Factory(), "Factory1").start();
		new Thread(new Seller(), "Seller1").start();
		new Thread(new Seller(), "Seller2").start();
		new Thread(new Factory(), "Factory2").start();
	}

}
