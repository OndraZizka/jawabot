package org.jboss.weld.environment.se.jpa;
//package de.laliluna.transactions;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
//import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
   The fake entity manager is not the real instance of the entity manager but it is a delegate. It will fetch the current entity manager from the EntityManagerStore and will delegate to this entity manager.
   Don’t forget that your IDE can generate delegate methods. Don’t write the code!

 * @author Sebastian Hennebrueder
 */
@ApplicationScoped
public class EntityManagerDelegate implements EntityManager{

	@Inject
	private EntityManagerStore entityManagerStore;

	public void persist(Object entity) {
		entityManagerStore.get().persist(entity);
	}

	public <T> T merge(T entity) {
		return entityManagerStore.get().merge(entity);
	}

	public void remove(Object entity) {
		entityManagerStore.get().remove(entity);
	}

	public <T> T find(Class<T> entityClass, Object primaryKey) {
		return entityManagerStore.get().find(entityClass, primaryKey);
	}

   @Override
   public void clear() {
      entityManagerStore.get().clear();
   }

   @Override
   public void close() {
      entityManagerStore.get().close();
   }

   @Override
   public boolean contains(Object entity) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Query createNamedQuery(String name) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Query createNativeQuery(String sqlString) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Query createNativeQuery(String sqlString, Class resultClass) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Query createNativeQuery(String sqlString, String resultSetMapping) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Query createQuery(String qlString) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void detach(Object entity) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
      return this.entityManagerStore.get().find(entityClass, primaryKey, lockMode);
   }

   @Override
   public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void flush() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public CriteriaBuilder getCriteriaBuilder() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Object getDelegate() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public EntityManagerFactory getEntityManagerFactory() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public FlushModeType getFlushMode() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public LockModeType getLockMode(Object entity) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Metamodel getMetamodel() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Map<String, Object> getProperties() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public <T> T getReference(Class<T> entityClass, Object primaryKey) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public EntityTransaction getTransaction() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public boolean isOpen() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void joinTransaction() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void lock(Object entity, LockModeType lockMode) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void refresh(Object entity) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void refresh(Object entity, Map<String, Object> properties) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void refresh(Object entity, LockModeType lockMode) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void setFlushMode(FlushModeType flushMode) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void setProperty(String propertyName, Object value) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public <T> T unwrap(Class<T> cls) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   

}// class

