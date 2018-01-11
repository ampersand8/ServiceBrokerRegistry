package com.swisscom.beans;

import com.swisscom.model.User;
import com.swisscom.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.faces.bean.ManagedBean;
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
    private String password;
    private boolean admin = false;
    private boolean anonymous = true;

    private static final String STARTPAGESUCCESSFULLOGIN = "brokers?faces-redirect=true";
    private static final String PAGEFAILEDREGISTER = "/?failed=register";
    private static final String PAGEFAILEDLOGIN = "/?failed=login";
    private static final String STARTPAGESUCCESSFULLOGOUT = "/";

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean getAnonymous() {
        return this.anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String add() {
        Session session = HibernateUtil.getHibernateSession();
        Transaction transaction = null;
        User user = new User(username, password);
        try {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            this.anonymous = false;
            this.admin = false;
        } catch (PersistenceException e) {
            if (transaction != null) transaction.rollback();
            return PAGEFAILEDREGISTER;
        }
        this.id = user.getId();
        return STARTPAGESUCCESSFULLOGIN;
    }

    public String login() {
        Session session = HibernateUtil.getHibernateSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> query = builder.createQuery(User.class);
            Root<User> root = query.from(User.class);
            query.select(root).where(builder.equal(root.get("username"), username));
            Query<User> q = session.createQuery(query);
            User user = q.getSingleResult();
            transaction.commit();
            if (password.equals(user.getPassword())) {
                this.anonymous = false;
                this.id = user.getId();
                this.admin = user.isAdmin();
                return STARTPAGESUCCESSFULLOGIN;
            } else {
                return PAGEFAILEDLOGIN;
            }
        } catch (NoResultException e) {
            return PAGEFAILEDLOGIN;
        }
    }

    public void logout() {
        try {
            this.anonymous = true;
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext ec = context.getExternalContext();
            ec.redirect(STARTPAGESUCCESSFULLOGOUT);
            final HttpServletRequest request = (HttpServletRequest) ec.getRequest();
            request.getSession(false).invalidate();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            e.printStackTrace();
            return null;
        }
    }
}
