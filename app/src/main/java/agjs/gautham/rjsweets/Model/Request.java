package agjs.gautham.rjsweets.Model;

import java.util.List;

public class Request {

    private String id;
    private String name;
    private String phone;
    private String address;
    private String total;
    private String status;
    private String comment;
    private String time;
    private String date;
    private String reason;
    private String paymentMethod;
    private String picked;
    private String pickedBy;
    private List<SweetOrder> sweetOrders; //list of sweets

    public Request(){

    }

    public Request(String id, String name, String phone, String address, String total, String status, String comment, String time, String date, String reason, String paymentMethod, String picked, String pickedBy, List<SweetOrder> sweetOrders) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.total = total;
        this.status = status;
        this.comment = comment;
        this.time = time;
        this.date = date;
        this.reason = reason;
        this.paymentMethod = paymentMethod;
        this.picked = picked;
        this.pickedBy = pickedBy;
        this.sweetOrders = sweetOrders;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPicked() {
        return picked;
    }

    public void setPicked(String picked) {
        this.picked = picked;
    }

    public String getPickedBy() {
        return pickedBy;
    }

    public void setPickedBy(String pickedBy) {
        this.pickedBy = pickedBy;
    }

    public List<SweetOrder> getSweetOrders() {
        return sweetOrders;
    }

    public void setSweetOrders(List<SweetOrder> sweetOrders) {
        this.sweetOrders = sweetOrders;
    }
}
