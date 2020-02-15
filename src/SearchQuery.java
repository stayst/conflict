import javax.swing.text.html.HTMLDocument;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;

public class SearchQuery {

    BiwordIndex bi;
    PermutermIndexes pi;
    KGram kg;

    SearchQuery(String filename){
        bi = new BiwordIndex(filename);
        pi = new PermutermIndexes(filename);
        kg = new KGram(filename);
    }

    public TreeSet<String> search (String query){
        String polish = toPolish(query);
        TreeSet<Integer> indexes = evaluate(polish);
        TreeSet<String> res = new TreeSet<>();
        Iterator<Integer> it = indexes.iterator();
        while(it.hasNext()){
            int doc = it.next();
            res.add(bi.docs.get(doc));
        }
        return res;
    }


    private String toPolish(String query){
        String[] q = query.split("\\s|,|\\.|:");
        String res = "";
        Stack<String> st = new Stack<>();
        for (int i = 0; i < q.length; ++i){
            if(!q[i].matches("[&|()\\\\]")) {
                while (q.length > i && !q[i].matches("[&|()\\\\]")) {
                    res += q[i] + " ";
                    ++i;
                }
                --i;
                res +=",";
            }
            else{
                switch(q[i].charAt(0)){
                    case '(':
                        st.push(q[i]);
                        break;
                    case ')':
                        while (st.peek().charAt(0)!='(') {
                            res += st.peek() + ",";
                            st.pop();
                        }
                        st.pop();
                        break;
                    case '\\':
                        while (!st.empty() && st.peek().charAt(0)=='&') {
                            res += st.peek() + ",";
                            st.pop();
                        }
                        st.push(q[i]);
                        break;
                    case '|':
                        while (!st.empty() && st.peek().charAt(0)=='&') {
                            res += st.peek() + ",";
                            st.pop();
                        }
                        st.push(q[i]);
                        break;
                    case '&':
                        st.push(q[i]);
                        break;
                }
            }
        }
        while (!st.empty()) {
            res += st.peek() + ",";
            st.pop();
        }
        return res;
    }


    private TreeSet<Integer> evaluate (String query){
        Stack <TreeSet> st = new Stack<>();
        TreeSet<Integer> res = new TreeSet<>();
        String[] q = query.split(",");
        for (int i = 0; i < q.length; ++i){
            res = new TreeSet<>();
            switch(q[i].charAt(0)){
                case '|':
                    or(res, st.peek());
                    st.pop();
                    or(res, st.peek());
                    st.pop();
                    st.push(res);
                    break;
                case '\\':
                    or(res, st.peek());
                    st.pop();
                    TreeSet<Integer> temp = new TreeSet<>();
                    if(st.empty()){
                        for (int j = 1; j <= bi.count; ++j){
                            temp.add(j);
                        }
                    }
                    else{
                        temp = st.peek();
                        st.pop();
                    }
                    not(temp, res);
                    res = temp;
                    st.push(res);
                    break;
                case '&':
                    or(res, st.peek());
                    st.pop();
                    and(res, st.peek());
                    st.pop();
                    st.push(res);
                    break;
                default:
                    String w = q[i].trim();
                    if (!w.contains(" ")){
                        if(w.contains("*")){
                            TreeSet<Integer> token = pi.search(w);
                            st.push(token);
                        }
                        else {
                            TreeSet<Integer> token = new TreeSet<>();
                            if (bi.positionalPosting.containsKey(w)) {
                                for (Integer entry : bi.positionalPosting.get(w).keySet()) {
                                    token.add(entry);
                                }
                            }
                            st.push(token);
                        }
                    }
                    else st.push(bi.phraseSearch(w));
            }
        }
        TreeSet<Integer> r = st.peek();
        st.pop();
        return r;
    }


    private void not (TreeSet<Integer> a, TreeSet<Integer> b){
        a.removeAll(b);
    }

    private void and (TreeSet<Integer> a, TreeSet<Integer> b){
        a.retainAll(b);
    }

    private void or (TreeSet<Integer> a, TreeSet<Integer> b){
        if(a == null) a = b;
        else if (b == null) b = a;
        else a.addAll(b);
    }

}
