/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.injection.full;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mockit.internal.injection.InjectionPoint;
import mockit.internal.injection.InjectionProvider;
import mockit.internal.injection.InjectionState;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Detects and resolves dependencies belonging to the <code>javax.persistence</code> API, namely
 * <code>EntityManagerFactory</code> and <code>EntityManager</code>.
 */
final class JPADependencies {
    static boolean isApplicable(@Nonnull Class<?> dependencyType) {
        return dependencyType == EntityManager.class || dependencyType == EntityManagerFactory.class;
    }

    @Nonnull
    private final InjectionState injectionState;
    @Nullable
    private String defaultPersistenceUnitName;

    JPADependencies(@Nonnull InjectionState injectionState) {
        this.injectionState = injectionState;
    }

    @Nullable
    InjectionPoint getInjectionPointIfAvailable(@Nonnull Annotation jpaAnnotation) {
        Class<? extends Annotation> annotationType = jpaAnnotation.annotationType();
        Class<?> jpaClass;
        String unitName;

        if (annotationType == PersistenceUnit.class) {
            jpaClass = EntityManagerFactory.class;
            unitName = ((PersistenceUnit) jpaAnnotation).unitName();
        } else if (annotationType == PersistenceContext.class) {
            jpaClass = EntityManager.class;
            unitName = ((PersistenceContext) jpaAnnotation).unitName();
        } else {
            return null;
        }

        if (unitName.isEmpty()) {
            unitName = discoverNameOfDefaultPersistenceUnit();
        }

        return new InjectionPoint(jpaClass, unitName, true);
    }

    @Nonnull
    private String discoverNameOfDefaultPersistenceUnit() {
        if (defaultPersistenceUnitName != null) {
            return defaultPersistenceUnitName;
        }

        defaultPersistenceUnitName = "<unknown>";
        InputStream xmlFile = getClass().getResourceAsStream("/META-INF/persistence.xml");

        if (xmlFile != null) {
            try {
                SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                parser.parse(xmlFile, new DefaultHandler() {
                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes attributes) {
                        if ("persistence-unit".equals(qName)) {
                            defaultPersistenceUnitName = attributes.getValue("name");
                        }
                    }
                });
                xmlFile.close();
            } catch (ParserConfigurationException | SAXException | IOException ignore) {
            }
        }

        return defaultPersistenceUnitName;
    }

    @Nullable
    Object createAndRegisterDependency(@Nonnull Class<?> dependencyType, @Nonnull InjectionPoint dependencyKey,
            @Nullable InjectionProvider injectionProvider) {
        if (injectionProvider != null) {
            if (dependencyType == EntityManagerFactory.class
                    && injectionProvider.hasAnnotation(PersistenceUnit.class)) {
                InjectionPoint injectionPoint = createFactoryInjectionPoint(dependencyKey);
                return createAndRegisterEntityManagerFactory(injectionPoint);
            }

            if (dependencyType == EntityManager.class && injectionProvider.hasAnnotation(PersistenceContext.class)) {
                return createAndRegisterEntityManager(dependencyKey);
            }
        }

        return null;
    }

    @Nonnull
    private InjectionPoint createFactoryInjectionPoint(@Nonnull InjectionPoint injectionPoint) {
        String persistenceUnitName = getNameOfPersistentUnit(injectionPoint.name);
        return new InjectionPoint(EntityManagerFactory.class, persistenceUnitName, injectionPoint.qualified);
    }

    @Nonnull
    private String getNameOfPersistentUnit(@Nullable String injectionPointName) {
        return injectionPointName != null && !injectionPointName.isEmpty() ? injectionPointName
                : discoverNameOfDefaultPersistenceUnit();
    }

    @Nonnull
    private static EntityManagerFactory createAndRegisterEntityManagerFactory(@Nonnull InjectionPoint injectionPoint) {
        String persistenceUnitName = injectionPoint.name;
        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
        InjectionState.saveGlobalDependency(injectionPoint, emFactory);
        return emFactory;
    }

    @Nonnull
    private EntityManager createAndRegisterEntityManager(@Nonnull InjectionPoint injectionPoint) {
        InjectionPoint emFactoryKey = createFactoryInjectionPoint(injectionPoint);
        EntityManagerFactory emFactory = InjectionState.getGlobalDependency(emFactoryKey);

        if (emFactory == null) {
            emFactory = createAndRegisterEntityManagerFactory(emFactoryKey);
        }

        EntityManager entityManager = emFactory.createEntityManager();
        injectionState.saveInstantiatedDependency(injectionPoint, entityManager);
        return entityManager;
    }
}
