package com.swisscom.beans;

import com.swisscom.model.User;
import com.swisscom.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;

@ManagedBean(name = "loginSessionBean")
@SessionScoped
public class LoginSessionBean implements Serializable {
    private String id;
    private String username;
    private boolean admin = false;
    private boolean anonymous = true;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean getAnonymous() {
        return this.anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public boolean isLoggedIn() {
        return !this.anonymous;
    }

    public User getLoggedInUser() {
        Session session = HibernateUtil.getHibernateSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> query = builder.createQuery(User.class);
            Root<User> root = query.from(User.class);
            query.select(root).where(builder.equal(root.get("id"), this.id));
            Query<User> q = session.createQuery(query);
            User user = q.getSingleResult();
            transaction.commit();
            return user;
        } catch (NoResultException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return null;
        }
    }
}
