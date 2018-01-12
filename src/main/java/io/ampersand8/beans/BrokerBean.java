package io.ampersand8.beans;

import io.ampersand8.model.Broker;
import io.ampersand8.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "brokerBean")
@RequestScoped
public class BrokerBean implements Serializable {

    final static Logger logger = Logger.getLogger(BrokerBean.class);
    private String name;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Broker> getList() {

        Session session = HibernateUtil.getHibernateSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Broker> query = builder.createQuery(Broker.class);
            Root<Broker> root = query.from(Broker.class);
            query.select(root);
            Query<Broker> q = session.createQuery(query);
            List<Broker> brokers = q.getResultList();
            transaction.commit();
            return brokers;
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }
}