package cc.landingzone.dreamweb.sso;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import io.jsonwebtoken.lang.Assert;
import org.opensaml.xml.security.x509.BasicX509Credential;

/**
 * CertManager
 *
 * @author charles
 * @date 2020-09-29
 */
public class CertManager {

    public static BasicX509Credential credential;

    public static void initSigningCredential() throws Exception {

        URL publicKeyURL = CertManager.class.getResource(SSOConstants.PUBLIC_KEY_PATH);
        URL privateKeyURL = CertManager.class.getResource(SSOConstants.PRIVATE_KEY_PATH);
        Assert.notNull(publicKeyURL, "can not find publicKey:" + SSOConstants.PUBLIC_KEY_PATH);
        Assert.notNull(privateKeyURL, "can not find privateKey:" + SSOConstants.PRIVATE_KEY_PATH);

        String publicKeyLocation = publicKeyURL.getFile();
        String privateKeyLocation = privateKeyURL.getFile();

        InputStream inStream = new FileInputStream(publicKeyLocation);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate publicKey = (X509Certificate) cf.generateCertificate(inStream);
        inStream.close();

        // create private key
        RandomAccessFile raf = new RandomAccessFile(privateKeyLocation, "r");
        byte[] buf = new byte[(int) raf.length()];
        raf.readFully(buf);
        raf.close();

        PKCS8EncodedKeySpec kspec = new PKCS8EncodedKeySpec(buf);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(kspec);

        // create credential and initialize
        BasicX509Credential basicX509Credential = new BasicX509Credential();
        basicX509Credential.setEntityCertificate(publicKey);
        basicX509Credential.setPrivateKey(privateKey);

        credential = basicX509Credential;
    }

    public static BasicX509Credential getCredential() {
        return credential;
    }

}
