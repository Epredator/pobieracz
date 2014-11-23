import com.meterware.httpunit.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * User: trojnaradam@gmail.com
 * Date: 22.10.14
 * Time: 18:42
 */

public class Scraper {
  private List<wordsCounter> webDocs = new ArrayList<>(); //this is dictionary of all unique words, sorted alphabetically
  private List<String> totalWords = new ArrayList<String>();
  private static String dictionaryWords;
  private WebResponse response;
  private ArrayList<String> uniqueWordsDictionary = new ArrayList<String>();
  private Map<String, Integer> sortedDictionaryUniqueWords;
  private static int k = 0; //liczba slow uwzgledniona w rankingu
  private static final int THRESH = 0;  //minimalna liczba wystapien


  private List<String> webLinks = new ArrayList<String>();

  Scraper() throws IOException, SAXException {
//    setProxy();
    scrapeDocs();
    makeDictionary();
    countVectors();
  }

  private void countVectors() {

    for (wordsCounter doc : webDocs){
      HashMap<String, Integer> uniqueCounter = doc.uniqueCounter;
//      sortedDictionaryUniqueWords.

    }
  }

  private void scrapeDocs() throws IOException, SAXException {
    webLinks = Arrays.asList(
        "http://pl.wikipedia.org/wiki/Koprofagia",     //zoologia
        "http://pl.wikipedia.org/wiki/Brzuchorz%C4%99ski",
        "http://pl.wikipedia.org/wiki/Pareczniki",
        "http://pl.wikipedia.org/wiki/Stawonogi",
        "http://pl.wikipedia.org/wiki/Borowik_szlachetny",
//
//        "http://pl.wikipedia.org/wiki/Antonio_Vivaldi",  //Muzyka poważna
//        "http://pl.wikipedia.org/wiki/Fryderyk_Chopin",
//        "http://pl.wikipedia.org/wiki/Piotr_Czajkowski",
//        "http://pl.wikipedia.org/wiki/Georg_Friedrich_H%C3%A4ndel",
//        "http://pl.wikipedia.org/wiki/Ludwig_van_Beethoven",
//
//        "http://pl.wikipedia.org/wiki/Linux",          //informatyka
//        "http://pl.wikipedia.org/wiki/GNU",
        "http://pl.wikipedia.org/wiki/Internet",
        "http://pl.wikipedia.org/wiki/Java"
//        "http://pl.wikipedia.org/wiki/Generator_liczb_pseudolosowych"
    );


    for (String link : webLinks) {
      String htmlTextFromDisc = "";
      fetchPage(link);
      savePageToHtml(link);
      htmlTextFromDisc = openHtmlPageFromDisc(link, htmlTextFromDisc);
      String textDoc = convertHtmlPageToText(htmlTextFromDisc);
      savePageToTxt(link, textDoc);

      wordsCounter count = new wordsCounter(textDoc);
      HashMap<String, Integer> docUniqueWords = count.getUniqueCounter();
      uniqueWordsDictionary.addAll(count.getUniqueWords());
      webDocs.add(count);
      webLinks.size();
      uniqueWordsDictionary.size();
    }
    webDocs.size();
  }

  private void makeDictionary() {
    String text = uniqueWordsDictionary.toString();
    wordsCounter count = new wordsCounter(text);
    HashMap<String, Integer> dictionaryUniqueWords = count.getUniqueCounter();
    sortedDictionaryUniqueWords = sortByKeys(dictionaryUniqueWords);
    sortedDictionaryUniqueWords.size();
  }


  /*
    * Paramterized method to sort Map e.g. HashMap or Hashtable in Java
    * throw NullPointerException if Map contains null key
    */
  public static <K extends Comparable,V extends Comparable> Map<K,V> sortByKeys(Map<K,V> map){
    List<K> keys = new LinkedList<K>(map.keySet());
    Collections.sort(keys);

    //LinkedHashMap will keep the keys in the order they are inserted
    //which is currently sorted on natural ordering
    Map<K,V> sortedMap = new LinkedHashMap<K,V>();
    for(K key: keys){
      sortedMap.put(key, map.get(key));
    }

    return sortedMap;
  }



  private void fetchPage(String link) throws IOException, SAXException {
    WebConversation conversation = new WebConversation();
    HttpUnitOptions.setScriptingEnabled(false);
    String url = link;
    System.out.println("visiting " + url);
    WebRequest request = new GetMethodWebRequest(url);
    response = conversation.getResponse(request);
  }

  private void savePageToHtml(String link) throws IOException {
    String htmlPageToSave = response.getText();
    FileWriter fw = new FileWriter("page" + onlyText(link) + ".html");
    fw.write(htmlPageToSave);
    fw.close();
  }

  private String openHtmlPageFromDisc(String link, String htmlTextFromDisc) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader("page" + onlyText(link) + ".html"));
    String sCurrentLine;
    while ((sCurrentLine = br.readLine()) != null)
      htmlTextFromDisc += sCurrentLine;
    return htmlTextFromDisc;
  }

  private String convertHtmlPageToText(String htmlTextFromDisc) {
    Document doc = Jsoup.parse(htmlTextFromDisc.replaceFirst("null", ""));
    String textAll = onlyText(doc.text());
    return removeStopWords(textAll);
  }

  private String removeStopWords(String textAll) {
    List<String> stopWords = new ArrayList<String>();
    stopWords = Arrays.asList("w", "na", "u", "np", "gdy", "i", "z", "się", "nie", "się", "do", "to", "że", "jest", "o", "szukaj", "od", "bo", "po", "edytuj", "a", "ma", "dla");
    for (String word : stopWords) {
      textAll = textAll.replaceAll(" " + word + " ", " ");
    }
    return textAll;
  }

  private String onlyText(String text) {
    return text.replaceAll("[^A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ\\s]*", "").toLowerCase();
  }

  private void savePageToTxt(String link, String textDoc) throws IOException {
    FileWriter fw = new FileWriter("page" + onlyText(link) + ".html");
    fw.write(textDoc);
    fw.close();
  }


  private void countAllSplitedElements(String textDoc) {
    String[] txts;
    txts = textDoc.split(" ");
    totalWords = Arrays.asList(txts);
  }

  public void setProxy() {
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
