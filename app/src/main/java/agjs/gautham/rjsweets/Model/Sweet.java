package agjs.gautham.rjsweets.Model;

public class Sweet {

    private String Description;
    private String Discount;
    private String Image;
    private String Name;
    private String Price;
    private String AvaQuantity;

    public Sweet(){

    }

    public Sweet(String description, String discount, String image, String name, String price, String avaQuantity) {
        Description = description;
        Discount = discount;
        Image = image;
        Name = name;
        Price = price;
        AvaQuantity = avaQuantity;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getAvaQuantity() {
        return AvaQuantity;
    }

    public void setAvaQuantity(String avaQuantity) {
        AvaQuantity = avaQuantity;
    }
}
