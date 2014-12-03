import com.meterware.httpunit.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class zadanie4analizaHiperlaczy {

  private List<wordsDocument> webDocs = new ArrayList<>(); //this is dictionary of all unique words, sorted alphabetically
  private List<String> totalWords = new ArrayList<String>();
  private WebResponse response;
  private ArrayList<String> uniqueWordsDictionary = new ArrayList<String>();
  private Map<String, Integer> sortedDictionaryUniqueWords;
  private int dictionarySize;
  private String dictionaryAllWords = "";
  private List<String> webLinks = new ArrayList<String>();
  private Map<Double, String> intervals = new HashMap<Double, String>();

  zadanie4analizaHiperlaczy() throws IOException, SAXException {
    scrapeDocs();
    makeDictionary();
    powVectors();
    countVectorsModulo();
    countCosinus();
    printWeakestResult();
    printStrongestResult();
  }

  private void printStrongestResult() {
    Map<Double, String> ascSortedMap = new TreeMap<Double, String>();
    ascSortedMap.putAll(intervals);
    System.out.println("10 najbardziej podobnych dokumentów: ");
    int i = 0;
    for (Map.Entry<Double, String> entry : ascSortedMap.entrySet()) {
      i++;
      if (i > intervals.size() - 11)
        System.out.println(10 - (intervals.size() - i) + ". " + "Wartości miary cosinusowej : " + entry.getKey() + " Dla pary dokumentów : "
            + entry.getValue());
    }
  }

  private void printWeakestResult() {
    Map<Double, String> ascSortedMap = new TreeMap<Double, String>();
    ascSortedMap.putAll(intervals);
    System.out.println("10 najmniej podobnych dokumentów: ");
    int i = 0;
    for (Map.Entry<Double, String> entry : ascSortedMap.entrySet()) {
      i++;
      if (i < 11)
        System.out.println(i + ". " + "Wartości miary cosinusowej : " + entry.getKey() + " Dla pary dokumentów : "
            + entry.getValue());
    }
  }


  private void countCosinus() {
    for (wordsDocument doc : webDocs)
      for (wordsDocument otherDoc : webDocs)
        if (doc != otherDoc) {
          Map<Integer, Integer> converslyDocComparer = new HashMap<Integer, Integer>();
          converslyDocComparer.put(otherDoc.docName, doc.docName);
          if (intervals.containsValue(converslyDocComparer) == false) {
            double matrixsMultiplication = multiplicateMatrixes(doc, otherDoc);
            double aaa = (doc.modVector * otherDoc.modVector);
            double cos = matrixsMultiplication / (aaa);
            Map<Integer, Integer> docsComparer = new HashMap<Integer, Integer>();
            docsComparer.put(doc.docName, otherDoc.docName);
            intervals.put(cos, docsComparer.toString());
          }
        }

  }

  private double multiplicateMatrixes(wordsDocument doc, wordsDocument otherDoc) {
    double matrixsMultiplication = 0;
    for (int i = 0; i < doc.vectors.length; i++)
      matrixsMultiplication += doc.vectors[i] * otherDoc.vectors[i];
    return matrixsMultiplication;
  }

  private void powVectors() {
    for (wordsDocument doc : webDocs) {
      HashMap<String, Integer> uniqueCounter = doc.uniqueCounter;
      doc.vectors = new double[dictionarySize];
      int i = 0;
      for (String dictionaryKey : sortedDictionaryUniqueWords.keySet()) {
        for (String uniqueWord : doc.uniqueWords) {
          if (dictionaryKey.equals(uniqueWord)) {
            doc.vectors[i] = (double) uniqueCounter.get(dictionaryKey) / (double) uniqueCounter.size();
            break;
          } else {
            if (doc.vectors[i] == 0)
              doc.vectors[i] = (double) 0;
          }
        }
        i++;
      }
    }
  }

  private void scrapeDocs() throws IOException, SAXException {
    webLinks = Arrays.asList(
        "http://pl.wikipedia.org/wiki/Koprofagia",     //zoologia
        "http://pl.wikipedia.org/wiki/Brzuchorz%C4%99ski",
        "http://pl.wikipedia.org/wiki/Pareczniki",
        "http://pl.wikipedia.org/wiki/Stawonogi",
        "http://pl.wikipedia.org/wiki/Borowik_szlachetny",

        "http://pl.wikipedia.org/wiki/Antonio_Vivaldi",  //Muzyka poważna
        "http://pl.wikipedia.org/wiki/Fryderyk_Chopin",
        "http://pl.wikipedia.org/wiki/Piotr_Czajkowski",
        "http://pl.wikipedia.org/wiki/Georg_Friedrich_H%C3%A4ndel",
        "http://pl.wikipedia.org/wiki/Ludwig_van_Beethoven",

        "http://pl.wikipedia.org/wiki/Linux",          //informatyka
        "http://pl.wikipedia.org/wiki/GNU",
        "http://pl.wikipedia.org/wiki/Internet",
        "http://pl.wikipedia.org/wiki/Java",
        "http://pl.wikipedia.org/wiki/Generator_liczb_pseudolosowych"
    );

    int counter = 0;
    for (String link : webLinks) {
      String htmlTextFromDisc = "";
      fetchPage(link);
      savePageToHtml(link);
      htmlTextFromDisc = openHtmlPageFromDisc(link, htmlTextFromDisc);
      String textDoc = convertHtmlPageToText(htmlTextFromDisc);
      savePageToTxt(link, textDoc);
      wordsDocument count = new wordsDocument(textDoc);
      dictionaryAllWords += textDoc;
      HashMap<String, Integer> docUniqueWords = count.getUniqueCounter();
      count.docName = counter++;
      webDocs.add(count);
      webLinks.size();
      uniqueWordsDictionary.size();
    }
  }

  private void countVectorsModulo() {
    for (wordsDocument doc : webDocs) {
      doc.powVectors = new double[dictionarySize];
      for (int i = 0; i < doc.powVectors.length; i++)
        doc.powVectors[i] = Math.pow(doc.vectors[i], 2);
      double allNumbers = 0;
      for (int i = 0; i < doc.powVectors.length; i++)
        allNumbers += doc.powVectors[i];
      doc.modVector = Math.sqrt(allNumbers);
    }
  }

  private void makeDictionary() {
    String text = dictionaryAllWords.toString();
    wordsDocument count = new wordsDocument(text);
    HashMap<String, Integer> dictionaryUniqueWords = count.getUniqueCounter();
    sortedDictionaryUniqueWords = sortByKeys(dictionaryUniqueWords);
    dictionarySize = sortedDictionaryUniqueWords.size();
  }


  /*
    * Paramterized method to sort Map e.g. HashMap or Hashtable in Java
    * throw NullPointerException if Map contains null key
    */
  public static <K extends Comparable, V extends Comparable> Map<K, V> sortByKeys(Map<K, V> map) {
    List<K> keys = new LinkedList<K>(map.keySet());
    Collections.sort(keys);

    //LinkedHashMap will keep the keys in the order they are inserted
    //which is currently sorted on natural ordering
    Map<K, V> sortedMap = new LinkedHashMap<K, V>();
    for (K key : keys)
      sortedMap.put(key, map.get(key));
    return sortedMap;
  }


  private void fetchPage(String link) throws IOException, SAXException {
    WebConversation conversation = new WebConversation();
    HttpUnitOptions.setScriptingEnabled(false);
    String url = link;
    System.out.println("odwiedzam " + url);
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


}