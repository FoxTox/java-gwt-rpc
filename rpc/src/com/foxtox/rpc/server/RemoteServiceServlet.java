package com.foxtox.rpc.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RemoteServiceServlet extends HttpServlet {

	private static final int BUFFER_SIZE = 4096;

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

	protected void processPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		byte[] content = readContent(request);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			processPost(request, response);
		} catch (Exception e) {
		}
	}

}