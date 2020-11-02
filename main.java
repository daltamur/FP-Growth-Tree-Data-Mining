import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.sql.SQLOutput;
import java.util.*;

public class main implements minSupport{
    public static void main(String[] args) throws IOException {
        //Just creates the initial list list for task 1.
        CSVReader reader = new CSVReader();
        Hashtable x = reader.run();
        reader.write(x);
        //We'll keep track of the frequencies here and compare supports to them later on. The next 10 or so lines just go down the reformatted
        //list and get the frequencies of items
        Hashtable<String, Integer> frequency = new Hashtable<>();
        String filePath = "/Users/theif/Downloads/resultingList.txt";
        String line = "";
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        while ((line = br.readLine()) != null) {
            productList list = new productList();
            String[] contents = line.split(",");
            for (int i = 3; i < contents.length; i++) {
                String product = contents[i];
                int freq;
                if (!(frequency.containsKey(product))) {
                    freq = 1;
                    frequency.put(product, freq);
                    list.add(contents[i]);
                } else {
                    if (!(list.getItems().contains(contents[i]))) {
                        freq = frequency.get(product);
                        freq++;
                        frequency.replace(product, freq);
                        list.add(contents[i]);
                    }
                }
            }
        }
        //This array will hold the keys that get the frequencies of items, will be used to sort the frequencies from greatest to least
        ArrayList<String> keys = new ArrayList<>();
        Enumeration enu = frequency.keys();
        while (enu.hasMoreElements()) {
            String key = enu.nextElement().toString();
            keys.add(key);
        }
        //Next few lines to the quicksort operation. See that function for details.
        int p = 0;
        int r = keys.size() - 1;
        quicksort(frequency, p, r, keys);
        BufferedReader br2 = new BufferedReader(new FileReader(filePath));
        Enumeration enu1=frequency.keys();
        for(String key:keys){
            System.out.println(key+":"+frequency.get(key));
        }
        //Creates a new list of the sorted transactions
        ArrayList<productList> sortedTransactions = new ArrayList<>();
        //this will hold the counts of the strings from the conditional associations we'll get later. this will be a while from now.
        Hashtable<String,Hashtable<String,Integer>>conditionalCounts=new Hashtable<>();
        //reads through the transactions again
        while ((line = br2.readLine()) != null) {
            String[] transaction = line.split(",");
            ArrayList<String> transatctionItems = new ArrayList<>();
            //i=3 is where the items start
            for (int i = 3; i < transaction.length; i++) {
                transatctionItems.add(transaction[i]);
            }
            String[] items = new String[transatctionItems.size()];
            transatctionItems.toArray(items);
            //Sort the items from greatest to least so we can insert them into the trie. This is used later on to find unique products.
            items = insertionSort(items, keys);
            //see the product list file to see how that works, pretty much just a linked list.
            productList transactionList = new productList();
            //only put the items in the productList if the item is unique already and it meets the minsup
            for (String item : items) {
                if (!(transactionList.getItems().contains(item)) && (frequency.get(item) >= minSupport)) {
                    transactionList.add(item);
                }
            }
            //if the transactionlist isn't empty, throw it in the arrayList of sorted transactions.
            if (!(transactionList.getItems().equals("No Products"))) {
                sortedTransactions.add(transactionList);
            }
        }
        //just a test to make sure the transactions reached their appropriate size
        System.out.println("NUMBER OF TRANSACTIONS: " + sortedTransactions.size());


        //Now we make the trie structure almost 100 lines in.
        trie trieStructure = new trie();
        //insert each product from the transaction into the string
        for (productList sortedTransaction : sortedTransactions) {
            String products = sortedTransaction.getItems();
            String[] list = products.split(",");
            trieStructure.insert(list);
        }
        //This wasn't needed being that we took infrequent items out of the lists, but now the frequency hashtable only consists of frequent items
        for (int i = keys.size() - 1; i >= 0; i--) {
            if (frequency.get(keys.get(i)) < minSupport) {
                String removed = keys.get(i);
                keys.remove(removed);
            }
        }

        //this creates the patterns that the tree is made up of, used to get a hashtable of the paths
        trieStructure.constructConditionalPatternBase();
        Hashtable<String, ArrayList<String>> paths = trieStructure.getHashTable();
        //go through each path, this is the bulk of the program and is used to get the frequent patterns later used for the association rules
        Enumeration enu9=paths.keys();
        //this holds the association the patterns that'll be used for the association rules later on.
        Hashtable<String,ArrayList<ArrayList<String>>>associationRules=new Hashtable<>();
        while (enu9.hasMoreElements()) {
            //create the conditional trees that will be used to get frequent patters
            trie conditionalTree = new trie();
            String key = enu9.nextElement().toString();
            //every path that leads to the current key value
            ArrayList<String> pathsToCurrentValue = paths.get(key);
            //go through every path
            for (String current : pathsToCurrentValue) {
                //break the path string into an array
                String[] currentList = current.split(",");
                int currentLength = currentList.length;
                //put the value the path ends with inside the array
                String[] currentListWithEndNode = new String[currentLength + 1];
                for (int e = 0; e < currentListWithEndNode.length; e++) {
                    if (e == currentListWithEndNode.length - 2) {
                        currentListWithEndNode[e] = key;
                    } else if (e == currentListWithEndNode.length - 1) {
                        currentListWithEndNode[e] = currentList[currentList.length - 1];
                    } else {
                        currentListWithEndNode[e] = currentList[e];
                    }
                }
                //insert that path into the current conditional tree
                conditionalTree.conditionalInsert(currentListWithEndNode);
            }
            //this gets the frequent paths from the trie node class, check that out for details
            ArrayList<ArrayList<trieNode>> frequentPaths = conditionalTree.getFrequentPaths();
            //some won't have a frequent path, so only go further if the frequent path list has something in it, otherwise onto the next key
            if (frequentPaths.size() != 0) {
                //get the subpaths that result from the frequent paths created
                for (int j = 1; j < frequentPaths.size(); j++) {
                    int key2 = frequentPaths.get(j).size();
                    int i = j - 1;
                    while (i >= 0 && frequentPaths.get(i).size() < key2) {
                        ArrayList<trieNode> temp = frequentPaths.get(i);
                        frequentPaths.set(i, frequentPaths.get(i + 1));
                        frequentPaths.set(i + 1, temp);
                        i--;
                    }
                }

                //remove the node from the frequent paths list if it doesn't have any unique paths that lead to it, check out trieNode to see how
                for (int i = 0; i < frequentPaths.size(); i++) {
                    ArrayList<trieNode> current = frequentPaths.get(i);
                    current = conditionalTree.getUniquePaths(current);
                    if (current.isEmpty()) {
                        frequentPaths.remove(current);
                    }
                }

                //create the pattern that leads up to the current key value
                for (ArrayList<trieNode> current : frequentPaths) {
                    String pattern = "";
                    for (int y = 0; y < current.size(); y++) {
                        if (y != current.size() - 1) {
                            pattern += current.get(y).getProduct() + ":" + current.get(y).getCount() + " ";
                        } else {
                            pattern = pattern.substring(0, pattern.length() - 1);
                            current.remove(y);
                        }
                    }
                }

                //get the counts on the frequent patterns created by the conditional fp tree
                for (ArrayList<trieNode> currentPath : frequentPaths) {
                    for (int w = 0; w < currentPath.size(); w++) {
                        Hashtable<String, Integer> itemCount = conditionalCounts.get(key);
                        if (itemCount == null) {
                            itemCount = new Hashtable<>();
                            int starter = currentPath.get(w).getCount();
                            itemCount.put(currentPath.get(w).getProduct(), starter);
                            conditionalCounts.put(key, itemCount);
                        } else {
                            int currentCount = 0;
                            if (itemCount.containsKey(currentPath.get(w).getProduct())) {
                                currentCount = currentPath.get(w).getCount();
                                currentCount += itemCount.get(currentPath.get(w).getProduct());
                                itemCount.replace(currentPath.get(w).getProduct(), currentCount);
                                conditionalCounts.replace(key, itemCount);
                            } else {
                                currentCount = currentPath.get(w).getCount();
                                itemCount.put(currentPath.get(w).getProduct(), currentCount);
                                conditionalCounts.replace(key, itemCount);
                            }
                        }
                    }
                }

                for (ArrayList<trieNode> frequentPath : frequentPaths) {
                    //get the minimum number of the paths to so you can get minimums to put the support of item counts with more than 2 products
                    int minimumNumber = 0;
                    ArrayList<trieNode> currentPath = frequentPath;
                    for (int w = 0; w < currentPath.size(); w++) {
                        if (w == 0) {
                            minimumNumber = currentPath.get(w).getCount();
                        } else {
                            if (currentPath.get(w).getCount() < minimumNumber) {
                                minimumNumber = currentPath.get(w).getCount();
                            }
                        }
                    }
                    //create the subpatterns that make up the frequent pattern base for the current key
                    currentPath = frequentPath;
                    //if the current path size is just 1, then you're dealing with a root child and you can't do anything with it so it doesn't get evaluated
                    if (currentPath.size() > 1) {
                        //develop the frequent patterns and put them in the  conditional counts hashtable
                        String pattern = currentPath.get(0).getProduct();
                        for (int w = 1; w < currentPath.size(); w++) {
                            pattern += "," + currentPath.get(w).getProduct();
                            Hashtable<String, Integer> currentValue = conditionalCounts.get(key);
                            currentValue.put(pattern, minimumNumber);
                            conditionalCounts.replace(key, currentValue);
                        }
                    }
                }
                Hashtable<String, Integer>current = conditionalCounts.get(key);
                Enumeration enumeration2 = current.keys();
                ArrayList<String>associations=new ArrayList<>();

                //put the patterns support values into the frequency table for future use
                while (enumeration2.hasMoreElements()) {
                    String currentKey = enumeration2.nextElement().toString();
                    Integer number = current.get(currentKey);
                    String added=currentKey+","+key;
                    frequency.put(added,number);
                    associations.add(added);
                }
                ArrayList<ArrayList<String>>lists=associationRules.get(key);
                if(lists==null){
                    lists=new ArrayList<>();
                    lists.add(associations);
                    associationRules.put(key, lists);
                }
                else {
                    lists.add(associations);
                    associationRules.replace(key, lists);
                }
                System.out.println();
                for (String association : associations) {
                    System.out.println(association + ":" + frequency.get(association));
                }
                System.out.println();
                }
            }

        //get association rules where the precedent has only one product in it, matter of reformatting the string
        Enumeration enumeration=associationRules.keys();
        while(enumeration.hasMoreElements()){
            String key=enumeration.nextElement().toString();
            ArrayList<ArrayList<String>>frequentPatterns=associationRules.get(key);
            for(ArrayList<String>associations:frequentPatterns){
                for(String association:associations){
                    String[] currentSet = association.split(",");
                    for(int i=0;i<currentSet.length;i++){
                        String currentPrecedent=currentSet[i];
                        String currentAntecedent="";
                        currentAntecedent=association.replace(currentPrecedent,"");
                        if(currentAntecedent.startsWith(",")){
                            currentAntecedent=currentAntecedent.substring(1);
                        }else if(currentAntecedent.endsWith(",")){
                            currentAntecedent=currentAntecedent.substring(0,currentAntecedent.length()-1);
                        }else if(currentAntecedent.contains(",,")){
                            currentAntecedent=currentAntecedent.replace(",,"," & ");
                        }
                        double precedentSupport=frequency.get(currentPrecedent).doubleValue();
                        double antecedantSupport=frequency.get(association).doubleValue();
                        double currentConfidence=antecedantSupport/precedentSupport;
                        if(currentConfidence>=minConfidence){
                            String printedAntecedent = currentAntecedent.replace(",", " & ");
                            System.out.println(currentPrecedent+"->"+printedAntecedent+" Confidence:"+currentConfidence);
                        }
                    }
                    String[] largeSet=association.split(",");
                    int currentSubsetCount=2;
                    //only make more association rules with larger precedents if they're possible with the given size
                    if(largeSet.length>2) {
                        while(currentSubsetCount!=largeSet.length) {
                            int headSize = currentSubsetCount - 1;
                            if (headSize == 1) {
                                for (int i = 0; i < largeSet.length; i++) {
                                    String head = largeSet[i];
                                    for (int y = i + 1; y < largeSet.length; y++) {
                                        String currentPrecedent = head + "," + largeSet[y];
                                        String printedPrecedent = head + " & " + largeSet[y];
                                        String currentAntecedent = association.replace(largeSet[y], "");
                                        currentAntecedent=currentAntecedent.replace(head,"");
                                        if(currentAntecedent.contains(",,")){
                                            if (currentAntecedent.startsWith(",,")) {
                                                currentAntecedent = currentAntecedent.substring(2);
                                            }
                                            if (currentAntecedent.endsWith(",,")) {
                                                currentAntecedent = currentAntecedent.substring(0, currentAntecedent.length() - 2);
                                            }
                                            currentAntecedent=currentAntecedent.replace(",,"," & ");
                                        }
                                        if (currentAntecedent.contains(",")) {
                                            if(currentAntecedent.startsWith(",")){
                                                currentAntecedent=currentAntecedent.substring(1);
                                            }
                                            if(currentAntecedent.endsWith(",")){
                                                currentAntecedent=currentAntecedent.substring(0,currentAntecedent.length()-1);
                                            }
                                            currentAntecedent = currentAntecedent.replace(",", " & ");
                                        }
                                        double precedentSupport = frequency.get(currentPrecedent).doubleValue();
                                        double antecedantSupport = frequency.get(association).doubleValue();
                                        double currentConfidence = antecedantSupport / precedentSupport;
                                        if (currentConfidence >= minConfidence) {
                                            String printedAntecedent = currentAntecedent;//.replace(",", "&");
                                            System.out.println(printedPrecedent + "->" + printedAntecedent + " Confidence:" + currentConfidence);
                                        }
                                    }
                                }
                            }else{
                                System.out.println("BIG BOY");
                                for(int i=0;i<largeSet.length;i++){
                                    String currentHead=largeSet[i];
                                    for(int w=i+1;w<headSize-1;w++){
                                        currentHead+=currentHead+","+largeSet[w];
                                    }



                                }
                            }
                            currentSubsetCount++;
                        }
                    }
                }
            }
        }

    }
    private static String[] insertionSort(String[] transaction, ArrayList<String> keys) {
        //just ur average insertion sort, used it being that transactions were never too huge and it wasn't worth the backend on an already complicated program
        for(int j=1;j<transaction.length;j++){
            int key=keys.indexOf(transaction[j]);
            int i=j-1;
            while(i>=0&&keys.indexOf(transaction[i])>key) {
                String temp = transaction[i];
                transaction[i] = transaction[i + 1];
                transaction[i + 1] = temp;
                i--;
            }
        }
        return transaction;
    }

    private static void quicksort(Hashtable<String, Integer> frequency,int p, int r, ArrayList<String> keys) {
        //this sorts the frequency values from greatest to least
        if(p<r){
            int q=partition(keys,p,r,frequency);
            quicksort(frequency,p,q-1,keys);
            quicksort(frequency,q+1,r,keys);
        }

    }

    private static int partition(ArrayList<String> keys, int p, int r, Hashtable<String, Integer> frequency) {
        int x=frequency.get(keys.get(r));
        int i =p-1;
        int j=p;
        while(j<r){
            if(frequency.get(keys.get(j))>x){
                i=i+1;
                String temp=keys.get(j);
                keys.set(j,keys.get(i));
                keys.set(i,temp);
            }
            j++;
        }
        String temp=keys.get(r);
        keys.set(r,keys.get(i+1));
        keys.set(i+1,temp);
        return i+1;
    }
}
