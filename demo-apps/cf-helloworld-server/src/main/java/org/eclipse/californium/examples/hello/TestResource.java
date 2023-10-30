package org.eclipse.californium.examples.hello;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class TestResource extends CoapResource {

    public TestResource(String name) {
// define the resource name, also the url path
        super(name);

        getAttributes().setTitle(name);

    }

    @Override
    public void handleGET(CoapExchange exchange) {
        System.out.println("get start");
        exchange.respond("i got it");
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        System.out.println("post start");
        String result = exchange.getRequestText();
        System.out.println("the received text:" + result);
        //exchange.respond(ResponseCode.CHANGED);
        exchange.respond(ResponseCode.CHANGED, "good and your param is :"+ result);
    }


}