import java.io.*;
import java.util.*;

public class BiwordIndex{
    public TreeMap<String, TreeSet<Integer>> invertedIndexes;
    public TreeMap<String, TreeMap<Integer, ArrayList<Integer>>> positionalPosting;
    public TreeMap<Integer, String> docs;

    public int count = 0;

    BiwordIndex(String filename){
        invertedIndexes = new TreeMap<>();
        positionalPosting = new TreeMap<>();
        docs = new TreeMap<>();
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

    public void addBiword(String words){
        if (!invertedIndexes.containsKey(words)){
            TreeSet<Integer> arr = new TreeSet<>();
            arr.add(count);
            invertedIndexes.put(words, arr);
            return;
        }
        TreeSet<Integer> arr = invertedIndexes.get(words);
        if (!arr.contains(count)){
            arr.add(count);
        }
        return;
    }

    public void addPositions(String word, int position){
        if (!positionalPosting.containsKey(word)){
            TreeMap<Integer, ArrayList<Integer>> map = new TreeMap<>();
            ArrayList<Integer> arr = new ArrayList<>();
            arr.add(position);
            map.put(count, arr);
            positionalPosting.put(word, map);
            return;
        }
        TreeMap<Integer, ArrayList<Integer>> arr = positionalPosting.get(word);
        if (!arr.containsKey(count)){
            ArrayList<Integer> newArr = new ArrayList<>();
            newArr.add(position);
            arr.put(count, newArr);
            return;
        }
        arr.get(count).add(position);
        return;
    }


    public void readFile(String filename) throws IOException {
        int position = 0;
        BufferedReader bf = new BufferedReader(new FileReader(filename));
        ++count;
        docs.put(count, filename);
        String line = bf.readLine();
        String token = "";
        String previous = "";
        if (line != null){
            String[] words = line.split("\\W{2,}|^'|:|\\.+|,|!+|\\?+|(\\W?\\s+\\W?)|(\\d-.)|(\\d)|(\"-?)");
            previous = words[0].toLowerCase();
            if (!previous.equals("")) addPositions(previous.toLowerCase(), ++position);
            for (int i = 1; i < words.length; ++i){
                if (!words[i].equals("")){
                        addPositions(words[i].toLowerCase(), ++position);
                        if (!previous.equals("")) {
                            token = previous + " " + words[i].toLowerCase();
                            addBiword(token);
                        }
                    previous = words[i].toLowerCase();
                }
            }
        }
        while ((line = bf.readLine()) != null){
            String[] words = line.split("\\W{2,}|^'|:|\\.+|,|!+|\\?+|(\\W?\\s+\\W?)|(\\d-.)|(\\d)|(\"-?)");
            for (int i = 0; i < words.length; ++i){
                if (!words[i].equals("")){
                    addPositions(words[i].toLowerCase(), ++position);
                    token = previous + " " + words[i].toLowerCase();
                    addBiword(token);
                    previous = words[i].toLowerCase();
                }
            }
        }
        bf.close();
    }

    public ArrayList<Integer> positionSearch(String query) throws Exception{
        String[]q = query.split(" ");
        ArrayList<String> words = new ArrayList<>();
        ArrayList<Integer> dist = new ArrayList<>();
        if (q[0].charAt(0) == '\\') throw new Exception("Incorrect query!");
        for (int i = 0; i < q.length; ++i){
            String w = q[i];
            if (w.charAt(0) == '\\'){
                String d = w.substring(1);
                if (!d.matches("\\d+")) throw new Exception("Incorrect query!");
                dist.add(Integer.parseInt(d));
                ++i;
                words.add(q[i]);
                continue;
            }
            words.add(w);
            dist.add(0);
        }
        Set<Integer> docs = commonDocs(words);
        Iterator<Integer> it = docs.iterator();
        ArrayList<Integer> res = new ArrayList<>();
        while (it.hasNext()){
            int currDoc = it.next();
            if (isPresent(words, dist, currDoc)) res.add(currDoc);
        }
        return res;
    }

    private boolean isPresent(ArrayList<String> words, ArrayList<Integer> dist, int doc){
        int[] counter = new int[words.size()];
        boolean present = false;
        ArrayList<Integer>[] positions = new ArrayList[words.size()];
        for (int i = 0; i < counter.length; ++i){
            counter[i] = 0;
            positions[i] = positionalPosting.get(words.get(i)).get(doc);
        }
        while (true){
            present = true;
            for(int i = 0; i < words.size()-1; ++i){
                if (dist.get(i+1) == 0){
                    if (positions[i].get(counter[i]) + 1 != positions[i+1].get(counter[i+1])){
                        present = false;
                        break;
                    }
                }
                else if (Math.abs(positions[i].get(counter[i]) - positions[i+1].get(counter[i+1])) > dist.get(i+1)){
                    present = false;
                    break;
                }
            }
            if (present) break;
            int min = 0;
            for (int i = 1; i < words.size(); ++i){
                if (positions[i].get(counter[i]) < positions[min].get(counter[min])) min = i;
            }
            counter[min]++;
            if (counter[min] == positions[min].size()) break;
        }
        return present;
    }

    private Set<Integer> commonDocs(ArrayList<String> words){
        Set<Integer> arr = (Set<Integer>) positionalPosting.get(words.get(0)).keySet();
        for (int i = 1; i < words.size(); ++i) {
            arr.retainAll(positionalPosting.get(words.get(i)).keySet());
        }
        //System.out.println(arr);
        return arr;
    }

    public TreeSet<Integer> phraseSearch (String str){
        String[] words = str.split(" ");
        String token = words[0] + " " + words[1];
        TreeSet<Integer> res = invertedIndexes.get(token);
        for (int i = 1; i < words.length - 1; ++i){
            token = words[i] + " " + words[i+1];
            res.retainAll(invertedIndexes.get(token));
        }
        return res;
    }
}
