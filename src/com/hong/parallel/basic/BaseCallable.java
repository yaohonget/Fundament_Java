package com.hong.parallel.basic;

import java.util.concurrent.Callable;

public class BaseCallable implements Callable<Integer> {

	private int sum = 0;
	@Override
	public Integer call() throws Exception {
		System.out.println(Thread.currentThread().getName() + " Start.");
		Thread.sleep(2000); 
        for(int i=0 ;i<101;i++){  
            sum=sum+i;  
        }  
        System.out.println(Thread.currentThread().getName() + " End.");  
        return sum;  
	}

}
