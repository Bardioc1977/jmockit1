package petclinic.visits;

import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;

import java.util.Date;

import petclinic.pets.Pet;
import petclinic.pets.PetData;
import petclinic.util.TestDatabase;

/**
 * Utility class for creation of {@link Visit} data in the test database, to be used in integration tests.
 */
public final class VisitData extends TestDatabase {
    @Inject
    private PetData petData;

    @Nonnull
    public Visit create(@Nonnull String description) {
        Pet pet = petData.findOrCreate("Test", null, "mouse");

        Visit visit = new Visit();
        visit.setPet(pet);
        visit.setDate(new Date());
        visit.setDescription(description);
        db.save(visit);
        return visit;
    }

    @Nonnull
    public Visit create() {
        return create("Testing");
    }
}
