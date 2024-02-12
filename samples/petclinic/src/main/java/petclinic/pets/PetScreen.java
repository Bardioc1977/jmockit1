package petclinic.pets;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import petclinic.owners.Owner;
import petclinic.owners.OwnerMaintenance;

/**
 * An application service class that handles {@link Pet}-related operations from the pet screen.
 */
@Named
@Transactional
@ViewScoped
public class PetScreen {
    @Inject
    private PetMaintenance petMaintenance;
    @Inject
    private OwnerMaintenance ownerMaintenance;
    @Nullable
    private List<PetType> types;
    @Nullable
    private Owner owner;
    @Nullable
    private Pet pet;

    @Nonnull
    public List<PetType> getTypes() {
        if (types == null) {
            types = petMaintenance.findPetTypes();
        }

        return types;
    }

    @Nullable
    public Pet getPet() {
        return pet;
    }

    public void requestNewPet() {
        pet = new Pet();
    }

    public void selectOwner(int ownerId) {
        owner = ownerMaintenance.findById(ownerId);
        pet = new Pet();
    }

    public void selectPet(int petId) {
        pet = petMaintenance.findById(petId);
    }

    public void createOrUpdatePet() {
        if (pet != null) {
            if (pet.isNew()) {
                // noinspection ConstantConditions
                petMaintenance.createPet(owner, pet);
            } else {
                petMaintenance.updatePet(pet);
            }
        }
    }
}
