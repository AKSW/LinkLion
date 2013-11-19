package de.linkinglod.service;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class InitSessionFactory {
	   /** The single instance of hibernate SessionFactory */
	   private static SessionFactory sessionFactory;
	   
	   private InitSessionFactory() {
	   }
	   
	   static {
	      final Configuration configuration = new Configuration();
	      configuration.configure("/hibernate.cfg.xml");
	      sessionFactory = configuration.buildSessionFactory();
	   }
	   
	   public static SessionFactory getInstance() {
	      return sessionFactory;
	   }
	}