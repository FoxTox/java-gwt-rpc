package com.foxtox.rpc.core.client;

public interface SumService extends RemoteService {
	Integer getSum(Integer first, Integer second);
	Double getSum(Double first, Double second);
}
