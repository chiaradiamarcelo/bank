package entity;

public class Owner {

    private Long ownerID;
    private String name;
    private String lastName;

    public Owner(Long ownerID, String name, String lastName) {
        super();
        this.ownerID = ownerID;
        this.name = name;
        this.lastName = lastName;
    }

    public Long getOwnerID() {
        return ownerID;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return "Owner [ownerID=" + ownerID + ", name=" + name + ", lastName=" + lastName + "]";
    }

}
