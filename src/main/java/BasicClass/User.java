package BasicClass;
public class User {
    private int accountID; //tự động tạo khi cho vào sql
    private String fullName;
    private String email;
    private String phone;

    // Constructor
    public User(String fullName, String email, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    // Getter and Setter for accountID
    public int getAccountID() {
        return accountID;
    }

  
    // Getter and Setter for fullName
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Getter and Setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and Setter for phone
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Override toString method
    @Override
    public String toString() {
        return "User{" +
                "accountID=" + accountID +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}