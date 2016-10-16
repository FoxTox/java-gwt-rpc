package com.foxtox.rpc.core.client;

/*
 * Either setResult or setError should be called exactly once.
 */
public interface ResponseSerializer {
	void setResult(Object result);
	void setError(String reason);
}
