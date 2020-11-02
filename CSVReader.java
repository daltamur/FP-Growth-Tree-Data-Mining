import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

public class CSVReader {

    String path;
    String line;
    Hashtable<String, productList> hashTable;
    Hashtable<String, Integer> numberOfProducts;

    CSVReader(){
        path="/Users/theif/Downloads/Groceries_dataset.csv";
        line="";
    }

    public Hashtable<String, productList> run(){
        hashTable=new Hashtable<>();
        numberOfProducts=new Hashtable<>();
        String customerCode;
        String date;
        String product;
        try {
            BufferedReader br=bufferedReader(path);
            while ((line=br.readLine())!=null) {
                if (!(line.equals("Member_number,Date,itemDescription"))) {
                    String[] stringValues = line.split(",");
                    customerCode = stringValues[0];
                    date = stringValues[1];
                    product = stringValues[2];
                    String idAndDate=customerCode+","+date;
                    productList list=hashTable.get(idAndDate);
                    if(list==null){
                        list=new productList();
                        list.add(product);
                        hashTable.put(idAndDate,list);
                        numberOfProducts.put(idAndDate, 1);
                    }else{
                        list.add(product);
                        int amount=list.getAmount();
                        hashTable.replace(idAndDate, list);
                        numberOfProducts.replace(idAndDate, amount);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hashTable;
    }

    public void write (Hashtable hashtable) throws IOException {
        BufferedWriter outputWriter;
        outputWriter = new BufferedWriter(new FileWriter("/Users/theif/Downloads/resultingList.txt"));
        Enumeration enu=hashtable.keys();
        while(enu.hasMoreElements()){
            String key=enu.nextElement().toString();
            Integer amount=numberOfProducts.get(key);
            productList list=hashTable.get(key);
            outputWriter.write(key+","+amount+","+list.getItems());
            outputWriter.newLine();
        }
        outputWriter.flush();
        outputWriter.close();
    }

    private static BufferedReader  bufferedReader(String path) throws FileNotFoundException {
        return new BufferedReader(new FileReader(path));
    }
}
