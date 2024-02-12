package mockit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.Cache;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.SynchronizationType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;

/**
 * The Class TestedClassWithFullStandardDITest.
 */
@FixMethodOrder(NAME_ASCENDING)
public final class TestedClassWithFullStandardDITest {

    /**
     * The Class TestedClass.
     */
    public static class TestedClass {

        /** The dependency to be mocked. */
        @Inject
        private Runnable dependencyToBeMocked;

        /** The dependency 2. */
        @Inject
        private FirstLevelDependency dependency2;

        /** The dependency 3. */
        @Resource
        private FirstLevelDependency dependency3;

        /** The common dependency. */
        @Inject
        private CommonDependency commonDependency;

        /** The text. */
        String text;

        /** The initialized. */
        boolean initialized;

        /** The destroyed. */
        static boolean destroyed;

        /**
         * Initialize.
         */
        @PostConstruct
        void initialize() {
            assertNotNull(dependency3);
            initialized = true;
        }

        /**
         * Destroy.
         */
        @PreDestroy
        void destroy() {
            assertTrue("TestedClass not initialized", initialized);
            destroyed = true;
        }
    }

    /**
     * The Class AnotherTestedClass.
     */
    static final class AnotherTestedClass {

        /** The em. */
        @PersistenceContext
        EntityManager em;

        /** The session. */
        @Inject
        HttpSession session;

        /** The application context. */
        @Inject
        ServletContext applicationContext;
    }

    /**
     * The Class FirstLevelDependency.
     */
    public static class FirstLevelDependency {

        /** The dependency. */
        @EJB
        private SecondLevelDependency dependency;

        /** The static dependency. */
        @Inject
        private static SecondLevelDependency staticDependency;

        /** The common dependency. */
        @Inject
        private CommonDependency commonDependency;

        /** The dependency to be mocked. */
        @Resource
        private static Runnable dependencyToBeMocked;

        /** The em. */
        @PersistenceContext
        private EntityManager em;
    }

    /**
     * The Class SecondLevelDependency.
     */
    public static class SecondLevelDependency {

        /** The common dependency. */
        @Inject
        CommonDependency commonDependency;

        /** The em. */
        @PersistenceContext
        private EntityManager em;

        /** The servlet context. */
        @Inject
        ServletContext servletContext;

        /** The http session. */
        @Inject
        HttpSession httpSession;

        /** The initialized. */
        boolean initialized;

        /** The terminated. */
        static boolean terminated;

        /**
         * Initialize.
         */
        @PostConstruct
        void initialize() {
            initialized = true;
        }

        /**
         * Terminate.
         */
        @PreDestroy
        void terminate() {
            terminated = true;
        }
    }

    /**
     * The Class CommonDependency.
     */
    public static class CommonDependency {

        /** The em factory. */
        @PersistenceUnit(unitName = "test")
        private EntityManagerFactory emFactory;

        /** The em. */
        @PersistenceContext(unitName = "test")
        private EntityManager em;
    }

    /** The tested. */
    @Tested(fullyInitialized = true)
    TestedClass tested;

    /** The tested 2. */
    @Tested(fullyInitialized = true)
    AnotherTestedClass tested2;

    /** The mocked dependency. */
    @Injectable
    Runnable mockedDependency;

    /** The persistence xml file. */
    static File persistenceXmlFile;

    /** The named EM factory. */
    static EntityManagerFactory namedEMFactory;

    /** The named EM. */
    static EntityManager namedEM;

    /** The default EM factory. */
    static EntityManagerFactory defaultEMFactory;

    /** The default EM. */
    static EntityManager defaultEM;

    /**
     * Sets the up persistence.
     *
     * @throws Exception
     *             the exception
     */
    @BeforeClass
    @SuppressWarnings("rawtypes")
    public static void setUpPersistence() throws Exception {
        final class FakeEntityManager implements EntityManager {
            @Override
            public void persist(Object entity) {
            }

            @Override
            public <T> T merge(T entity) {
                return null;
            }

            @Override
            public void remove(Object entity) {
            }

            @Override
            public <T> T find(Class<T> entityClass, Object primaryKey) {
                return null;
            }

            @Override
            public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
                return null;
            }

            @Override
            public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
                return null;
            }

