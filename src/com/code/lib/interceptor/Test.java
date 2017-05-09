package com.code.lib.interceptor;

import java.util.ArrayList;
import java.util.List;

public class Test {
	public static List<Interceptor> mInterceptors = new ArrayList<Interceptor>();

	public static void main(String[] args) {
		InputData inputData = new InputData();
		Chain chain = init();
		addInterceptor(inputData);
		OutputData outputData = chain.proceed(inputData);
		System.out.println(outputData);
	}

	private static Chain init() {
		return new Chain(0);
	}

	private static void addInterceptor(final InputData inputData) {
		mInterceptors.add(new Interceptor() {

			@Override
			public OutputData intercept(Chain chain) {
				System.out.println("11111111------start------1111111");
				OutputData outData = chain.proceed(inputData);
				System.out.println("11111111------end------1111111");
				return outData;
			}
		});

		mInterceptors.add(new Interceptor() {

			@Override
			public OutputData intercept(Chain chain) {
				System.out.println("222222------start------222222");
				OutputData outData = chain.proceed(inputData);
				System.out.println("222222------end------222222");
				return outData;
			}
		});

		mInterceptors.add(new Interceptor() {

			@Override
			public OutputData intercept(Chain chain) {
				System.out.println("333333------start------33333");
				OutputData outData = chain.proceed(inputData);
				System.out.println("333333------end------3333333");
				return outData;
			}
		});
	}
}
