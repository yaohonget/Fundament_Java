package com.hong.parallel.consumerProducer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TVProductLine {

	private static int maxProd = 20;
	private static int logProd = 0;
	private static int inventories = 0;
	
	public static Lock lock = new ReentrantLock();
	
	public static boolean build(String name) {
		if(logProd >= maxProd) {
			System.out.println(name + " want, but no more product!");
			return false;
		}
		else {
			logProd++;
			inventories++;
			status(name + " produce. ");
			return true;
		}		
	}
	
	public static boolean delivery(String name) {
		if(inventories > 0) {
			inventories--;
			status(name + " sell. ");
		}
		if(logProd >= maxProd) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public static void status(String name) {
		System.out.println(name + " -> LOG : " + logProd + "  Inventory : " + inventories);
	}
}
