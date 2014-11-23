import java.util.*;
public class wordsCounter {
  ArrayList<String> totalWords = new ArrayList<String>();
  ArrayList<String> uniqueWords = new ArrayList<String>();
  HashMap<String, Integer> uniqueCounter = new HashMap<String, Integer>();
  public wordsCounter(String phrase){
        String[] temp = phrase.replaceAll("," , "").split(" ");
        for (String current : temp) {
          current = current.replaceAll("[\n,.?!:\";'']", "");
          if(current.contains("'")&&!(current.substring(0,1).equals("'"))){
            current = current.replace("'", "");
          }
          totalWords.add(current);
        }
        while(totalWords.contains("")){
          totalWords.remove("");
        }

  }
  public void setUniqueWords(){
    for(String x: totalWords){
      if(!uniqueWords.contains(x.toLowerCase())){
        uniqueWords.add(x.toLowerCase());
      }
    }
    while(uniqueWords.contains("-")){
      uniqueWords.remove("-");
    }
  }
  public ArrayList<String> getTotalWords(){
    return totalWords;
  }
  public ArrayList<String> getUniqueWords(){
    setUniqueWords();
    return uniqueWords;
  }

  public HashMap<String, Integer> getUniqueCounter(){
    setUniqueWords();
    countUnique();
    return uniqueCounter;
  }


  public void countUnique(){
    for(String x: uniqueWords){
      uniqueCounter.put(x, 0);
    }
    for(String x: totalWords){
      String check = x.toLowerCase();
      if(!uniqueCounter.containsKey(check)){
        uniqueCounter.put(check, 1);
      } else{
        int currentCount = uniqueCounter.get(check);
        uniqueCounter.put(check, currentCount + 1);
      }
    }
  }


  public void analyzeText(){
    setUniqueWords();
    countUnique();
  }

}