package mockit;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import mockit.internal.expectations.invocation.MissingInvocation;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * The Class WithCaptureTest.
 */
@SuppressWarnings("ConstantConditions")
public final class WithCaptureTest {

    /** The thrown. */
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    /**
     * The Class Person.
     */
    public static class Person {

        /** The name. */
        private String name;

        /** The age. */
        private int age;

        /**
         * Instantiates a new person.
         */
        public Person() {
        }

        /**
         * Instantiates a new person.
         *
         * @param name
         *            the name
         * @param age
         *            the age
         */
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        /**
         * Gets the name.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the age.
         *
         * @return the age
         */
        public int getAge() {
            return age;
        }
    }

    /**
     * The Interface DAO.
     *
     * @param <T>
     *            the generic type
     */
    public interface DAO<T> {
        /**
         * Creates the.
         *
         * @param t
         *            the t
         */
        @SuppressWarnings("unused")
        void create(T t);
    }

    /**
     * The Class PersonDAO.
     */
    @SuppressWarnings("UnusedParameters")
    public static final class PersonDAO implements DAO<Person> {
        @Override
        public void create(Person p) {
        }

        /**
         * Creates the.
         *
         * @param name
         *            the name
         * @param age
         *            the age
         *
         * @return the person
         */
        public Person create(String name, int age) {
            return new Person(name, age);
        }

        /**
         * Do something.
         *
         * @param i
         *            the i
         */
        public void doSomething(Integer i) {
        }

        /**
         * Do something.
         *
         * @param b
         *            the b
         */
        public void doSomething(boolean b) {
        }

        /**
         * Do something.
         *
         * @param n
         *            the n
         */
        public void doSomething(Number n) {
        }

        /**
         * Do something else.
         *
         * @param n
         *            the n
         */
        public void doSomethingElse(Number n) {
        }

        /**
         * Do something.
         *
         * @param nums
         *            the nums
         */
        public void doSomething(Number[] nums) {
        }

        /**
         * Do something.
         *
         * @param nums
         *            the nums
         */
        public void doSomething(List<Integer> nums) {
        }

        /**
         * Do something.
         *
         * @param s1
         *            the s 1
         * @param b
         *            the b
         * @param s2
         *            the s 2
         * @param d
         *            the d
         * @param f
         *            the f
         * @param l
         *            the l
         * @param o
         *            the o
         * @param c
         *            the c
         * @param bt
         *            the bt
         * @param sh
         *            the sh
         */
        public void doSomething(String s1, boolean b, String s2, double d, float f, long l, Object o, char c, byte bt,
                short sh) {
        }

        /**
         * Do something.
         *
         * @param names
         *            the names
         * @param ages
         *            the ages
         */
        void doSomething(String[] names, int[] ages) {
        }

        /**
         * Do something.
         *
         * @param f1
         *            the f 1
         * @param f2
         *            the f 2
         * @param flags
         *            the flags
         */
        void doSomething(Float f1, float f2, boolean... flags) {
        }

        /**
         * Do something.
         *
         * @param name
         *            the name
         * @param age
         *            the age
         */
        void doSomething(String name, Short age) {
        }
    }

    /** The dao. */
    @Mocked
    PersonDAO dao;

    /**
     * Capture argument from last matching invocation to local variable.
     */
    @Test
    public void captureArgumentFromLastMatchingInvocationToLocalVariable() {
        dao.create("Mary Jane", 10);
        dao.create("John", 25);

        new FullVerifications() {
            {
                int age;
                dao.create(null, age = withCapture());
                assertTrue(age >= 18);
            }
        };
    }

    /**
     * Capture argument of wrapper type to local variable of primitive type.
     */
    @Test
    public void captureArgumentOfWrapperTypeToLocalVariableOfPrimitiveType() {
        dao.doSomething(45);

        new Verifications() {
            {
                int i;
                dao.doSomething(i = withCapture());
                assertEquals(45, i);
            }
        };
    }

    /**
     * Capture null argument of wrapper type to local variable of same wrapper type.
     */
    @Test
    public void captureNullArgumentOfWrapperTypeToLocalVariableOfSameWrapperType() {
        dao.doSomething((Integer) null);

        new Verifications() {
            {
                Integer i;
                dao.doSomething(i = withCapture());
                assertNull(i);
            }
        };
    }

    /**
     * Capture argument of reference type to local variable of primitive type.
     */
    @Test
    public void captureArgumentOfReferenceTypeToLocalVariableOfPrimitiveType() {
        dao.doSomething(123.0F);

        new Verifications() {
            {
                float f;
                dao.doSomething(f = withCapture());
                assertEquals(123.0F, f, 0);
            }
        };
    }

