package com.swisscom.beans;

import com.swisscom.model.User;
import com.swisscom.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.persistence.PersistenceException;
import java.io.Serializable;

@ManagedBean(name = "profileBean")
@RequestScoped
public class ProfileBean implements Serializable {
    private String newPassword;
    private String oldPassword;
    private User currentUser;

    @ManagedProperty(value="#{loginSessionBean}")
    private LoginSessionBean loginSessionBean;

    @ManagedProperty(value="#{messageBean}")
    private MessageBean messageBean;

    public LoginSessionBean getLoginSessionBean() {
        return loginSessionBean;
    }

    public void setLoginSessionBean(LoginSessionBean loginSessionBean) {
        this.loginSessionBean = loginSessionBean;
    }

    public MessageBean getMessageBean() {
        return messageBean;
    }

    public void setMessageBean(MessageBean messageBean) {
        this.messageBean = messageBean;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void changePassword() {
        User oldUser = loginSessionBean.getLoggedInUser();
        if (oldUser != null && oldUser.getPassword().equals(oldPassword)) {
            Session session = HibernateUtil.getHibernateSession();
            Transaction transaction = null;
            oldUser.setPassword(newPassword);
            try {
                transaction = session.beginTransaction();
                session.update(oldUser);
                transaction.commit();
                messageBean.send("Successfully changed password", "success");
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                messageBean.send("Something went wrong, password change failed!" , "fail");
                e.printStackTrace();
            }
        } else {
            messageBean.send("Password incorrect", "fail");
        }
    }


    public String deleteAccount() {
        this.currentUser = loginSessionBean.getLoggedInUser();
        if (this.currentUser != null && this.currentUser.getPassword().equals(this.oldPassword)) {
            Session session = HibernateUtil.getHibernateSession();
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.remove(currentUser);
                transaction.commit();
                loginSessionBean.setAnonymous(true);
            } catch (PersistenceException e) {
                if (transaction != null) transaction.rollback();
                messageBean.send("Something went wrong, account deletion failed!", "fail");
                return null;
            }
            return "index.xhtml?faces-redirect=true";
        } else {
            messageBean.send("Password did not match",  "fail");
            return null;
        }
    }
}
