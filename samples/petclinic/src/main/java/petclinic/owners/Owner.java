package petclinic.owners;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import petclinic.pets.Pet;
import petclinic.util.Person;

/**
 * A pet owner.
 */
@Entity
public class Owner extends Person {
    private static final long serialVersionUID = 1L;
    private String address;
    private String city;
    @NotNull
    private String telephone;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    @OrderBy("name")
    private final List<Pet> pets = new ArrayList<>();

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void addPet(@Nonnull Pet pet) {
        pets.add(pet);
        pet.setOwner(this);
    }

    /**
     * Return the Pet with the given name, or null if none found for this Owner.
     *
     * @param name
     *            the name
     *
     * @return the pet
     */
    @Nullable
    public Pet getPet(@Nonnull String name) {
        return pets.stream().filter(pet -> pet.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
