package pythonteam.com.ventasapp.models;

public class Customer {

    private int id;
    private String name;
    private String phone;
    private String email;
    private Latlong latlong;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Latlong getLatlong() {
        return latlong;
    }

    public void setLatlong(Latlong latlong) {
        this.latlong = latlong;
    }

    @Override
    public String toString() {
        return name;
    }
}
