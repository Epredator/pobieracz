import com.meterware.httpunit.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class zadanie4analizaHiperlaczy {

  private static String base_url = "http://p.duch.pl";
  private String baseIP;
  private WebResponse response;
  private List<String> webLinks = new ArrayList<String>();
  private final static int IMMERSION = 2;
  private List<String> temporaryExternalHrefs = new ArrayList<String>();
  private List<String> temporaryInternalHrefs = new ArrayList<String>();
  private String linksFromDisc = "";
  private Integer numberOfInternalLinks = null;

  zadanie4analizaHiperlaczy() throws IOException, SAXException {
    baseIP = checkStartLink();
    if (baseIP != null) {
      checkWebLinks();
    } else
      System.out.println("Strona: " + base_url + " jest niedostępna, więc nie można ustalić numeru IP ");
    System.out.println("Pomyślnie zakończono pobieranie " + numberOfInternalLinks + " stron wewnętrznych dla nazwy domeny DNS: " + base_url);
  }

  private String checkStartLink() throws MalformedURLException, UnknownHostException {
    webLinks = Arrays.asList(base_url);
    return checkDNS(base_url);
  }

  private void checkWebLinks() throws IOException, SAXException {
    webLinks = scrapeDoc(webLinks);
    for (int i = 1; i < IMMERSION; i++) {
      System.out.println("Iteracja numer: " + (i + 1));
      linksFromDisc = openLinksFromDisc("wewnetrzne linki", linksFromDisc);
      String[] links = linksFromDisc.split(", ");
      webLinks = Arrays.asList(links);
      scrapeDoc(webLinks);
    }
    numberOfInternalLinks = temporaryInternalHrefs.size();
  }

  private List<String> scrapeDoc(List<String> webLinks) throws IOException, SAXException {
    for (String link : webLinks) {
      fetchPage(link);
      getWebLinks();
      saveLinksOnDisc("wewnetrzne linki", temporaryInternalHrefs);
      saveLinksOnDisc("zewnetrzne linki", temporaryExternalHrefs);
    }
    return temporaryInternalHrefs;
  }

  private void fetchPage(String link) throws IOException, SAXException {
    WebConversation conversation = new WebConversation();
    HttpUnitOptions.setScriptingEnabled(false);
    try {
      System.out.println("pobieram zawartość strony: " + link);
      WebRequest request = new GetMethodWebRequest(link);
      response = conversation.getResponse(request);
    } catch (Exception e) {
      System.out.println("strona " + link + " nie odpowiada");
    }
  }

  private List<String> getWebLinks() throws IOException {
    URL url = response.getURL();
    String new_base_url = "http://" + url.getHost().toString() + "/";
    if (!base_url.contains(new_base_url) || temporaryInternalHrefs.size() == 0) {
      base_url = new_base_url;
    }
    Document doc = Jsoup.parse(response.getText());
    Elements elems = doc.select("a");
    for (Element elm : elems) {
      String href = elm.attr("href");
      checkLink(href);
    }
    return temporaryInternalHrefs;
  }

  private void checkLink(String href) throws UnknownHostException, MalformedURLException {
    if (href.length() > 1)
      if (!href.contains("@")) {
        if (!href.contains("http://")) {
          href = base_url + href;
        }
        String ipToCheck = checkDNS(href);
        if (ipToCheck != null)
          if (baseIP.contains(ipToCheck)) {
            if (!temporaryInternalHrefs.contains(href)) {
              addItemToHrefs(href);
              System.out.println("Dodano " + href + " do linków wewnętrznych");
            }
          } else {
            if (!temporaryExternalHrefs.contains(href)) {
              temporaryExternalHrefs.add(href);
              System.out.println("Dodano " + href + " do linków zewnętrznych");
            }
          }
      }

  }

  private String checkDNS(String href) throws MalformedURLException, UnknownHostException {
    String res = null;
    URL oaiBaseURL = null;
    try {
      oaiBaseURL = new URL(href);
      String mainHost = oaiBaseURL.getHost();
      res = InetAddress.getByName(mainHost).getHostAddress();
    } catch (IOException e) {
      System.out.println("Nie można otworzyc strony: " + oaiBaseURL + ". Typ błędu: " + e);
    }
    return res;
  }

  private void addItemToHrefs(String href) {
    if (href.contains("https")) {
      String properHref = href.replace("https://" + base_url, "");
      if (!(temporaryInternalHrefs.contains(properHref)))
        temporaryInternalHrefs.add((properHref));
    } else if (href.contains("#")) {
      String properHref = href.replace("#", "/#");
      if (!(temporaryInternalHrefs.contains(properHref)))
        temporaryInternalHrefs.add((properHref));
    } else if (!(temporaryInternalHrefs.contains(href)))
      temporaryInternalHrefs.add(href);
  }

  private String openLinksFromDisc(String name, String htmlTextFromDisc) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader("page" + onlyText(name) + ".txt"));
    String sCurrentLine;
    htmlTextFromDisc = "";
    while ((sCurrentLine = br.readLine()) != null)
      htmlTextFromDisc += sCurrentLine;
    return htmlTextFromDisc;
  }

  private void saveLinksOnDisc(String name, List<String> hrefs) throws IOException {
    String htmlPageToSave = hrefs.toString().replaceAll("\\[", " ").replaceAll("]", "");
    FileWriter fw = new FileWriter("page" + onlyText(name) + ".txt");
    fw.write(htmlPageToSave);
    fw.close();
  }

  private String onlyText(String text) {
    return text.replaceAll("[^A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ\\s]*", "").toLowerCase();
  }

}