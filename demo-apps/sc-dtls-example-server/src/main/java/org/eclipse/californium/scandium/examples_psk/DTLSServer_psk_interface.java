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
 *    Bosch Software Innovations GmbH - migrate to SLF4J
 ******************************************************************************/
package org.eclipse.californium.scandium.examples_psk;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.Connector;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.elements.RawDataChannel;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedMultiPskStore;
import org.eclipse.californium.scandium.dtls.x509.SingleCertificateProvider;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;


/**
 * 增加了自定义接口
 */
public class DTLSServer_psk_interface {

    private static final int DEFAULT_PORT = 83;
    private static final Logger LOG = LoggerFactory
            .getLogger(DTLSServer_psk_interface.class.getName());
    private static final char[] KEY_STORE_PASSWORD = "endPass".toCharArray();
    private static final String KEY_STORE_LOCATION = "certs/keyStore.jks";
    private static final char[] TRUST_STORE_PASSWORD = "rootPass".toCharArray();
    private static final String TRUST_STORE_LOCATION = "certs/trustStore.jks";

    static {
        DtlsConfig.register();
    }

    /**
     * Special configuration defaults handler.
     */
    private static final DefinitionsProvider DEFAULTS = new DefinitionsProvider() {

        @Override
        public void applyDefinitions(Configuration config) {
            config.set(DtlsConfig.DTLS_CONNECTION_ID_LENGTH, 6);
            config.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, false);
        }

    };


    private static class RawDataChannelImpl implements RawDataChannel {

        private Connector connector;

        public RawDataChannelImpl(Connector con) {
            this.connector = con;
        }

        @Override
        public void receiveData(final RawData raw) {
            System.out.println("RawDataChannelImpl.receiveData");
            if (LOG.isInfoEnabled()) {
                LOG.info("Received request: {}", new String(raw.getBytes()));
            }
            RawData response = RawData.outbound("ACK".getBytes(),
                    raw.getEndpointContext(), null, false);
            connector.send(response);
        }
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        AdvancedMultiPskStore pskStore = new AdvancedMultiPskStore();
        // put in the PSK store the default identity/psk for tinydtls tests
        pskStore.setKey("Client_identity", "secretPSK".getBytes());
        pskStore.setKey("Client_identity2", "secretPSK2".getBytes());
        DTLSConnector dtlsConnector;
        // load the key store
        SslContextUtil.Credentials serverCredentials = SslContextUtil.loadCredentials(
                SslContextUtil.CLASSPATH_SCHEME + KEY_STORE_LOCATION, "server", KEY_STORE_PASSWORD,
                KEY_STORE_PASSWORD);
        Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(
                SslContextUtil.CLASSPATH_SCHEME + TRUST_STORE_LOCATION, "root", TRUST_STORE_PASSWORD);

        Configuration configuration = Configuration.createWithFile(Configuration.DEFAULT_FILE, "DTLS example server", DEFAULTS);
        DtlsConnectorConfig.Builder builder = DtlsConnectorConfig.builder(configuration)
                .setAddress(new InetSocketAddress(DEFAULT_PORT))
                .setAdvancedPskStore(pskStore)
                .setCertificateIdentityProvider(
                        new SingleCertificateProvider(serverCredentials.getPrivateKey(), serverCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509))
                .setAdvancedCertificateVerifier(StaticNewAdvancedCertificateVerifier.builder()
                        .setTrustedCertificates(trustedCertificates).setTrustAllRPKs().build());

        dtlsConnector = new DTLSConnector(builder.build());

        // todo 这个的作用是什么
//        dtlsConnector
//                .setRawDataReceiver(new RawDataChannelImpl(dtlsConnector));


        // 创建 CoapEndpoint 实例
        CoapEndpoint coapEndpoint = new CoapEndpoint.Builder().setConnector(dtlsConnector).build();
// 创建 CoapServer 实例
        CoapServer coapServer = new CoapServer();

// 添加 CoapEndpoint
        coapServer.addEndpoint(coapEndpoint);

        coapServer.add(new MyResource("myresource"));
        coapServer.start();

        try {
            for (; ; ) {
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
        }
    }
}