    /**
     * Capture null into A list during verification.
     */
    @Test
    public void captureNullIntoAListDuringVerification() {
        dao.create(null);

        new Verifications() {
            {
                List<Person> persons = new ArrayList<>();
                dao.create(withCapture(persons));
                assertEquals(1, persons.size());
                assertNull(persons.get(0));
            }
        };
    }

    /**
     * Capture argument to variable of specific subtype for separate invocations.
     */
    @Test
    public void captureArgumentToVariableOfSpecificSubtypeForSeparateInvocations() {
        dao.doSomething(new BigInteger("9999"));
        dao.doSomething((byte) -123);
        dao.doSomething(123.0F);
        dao.doSomething(1234L);
        dao.doSomething(1234.5);

        new Verifications() {
            {
                BigInteger bi;
                dao.doSomething(bi = withCapture());
                assertEquals(9999, bi.intValue());

                Float f;
                dao.doSomething(f = withCapture());
                assertEquals(123.0F, f, 0);

                long l;
                dao.doSomething(l = withCapture());
                assertEquals(1234L, l);

                Double d;
                dao.doSomething(d = withCapture());
                assertEquals(1234.5, d, 0);

                byte b;
                dao.doSomething(b = withCapture());
                assertEquals(-123, b);
            }
        };
    }

    /**
     * Capture array arguments to variables with specific element subtypes.
     */
    @Test
    public void captureArrayArgumentsToVariablesWithSpecificElementSubtypes() {
        final Integer[] ints = { 1, 2, 3 };
        dao.doSomething(ints);

        final Double[] doubles = { 1.0, 2.5, -3.2 };
        dao.doSomething(doubles);

        final BigInteger[] bigInts = { new BigInteger("12"), new BigInteger("45") };
        dao.doSomething(bigInts);

        new VerificationsInOrder() {
            {
                Integer[] capturedInts;
                dao.doSomething(capturedInts = withCapture());
                assertSame(ints, capturedInts);

                Double[] capturedDoubles;
                dao.doSomething(capturedDoubles = withCapture());
                assertSame(doubles, capturedDoubles);

                BigInteger[] capturedBigInts;
                dao.doSomething(capturedBigInts = withCapture());
                assertSame(bigInts, capturedBigInts);
            }
        };
    }

    /**
     * Capture argument of primitive type to local variable of primitive type.
     */
    @Test
    public void captureArgumentOfPrimitiveTypeToLocalVariableOfPrimitiveType() {
        dao.doSomething(true);

        new Verifications() {
            {
                boolean b;
                dao.doSomething(b = withCapture());
                assertTrue(b);
            }
        };
    }

    /**
     * Capture argument of primitive type to local variable of reference type.
     */
    @Test
    public void captureArgumentOfPrimitiveTypeToLocalVariableOfReferenceType() {
        dao.doSomething(true);

        new Verifications() {
            {
                Boolean b;
                dao.doSomething(b = withCapture());
                assertTrue(b);
            }
        };
    }

    /**
     * Capture arguments to local variables.
     */
    @Test
    public void captureArgumentsToLocalVariables() {
        final Person p = new Person("John", 10);
        dao.create(p);
        dao.create("Mary Jane", 30);
        dao.doSomething("test", true, "Test", 4.5, -2.3F, 123, p, 'g', (byte) 127, (short) -32767);

        new Verifications() {
            {
                Person created;
                dao.create(created = withCapture());
                assertEquals("John", created.getName());
                assertEquals(10, created.getAge());

                String name;
                int age;
                dao.create(name = withCapture(), age = withCapture());
                assertEquals("Mary Jane", name);
                assertEquals(30, age);

                String s1;
                boolean b;
                double d;
                float f;
                long l;
                Object o;
                char c;
                byte bt;
                short sh;
                dao.doSomething(s1 = withCapture(), b = withCapture(), "Test", d = withCapture(), f = withCapture(),
                        l = withCapture(), o = withCapture(), c = withCapture(), bt = withCapture(),
                        sh = withCapture());
                assertEquals("test", s1);
                assertTrue(b);
                assertEquals(4.5, d, 0);
                assertEquals(-2.3, f, 0.001);
                assertEquals(123, l);
                assertSame(p, o);
                assertEquals('g', c);
                assertEquals(127, bt);
                assertEquals(-32767, sh);
            }
        };
    }

