package petclinic.owners;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import petclinic.util.Database;

/**
 * A domain service class for {@link Owner}-related business operations.
 */
@Transactional
public class OwnerMaintenance {
    @Inject
    private Database db;

    @Nullable
    public Owner findById(int ownerId) {
        return db.findById(Owner.class, ownerId);
    }

    /**
     * Finds the owners whose last name <em>starts</em> with the given name.
     *
     * @param lastName
     *            a prefix of the owner's last name to search for
     *
     * @return list of matching owners (empty if none found)
     */
    @Nonnull
    public List<Owner> findByLastName(@Nonnull String lastName) {
        return db.find("select o from Owner o where o.lastName like ?1", lastName + '%');
    }

    public void createOrUpdate(@Nonnull Owner newData) {
        db.save(newData);
    }
}
