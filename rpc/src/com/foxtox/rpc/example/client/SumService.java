package com.foxtox.rpc.example.client;

import com.foxtox.rpc.RemoteService;

public interface SumService extends RemoteService {
	Integer getSum(Integer a, Integer b);
	Double getSum(Double a, Double b);
}
