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
import java.net.UnknownHostException;
import java.util.*;

public class zadanie4analizaHiperlaczy {

  private static final String BASE_URL = "pl.wikipedia.org";
  private WebResponse response;
  private List<String> webLinks = new ArrayList<String>();
  private final static int IMMERSION = 2;
  private List<String> otherHrefs = new ArrayList<String>();
  List<String> hrefs = new ArrayList<String>();
  String linksFromDisc = "";

  zadanie4analizaHiperlaczy() throws IOException, SAXException {
    webLinks = Arrays.asList(
        BASE_URL
    );

    webLinks = scrapeDoc(webLinks);
    for (int i = 0; i<2; i++) {
      linksFromDisc = openLinksFromDisc("wewnetrzne", linksFromDisc);
      String[] links = linksFromDisc.split(", ");
      webLinks = Arrays.asList(links);
      scrapeDoc(webLinks);
    }
  }

  private List<String> scrapeDoc(List<String> webLinks) throws IOException, SAXException {
    for(String link : webLinks) {
      fetchPage(link);
      getWebLinks(hrefs);

      saveLinksOnDisc("wewnetrzne linki", hrefs);
      saveLinksOnDisc("zewnetrzne linki", otherHrefs);
    }
    return hrefs;
  }

  private void fetchPage(String link) throws IOException, SAXException {
    WebConversation conversation = new WebConversation();
    HttpUnitOptions.setScriptingEnabled(false);
    String url = "http://" + link + "/";
        try {
          System.out.println("odwiedzam " + url);
          WebRequest request = new GetMethodWebRequest(url);
          response = conversation.getResponse(request);
        }catch (Exception e) {
          System.out.println("strona " + url + " nie odpowiada");

        }
  }

  private List<String>  getWebLinks(List<String> hrefs) throws IOException {
    Document doc = Jsoup.parse(response.getText());
    Elements elems = doc.select("a");
    for (Element elm : elems){
      String href = elm.attr("href");
      checkLink(href );

      if (!hrefs.contains(href) || !hrefs.contains(BASE_URL + href)) {


        if (href.contains("http") ) {
            if (!otherHrefs.contains(href)) {
              otherHrefs.add(href);
            }
          }
        else
            if (!hrefs.contains(BASE_URL + href))
          hrefs.add(BASE_URL + href);
      }
    }
    return hrefs;
  }

  private void checkLink(String href) throws UnknownHostException {
    InetAddress address = null;
    if (href.contains("http") ) {
      address = InetAddress.getByName(href);
    } else {
      address = InetAddress.getByName(BASE_URL+ "/" + href );
    }
    System.out.println(address);



  }

  private String openLinksFromDisc(String name, String htmlTextFromDisc) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader("page" + onlyText(name) + ".txt"));
    String sCurrentLine;
    while ((sCurrentLine = br.readLine()) != null)
      htmlTextFromDisc += sCurrentLine;
    return htmlTextFromDisc;
  }

  private void saveLinksOnDisc(String name, List<String> hrefs) throws IOException {
    String htmlPageToSave = hrefs.toString().replaceAll("\\[" , " ").replaceAll("]" , "");
    FileWriter fw = new FileWriter("page" + onlyText(name) + ".txt");
    fw.write(htmlPageToSave);
    fw.close();
  }

  private String onlyText(String text) {
    return text.replaceAll("[^A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ\\s]*", "").toLowerCase();
  }



}