package peetme.app.trunimal.com.peetme;

/**
 * Created by elias on 21/9/2016.
 */

public class Pet {

    String name;
    String description;
    String image;

    public Pet() {
    }

    public Pet(String title, String description, String image) {
        this.name = title;
        this.description = description;
        this.image = image;
    }

    public String getTitle() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
