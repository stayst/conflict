import java.util.HashMap;

public class Trie {

    private TrieNode root;

    public Trie() {
        root = new TrieNode((char) 0);
    }

    public void insert(String word) {
        TrieNode node = root;
        for (int i = 0; i < word.length(); i++) {
            HashMap<Character, TrieNode> child = node.getChildren();
            char ch = word.charAt(i);
            if (child.containsKey(ch))
                node = child.get(ch);
            else
            {
                TrieNode temp = new TrieNode(ch);
                child.put(ch, temp);
                node = temp;
            }
        }
        node.setIsEnd(true);
    }

    public boolean exists(String word){
        TrieNode node = root;
        for (int i = 0; i < word.length(); i++){
            if (root.getChildren().containsKey(word.charAt(i))){
                node = node.getChildren().get(word.charAt(i));
            }
            else return false;
        }
        if (node.isEnd()) return true;
        return false;
    }

    public TrieNode getRoot(){
        return root;
    }
}

class TrieNode {
    private char value;
    private HashMap<Character, TrieNode> children;
    private boolean bIsEnd;

    public TrieNode(char ch) {
        value = ch;
        children = new HashMap<>();
        bIsEnd = false;
    }

    public HashMap<Character, TrieNode> getChildren() {
        return children;
    }

    public char getValue() {
        return value;
    }

    public void setIsEnd(boolean val) {
        bIsEnd = val;
    }

    public boolean isEnd() {
        return bIsEnd;
    }

    public boolean isLeaf(){
        if (children == null) return true;
        return false;
    }
}

