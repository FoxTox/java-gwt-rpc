package com.foxtox.rpc.example.server;

import com.foxtox.rpc.example.client.SumService;

public class SumServiceImpl implements SumService {

	@Override
	public Integer getSum(Integer a, Integer b) {
		return a + b;
	}

	@Override
	public Double getSum(Double a, Double b) {
		return a + b;
	}

}
