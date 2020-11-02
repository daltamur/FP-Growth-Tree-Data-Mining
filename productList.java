public class productList {
    private productNode head;

    public productList(){}

    public void add(String product){
        if(head==null){
            head=new productNode(product);
        }else{
            head.add(product);
        }

    }

    public String getItems(){
        if(head==null){
            return "No Products";
        }else{
            return head.getItems();
        }

    }

    public Integer getAmount(){
        int returned=0;
        if(head==null){
            return returned;
        }else{
            returned=head.getAmount();
        }

        return returned;
    }

}
