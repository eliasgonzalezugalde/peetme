package peetme.app.trunimal.com.peetme;

/**
 * Created by elias on 21/9/2016.
 */

public class Pet {

    private int userId;
    private  String name, description, image, additionalInfo, phone, modifiedDate, createdDate;
    private int species, gender, size, health, age, reports;
    private Boolean castrated, wormed, vaccinated, adopted, active;

    public Pet() {
    }

    public Pet(int userId, String name, String description, String image, String additionalInfo, String phone, String modifiedDate, String createdDate, int species, int gender, int size, int health, int age, int reports, Boolean castrated, Boolean wormed, Boolean vaccinated, Boolean adopted, Boolean active) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.image = image;
        this.additionalInfo = additionalInfo;
        this.phone = phone;
        this.modifiedDate = modifiedDate;
        this.createdDate = createdDate;
        this.species = species;
        this.gender = gender;
        this.size = size;
        this.health = health;
        this.age = age;
        this.reports = reports;
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

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public int getSpecies() {
        return species;
    }

    public void setSpecies(int species) {
        this.species = species;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getReports() {
        return reports;
    }

    public void setReports(int reports) {
        this.reports = reports;
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
