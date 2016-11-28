package peetme.app.trunimal.com.peetme;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by elias on 21/9/2016.
 */

public class Pet {

    private int userId;
    private  String name, description, image, species, gender, size, health, additionalInfo, ubication, modifiedDate, createdDate, age, reports;
    private Boolean castrated, wormed, vaccinated, adopted, active;

    public Pet() {
    }

    public Pet(int userId, String age, String reports, String name, String description, String image, String species, String breed, String gender, String size, String health, String additionalInfo, String modifiedDate, String createdDate, String ubication, Boolean castrated, Boolean wormed, Boolean vaccinated, Boolean adopted, Boolean active) {
        this.userId = userId;
        this.age = age;
        this.reports = reports;
        this.name = name;
        this.description = description;
        this.image = image;
        this.species = species;
        this.gender = gender;
        this.size = size;
        this.health = health;
        this.additionalInfo = additionalInfo;
        this.modifiedDate = modifiedDate;
        this.createdDate = createdDate;
        this.ubication = ubication;
        this.castrated = castrated;
        this.wormed = wormed;
        this.vaccinated = vaccinated;
        this.adopted = adopted;
        this.active = active;
    }



    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getReports() {
        return reports;
    }

    public void setReports(String reports) {
        this.reports = reports;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUbication() {
        return ubication;
    }

    public void setUbication(String ubication) {
        this.ubication = ubication;
    }

    public Boolean getCastrated() {
        return castrated;
    }

    public void setCastrated(Boolean castrated) {
        this.castrated = castrated;
    }

    public Boolean getWormed() {
        return wormed;
    }

    public void setWormed(Boolean wormed) {
        this.wormed = wormed;
    }

    public Boolean getVaccinated() {
        return vaccinated;
    }

    public void setVaccinated(Boolean vaccinated) {
        this.vaccinated = vaccinated;
    }

    public Boolean getAdopted() {
        return adopted;
    }

    public void setAdopted(Boolean adopted) {
        this.adopted = adopted;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}
