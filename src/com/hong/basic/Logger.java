package com.hong.basic;

public class Logger {
	
	public static Object SYNC = new Object();
	
	public static void info(Object pClass, String pMsg) {
		synchronized (SYNC) {
			System.out.println("[Info] " + pClass.getClass().getName() + " : " + pMsg);
		}
	}

	public static void err(Object pClass, String pMsg) {
		synchronized (SYNC) {
			System.err.println("[Error] " + pClass.getClass().getName() + " : " + pMsg);
		}
	}
	
	public static void info(String pMsg) {
		synchronized (SYNC) {
			System.out.println("[Info] " + pMsg);
		}
	}

	public static void err(String pMsg) {
		synchronized (SYNC) {
			System.err.println("[Error] " + pMsg);
		}
	}
	
}
