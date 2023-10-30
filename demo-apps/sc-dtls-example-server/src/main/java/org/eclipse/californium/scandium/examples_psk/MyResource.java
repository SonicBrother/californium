package org.eclipse.californium.scandium.examples_psk;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * 自定义Coap服务的资源
 */
public class MyResource extends CoapResource {
    // 构造函数和其他方法...

    // name 标识该资源的名称
    public MyResource(String name) {
        super(name);
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        // 处理 GET 请求
        // 从请求中获取参数、执行业务逻辑等

        // 创建响应
        Response response = new Response(CoAP.ResponseCode.CONTENT);
        response.setPayload("Hello, CoAP!");
        System.out.println("MyResource.handleGET");
        System.out.println("============================");
        // 发送响应
        exchange.respond(response);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        Response response = new Response(CoAP.ResponseCode.CONTENT);
        byte[] requestPayload = exchange.getRequestPayload();
        response.setPayload("Hello, CoAP  post 请求!" + new String(requestPayload));
        System.out.println("MyResource.handlePOST");
        System.out.println("============================");

        // 发送响应
        exchange.respond(response);
    }
}
