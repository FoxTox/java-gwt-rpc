package com.foxtox.rpc.rebind;

import java.io.PrintWriter;

import com.foxtox.rpc.client.AsyncCallback;
import com.foxtox.rpc.common.SerializableType;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class RemoteServiceProxyGenerator extends Generator {
  
  private TreeLogger logger;
  private GeneratorContext context;
  private SourceWriter sourceWriter;

  private JClassType serviceInterface;

  @Override
  public String generate(TreeLogger logger, GeneratorContext context, String requestedClass)
      throws UnableToCompleteException {
    this.logger = logger;
    this.context = context;

    TypeOracle typeOracle = context.getTypeOracle();
    serviceInterface = typeOracle.findType(requestedClass);
    if (serviceInterface == null)
      logErrorAndThrow("Couldn't find type: " + requestedClass);

    String asyncServiceQualifiedName = serviceInterface.getQualifiedSourceName() + "Async";
    String implQualifiedName = asyncServiceQualifiedName + "Impl";

    JClassType asyncInterface = typeOracle.findType(asyncServiceQualifiedName);
    if (asyncInterface != null)
      asyncInterface = asyncInterface.isInterface();
    if (asyncInterface == null)
      logErrorAndThrow("Couldn't find Async interface: " + asyncServiceQualifiedName);

    initSourceWriter(asyncInterface);
    if (sourceWriter == null) {
      logger.log(TreeLogger.Type.INFO, "Failed to create SourceWriter. Skipping.");
      return implQualifiedName;
    }

    printConstructor();

    JMethod[] methods = asyncInterface.getMethods();
    for (JMethod method : methods)
      printAsyncMethod(method);

    sourceWriter.commit(logger);
    logger.log(TreeLogger.Type.INFO, "Committed RPC proxy for " + asyncServiceQualifiedName);
    return implQualifiedName;
  }

  private void printConstructor() {
    sourceWriter.println();
    sourceWriter.println("public %s() {", serviceInterface.getSimpleSourceName() + "AsyncImpl");
    sourceWriter.println("}");
  }

  private void printMethodParameter(JParameter param) {
    sourceWriter.print("%s %s", param.getType().getParameterizedQualifiedSourceName(), param.getName());
  }

  private void printAsyncMethod(JMethod method) throws UnableToCompleteException {
    JParameter[] params = method.getParameters();
    if (params.length == 0)
      logErrorAndThrow("Method should have at least 1 parameter: " + method.getName());

    JParameter callbackParam = params[params.length - 1];
    JParameterizedType callbackType = callbackParam.getType().isParameterized();
    if (callbackType == null || callbackType.getTypeArgs().length != 1)
      logErrorAndThrow("Last method's parameter should have 1 type parameter: " + method.getName());

    JClassType asyncCallbackType = null;
    try {
      asyncCallbackType = context.getTypeOracle().getType(AsyncCallback.class.getCanonicalName());
    } catch (NotFoundException exc) {
      logErrorAndThrow("AsyncCallback class not found.");
    }
    if (callbackType.getErasedType().getQualifiedSourceName() != asyncCallbackType.getQualifiedSourceName())
      logErrorAndThrow("Last method's parameter should be AsyncCallback: " + method.getName());

    JClassType resultType = callbackType.getTypeArgs()[0];
    SerializableType serializableResultType = null;
    try {
      serializableResultType = SerializableType.getFrom(Class.forName(resultType.getQualifiedSourceName()));
    } catch (ClassNotFoundException exc) {
      logErrorAndThrow("Couldn't find java Class for result type: " + resultType.getQualifiedSourceName());
    }
    if (serializableResultType == SerializableType.UNSUPPORTED)
      logErrorAndThrow("Unsupported SerializableType: " + resultType.getQualifiedSourceName());

    if (!method.getReturnType().getQualifiedSourceName().equals("void"))
      logErrorAndThrow("Async service method should return void: " + method.getName());

    sourceWriter.println();
    sourceWriter.println("@Override");
    sourceWriter.print("public void %s(", method.getName());
    if (params.length > 0) {
      printMethodParameter(params[0]);
      for (int index = 1; index < params.length; ++index) {
        sourceWriter.print(", ");
        printMethodParameter(params[index]);
      }
    }
    sourceWriter.println(") {");
    sourceWriter.indent();
    {
      // TODO: Replace "/sum" by a path extracted from annotations.
      sourceWriter.println("RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, \"/sum\");");
      sourceWriter.println("RequestSerializer serializer = new JsonRequestSerializer();");
      sourceWriter.println("serializer.setServiceName(\"" + serviceInterface.getQualifiedSourceName() + "\");");
      sourceWriter.println("serializer.setServiceMethod(\"" + method.getName() + "\");");
      // Iterate through all parameters except the last one, which is
      // AsyncCallback.
      for (int index = 0; index + 1 < params.length; ++index) {
        sourceWriter.println("serializer.addParameter(" + params[index].getName() + ");");
      }
      sourceWriter.println("String requestData = new String(serializer.serialize());");
      sourceWriter.println("builder.setRequestData(requestData);");

      sourceWriter.println();
      sourceWriter.println("builder.setCallback(new RequestCallback() {");
      sourceWriter.indent();
      {
        sourceWriter.println("public void onResponseReceived(Request request, Response response) {");
        sourceWriter.indent();
        {
          sourceWriter.println("ResponseDeserializer deserializer = new JsonResponseDeserializer();");
          sourceWriter.println("RpcResponse rpcResponse = deserializer.deserialize(SerializableType."
              + serializableResultType.toString() + ", response.getText().getBytes());");

          sourceWriter.println("if (rpcResponse.getType() == RpcResponse.Type.SUCCESS)");
          sourceWriter
              .indentln("callback.onSuccess((" + resultType.getQualifiedSourceName() + ") rpcResponse.getResult());");
          sourceWriter.println("else if (rpcResponse.getType() == RpcResponse.Type.ERROR)");
          sourceWriter.indentln("callback.onFailure(rpcResponse.getError());");
          sourceWriter.println("else");
          sourceWriter.indentln("callback.onFailure(\"Received wrong response type: \" + rpcResponse.getType());");
        }
        sourceWriter.outdent();
        sourceWriter.println("}\n");

        sourceWriter.println("public void onError(Request request, Throwable throwable) {");
        sourceWriter.indentln("callback.onFailure(\"RPC Error: \" + throwable);");
        sourceWriter.println("}");
      }
      sourceWriter.outdent();
      sourceWriter.println("});");

      sourceWriter.println();
      sourceWriter.println("try {");
      sourceWriter.indentln("builder.send();");
      sourceWriter.println("} catch (RequestException e) {");
      sourceWriter.indentln("// Ignore.");
      sourceWriter.println("}");
    }
    sourceWriter.outdent();
    sourceWriter.println("}\n");
  }

  private void initSourceWriter(JClassType iface) {
    String packageName = iface.getPackage().getName();
    String serviceName = iface.getSimpleSourceName();
    String implName = serviceName + "Impl";

    PrintWriter printWriter = context.tryCreate(logger, packageName, implName);
    if (printWriter == null)
      return;

    ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName,
        implName);

    String[] imports = new String[] {
        com.foxtox.rpc.client.gwt.JsonResponseDeserializer.class.getCanonicalName(),
        com.foxtox.rpc.client.gwt.JsonRequestSerializer.class.getCanonicalName(),
        com.foxtox.rpc.common.ResponseDeserializer.class.getCanonicalName(),
        com.foxtox.rpc.common.RequestSerializer.class.getCanonicalName(),
        com.foxtox.rpc.common.RpcResponse.class.getCanonicalName(),
        com.foxtox.rpc.common.SerializableType.class.getCanonicalName(),

        com.google.gwt.http.client.Request.class.getCanonicalName(),
        com.google.gwt.http.client.RequestBuilder.class.getCanonicalName(),
        com.google.gwt.http.client.RequestCallback.class.getCanonicalName(),
        com.google.gwt.http.client.RequestException.class.getCanonicalName(),
        com.google.gwt.http.client.Response.class.getCanonicalName(),
        com.google.gwt.user.client.Window.class.getCanonicalName(), };

    for (String imp : imports) {
      composerFactory.addImport(imp);
    }
    composerFactory.addImplementedInterface(iface.getQualifiedSourceName());
    sourceWriter = composerFactory.createSourceWriter(context, printWriter);
  }

  private void logErrorAndThrow(String errorMessage) throws UnableToCompleteException {
    logger.log(TreeLogger.Type.ERROR, errorMessage);
    throw new UnableToCompleteException();
  }

}
