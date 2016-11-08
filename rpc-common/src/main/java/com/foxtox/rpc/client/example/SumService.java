package com.foxtox.rpc.client.example;

import com.foxtox.rpc.client.RemoteService;

public interface SumService extends RemoteService {
	Integer getSum(Integer first, Integer second);
	Double getSum(Double first, Double second);
}