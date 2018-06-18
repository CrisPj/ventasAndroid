package pythonteam.com.ventasapp.models;

public class Route {
    private int idRoute;
    private Latlong latLong;
    private String customerName;
    private int idEmployee;
    private int idCustomer;

    public int getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(int idEmployee) {
        this.idEmployee = idEmployee;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(int idRoute) {
        this.idRoute = idRoute;
    }

    public Latlong getLatLong() {
        return latLong;
    }

    public void setLatLong(Latlong latLong) {
        this.latLong = latLong;
    }
}
