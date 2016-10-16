package com.foxtox.rpc.core.client;

/*
 * Processing sequence:
 *   1. addServiceName(...)
 *   2. addServiceMethod(...)
 *   3. addParameters(...)
 *   4. finish()
 */
public interface RequestSerializer {
	
	void addServiceName(String service);
	void addServiceMethod(String method);
	
	// Adds params[begin..end)
	void addParameters(Object[] params, int begin, int end);
	
	void finish();
}
