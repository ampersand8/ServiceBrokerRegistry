package com.swisscom.beans;

import com.swisscom.model.Service;
import com.swisscom.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "serviceBean")
@SessionScoped
public class ServiceBean implements Serializable {
    public List<Service> getList() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Service> query = builder.createQuery(Service.class);
            Root<Service> root = query.from(Service.class);
            query.select(root);
            Query<Service> q = session.createQuery(query);
            List<Service> services = q.getResultList();
            transaction.commit();
            return services;
        } catch (NoResultException e) {
            return new ArrayList<>();
        } finally {
            session.close();
        }
    }
}
