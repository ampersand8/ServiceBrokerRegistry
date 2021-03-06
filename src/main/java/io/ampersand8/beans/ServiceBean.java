package io.ampersand8.beans;

import io.ampersand8.model.Plan;
import io.ampersand8.model.Service;
import io.ampersand8.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "serviceBean")
@RequestScoped
public class ServiceBean implements Serializable {
    private Service currentService;

    public List<Service> getList() {
        Session session = HibernateUtil.getHibernateSession();
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
            if (transaction != null) transaction.rollback();
            return new ArrayList<>();
        }
    }

    public List<Plan> getPlanList() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String requestServiceId = params.get("requestServiceId");
        if (this.currentService != null && this.currentService.getId().equals(requestServiceId)) {
            return this.currentService.getPlans();
        } else {
            Service service = getService(requestServiceId);
            if (service != null) {
                return service.getPlans();
            } else {
                return null;
            }
        }
    }

    public Service getCurrentService() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String requestServiceId = params.get("requestServiceId");
        if (this.currentService != null && this.currentService.getId().equals(requestServiceId)) {
            return this.currentService;
        } else {
            return getService(requestServiceId);
        }
    }

    private Service getService(String id) {
        Session session = HibernateUtil.getHibernateSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Service> query = builder.createQuery(Service.class);
            Root<Service> root = query.from(Service.class);
            query.select(root).where(builder.equal(root.get("id"), id));
            Query<Service> q = session.createQuery(query);
            Service service = q.getSingleResult();
            transaction.commit();
            this.currentService = service;

            return service;
        } catch (NoResultException e) {
            if (transaction != null) transaction.rollback();
            return null;
        }
    }
}
