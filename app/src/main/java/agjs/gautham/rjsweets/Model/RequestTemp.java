package agjs.gautham.rjsweets.Model;

import java.util.List;

public class RequestTemp {

    private String id;
    private List<SweetOrder> sweetOrders; //list of sweets

    public RequestTemp(){

    }

    public RequestTemp(String id, List<SweetOrder> sweetOrders) {
        this.id = id;
        this.sweetOrders = sweetOrders;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SweetOrder> getSweetOrders() {
        return sweetOrders;
    }

    public void setSweetOrders(List<SweetOrder> sweetOrders) {
        this.sweetOrders = sweetOrders;
    }
}
