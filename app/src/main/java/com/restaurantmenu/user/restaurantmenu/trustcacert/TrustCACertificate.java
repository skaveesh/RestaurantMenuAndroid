package com.restaurantmenu.user.restaurantmenu.trustcacert;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by samintha on 2/27/2017.
 */

public class TrustCACertificate {
    private Context applicationContext;
    protected SSLContext sslContext;

    public TrustCACertificate(Context applicationContext) {
        this.applicationContext = applicationContext;
        trustCA();
    }

    private void trustCA() {
        try {
            //this block of code trust server's, self signed certificate on run time
            //otherwise it will throw  javax.net.ssl.SSLHandshakeException: java.security.cert.CertPathValidatorException:
            // Trust anchor for certification path not found.
            //this needs because we send post request to aws server and it needs HTTPS
            //without HTTPS, the connection will not be secure
            //I have to manually provide certificate(.crt) file in order to validate on run time
            //I Have to pass Context from LoginActivity to read the certificate file through AssetManager

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            AssetManager am = applicationContext.getAssets();
            InputStream caInput = new BufferedInputStream(am.open("certificate_restaurantmenu.crt"));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
            } finally {
                try {
                    caInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
