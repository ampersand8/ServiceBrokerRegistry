package com.swisscom.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    /*private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }*/

    private final static SessionFactory sf = new Configuration()
            .configure().buildSessionFactory();
    private volatile static Session session = sf.openSession();

    private HibernateUtil() {
    }

    public static Session getHibernateSession() {
        if (session == null) {
            session = (Session) new HibernateUtil();
        }
        return session;
/*
        final SessionFactory sf = new Configuration()
                .configure().buildSessionFactory();

        // factory = new Configuration().configure().buildSessionFactory();
        final Session session = sf.openSession();
        return session;*/
    }
}