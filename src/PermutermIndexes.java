import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class PermutermIndexes {

    public TreeMap<String, TreeSet<Integer>> invertedIndexes;
    public Trie permutermIndexes;
    private ArrayList<String> words;
    private int count = 0;

    PermutermIndexes(){
        invertedIndexes = new TreeMap<>();
        permutermIndexes = new Trie();
    }

    PermutermIndexes(String filename){
        invertedIndexes = new TreeMap<>();
        permutermIndexes = new Trie();
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
            addPermuterm(word);
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

    private void addPermuterm(String word){
        int length = word.length()+1;
        word = word + "$" + word;
        for (int i = 0; i < length; ++i){
            String sub = word.substring(i, i+length);
            permutermIndexes.insert(sub);
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

    public ArrayList<String> jokerQuery(String word){
        words = new ArrayList<>();
        int length = word.length();
        int firstJ = word.indexOf('*');
        int lastJ = word.lastIndexOf('*');
        word = word+"$"+word;
        if (firstJ==lastJ){
            String forSearch = word.substring(firstJ+1, firstJ+length+1);
            System.out.println(forSearch);
            sink(findNode(forSearch), forSearch.substring(0, forSearch.length()-1));
        }
        else{
            String forSearch = word.substring(lastJ+1, (length+firstJ+1));
            sink(findNode(forSearch), forSearch.substring(0, forSearch.length()-1));
            System.out.println(forSearch);
            String toCheck = word.substring(firstJ+1, lastJ);
            ArrayList<String> temp = new ArrayList<>();
            for (int i = 0; i < words.size(); ++i){
                if (words.get(i).contains(toCheck)){
                    temp.add(words.get(i));
                }
            }
            words = temp;
        }
        ArrayList<String> res = new ArrayList<>();
        for (int i = 0; i < words.size(); ++i){
            String w = words.get(i);
            String doubleWord = w+w;
            res.add(doubleWord.substring(w.indexOf('$')+1, w.indexOf('$')+w.length()));
        }
        System.out.println(res);
        return res;
    }

    private TrieNode findNode(String word){
        TrieNode node = permutermIndexes.getRoot();
        for(int i = 0; i < word.length(); ++i){
            if(!node.getChildren().containsKey(word.charAt(i))){
                return null;
            }
            node = node.getChildren().get(word.charAt(i));
        }
        return node;
    }

    private void sink(TrieNode node, String word){
        word += node.getValue();
        if (node.isEnd()){
            words.add(word);
        }
        if (node.isLeaf()) return;
        HashMap<Character, TrieNode> subtrie = node.getChildren();
        for (TrieNode child :subtrie.values()) {
            sink(child, word);
        }
    }

    public TreeSet<Integer> search(String word){
        ArrayList<String> arr = jokerQuery(word);
        TreeSet<Integer> set = new TreeSet<>();
        for (int i = 0; i < arr.size(); ++i){
            set.addAll(invertedIndexes.get(arr.get(i)));
        }
        return set;
    }

    public static void main(String[] args) {
        PermutermIndexes dict = new PermutermIndexes();
        try {
            File folder = new File("docs\\");
            File[] listOfFiles = folder.listFiles();
            for (File file: listOfFiles) {
                String name = file.getName();
                if (name.substring(name.length()-4).equals(".txt")) dict.readFile("docs\\"+name);
            }
        } catch (IOException e) {
            System.out.println("Incorrect query");
            e.printStackTrace();
        }
        System.out.println(dict.search("c*l*tor"));
    }

}
