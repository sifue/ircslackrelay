package org.soichiro.ircslackrelay

import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext

import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Provide IgnoreTLSContext
 */
object IgnoreTLSContextProvider {

  def getIgnoreSSLContext: SSLContext = {
    val tm: Array[TrustManager]  = Array (new X509TrustManager(){
      override def checkClientTrusted(p1: Array[X509Certificate], p2: String): Unit = Unit
      override def getAcceptedIssuers: Array[X509Certificate] = null
      override def checkServerTrusted(p1: Array[X509Certificate], p2: String): Unit = Unit
    })
    val sslContext = SSLContext.getInstance("TLSv1.2");
    sslContext.init(null, tm, null);
    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier {
      override def verify(p1: String, p2: SSLSession): Boolean = true
    })
    sslContext
  }
}