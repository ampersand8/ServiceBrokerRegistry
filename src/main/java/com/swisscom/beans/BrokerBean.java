package com.swisscom.beans;

import com.swisscom.model.Broker;
import com.swisscom.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.persistence.PersistenceException;
import java.io.Serializable;

@ManagedBean(name = "brokerBean")
@SessionScoped
public class BrokerBean implements Serializable {
    private String id;
    private String name;
    private String url;
    private String username;
    private String password;

    @ManagedProperty(value="#{userBean}")
    private UserBean userBean;

    private static final String PAGESUCCESSREGISTER = "brokers?faces-redirect=true&success=brokerRegister";
    private static final String PAGEFAILEDREGISTER= "brokers?failed=brokerRegister";

    public UserBean getUserBean() {
        return this.userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public String getId() {
        return id;
    }

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
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        Broker broker = new Broker(userBean.getId(), name, url, username, password);
        try {
            transaction = session.beginTransaction();
            session.save(broker);
            transaction.commit();
        } catch (PersistenceException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return PAGEFAILEDREGISTER;
        } finally {
            session.close();
        }
        return PAGESUCCESSREGISTER;
    }
}
