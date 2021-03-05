package entity;

public class Owner {
    private final Long ownerID;
    private final String name;
    private final String lastName;

    public Owner(final Long ownerID, final String name, final String lastName) {
        this.ownerID = ownerID;
        this.name = name;
        this.lastName = lastName;
    }

    public Long getOwnerID() {
        return this.ownerID;
    }

    public String getName() {
        return this.name;
    }

    public String getLastName() {
        return this.lastName;
    }

    @Override
    public String toString() {
        return "Owner [ownerID=" + this.ownerID + ", name=" + this.name + ", lastName=" + this.lastName + "]";
    }
}
