package peetme.app.trunimal.com.peetme;

/**
 * Created by elias on 21/9/2016.
 */

public class Pet {

    String name;
    String cat;
    int photo;

    public Pet() {
    }

    public Pet(String name, String cat, int photo) {
        this.name = name;
        this.cat = cat;
        this.photo = photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public String getName() {

        return name;
    }

    public String getCat() {
        return cat;
    }

    public int getPhoto() {
        return photo;
    }
}
