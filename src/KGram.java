import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class KGram {

    public TreeMap<String, TreeSet<Integer>> invertedIndexes;
    public TreeMap<String, TreeSet<String>> threeGramIndexes;
    private ArrayList<String> words;
    private int count = 0;

    KGram(){
        invertedIndexes = new TreeMap<>();
        threeGramIndexes = new TreeMap<>();
    }

    KGram(String filename){
        invertedIndexes = new TreeMap<>();
        threeGramIndexes = new TreeMap<>();
        try {
            File folder = new File(filename+"\\");
            File[] listOfFiles = folder.listFiles();
            for (File file: listOfFiles) {
                String name = file.getName();
                if (name.substring(name.length()-4).equals(".txt")) readFile(filename+"\\"+name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void add(String word){
        if (!invertedIndexes.containsKey(word)){
            addThreeGram(word);
            TreeSet<Integer> set = new TreeSet<>();
            set.add(count);
            invertedIndexes.put(word, set);
            return;
        }
        else{
            TreeSet<Integer> docs = invertedIndexes.get(word);
            if (!docs.contains(count)) docs.add(count);
            return;
        }
    }

    private void addThreeGram(String word){
        int length = word.length()+1;
        word = "$" + word + "$";
        for (int i = 0; i+2 <= length; ++i){
            String sub = word.substring(i, i+3);
            if (threeGramIndexes.containsKey(sub)){
                threeGramIndexes.get(sub).add(word);
            }
            else{
                TreeSet<String> newset = new TreeSet<>();
                newset.add(word);
                threeGramIndexes.put(sub, newset);
            }
        }
        return;
    }

    public void readFile(String filename) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(filename));
        count++;
        String line = "";
        while ((line = bf.readLine()) != null){
            String[] words = line.split("\\W{2,}|^'|:|\\.+|,|!+|\\?+|(\\W?\\s+\\W?)|(\\d-.)|(\\d)|(\"-?)");
            for (int i = 0; i < words.length; ++i){
                if (!words[i].equals(""))add(words[i].toLowerCase());
            }
        }
        bf.close();
    }

    public TreeSet<String> search(String word){
        if (word.charAt(0)!='*') word = "$" + word;
        if (word.charAt(word.length()-1)!='*') word += "$";
        String[] q = word.split("\\*");
        TreeSet<String> res = new TreeSet<>();
        for(int i = 0; i < q.length; ++i){
            if(q[i].length() >= 3){
                res = threeGramIndexes.get(q[i].substring(0, 3));
                break;
            }
        }
        for(int i = 0; i < q.length; ++i){
            String part = q[i];
            for (int j = 0; j+3 <= part.length(); ++j){
                res.retainAll(threeGramIndexes.get(part.substring(j, j+3)));
            }
        }
        return postFilter(res, word);
    }

    private TreeSet<String> postFilter(TreeSet<String> set, String word){
        TreeSet<String> res = new TreeSet<>();
        String[] q = word.split("\\*");
        Iterator<String> it = set.iterator();
        while (it.hasNext()){
            boolean isPresent = true;
            String toCheck = it.next();
            for (int i = 0; i < q.length; ++i){
                if(!toCheck.contains(q[i])){
                    isPresent = false;
                    break;
                }
            }
            if (isPresent) res.add(toCheck.substring(1, toCheck.length()-1));
        }
        return res;
    }


    public static void main(String[] args) {
        KGram dict = new KGram();
        try {
            File folder = new File("docs\\");
            File[] listOfFiles = folder.listFiles();
            for (File file: listOfFiles) {
                String name = file.getName();
                if (name.substring(name.length()-4).equals(".txt")) dict.readFile("docs\\"+name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(dict.search("col*"));
    }


}
