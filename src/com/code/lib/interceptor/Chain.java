package com.code.lib.interceptor;

public class Chain {
	private int index;

	public Chain(int index) {
		this.index = index;
	}

	public OutputData proceed(InputData input) {
		if (index >= Test.mInterceptors.size()) {
			return realProcced(input);
		}
		Interceptor interceptor = Test.mInterceptors.get(index);
		Chain chain = new Chain(index + 1);
		return interceptor.intercept(chain);
	}

	private OutputData realProcced(InputData input) {
		System.out.println("...realProcced...");
		return null;
	}
}
