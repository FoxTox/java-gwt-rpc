package com.foxtox.rpc.core.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.foxtox.rpc.core.client.JsonRequestDeserializer;
import com.foxtox.rpc.core.client.JsonResponseSerializer;
import com.foxtox.rpc.core.client.RequestDeserializer;
import com.foxtox.rpc.core.client.ResponseSerializer;
import com.foxtox.rpc.core.client.SumService;

@SuppressWarnings("serial")
@WebServlet("/")
public class RemoteServiceServlet extends HttpServlet implements SumService {
	// TODO: Remove SumService example implementation.

	private static final int BUFFER_SIZE = 4096;

	public RemoteServiceServlet() {
		implementedInterfaces = new HashMap<String, Class<?>>();
		for (Class<?> cls : getClass().getInterfaces()) {
			implementedInterfaces.put(cls.getCanonicalName(), cls);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		ResponseSerializer serializer = new JsonResponseSerializer(response.getOutputStream());
		try {
			processPost(request, response, serializer);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			serializer.setError(sw.getBuffer().toString());
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter writer = response.getWriter();
		writer.println("<h1>Java-GWT RPC servlet</h1>");

		Class<?>[] ifaces = this.getClass().getInterfaces();
		if (ifaces.length > 0) {
			writer.println("<h2>Implemented interfaces:</h2>\n<ul>");
			for (Class<?> iface : ifaces)
				writer.println("<li>" + iface.getSimpleName() + "</li>");
			writer.println("</ul>");
		} else {
			writer.println("<h2>Implemented interfaces:</h2>none");
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
		ByteArrayInputStream contentInput = new ByteArrayInputStream(content);
		RequestDeserializer deserializer = new JsonRequestDeserializer(contentInput);

		Object[] params = deserializer.getParameters();
		Class<?>[] paramTypes = new Class<?>[params.length];
		for (int i = 0; i < params.length; ++i) {
			paramTypes[i] = params[i].getClass();
		}

		Class<?> iface = implementedInterfaces.get(deserializer.getServiceName());
		if (iface == null) {
			throw new Exception("Interface " + deserializer.getServiceName() + " not found");
		}
		Method method = iface.getMethod(deserializer.getServiceMethod(), paramTypes);
		if (method == null) {
			throw new Exception(
					"Method " + deserializer.getServiceName() + "." + deserializer.getServiceMethod() + " not found");
		}

		Object result = method.invoke(iface.cast(this), params);
		serializer.setResult(result);
	}

	public Integer getSum(Integer first, Integer second) {
		return first + second;
	}

	public Double getSum(Double first, Double second) {
		return first + second;
	}

	private Map<String, Class<?>> implementedInterfaces;

}