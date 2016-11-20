package com.foxtox.rpc.client.example;

import com.foxtox.rpc.client.RemoteService;
import com.foxtox.rpc.client.RemoteServiceRelativePath;

@RemoteServiceRelativePath("/concatenate")
public interface ConcatenationService extends RemoteService {
  String concatenate(String first, String second);
}
