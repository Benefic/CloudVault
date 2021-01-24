package ru.abenefic.cloudvault.server.support;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.Serializable;

public class Database {
    private static final Logger LOG = LogManager.getLogger(Database.class);

    private static SessionFactory sessionFactory;

    private Database() {
    }

    public static SessionFactory instance() throws Exception {
        if (sessionFactory == null) {
            setUp();
        }
        return sessionFactory;
    }

    protected static void setUp() throws Exception {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
            LOG.error("Database error", e);
        }
    }

    public static Serializable saveInTransaction(Object obj) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Serializable id = session.save(obj);
        session.getTransaction().commit();
        return id;
    }


}
