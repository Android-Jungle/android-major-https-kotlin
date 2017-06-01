package com.jungle.majorhttps.kotlin.network

import android.content.Context
import android.support.annotation.RawRes
import java.io.*
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

class HttpsUtils {

    companion object {

        open class DomainHostnameVerifier(domain: Array<String>) : HostnameVerifier {

            /**
             * Verifier domain name, such as `biz.main.com`.
             */
            protected var mVerifyDomain = domain


            override fun verify(hostname: String?, session: SSLSession?): Boolean {
                val verifier = HttpsURLConnection.getDefaultHostnameVerifier()
                return mVerifyDomain.any { verifier.verify(it, session) }
            }
        }

        class DefaultHostnameVerifier : HostnameVerifier {
            override fun verify(hostname: String?, session: SSLSession?) = true
        }

        /**
         * @param crtText Certificate file content.
         *                Can use `keytool -printcert -rfc -file uwca.crt` command to print it.
         */
        fun createCertificateByCrtText(crtText: String): X509Certificate?
                = createCertificateByStream(ByteArrayInputStream(crtText.toByteArray()))

        fun createCertificateByCrtFile(fileName: String): X509Certificate? {
            var stream: InputStream? = null
            try {
                stream = FileInputStream(fileName)
                return createCertificateByStream(stream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                try {
                    stream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            return null
        }

        fun createCertificateByCrtAsset(context: Context, fileName: String): X509Certificate? {
            try {
                return createCertificateByStream(context.assets.open(fileName))
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        fun createCertificateByRawResource(context: Context, @RawRes resId: Int): X509Certificate?
                = createCertificateByStream(context.resources.openRawResource(resId))

        fun createCertificateByStream(stream: InputStream): X509Certificate?
                = createCertificateByStream(stream, "X509") as X509Certificate

        fun createCertificateByStream(stream: InputStream, type: String): Certificate? {
            try {
                val factory = CertificateFactory.getInstance(type)
                return factory.generateCertificate(BufferedInputStream(stream))
            } catch (e: CertificateException) {
                e.printStackTrace()
            }

            return null
        }


        fun createTrustManagerByCerts(certs: Array<Certificate>): Array<TrustManager>? {
            try {
                val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                keyStore.load(null, null)

                for (i in certs.indices) {
                    keyStore.setCertificateEntry("ca_$i", certs[i])
                }

                val factory = TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm())
                factory.init(keyStore)

                return factory.trustManagers
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        fun getSSLContext(trustManagers: Array<TrustManager>): SSLContext? {
            try {
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, trustManagers, null)
                return sslContext
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }
    }
}