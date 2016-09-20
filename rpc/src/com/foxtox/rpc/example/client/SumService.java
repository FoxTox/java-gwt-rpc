package com.foxtox.rpc.example.client;

import com.foxtox.rpc.RemoteService;

public interface SumService extends RemoteService {
	int getSum(int a, int b);
	double getSum(double a, double b);
}