            @Override
            public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode,
                    Map<String, Object> properties) {
                return null;
            }

            @Override
            public <T> T getReference(Class<T> entityClass, Object primaryKey) {
                return null;
            }

            @Override
            public void flush() {
            }

            @Override
            public void setFlushMode(FlushModeType flushMode) {
            }

            @Override
            public FlushModeType getFlushMode() {
                return null;
            }

            @Override
            public void lock(Object entity, LockModeType lockMode) {
            }

            @Override
            public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
            }

            @Override
            public void refresh(Object entity) {
            }

            @Override
            public void refresh(Object entity, Map<String, Object> properties) {
            }

            @Override
            public void refresh(Object entity, LockModeType lockMode) {
            }

            @Override
            public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
            }

            @Override
            public void clear() {
            }

            @Override
            public void detach(Object entity) {
            }

            @Override
            public boolean contains(Object entity) {
                return false;
            }

            @Override
            public LockModeType getLockMode(Object entity) {
                return null;
            }

            @Override
            public void setProperty(String propertyName, Object value) {
            }

            @Override
            public Map<String, Object> getProperties() {
                return null;
            }

            @Override
            public Query createQuery(String qlString) {
                return null;
            }

            @Override
            public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
                return null;
            }

            @Override
            public Query createQuery(CriteriaUpdate updateQuery) {
                return null;
            }

            @Override
            public Query createQuery(CriteriaDelete deleteQuery) {
                return null;
            }

            @Override
            public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
                return null;
            }

            @Override
            public Query createNamedQuery(String name) {
                return null;
            }

            @Override
            public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
                return null;
            }

            @Override
            public Query createNativeQuery(String sqlString) {
                return null;
            }

            @Override
            public Query createNativeQuery(String sqlString, Class resultClass) {
                return null;
            }

            @Override
            public Query createNativeQuery(String sqlString, String resultSetMapping) {
                return null;
            }

            @Override
            public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
                return null;
            }

            @Override
            public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
                return null;
            }

            @Override
            public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
                return null;
            }

            @Override
            public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
                return null;
            }

            @Override
            public void joinTransaction() {
            }

            @Override
            public boolean isJoinedToTransaction() {
                return false;
            }

            @Override
            public <T> T unwrap(Class<T> cls) {
                return null;
            }

            @Override
            public Object getDelegate() {
                return null;
            }

            @Override
            public void close() {
            }

            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public EntityTransaction getTransaction() {
                return null;
            }

            @Override
            public EntityManagerFactory getEntityManagerFactory() {
                return null;
            }

            @Override
            public CriteriaBuilder getCriteriaBuilder() {
                return null;
            }

            @Override
            public Metamodel getMetamodel() {
                return null;
            }

            @Override
            public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
                return null;
            }

            @Override
            public EntityGraph<?> createEntityGraph(String graphName) {
                return null;
            }

            @Override
            public EntityGraph<?> getEntityGraph(String graphName) {
                return null;
            }

            @Override
            public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
                return null;
            }
        }
        namedEM = new FakeEntityManager();
        defaultEM = new FakeEntityManager();

        final class FakeEntityManagerFactory implements EntityManagerFactory {
            final EntityManager em;

            FakeEntityManagerFactory(EntityManager em) {
                this.em = em;
            }

            @Override
            public EntityManager createEntityManager() {
                return em;
            }

            @Override
            public EntityManager createEntityManager(Map map) {
                return null;
            }

            @Override
            public EntityManager createEntityManager(SynchronizationType synchronizationType) {
                return null;
            }

            @Override
            public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
                return null;
            }

            @Override
            public CriteriaBuilder getCriteriaBuilder() {
                return null;
            }

            @Override
            public Metamodel getMetamodel() {
                return null;
            }

            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public void close() {
            }

            @Override
            public Map<String, Object> getProperties() {
                return null;
            }

            @Override
            public Cache getCache() {
                return null;
            }

            @Override
            public PersistenceUnitUtil getPersistenceUnitUtil() {
                return null;
            }

            @Override
            public void addNamedQuery(String name, Query query) {
            }

            @Override
            public <T> T unwrap(Class<T> cls) {
                return null;
            }

            @Override
            public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
            }
        }
        namedEMFactory = new FakeEntityManagerFactory(namedEM);
        defaultEMFactory = new FakeEntityManagerFactory(defaultEM);

        new MockUp<Persistence>() {
            @Mock
            EntityManagerFactory createEntityManagerFactory(String persistenceUnitName) {
                if ("test".equals(persistenceUnitName)) {
                    return namedEMFactory;
                }

                if ("default".equals(persistenceUnitName)) {
                    return defaultEMFactory;
                }

                fail("Unexpected persistence unit");
                return null;
            }
        };

        createTemporaryPersistenceXmlFileWithDefaultPersistenceUnit();
    }

    /**
     * Creates the temporary persistence xml file with default persistence unit.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    static void createTemporaryPersistenceXmlFileWithDefaultPersistenceUnit() throws IOException {
        String rootOfClasspath = TestedClass.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        File tempFolder = new File(rootOfClasspath + "META-INF");
        if (tempFolder.mkdir()) {
            tempFolder.deleteOnExit();
        }

        persistenceXmlFile = new File(tempFolder, "persistence.xml");

        Writer xmlWriter = new FileWriter(persistenceXmlFile, StandardCharsets.UTF_8);
        xmlWriter.write("<persistence><persistence-unit name='default'/></persistence>");
        xmlWriter.close();
    }

    /**
     * Delete default persistence xml file.
     */
    @AfterClass
    public static void deleteDefaultPersistenceXmlFile() {
        persistenceXmlFile.delete();
        persistenceXmlFile.getParentFile().delete();
    }

    /**
     * Use fully initialized tested object.
     */
    @Test
    public void useFullyInitializedTestedObject() {
        // First level dependencies:
        assertSame(mockedDependency, tested.dependencyToBeMocked);
        assertNotNull(tested.dependency2);
        assertSame(tested.dependency2, tested.dependency3);
        assertNotNull(tested.commonDependency);
        assertNull(tested.text);

        // Second level dependencies:
        assertNotNull(tested.dependency2.dependency);
        assertSame(FirstLevelDependency.staticDependency, tested.dependency2.dependency);
        assertSame(tested.dependency3.dependency, tested.dependency2.dependency);
        assertSame(tested.commonDependency, tested.dependency2.commonDependency);
        assertSame(tested.commonDependency, tested.dependency3.commonDependency);
        assertSame(mockedDependency, FirstLevelDependency.dependencyToBeMocked);
        assertSame(mockedDependency, FirstLevelDependency.dependencyToBeMocked);
        assertSame(defaultEM, tested.dependency2.em);
        assertSame(tested.dependency2.em, tested.dependency3.em);
        assertSame(namedEMFactory, tested.commonDependency.emFactory);
        assertSame(namedEM, tested.commonDependency.em);
        assertNotSame(tested.dependency2.em, tested.commonDependency.em);
        assertSame(tested2.em, tested.dependency2.em);

        // Third level dependencies:
        assertSame(tested.commonDependency, tested.dependency2.dependency.commonDependency);
        assertSame(tested.dependency2.em, tested.dependency2.dependency.em);

        // Lifecycle methods:
        assertTrue(tested.initialized);
        assertTrue(tested.dependency2.dependency.initialized);
    }

    /**
     * Use fully initialized tested object again.
     */
    @Test
    public void useFullyInitializedTestedObjectAgain() {
        assertNull(tested.text);
    }

    /**
     * Verify emulated http session.
     */
    @Test
    public void verifyEmulatedHttpSession() {
        HttpSession session = tested2.session;
        assertFalse(session.isNew());
        assertFalse(session.getId().isEmpty());
        assertTrue(session.getCreationTime() > 0);
        assertTrue(session.getLastAccessedTime() > 0);
        assertFalse(session.getAttributeNames().hasMoreElements());

        session.setMaxInactiveInterval(600);
        assertEquals(600, session.getMaxInactiveInterval());

        session.setAttribute("test", 123);
        assertEquals(123, session.getAttribute("test"));
        assertEquals("test", session.getAttributeNames().nextElement());

        session.removeAttribute("test");
        assertNull(session.getAttribute("test"));

        session.setAttribute("test2", "abc");
        session.invalidate();

        try {
            session.isNew();
            fail();
        } catch (IllegalStateException invalidatedSession) {
            /* ok */ }
        try {
            session.getCreationTime();
            fail();
        } catch (IllegalStateException invalidatedSession) {
            /* ok */ }
        try {
            session.getLastAccessedTime();
            fail();
        } catch (IllegalStateException invalidatedSession) {
            /* ok */ }
        try {
            session.getAttributeNames();
            fail();
        } catch (IllegalStateException invalidatedSession) {
            /* ok */ }
        try {
            session.getAttribute("test2");
            fail();
        } catch (IllegalStateException invalidatedSession) {
            /* ok */ }
        try {
            session.setAttribute("x", "");
            fail();
        } catch (IllegalStateException invalidatedSession) {
            /* ok */ }
        try {
            session.removeAttribute("x");
            fail();
        } catch (IllegalStateException invalidatedSession) {
            /* ok */ }
        try {
            session.invalidate();
            fail();
        } catch (IllegalStateException invalidatedSession) {
            /* ok */ }

        assertSame(tested2.applicationContext, session.getServletContext());
        assertSame(session, tested.dependency3.dependency.httpSession);
    }

    /**
     * Verify emulated servlet context.
     */
    @Test
    public void verifyEmulatedServletContext() {
        ServletContext ctx = tested2.applicationContext;

        assertFalse(ctx.getAttributeNames().hasMoreElements());

        ctx.setInitParameter("test", "abc");
        assertEquals("abc", ctx.getInitParameter("test"));
        assertEquals("test", ctx.getInitParameterNames().nextElement());

        ctx.setAttribute("test", 123);
        assertEquals(123, ctx.getAttribute("test"));
        assertEquals("test", ctx.getAttributeNames().nextElement());

        ctx.removeAttribute("test");
        assertNull(ctx.getAttribute("test"));

        assertSame(ctx, tested.dependency2.dependency.servletContext);
    }

    /**
     * Verify that tested fields were cleared and pre destroy methods were executed.
     */
    @After
    public void verifyThatTestedFieldsWereClearedAndPreDestroyMethodsWereExecuted() {
        assertNull(tested);
        assertNull(tested2);
        assertTrue(TestedClass.destroyed);
        assertTrue(SecondLevelDependency.terminated);
    }

    /**
     * Clear entity managers.
     */
    @After
    public void clearEntityManagers() {
        namedEM = null;
        defaultEM = null;
    }
}
