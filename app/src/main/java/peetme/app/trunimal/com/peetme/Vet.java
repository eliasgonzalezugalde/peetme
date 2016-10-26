package peetme.app.trunimal.com.peetme;

/**
 * Created by elias on 21/9/2016.
 */

public class Vet {

    //reports
    String name, ubication, phone, facebook, image, schedule, schedule2;
    Boolean active, open247;

    public Vet() {
    }

    public Vet(String name, String ubication, String phone, String facebook, String image, String schedule, String schedule2, Boolean active, Boolean open247) {
        this.name = name;
        this.ubication = ubication;
        this.phone = phone;
        this.facebook = facebook;
        this.image = image;
        this.schedule = schedule;
        this.schedule2 = schedule2;
        this.active = active;
        this.open247 = open247;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUbication() {
        return ubication;
    }

    public void setUbication(String ubication) {
        this.ubication = ubication;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getSchedule2() {
        return schedule2;
    }

    public void setSchedule2(String schedule2) {
        this.schedule2 = schedule2;
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
