package com.foxtox.rpc.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.foxtox.rpc.client.jvm.JsonRequestDeserializer;
import com.foxtox.rpc.client.jvm.JsonResponseSerializer;
import com.foxtox.rpc.common.RequestDeserializer;
import com.foxtox.rpc.common.ResponseSerializer;
import com.foxtox.rpc.common.RpcRequest;

@SuppressWarnings("serial")
public class RemoteServiceServlet extends HttpServlet {

	private static final int BUFFER_SIZE = 4096;

	public RemoteServiceServlet() {
		try {
			for (Class<?> cls : getClass().getInterfaces()) {
				if (cls.getCanonicalName().endsWith("Service"))
					addService(cls, this);
			}
		} catch (Exception e) {
			assert(false);
		}
	}

	public void addService(Class<?> iface, Object impl) throws Exception {
		if (iface == null || impl == null)
			throw new NullPointerException();
		if (!iface.isInstance(impl))
			throw new Exception("Object does not implement " + iface.getCanonicalName());
		ServiceInfo info = new ServiceInfo(iface, impl);
		ServiceInfo service = services.putIfAbsent(iface.getCanonicalName(), info);
		if (service != null)
			throw new Exception("Service " + iface.getCanonicalName() + " is already implemented.");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		ResponseSerializer serializer = new JsonResponseSerializer();
		try {
			processPost(request, response, serializer);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			response.getOutputStream().write(serializer.serializeError(sw.getBuffer().toString()));
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter writer = response.getWriter();
		writer.println("<h1>RPC Servlet</h1>");

		if (services.size() > 0) {
			writer.println("<h2>Implemented services:</h2>\n<ul>");
			for (Map.Entry<String, ServiceInfo> service : services.entrySet())
				writer.println("<li>" + service.getKey() + "</li>");
			writer.println("</ul>");
		} else {
			writer.println("<h2>Implemented services:</h2>none");
		}
	}

	protected byte[] readContent(HttpServletRequest request) throws IOException {
		InputStream input = request.getInputStream();
		byte[] buffer = new byte[BUFFER_SIZE];
		ByteArrayOutputStream output = new ByteArrayOutputStream(BUFFER_SIZE);
		while (true) {
			int byteCount = input.read(buffer);
			if (byteCount == -1)
				break;
			output.write(buffer, 0, byteCount);
		}
		input.close();
		return output.toByteArray();
	}

	protected void processPost(HttpServletRequest request, HttpServletResponse response, ResponseSerializer serializer)
			throws Exception {
		byte[] content = readContent(request);
		RequestDeserializer deserializer = new JsonRequestDeserializer();
		RpcRequest rpcRequest = deserializer.deserialize(content);

		Object[] params = rpcRequest.getParameters();
		Class<?>[] paramTypes = new Class<?>[params.length];
		for (int i = 0; i < params.length; ++i) {
			paramTypes[i] = params[i].getClass();
		}

		ServiceInfo service = services.get(rpcRequest.getServiceName());
		if (service == null) {
			throw new Exception("Service " + rpcRequest.getServiceName() + " not found");
		}
		Method method = service.iface.getMethod(rpcRequest.getServiceMethod(), paramTypes);
		if (method == null) {
			throw new Exception(
					"Method " + rpcRequest.getServiceName() + "." + rpcRequest.getServiceMethod() + " not found");
		}

		Object result = method.invoke(service.iface.cast(service.impl), params);
		response.getOutputStream().write(serializer.serializeResult(result));
	}
	
	// =========================================================================

	private class ServiceInfo {
		public Class<?> iface;
		public Object impl;

		public ServiceInfo(Class<?> iface, Object impl) {
			this.iface = iface;
			this.impl = impl;
		}
	};

	private Map<String, ServiceInfo> services = new HashMap<String, ServiceInfo>();

}