package com.foxtox.rpc.example.server;

import com.foxtox.rpc.example.client.SumService;

public class SumServiceImpl implements SumService {

	@Override
	public int getSum(int a, int b) {
		return a + b;
	}

	@Override
	public double getSum(double a, double b) {
		return a + b;
	}

}
