package peetme.app.trunimal.com.peetme;

/**
 * Created by elias on 21/9/2016.
 */

public class Vet {

    //reports
    String name, phone, image,reports;
    Boolean active, open247;

    public Vet() {
    }

    public Vet(String name, String phone, String image, Boolean active, Boolean open247,String reports) {
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.active = active;
        this.open247 = open247;
        this.reports = reports;
    }

    public String getReports() { return reports; }

    public void setReports(String reports) { this.reports = reports; }

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getOpen247() {
        return open247;
    }

    public void setOpen247(Boolean open247) {
        this.open247 = open247;
    }

}
