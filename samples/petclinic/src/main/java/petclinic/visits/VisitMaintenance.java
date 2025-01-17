package petclinic.visits;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import petclinic.pets.Pet;
import petclinic.util.Database;

/**
 * A domain service class for {@link Visit}-related business operations.
 */
@Transactional
public class VisitMaintenance {
    @Inject
    private Database db;

    public void create(@Nonnull Pet visitedPet, @Nonnull Visit visitData) {
        visitData.setPet(visitedPet);
        visitedPet.addVisit(visitData);
        db.save(visitData);
    }

    @Nullable
    public Visit findById(int visitId) {
        return db.findById(Visit.class, visitId);
    }

    @Nonnull
    public List<Visit> findByPetId(int petId) {
        return db.find("select v from Visit v where v.pet.id = ?1", petId);
    }
}
