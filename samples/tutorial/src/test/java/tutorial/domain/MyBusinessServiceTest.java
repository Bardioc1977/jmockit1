package tutorial.domain;

import static org.junit.Assert.assertNotEquals;
import static tutorial.persistence.Database.persist;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * The Class MyBusinessServiceTest.
 */
public final class MyBusinessServiceTest {

    /** The thrown. */
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    /** The data. */
    @Tested
    final EntityX data = new EntityX(1, "abc", "someone@somewhere.com");

    /** The business service. */
    @Tested(fullyInitialized = true)
    MyBusinessService businessService;

    /** The any email. */
    @Mocked
    SimpleEmail anyEmail;

    /**
     * Do business operation xyz.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void doBusinessOperationXyz() throws Exception {
        EntityX existingItem = new EntityX(1, "AX5", "abc@xpta.net");
        persist(existingItem);

        businessService.doBusinessOperationXyz();

        assertNotEquals(0, data.getId()); // implies "data" was persisted
        new Verifications() {
            {
                anyEmail.send();
                times = 1;
            }
        };
    }

    /**
     * Do business operation xyz with invalid email address.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void doBusinessOperationXyzWithInvalidEmailAddress() throws Exception {
        String email = "invalid address";
        data.setCustomerEmail(email);
        new Expectations() {
            {
                anyEmail.addTo(email);
                result = new EmailException();
            }
        };
        thrown.expect(EmailException.class);

        businessService.doBusinessOperationXyz();
    }
}
