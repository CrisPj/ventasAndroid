package pythonteam.com.ventasapp.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable{
    private int orderId;
    private int customerId;
    private int employeeId;
    private boolean status;
    private ArrayList<Product> productList = new ArrayList<>();
    private Date orderdate;

    public Date getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(Date orderdate) {
        this.orderdate = orderdate;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void addProduct(Product product)
    {
        Product h = new Product();
        h.setQuantity(product.getQuantity());
        h.setPrice(product.getPrice());
        h.setId(product.getId());
        h.setName(product.getName());

        for (Product p : productList) {
            if (p.getId() == h.getId()) {
                p.setQuantity(p.getQuantity() + h.getQuantity());
                return;
            }
        }
        productList.add(h);
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
