public class productNode {
    private String product;
    private productNode previous;
    private productNode next;


    public productNode(String product){
        this.product=product;
    }



    public void add(String product){
        if(next==null){
            next=new productNode(product);
            next.previous=this;
        }else{
            next.add(product);
        }
    }

    public String getItems(){
        String representation;
        if(next==null){
            representation=this.product;
        }else{
            representation=this.product+","+next.getItems();
        }
        return representation;

    }

    public Integer getAmount(){
        if(next==null){
            return 1;
        }else{
            return 1+next.getAmount();
        }
    }
}
