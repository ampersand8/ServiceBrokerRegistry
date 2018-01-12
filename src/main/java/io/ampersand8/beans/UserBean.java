package io.ampersand8.beans;

import io.ampersand8.model.User;
import io.ampersand8.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "userBean")
@RequestScoped
public class UserBean implements Serializable {
    private List<User> usersList;

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

    public void deleteUser(String id) {
        if (loginSessionBean.isAdmin()) {
            Session session = HibernateUtil.getHibernateSession();
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                String hql = "delete User where id = :id";
                Query q = session.createQuery(hql).setParameter("id", id);
                q.executeUpdate();
                transaction.commit();
                messageBean.send("User successfully deleted", "success");
            } catch (PersistenceException e) {
                if (transaction != null) transaction.rollback();
                messageBean.send("Something went wrong, user deletion failed!", "fail");
            }
        } else {
            messageBean.send("Only admin can delete other users", "fail");
        }
    }

    public List<User> getUsersList() {
        User user = loginSessionBean.getLoggedInUser();
        if (user != null  && user.isAdmin()) {
            Session session = HibernateUtil.getHibernateSession();
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<User> query = builder.createQuery(User.class);
                Root<User> root = query.from(User.class);
                query.select(root);
                Query<User> q = session.createQuery(query);
                List<User> users = q.getResultList();
                transaction.commit();
                return users;
            } catch (NoResultException e) {
                if (transaction != null) transaction.rollback();
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
    }
}
