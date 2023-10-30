/*******************************************************************************
 * Copyright (c) 2015, 2017 Institute for Pervasive Computing, ETH Zurich and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 *
 * Contributors:
 *    Matthias Kovatsch - creator and main architect
 *    Stefan Jucker - DTLS implementation
 *    Achim Kraus (Bosch Software Innovations GmbH) - add support for multiple clients
 *                                                    exchange multiple messages
 *    Achim Kraus (Bosch Software Innovations GmbH) - add client statistics
 *    Bosch Software Innovations GmbH - migrate to SLF4J
 *    Achim Kraus (Bosch Software Innovations GmbH) - add argument for payload length
 ******************************************************************************/
package org.eclipse.californium.scandium.examples_psk;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedSinglePskStore;

import java.nio.charset.StandardCharsets;


public class DTLSClient_psk {


    static {
        DtlsConfig.register();
    }

    /**
     * Special configuration defaults handler.
     */
    private static final DefinitionsProvider DEFAULTS = config -> {
        config.set(DtlsConfig.DTLS_CONNECTION_ID_LENGTH, 0);
        config.set(DtlsConfig.DTLS_RECEIVER_THREAD_COUNT, 2);
        config.set(DtlsConfig.DTLS_CONNECTOR_THREAD_COUNT, 2);
    };


    public static void main(String[] args) throws Exception {
        Configuration configuration = Configuration.createWithFile(Configuration.DEFAULT_FILE, "DTLS example client", DEFAULTS);

        DtlsConnectorConfig.Builder builder = DtlsConnectorConfig.builder(configuration);
        builder.setAdvancedPskStore(new AdvancedSinglePskStore("Client_identity", "secretPSK".getBytes()));
        DTLSConnector dtlsConnector = new DTLSConnector(builder.build());

        // 创建 CoapEndpoint 实例
        CoapEndpoint coapEndpoint = new CoapEndpoint.Builder()
                .setConnector(dtlsConnector).build();

        CoapClient coapClient = new CoapClient();
//		coapClient.setURI("coaps://49.232.111.163:86/myresource");
        coapClient.setURI("coaps://127.0.0.1:83/myresource1");
        coapClient.setEndpoint(coapEndpoint);

        // 发送 GET 请求
        CoapResponse response = coapClient.get();
        System.out.println(response.getResponseText());

        CoapResponse post = coapClient.post("{\"name\":\"aa\"}".getBytes(StandardCharsets.UTF_8), 50);
        System.out.println(post.getResponseText());
    }
}
