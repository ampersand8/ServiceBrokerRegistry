package com.swisscom.beans;

import com.swisscom.model.User;
import com.swisscom.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
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

@ManagedBean(name = "loginBean")
@RequestScoped
public class LoginBean implements Serializable {
    private String id;
    private String username;
    private String password;

    private static final String STARTPAGESUCCESSFULLOGIN = "brokers?faces-redirect=true";
    private static final String STARTPAGESUCCESSFULLOGOUT = "index.xhtml";

    @ManagedProperty(value="#{messageBean}")
    private MessageBean messageBean;

    @ManagedProperty(value="#{loginSessionBean}")
    private LoginSessionBean loginSessionBean;

    public MessageBean getMessageBean() {
        return messageBean;
    }

    public void setMessageBean(MessageBean messageBean) {
        this.messageBean = messageBean;
    }

    public LoginSessionBean getLoginSessionBean() {
        return loginSessionBean;
    }

    public void setLoginSessionBean(LoginSessionBean loginSessionBean) {
        this.loginSessionBean = loginSessionBean;
    }

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

    public String add() {
        Session session = HibernateUtil.getHibernateSession();
        Transaction transaction = null;
        User user = new User(this.username, this.password);
        try {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            loginSessionBean.setAnonymous(false);
            loginSessionBean.setAdmin(false);
            loginSessionBean.setUsername(this.username);
            loginSessionBean.setId(user.getId());
        } catch (PersistenceException e) {
            if (transaction != null) transaction.rollback();
            if (e.getCause() instanceof ConstraintViolationException) {
                messageBean.send("Username already taken", "fail");
            } else {
                messageBean.send("Registration failed", "fail");
            }
            return null;
        }
        return STARTPAGESUCCESSFULLOGIN;
    }

    public String login() {
        Session session = HibernateUtil.getHibernateSession();
        Transaction transaction = null;
        try {
            session.setDefaultReadOnly(true);
            transaction = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> query = builder.createQuery(User.class);
            Root<User> root = query.from(User.class);
            query.select(root).where(builder.equal(root.get("username"), this.username));
            Query<User> q = session.createQuery(query);
            User user = q.getSingleResult();
            transaction.commit();
            if (password.equals(user.getPassword())) {
                loginSessionBean.setAnonymous(false);
                loginSessionBean.setId(user.getId());
                loginSessionBean.setAdmin(user.isAdmin());
                loginSessionBean.setUsername(this.username);
                return STARTPAGESUCCESSFULLOGIN;
            } else {
                messageBean.send("Username or password wrong", "fail");
                return null;
            }
        } catch (NoResultException e) {
            if (transaction != null) transaction.rollback();
            messageBean.send("Username or password wrong", "fail");
            return null;
        }
    }

    public void logout() {
        try {
            loginSessionBean.setAnonymous(true);
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext ec = context.getExternalContext();
            ec.redirect(STARTPAGESUCCESSFULLOGOUT);
            final HttpServletRequest request = (HttpServletRequest) ec.getRequest();
            request.getSession(false).invalidate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
