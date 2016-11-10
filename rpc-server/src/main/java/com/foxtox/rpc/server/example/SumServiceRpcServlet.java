package com.foxtox.rpc.server.example;

import javax.servlet.annotation.WebServlet;

import com.foxtox.rpc.client.example.SumService;
import com.foxtox.rpc.server.RemoteServiceServlet;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/sum")
public class SumServiceRpcServlet extends RemoteServiceServlet implements SumService {

  public Integer getSum(Integer first, Integer second) {
    return first + second;
  }

  public Double getSum(Double first, Double second) {
    return first + second;
  }
}
