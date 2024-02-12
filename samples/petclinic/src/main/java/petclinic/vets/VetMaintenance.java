package petclinic.vets;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

import javax.annotation.Nonnull;

import petclinic.util.Database;

/**
 * A domain service class for {@link Vet}-related business operations.
 */
@Transactional
public class VetMaintenance {
    @Inject
    private Database db;

    @Nonnull
    public List<Vet> findAll() {
        return db.find("select v from Vet v order by v.lastName, v.firstName");
    }
}
