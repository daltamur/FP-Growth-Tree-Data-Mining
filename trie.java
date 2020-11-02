import java.util.ArrayList;
import java.util.Hashtable;

public class trie {
private trieNode root;
private Hashtable<String, ArrayList<String>> paths;
private ArrayList<ArrayList<trieNode>> frequentItems;

//all these functions act like the opening functions you'd find in a linked list, the real work happens at the trieNode class.

public trie(){
    root=new trieNode(null);
    paths=new Hashtable<>();
    frequentItems=new ArrayList<>();
}

public void insert(String[] insertedList){
    ArrayList<trieNode>nodes=new ArrayList<>();
    root.insert(insertedList, paths,nodes);
}

public Hashtable<String, ArrayList<String>> getHashTable(){
    return paths;
}

public void print(){
    root.print();
}


    public void constructConditionalPatternBase(){
        root.constructConditionalPatternBase("", paths);
    }

    public void printPatternBase(){
    root.printPatternBases();
    }

    public void conditionalInsert(String[] list){
    root.conditionalInsert(list);

    }

    public ArrayList<ArrayList<trieNode>> getFrequentPaths(){
    ArrayList<trieNode>nodes=new ArrayList<>();
    frequentItems=root.getFrequentPaths(frequentItems,nodes);
    return frequentItems;
    }

    public ArrayList<trieNode> getUniquePaths(ArrayList<trieNode> path){
        return root.getUniquePaths(root,path,0,true);
    }


}
