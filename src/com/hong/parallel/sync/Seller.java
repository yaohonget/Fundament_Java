package com.hong.parallel.sync;

public class Seller extends Thread {
	Seller(Runnable runnable, String name) {
		super(runnable, name);
	}
}