    /**
     * Attempt to capture arguments into fields.
     */
    @Test
    public void attemptToCaptureArgumentsIntoFields() {
        dao.doSomething(56);

        new Verifications() {
            final Integer i;

            {
                dao.doSomething(i = withCapture());
                assertNull(i);
            }
        };
    }

    /**
     * Capture first argument in two parameter method.
     */
    @Test
    public void captureFirstArgumentInTwoParameterMethod() {
        final String name1 = "Ted";
        final Short age = 15;
        dao.doSomething(name1, age);

        final String name2 = "Jane";
        dao.doSomething(name2, age);

        new VerificationsInOrder() {
            {
                String nameCapture;
                dao.doSomething(nameCapture = withCapture(), age);
                assertEquals(name1, nameCapture);

                String strCapture;
                dao.doSomething(strCapture = withCapture(), age);
                assertEquals(name2, strCapture);
            }
        };
    }

    /**
     * Capture arguments for invocation already matched by recorded expectation.
     */
    @Test
    public void captureArgumentsForInvocationAlreadyMatchedByRecordedExpectation() {
        new Expectations() {
            {
                dao.doSomething(anyString, anyShort);
            }
        };

        dao.doSomething("testing", (short) 15);

        new Verifications() {
            {
                String s;
                short i;
                dao.doSomething(s = withCapture(), i = withCapture());
                assertEquals("testing", s);
                assertEquals(15, i);
            }
        };
    }

    /**
     * Capture arguments from consecutive matching invocations.
     */
    @Test
    public void captureArgumentsFromConsecutiveMatchingInvocations() {
        dao.doSomething((byte) 56);
        dao.doSomething(123.4F);
        dao.doSomething((short) -78);
        dao.doSomething(91);
        dao.doSomething(92);

        final String[] names1 = { "Ted" };
        final int[] ages1 = { 15, 46 };
        dao.doSomething(names1, ages1);

        final String[] names2 = { "Jane" };
        final int[] ages2 = { 101 };
        dao.doSomething(names2, ages2);

        new VerificationsInOrder() {
            {
                byte bt;
                dao.doSomething(bt = withCapture());
                assertEquals(56, bt);

                Number n;
                dao.doSomething(n = withCapture());
                assertEquals(123.4, n.floatValue(), 0.001);

                short sh;
                dao.doSomething(sh = withCapture());
                assertEquals(-78, sh);

                int i1;
                dao.doSomething(i1 = withCapture());
                assertEquals(91, i1);

                Integer i2;
                dao.doSomething(i2 = withCapture());
                assertEquals(92, i2.intValue());

                String[] namesCapture;
                int[] agesCapture;
                dao.doSomething(namesCapture = withCapture(), agesCapture = withCapture());
                assertSame(names1, namesCapture);
                assertSame(ages1, agesCapture);

                dao.doSomething(namesCapture = withCapture(), agesCapture = withCapture());
                assertSame(names2, namesCapture);
                assertSame(ages2, agesCapture);
            }
        };
    }

    /**
     * Capture array arguments.
     */
    @Test
    public void captureArrayArguments() {
        final String[] names = { "Ted", "Lisa" };
        final int[] ages = { 67, 19 };
        dao.doSomething(names, ages);

        new Verifications() {
            {
                String[] capturedNames;
                int[] capturedAges;
                dao.doSomething(capturedNames = withCapture(), capturedAges = withCapture());

                assertArrayEquals(names, capturedNames);
                assertArrayEquals(ages, capturedAges);
            }
        };
    }

    /**
     * Capture varargs parameter.
     */
    @Test
    public void captureVarargsParameter() {
        dao.doSomething(1.2F, 1.0F, true, false, true);
        dao.doSomething(0.0F, 2.0F, false, true);
        dao.doSomething(-2.0F, 3.0F);

        new VerificationsInOrder() {
            {
                boolean[] flags;

                dao.doSomething(anyFloat, 1.0F, flags = withCapture());
                assertEquals(3, flags.length);
                assertTrue(flags[0]);
                assertFalse(flags[1]);
                assertTrue(flags[2]);

                dao.doSomething(null, 2.0F, flags = withCapture());
                assertEquals(2, flags.length);
                assertFalse(flags[0]);
                assertTrue(flags[1]);

                dao.doSomething(withAny(0.0F), 3.0F, flags = withCapture());
                assertEquals(0, flags.length);
            }
        };
    }

