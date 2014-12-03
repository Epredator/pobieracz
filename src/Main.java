import org.xml.sax.SAXException;

import java.io.IOException;

public class Main {
  public static void main( String[] params ) throws IOException, SAXException {
    setProxy();

    new zadanie3porownanieDokumentow();
    new zadanie4analizaHiperlaczy();
  }

  private static void setProxy() {
    String proxyHost = "proxy.non.3dart.com";
    String proxyPort = "3128";
//    String proxyHost = "127.0.0.1"; // Burp
//    String proxyPort = "8080";
    /*String proxyHost = "127.0.0.1"; // Charles
    String proxyPort = "8888";*/
    System.setProperty("http.proxyHost", proxyHost);
    System.setProperty("http.proxyPort", proxyPort);
    System.setProperty("https.proxyHost", proxyHost);
    System.setProperty("https.proxyPort", proxyPort);
  }


}


