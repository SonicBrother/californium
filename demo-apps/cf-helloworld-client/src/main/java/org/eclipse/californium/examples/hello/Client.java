package org.eclipse.californium.examples.hello;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
 
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.elements.config.UdpConfig;
import org.eclipse.californium.elements.exception.ConnectorException;
 
 
 
public class Client {

       static {
              CoapConfig.register();
              UdpConfig.register();
       }
       URI uri = null;
       CoapClient coapClient = null;
       String body = "";
      
       public void init() throws URISyntaxException{
        // define the ip and resource name here, ‘test’ is the resource name
              uri = new URI("coap://127.0.0.1:5666/test");
              coapClient = new CoapClient(uri);
             
             
       }
       // send with post
       public void sendPost() throws ConnectorException, IOException {
              // the first arg is the request body
              // the second arg is the format id of the body, 0-text|50-json
              CoapResponse response = coapClient.post(this.body.getBytes(), 50);
              System.out.println("the respnse code is "+response.getCode());
              System.out.println("the respnse body is "+response.getResponseText());
       }

       public void get() throws ConnectorException, IOException {
              // the first arg is the request body
              // the second arg is the format id of the body, 0-text|50-json
              CoapResponse response = coapClient.get();
              System.out.println("the respnse code is "+response.getCode());
              System.out.println("the respnse body is "+response.getResponseText());
       }
      
       public void setBody(String body) {
              this.body = body;
       }
}