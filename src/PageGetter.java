import com.meterware.httpunit.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * User: trojnaradam@gmail.com
 * Date: 26.10.14
 * Time: 15:08
 */
public class PageGetter {
  private WebResponse response;
  private String htmlTextFromDisc;
  private String onlyText;

  private void fetchPage() throws IOException, SAXException {
    WebConversation conversation = new WebConversation();
    HttpUnitOptions.setScriptingEnabled(false);
    String url = "http://docs.oracle.com/javase/tutorial/index.html";
    System.out.println("visiting " + url);
    WebRequest request = new GetMethodWebRequest(url);
    response = conversation.getResponse(request);
  }

  private void savePageToHtml() throws IOException {
    String htmlPageToSave = response.getText();
    FileWriter fw = new FileWriter("page.html");
    fw.write(htmlPageToSave);
    fw.close();
  }

  private void openHtmlPageFromDisc() throws IOException {
    BufferedReader br = new BufferedReader(new FileReader("page.html"));
    String sCurrentLine;
    while ((sCurrentLine = br.readLine()) != null)
      htmlTextFromDisc += sCurrentLine;
  }

  private void convertHtmlPageToText() {
    Document doc = Jsoup.parse(htmlTextFromDisc.replaceFirst("null", ""));
    onlyText = doc.text().replaceAll("[^A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ\\s]*", "").toLowerCase();
  }

  private void savePageToTxt() throws IOException {
    FileWriter fw = new FileWriter("page.txt");
    fw.write(onlyText);
    fw.close();
  }

}
