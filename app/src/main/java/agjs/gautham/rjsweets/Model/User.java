package agjs.gautham.rjsweets.Model;

public class User {

    private String Name;
    private String Password;
    private String Phone;
    private String IsStaff;
    private String Email;
    private String PendingPayment;
    private String Blacklist;
    private String BlacklistCount;

    public User(){

    }

    public User(String name, String password, String email, String phone) {
        Name = name;
        Password = password;
        Email = email;
        IsStaff="false";
        Phone = phone;
        PendingPayment = "0";
        Blacklist = "0";

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getIsStaff() { return IsStaff; }

    public void setIsStaff(String isStaff) { IsStaff = isStaff; }

    public String getPendingPayment() {
        return PendingPayment;
    }

    public void setPendingPayment(String pendingPayment) {
        PendingPayment = pendingPayment;
    }

    public String getBlacklist() {
        return Blacklist;
    }

    public void setBlacklist(String blacklist) {
        Blacklist = blacklist;
    }

    public String getBlacklistCount() {
        return BlacklistCount;
    }

    public void setBlacklistCount(String blacklistCount) {
        BlacklistCount = blacklistCount;
    }
}


