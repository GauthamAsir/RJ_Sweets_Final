package agjs.gautham.rjsweets.Model;

public class SavedAddress {

    private String Line1;
    private String Line2;
    private String Landmark;
    private String City;
    private String State;
    private String Pincode;

    public SavedAddress() {
    }

    public SavedAddress(String line1, String line2, String landmark, String city, String state, String pincode) {
        Line1 = line1;
        Line2 = line2;
        Landmark = landmark;
        City = city;
        State = state;
        Pincode = pincode;
    }

    public String getLine1() {
        return Line1;
    }

    public void setLine1(String line1) {
        Line1 = line1;
    }

    public String getLine2() {
        return Line2;
    }

    public void setLine2(String line2) {
        Line2 = line2;
    }

    public String getLandmark() {
        return Landmark;
    }

    public void setLandmark(String landmark) {
        Landmark = landmark;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getPincode() {
        return Pincode;
    }

    public void setPincode(String pincode) {
        Pincode = pincode;
    }
}
