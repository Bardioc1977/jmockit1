package petclinic.pets;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import petclinic.owners.Owner;
import petclinic.util.Database;

/**
 * A domain service class for {@link Pet}-related business operations.
 */
@Transactional
public class PetMaintenance {
    @Inject
    private Database db;

    @Nullable
    public Pet findById(int id) {
        return db.findById(Pet.class, id);
    }

    /**
     * Finds all pet types.
     *
     * @return the types found, in order of name
     */
    @Nonnull
    public List<PetType> findPetTypes() {
        return db.find("select t from PetType t order by t.name");
    }

    public void createPet(@Nonnull Owner owner, @Nonnull Pet data) {
        validate(owner, data);

        data.setOwner(owner);
        owner.addPet(data);
        db.save(data);
    }

    private void validate(@Nonnull Owner owner, @Nonnull Pet pet) {
        Pet existingPetOfSameName = owner.getPet(pet.getName());

        if (existingPetOfSameName != null) {
            throw new ValidationException("The owner already has a pet with this name.");
        }
    }

    public void updatePet(@Nonnull Pet data) {
        db.save(data);
    }
}
