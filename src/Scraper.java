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

  private WebResponse response;
  private String htmlTextFromDisc;
  private String onlyText;
  private static int k = 10;
  private static final int THRESH = 4;
  String[] naiveTxts;
  String[] naiveTextRanking;
  private Integer[] naiveNumberOfOccurences;
  Map<String, Integer> naiveTextMap = new HashMap();
  long startTime;
  long endTime;

  Scraper() throws IOException, SAXException {
    fetchPage();
    savePageToHtml();
    openHtmlPageFromDisc();
    convertHtmlPageToText();
    savePageToTxt();
    sortElementsNaive();
    sortElementsUpggradedVersion();
  }

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

  private void sortElementsNaive() {
    startTime = System.nanoTime();
    countAllSplitedElements();
    makeRanking();
  }

  private void sortElementsUpggradedVersion() {
    startTime = System.nanoTime();
    countAllSplitedElementsUpgradedVersion();
    makeRanking();
  }

  private void countAllSplitedElements() {
    naiveTxts = onlyText.split(" ");
    naiveTextRanking = new String[naiveTxts.length];
    naiveNumberOfOccurences = new Integer[naiveTxts.length];
    for (int i = 1; i < naiveTxts.length; i++)
      if (!naiveTxts[i].isEmpty())
        for (int j = 1; j < naiveTextRanking.length; j++) {
          if (naiveTextRanking[j] != null) {
            if (naiveTextRanking[j].equals(naiveTxts[i])) {
              naiveTextRanking[j] = naiveTxts[i];
              naiveNumberOfOccurences[j] = naiveNumberOfOccurences[j].intValue() + 1;
              break;
            }
          } else {
            naiveTextRanking[j] = naiveTxts[i];
            naiveNumberOfOccurences[j] = 1;
            break;
          }
        }
  }



  private void countAllSplitedElementsUpgradedVersion() {
    naiveTxts = onlyText.split(" ");
    naiveTextRanking = new String[(int) Math.sqrt(naiveTxts.length)];
    naiveNumberOfOccurences = new Integer[(int) Math.sqrt(naiveTxts.length)];
    for (int i = 1; i < naiveTxts.length; i++)
      if (!naiveTxts[i].isEmpty())
        for (int j = 1; j < naiveTextRanking.length; j++) {
          if (naiveTextRanking[j] != null) {
            if (naiveTextRanking[j].equals(naiveTxts[i])) {
              naiveTextRanking[j] = naiveTxts[i];
              naiveNumberOfOccurences[j] = naiveNumberOfOccurences[j].intValue() + 1;
              break;
            }
          } else {
            naiveTextRanking[j] = naiveTxts[i];
            naiveNumberOfOccurences[j] = 1;
            break;
          }
        }
  }

  private void makeRanking() {
   addSuitableElementsToMap();
   printValues();
  }


  private void addSuitableElementsToMap() {
    for (int i = 1; i < naiveNumberOfOccurences.length; i++)
      if (naiveNumberOfOccurences[i] != null)
        if (naiveNumberOfOccurences[i] > THRESH)
          naiveTextMap.put(naiveTextRanking[i], naiveNumberOfOccurences[i]);
  }

  private void printValues() {
    Map<String, Integer> map = sortByValues(naiveTextMap);
    System.out.println("After sorting:");
    Set set = map.entrySet();
    Iterator iterator = set.iterator();
    while (iterator.hasNext() ) {
      Map.Entry me = (Map.Entry) iterator.next();
      System.out.print(me.getKey() + ": ");
      System.out.print(me.getValue() + "\n");
    }
    endTime = System.nanoTime();
    System.out.println("Czas działania algorytmu: " + ( endTime - startTime) + " nanosekund");


  }

  private Map<String, Integer> sortByValues(Map<String, Integer> map) {
    List list = new LinkedList(map.entrySet());
    Collections.sort(list, new Comparator() {
      public int compare(Object firstElem, Object secondElem) {
        return ((Comparable) ((Map.Entry) (secondElem)).getValue())
            .compareTo(((Map.Entry) (firstElem)).getValue());
      }
    });

    HashMap sortedHashMap = new LinkedHashMap();
    int i = 0;
    for (Iterator it = list.iterator(); it.hasNext(); ) {
      Map.Entry entry = (Map.Entry) it.next();
      if (i<k) {
        sortedHashMap.put(entry.getKey(), entry.getValue());
        i++;
      }
    }
    return sortedHashMap;
  }


}