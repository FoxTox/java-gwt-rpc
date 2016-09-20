package com.foxtox.rpc;

public interface AsyncCallback<ReturnType> {
	void onSuccess(ReturnType returnValue);
	void ofFailure(String reason);
}
