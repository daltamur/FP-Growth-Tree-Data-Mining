import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class trieNode implements minSupport {
    private Hashtable<String, trieNode> children;
    private int count;
    private String product;
    private trieNode parent;
    private String conditionalPatternBase;
    private boolean traversed;

    public trieNode(String product){
        this.product=product;
        count=0;
        children=new Hashtable<>();
        parent=null;
        conditionalPatternBase=null;
        traversed=false;
    }

    public int getCount(){return count;}

    public trieNode getParent(){
        return parent;
    }

    public String getProduct(){
        return product;
    }

    public void insert(String[] list, Hashtable<String, ArrayList<String>> paths, ArrayList<trieNode> nodes) {
        //we always start at the root, so the process is similar throughout
        trieNode currentNode = this;
        trieNode nextNode;
        for (int i = 0; i <list.length; i++) {
           nextNode=currentNode.children.get(list[i]);
           //if the child never existed, make it.
           if(nextNode==null){
               nextNode=new trieNode(list[i]);
               nextNode.count++;
               nextNode.parent=currentNode;
               currentNode.children.put(list[i],nextNode);
               currentNode=nextNode;
           }else{
               //if it did exist, just increase the count and insert on the next node's child
               nextNode.count++;
               nextNode.parent=currentNode;
               currentNode.children.replace(list[i],nextNode);
               currentNode=nextNode;
           }
        }
    }

    public void print(){

        //this was used for testing purposes, just printed the base paths before I went further
        if(product!=null&&parent.getProduct()==null){
            System.out.println(product+":"+count+"   START");
            Enumeration enu=children.keys();
            while(enu.hasMoreElements()){
                trieNode child=children.get(enu.nextElement().toString());
                child.print();
            }
        }else if(product!=null){
            //System.out.println(product+":"+count);
            Enumeration enu=children.keys();
            while(enu.hasMoreElements()){
                trieNode child=children.get(enu.nextElement().toString());
                child.print();
            }
        }
        else{
            Enumeration enu=children.keys();
            while(enu.hasMoreElements()){
                trieNode child=children.get(enu.nextElement().toString());
                child.print();
            }
        }
    }


    public Hashtable<String, ArrayList<String>> constructConditionalPatternBase(String patternBase, Hashtable<String, ArrayList<String>> paths){
        if(this.product==null){//dealing with the root
            Enumeration enu=this.children.keys();
            //get the paths of the rest of the things, the null root does not count.
            while(enu.hasMoreElements()){
                String key=enu.nextElement().toString();
                this.children.get(key).constructConditionalPatternBase(" ",paths);
            }
        }else if(this.parent.product==null){
            //dealing with a root child, start the current patternBase and keep going. Don't add it to the list because it is only one product, no pattern
            patternBase=product;
            Enumeration enu=this.children.keys();
            while(enu.hasMoreElements()){
                String key=enu.nextElement().toString();
                this.children.get(key).constructConditionalPatternBase(patternBase,paths);
            }
        }
        else{
            //put every path you come across in the list now. So long as the current node has children, keep doing it
            ArrayList<String>pathList=paths.get(this.product);
            if(pathList==null){
                pathList=new ArrayList<>();
                pathList.add(patternBase+","+count);
                paths.put(this.product,pathList);
            }else{
                pathList.add(patternBase+","+count);
                paths.replace(this.product,pathList);
            }
            conditionalPatternBase=patternBase+","+count;
            patternBase=patternBase+","+this.product;
            Enumeration enu=this.children.keys();
            while(enu.hasMoreElements()){
                String key=enu.nextElement().toString();
                this.children.get(key).constructConditionalPatternBase(patternBase,paths);
            }
        }
        return paths;
    }

    public void printPatternBases(){
        //more tests, just allowed me to make sure the appropriate paths were being made
        if(this.product!=null&&this.parent.product!=null){
            System.out.println(product+"'s pattern base: "+conditionalPatternBase);
            Enumeration enu=this.children.keys();
            while(enu.hasMoreElements()){
                String key=enu.nextElement().toString();
                this.children.get(key).printPatternBases();
            }
        }else {
            Enumeration enu = this.children.keys();
            while (enu.hasMoreElements()) {
                String key = enu.nextElement().toString();
                this.children.get(key).printPatternBases();
            }
        }
    }

    public void conditionalInsert(String[] list){
        //making the conditional FP tree when making the frequent patterns
        trieNode currentNode = this;
        trieNode nextNode;
        for (int i = 0; i <list.length-1; i++) {
            //same deal as regular insert except now the count is increased by the given number, not incrementally
            nextNode=currentNode.children.get(list[i]);
            if(nextNode==null){
                nextNode=new trieNode(list[i]);
                nextNode.count+=Integer.parseInt(list[list.length-1]);
                nextNode.parent=currentNode;
                currentNode.children.put(list[i],nextNode);
                currentNode=nextNode;
            }else{
                nextNode.count+=Integer.parseInt(list[list.length-1]);
                nextNode.parent=currentNode;
                currentNode.children.replace(list[i],nextNode);
                currentNode=nextNode;
            }
        }

    }

    public Hashtable<String, trieNode> getChildren(){
        return children;
    }

    public ArrayList<ArrayList<trieNode>> getFrequentPaths(ArrayList<ArrayList<trieNode>> frequentItems,ArrayList<trieNode> nodes){
        if(this.product==null){//dealing w/root
            Enumeration enu=this.children.keys();
            //do nothing at the root, start at the children of the root
            while(enu.hasMoreElements()){
                String key=enu.nextElement().toString();
                this.children.get(key).getFrequentPaths(frequentItems,nodes);
            }
        }else if(this.parent.product==null){
            //add the current node to the frequent path list, perform function on children
            Enumeration enu=this.children.keys();
            if(this.count>=minSupport) {
                nodes = new ArrayList<>();
                nodes.add(this);
                while (enu.hasMoreElements()) {
                    nodes = new ArrayList<>();
                    nodes.add(this);
                    String key = enu.nextElement().toString();
                    this.children.get(key).getFrequentPaths(frequentItems, nodes);
                }
            }
        }else if(this.children.size()==0){//at a leaf
            //add the path to the frequent path list only if it reaches the minsup
            if(this.getParent().count>=minSupport) {
                nodes.add(this);
                frequentItems.add(nodes);
            }
        }else{
            Enumeration enu=this.children.keys();
            if(count>=minSupport) {
                ArrayList<trieNode> nextPath=new ArrayList<>();
                for(int i=0;i<nodes.size();i++) {
                    nextPath.add(nodes.get(i));
                }
                nextPath.add(this);
                while (enu.hasMoreElements()) {
                    String key = enu.nextElement().toString();
                    this.children.get(key).getFrequentPaths(frequentItems,nextPath);
                }
            }
        }
        return frequentItems;
    }

    public ArrayList<trieNode> getUniquePaths(trieNode current,ArrayList<trieNode>path, int i,boolean unique){
        //there was some case where the node was null. I think it was from the frequent paths I put in the frequency list so I just added this condition so it worked
        if(current==null){

        }
        else{
            if (current.product == null) {
                int previousI = i;
                i++;
                getUniquePaths(current.children.get(path.get(previousI).product), path, i, unique);
            } else {
                // make traversed true if you hit it and say it is unique if never hit before.
                if (i <= path.size() - 1) {
                    if (!current.traversed) {
                        unique = true;
                        current.traversed = true;
                    } else {
                        unique = false;
                    }
                    int previousI = i;
                    i++;
                    getUniquePaths(current.children.get(path.get(previousI).product), path, i, unique);
                } else {
                    //if it isn't unique, we'll make the path empty and return it, remove empty paths from the list.
                    if (!unique) {
                        path.clear();
                    }
                    //just a test to determine if it found the correct unique paths
                    if (!path.isEmpty()) {
                        System.out.print("UNIQUE PATH: ");
                        for (int t = 0; t < path.size(); t++) {
                            System.out.print(" " + path.get(t).product);
                            if (t == path.size() - 1) {
                                System.out.print(" STOP");
                            }
                        }
                        System.out.println(" ");
                    }
                }
            }
        }
        return path;
    }

}
