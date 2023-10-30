
package org.eclipse.californium.scandium.examples_psk;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.pskstore.MultiPskFileStore;
import org.eclipse.californium.scandium.dtls.x509.SingleCertificateProvider;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;
import org.eclipse.californium.scandium.util.SecretUtil;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;

/**
 * 增加了自定义接口
 */
public class DTLSServer_multi_psk_file_interface {

    private static final int DEFAULT_PORT = 88;

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


    public static void main(String[] args) throws GeneralSecurityException, IOException {

        // 多键值对的文件的预置psk证书
        // 这个类可以动态的增删psk证书
        MultiPskFileStore multiPskFileStore = new MultiPskFileStore();
        SecretKey psk = SecretUtil.create("secretPSK".getBytes(), "PSK");
        multiPskFileStore.addKey("aa", psk);

        // load the key store
        SslContextUtil.Credentials serverCredentials = SslContextUtil.loadCredentials(
                SslContextUtil.CLASSPATH_SCHEME + KEY_STORE_LOCATION, "server", KEY_STORE_PASSWORD,
                KEY_STORE_PASSWORD);
        Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(
                SslContextUtil.CLASSPATH_SCHEME + TRUST_STORE_LOCATION, "root", TRUST_STORE_PASSWORD);

        Configuration configuration = Configuration.createWithFile(Configuration.DEFAULT_FILE, "DTLS example server", DEFAULTS);
        DtlsConnectorConfig.Builder builder = DtlsConnectorConfig.builder(configuration)
                .setAddress(new InetSocketAddress(DEFAULT_PORT))
                .setAdvancedPskStore(multiPskFileStore)
                .setCertificateIdentityProvider(
                        new SingleCertificateProvider(serverCredentials.getPrivateKey(), serverCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509))
                .setAdvancedCertificateVerifier(StaticNewAdvancedCertificateVerifier.builder()
                        .setTrustedCertificates(trustedCertificates).setTrustAllRPKs().build());

        DTLSConnector dtlsConnector = new DTLSConnector(builder.build());

        // 创建 CoapEndpoint 实例
        CoapEndpoint coapEndpoint = new CoapEndpoint.Builder().setConnector(dtlsConnector).build();
// 创建 CoapServer 实例
        CoapServer coapServer = new CoapServer();

// 添加 CoapEndpoint
        coapServer.addEndpoint(coapEndpoint);

        coapServer.add(new MyResource("myresource"));
        coapServer.start();

        multiPskFileStore.addKey("bb", psk);
        try {
            int i = 0;
            for (; ; ) {
                Thread.sleep(5000);
                i++;
                if (i > 3) {
                    multiPskFileStore.removeKey("bb");
                    System.out.println("remove bbbb");
                }
            }
        } catch (InterruptedException e) {
        }
    }
}
