import com.meterware.httpunit.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class zadanie4analizaHiperlaczy {

  private static final String BASE_URL = "http://kontomierz.pl/";
  private WebResponse response;
  private List<String> webLinks = new ArrayList<String>();
  private final static int IMMERSION = 2;

  zadanie4analizaHiperlaczy() throws IOException, SAXException {
    List<String> links = new ArrayList<String>();
    webLinks = Arrays.asList(
        BASE_URL
    );

    for (int i = 0; i<4; i++) {
      webLinks = scrapeDoc(webLinks);
      scrapeDoc(webLinks);
    }
  }

  private List<String> scrapeDoc(List<String> webLinks) throws IOException, SAXException {
    List<String> temporaryLinks = new ArrayList<String>();
    List<String> hrefs = new ArrayList<String>();
    for(String link : webLinks) {
      fetchPage(link);
//      List<String> hrefs = new ArrayList<String>();
      hrefs = getWebLinks(hrefs);

      saveLinksOnDisc(link, hrefs);
    }
    return hrefs;
  }

  private void fetchPage(String link) throws IOException, SAXException {
    WebConversation conversation = new WebConversation();
    HttpUnitOptions.setScriptingEnabled(false);
    String url = link;
    System.out.println("odwiedzam " + url);
    WebRequest request = new GetMethodWebRequest(url);
    response = conversation.getResponse(request);
  }

  private List<String>  getWebLinks(List<String> hrefs) throws IOException {
    Document doc = Jsoup.parse(response.getText());
    Elements elems = doc.select("a");
    for (Element elm : elems){
      if (!hrefs.contains(elm)) {
        String href = elm.attr("href");
        if (!(href.contains("http")))
          hrefs.add(BASE_URL + href);
        else
          hrefs.add(href);
//          System.out.println(BASE_URL + href);
      }
    }
    return hrefs;
  }

  private void saveLinksOnDisc(String link, List<String> hrefs) throws IOException {
    String htmlPageToSave = hrefs.toString();
    FileWriter fw = new FileWriter("page" + onlyText("plik") + ".txt");
    fw.write(htmlPageToSave);
    fw.close();
  }

  private String onlyText(String text) {
    return text.replaceAll("[^A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ\\s]*", "").toLowerCase();
  }



}