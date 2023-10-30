package org.eclipse.californium.examples.hello;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.*;
import org.eclipse.californium.elements.exception.ConnectorException;

import java.io.IOException;
import java.net.URISyntaxException;

public class ClientApp {


       public static void main(String[] args) throws ConnectorException, IOException, InterruptedException, URISyntaxException {
             
              String body = "{\"id\":\"1\",\"name\":\"tom\"}";
              Client myclient = new Client();
              myclient.init();
              myclient.setBody(body);
              System.out.println("message sent");
              for(int i=0;i<1;i++) {
                     myclient.sendPost();
                     Thread.sleep(100);
              }
              System.out.println("-----------");
              myclient.get();

       }
 
}