    /**
     * Capture arguments while mixing any fields and literal values and calls to other methods.
     */
    @Test
    public void captureArgumentsWhileMixingAnyFieldsAndLiteralValuesAndCallsToOtherMethods() {
        final double d = 4.5;
        final long l = 123;
        dao.doSomething("Test", true, "data", d, 12.25F, l, "testing", '9', (byte) 11, (short) 5);

        new Verifications() {
            {
                float f;
                String s;
                byte b;

                // noinspection ConstantMathCall
                dao.doSomething(null, anyBoolean, getData(), Math.abs(-d), f = withCapture(), Long.parseLong("" + l),
                        s = withCapture(), Character.forDigit(9, 10), b = withCapture(), anyShort);

                assertEquals(12.25F, f, 0);
                assertEquals("testing", s);
                assertEquals(11, b);
            }
        };
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    private String getData() {
        return "data";
    }

    /**
     * Capture arguments into list in expectation block.
     */
    @Test
    public void captureArgumentsIntoListInExpectationBlock() {
        final List<Person> personsCreated = new ArrayList<>();
        final List<String> personNames = new LinkedList<>();
        final List<Integer> personAges = new LinkedList<>();

        new Expectations() {
            {
                dao.create(withCapture(personsCreated));
                dao.create(withCapture(personNames), withCapture(personAges));
            }
        };

        dao.create(new Person("John", 10));
        assertEquals(1, personsCreated.size());
        Person first = personsCreated.get(0);
        assertEquals("John", first.getName());
        assertEquals(10, first.getAge());

        dao.create(new Person("Jane", 20));
        assertEquals(2, personsCreated.size());
        Person second = personsCreated.get(1);
        assertEquals("Jane", second.getName());
        assertEquals(20, second.getAge());

        dao.create("Mary Jane", 35);
        assertEquals(1, personNames.size());
        assertEquals("Mary Jane", personNames.get(0));
        assertEquals(1, personAges.size());
        assertEquals(35, personAges.get(0).intValue());
    }

    /**
     * Capture arguments into list in verification block.
     */
    @Test
    public void captureArgumentsIntoListInVerificationBlock() {
        dao.create(new Person("John", 10));
        dao.create("Mary Jane", 35);
        dao.create("", 56);
        dao.create(new Person("Jane", 20));
        dao.create("Daisy Jones", 6);

        new Verifications() {
            final List<Person> created = new ArrayList<>();
            final List<Integer> ages = new ArrayList<>();

            {
                dao.create("", withCapture(ages));
                assertEquals(singletonList(56), ages);

                dao.create(withCapture(created));
                assertEquals(2, created.size());

                Person first = created.get(0);
                assertEquals("John", first.getName());
                assertEquals(10, first.getAge());

                Person second = created.get(1);
                assertEquals("Jane", second.getName());
                assertEquals(20, second.getAge());

                ages.clear();
                dao.create(withSubstring(" "), withCapture(ages));
                times = 2;
                assertEquals(asList(35, 6), ages);
            }
        };
    }

    /**
     * Capture newed instance.
     *
     * @param mockedPerson
     *            the mocked person
     */
    @Test
    public void captureNewedInstance(@Mocked Person mockedPerson) {
        Person p = new Person();
        dao.create(p);

        new Verifications() {
            {
                Person newInstance = withCapture(new Person()).get(0);

                Person capturedPerson;
                dao.create(capturedPerson = withCapture());

                assertSame(newInstance, capturedPerson);
            }
        };
    }

    /**
     * Capture multiple new instances.
     *
     * @param mockedPerson
     *            the mocked person
     */
    @Test
    public void captureMultipleNewInstances(@Mocked Person mockedPerson) {
        dao.create(new Person("Paul", 10));
        dao.create(new Person("Mary", 15));
        dao.create(new Person("Joe", 20));

        new Verifications() {
            {
                List<Person> personsInstantiated = withCapture(new Person(anyString, anyInt));

                List<Person> personsCreated = new ArrayList<>();
                dao.create(withCapture(personsCreated));

                // noinspection MisorderedAssertEqualsArguments
                assertEquals(personsInstantiated, personsCreated);
            }
        };
    }

    /**
     * Attempt to capture new instance when there was none.
     *
     * @param mockedPerson
     *            the mocked person
     */
    @Test
    public void attemptToCaptureNewInstanceWhenThereWasNone(@Mocked Person mockedPerson) {
        thrown.expect(MissingInvocation.class);
        thrown.expectMessage("Person(any String, any int)");

        dao.create("test", 14);

        new Verifications() {
            {
                List<Person> newInstances = withCapture(new Person());
                times = 0;
                assertTrue(newInstances.isEmpty());

                withCapture(new Person(anyString, anyInt));
            }
        };
    }

    /**
     * Capture two sets of new instances of the same type.
     *
     * @param mockedPerson
     *            the mocked person
     */
    @Test
    public void captureTwoSetsOfNewInstancesOfTheSameType(@Mocked Person mockedPerson) {
        // First set.
        final Person p1 = new Person();

        // Second set.
        final Person p2 = new Person("Paul", 10);
        final Person p3 = new Person("Mary", 15);

        new Verifications() {
            {
                List<Person> persons1 = withCapture(new Person(anyString, anyInt));
                assertEquals(asList(p2, p3), persons1);

                List<Person> persons2 = withCapture(new Person());
                assertEquals(singletonList(p1), persons2);
            }
        };
    }

    /**
     * Capture new instances after verifying new instance of different type.
     *
     * @param mockedPerson
     *            the mocked person
     */
    @Test
    public void captureNewInstancesAfterVerifyingNewInstanceOfDifferentType(@Mocked Person mockedPerson) {
        new PersonDAO();
        final Person p1 = new Person("Paul", 10);
        final Person p2 = new Person("Mary", 10);

        new Verifications() {
            {
                new PersonDAO();

                List<Person> persons = withCapture(new Person(anyString, 10));
                assertEquals(asList(p1, p2), persons);
            }
        };
    }

    /**
     * Attempt to capture argument for invocation that never occurred unordered.
     */
    @Test
    public void attemptToCaptureArgumentForInvocationThatNeverOccurred_unordered() {
        thrown.expect(MissingInvocation.class);
        thrown.expectMessage("Missing 1 invocation to:");
        thrown.expectMessage("PersonDAO#create(");
        thrown.expectMessage("any " + Person.class.getName());

        new Verifications() {
            {
                Person p;
                dao.create(p = withCapture());
                times = 1;
                assertEquals("...", p.getName());
            }
        };
    }

    /**
     * Attempt to capture argument for invocation that never occurred ordered.
     */
    @Test
    public void attemptToCaptureArgumentForInvocationThatNeverOccurred_ordered() {
        thrown.expect(MissingInvocation.class);

        new VerificationsInOrder() {
            {
                Person p;
                dao.create(p = withCapture());
                times = 1;
                assertEquals("...", p.getName());
            }
        };
    }

    /**
     * The Class ClassWithVarargsMethod.
     */
    static class ClassWithVarargsMethod {
        /**
         * Varargs method.
         *
         * @param s
         *            the s
         * @param values
         *            the values
         */
        @SuppressWarnings("unused")
        void varargsMethod(String s, String... values) {
        }
    }

    /**
     * Capture varargs values from all invocations.
     *
     * @param mock
     *            the mock
     */
    @Test
    public void captureVarargsValuesFromAllInvocations(@Mocked final ClassWithVarargsMethod mock) {
        final String[] expectedValues1 = { "a", "b" };
        mock.varargsMethod("First", expectedValues1);

        final String[] expectedValues2 = { "1", "2" };
        mock.varargsMethod("Second", expectedValues2);

        new Verifications() {
            {
                List<String> capturedNames = new ArrayList<>();
                List<String[]> capturedValues = new ArrayList<>();

                mock.varargsMethod(withCapture(capturedNames), withCapture(capturedValues));

                assertEquals(asList("First", "Second"), capturedNames);
                assertArrayEquals(expectedValues1, capturedValues.get(0));
                assertArrayEquals(expectedValues2, capturedValues.get(1));
            }
        };
    }

    /**
     * Capture arguments into A list of A subtype of the captured parameter type.
     */
    @Test
    public void captureArgumentsIntoAListOfASubtypeOfTheCapturedParameterType() {
        dao.doSomethingElse(1);
        dao.doSomethingElse(2.0);
        dao.doSomethingElse(3);

        final List<Integer> expectedValues = asList(1, 3);

        new Verifications() {
            {
                List<Integer> onlyIntegers = new ArrayList<>();
                dao.doSomethingElse(withCapture(onlyIntegers));
                // noinspection MisorderedAssertEqualsArguments
                assertEquals(expectedValues, onlyIntegers);
            }
        };
    }

    /**
     * Capture list arguments from multiple invocations.
     */
    @Test
    public void captureListArgumentsFromMultipleInvocations() {
        final List<Integer> integers1 = asList(1, 2, 3);
        dao.doSomething(integers1);

        final List<Integer> integers2 = asList(4, 5);
        dao.doSomething(integers2);

        new Verifications() {
            {
                List<List<Integer>> captures = new ArrayList<>();
                dao.doSomething(withCapture(captures));
                assertEquals(integers1, captures.get(0));
                assertEquals(integers2, captures.get(1));
            }
        };
    }
}
