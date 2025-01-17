package mockit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * The Class TestedClassWithNoPublicConstructorTest.
 */
public final class TestedClassWithNoPublicConstructorTest {

    /**
     * The Class TestedClassWithPackagePrivateConstructor.
     */
    @SuppressWarnings("unused")
    public static final class TestedClassWithPackagePrivateConstructor {

        /**
         * Instantiates a new tested class with package private constructor.
         *
         * @param values
         *            the values
         */
        private TestedClassWithPackagePrivateConstructor(int... values) {
            throw new RuntimeException("Must not occur");
        }

        /**
         * Instantiates a new tested class with package private constructor.
         *
         * @param i
         *            the i
         * @param collaborator
         *            the collaborator
         */
        TestedClassWithPackagePrivateConstructor(int i, Collaborator collaborator) {
            assertEquals(123, i);
            assertNotNull(collaborator);
        }

        /**
         * Instantiates a new tested class with package private constructor.
         *
         * @param i
         *            the i
         * @param collaborator
         *            the collaborator
         * @param s
         *            the s
         */
        private TestedClassWithPackagePrivateConstructor(int i, Collaborator collaborator, String s) {
            throw new RuntimeException("Must not occur");
        }
    }

    /**
     * The Class TestedClassWithPrivateConstructor.
     */
    @SuppressWarnings("UnusedDeclaration")
    static class TestedClassWithPrivateConstructor {

        /**
         * Instantiates a new tested class with private constructor.
         */
        private TestedClassWithPrivateConstructor() {
            throw new RuntimeException("Must not occur");
        }

        /**
         * Instantiates a new tested class with private constructor.
         *
         * @param collaborator
         *            the collaborator
         */
        private TestedClassWithPrivateConstructor(Collaborator collaborator) {
            assertNotNull(collaborator);
        }
    }

    /**
     * The Class Collaborator.
     */
    static class Collaborator {
        /**
         * Do something.
         */
        static void doSomething() {
        }
    }

    /** The tested 1. */
    @Tested
    TestedClassWithPackagePrivateConstructor tested1;

    /** The tested 2. */
    @Tested
    TestedClassWithPrivateConstructor tested2;

    /** The i. */
    @Injectable
    int i = 123;

    /** The collaborator. */
    @Injectable
    Collaborator collaborator;

    /**
     * Verify instantiation of tested objects through injected constructors.
     */
    @Test
    public void verifyInstantiationOfTestedObjectsThroughInjectedConstructors() {
        assertNotNull(tested1);
        assertNotNull(tested2);
    }
}
