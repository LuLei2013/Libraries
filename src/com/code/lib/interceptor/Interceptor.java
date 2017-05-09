package com.code.lib.interceptor;

public interface Interceptor {
	OutputData intercept(Chain chain);
}
