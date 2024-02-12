package petclinic.vets;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.util.List;

import javax.annotation.Nullable;

/**
 * An application service class that handles {@link Vet}-related operations from the vet screen.
 */
@Named
@Transactional
@ViewScoped
public class VetScreen {
    @Inject
    private VetMaintenance vetMaintenance;
    @Nullable
    private List<Vet> vets;

    @Nullable
    public List<Vet> getVets() {
        return vets;
    }

    public void showVetList() {
        vets = vetMaintenance.findAll();
    }
}
