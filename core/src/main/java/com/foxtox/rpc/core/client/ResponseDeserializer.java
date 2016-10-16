package com.foxtox.rpc.core.client;

public interface ResponseDeserializer {
	// Returns whether the call has been completed successfully.
	boolean isSuccess();
	
	// Returns the resulting object if isSuccess() == true, otherwise returns null.
	Object getResult();
	
	// Returns error string, if isSuccess() == false, otherwise returns null.
	String getError();
}
