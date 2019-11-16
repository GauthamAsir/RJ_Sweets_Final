package agjs.gautham.rjsweets.Model;

public class User {

    private String Name;
    private String Password;
    private String Phone;
    private String IsStaff;
    private String Email;

    public User(){

    }

    public User(String name, String password, String email, String phone) {
        Name = name;
        Password = password;
        Email = email;
        IsStaff="false";
        Phone = phone;
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
}


