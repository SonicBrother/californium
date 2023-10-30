package org.eclipse.californium.examples.hello;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.config.TcpConfig;
import org.eclipse.californium.elements.config.UdpConfig;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class Server extends CoapServer {


    static {
        CoapConfig.register();
        UdpConfig.register();
        TcpConfig.register();
    }


    public void myAddEndPoint() throws
            Exception {
// get the NetworkConfig object of californium
        String ip = "0.0.0.0";
        InetAddress addr = InetAddress.getByName(ip);
        //5683 is the default port
        InetSocketAddress bindToAddress = new InetSocketAddress(addr, 84);
// create a new endpoint
        CoapEndpoint.Builder builder = new CoapEndpoint.Builder();
        builder.setInetSocketAddress(bindToAddress);
// add the endpoint to the server
        super.addEndpoint(builder.build());
    }

    public void start() {
        // add the resource to the server, the resource name here is ‘test’ which is corresponding
        // to the uri in the client
        super.add(new TestResource("test"));
        try {
            this.myAddEndPoint();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("sever start");
        super.start();
    }

}