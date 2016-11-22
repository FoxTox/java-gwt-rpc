package com.foxtox.rpc.server.example;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import com.foxtox.rpc.client.example.ConcatenationService;
import com.foxtox.rpc.client.example.SumService;
import com.foxtox.rpc.server.RemoteServiceServlet;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/sum", "/concatenate" })
public class SumServiceRpcServlet extends RemoteServiceServlet implements SumService {

  public void init() throws ServletException {
    super.init();
    
    addService(ConcatenationService.class, new ConcatenationService() {
      public String concatenate(String first, String second) {
        return first.concat(second);
      }
    });
  }

  public Integer getSum(Integer first, Integer second) {
    return first + second;
  }

  public Double getSum(Double first, Double second) {
    return first + second;
  }
}
