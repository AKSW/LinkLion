package de.linkinglod.service;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
public class InitSessionFactory {
   /** The single instance of hibernate SessionFactory */
   private static org.hibernate.SessionFactory sessionFactory;
   private InitSessionFactory() {
   }
   static {
      final AnnotationConfiguration cfg = new
      AnnotationConfiguration();
      cfg.configure("../hibernate.cfg.xml");
      sessionFactory = cfg.buildSessionFactory();
   }
   public static SessionFactory getInstance() {
      return sessionFactory;
   }
}