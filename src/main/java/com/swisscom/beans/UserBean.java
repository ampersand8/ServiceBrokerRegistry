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
import java.util.List;

@ManagedBean(name = "userBean")
@SessionScoped
public class UserBean implements Serializable {
    private String id;
    private String username;
    private String password;
    private boolean admin = false;
    private String newPassword;
    private String oldPassword;
    private boolean anonymous = true;
    private List<User> usersList;

    private static final String STARTPAGESUCCESSFULLOGIN = "brokers?faces-redirect=true";
    private static final String PAGEFAILEDREGISTER = "/?failed=register";
    private static final String PAGEFAILEDLOGIN = "/?failed=login";
    private static final String STARTPAGESUCCESSFULLOGOUT = "/";

    private User currentUser;
    private String message;
    private String messageSuccess;

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

    public void setAdmin(boolean admin) {
        this.admin = admin;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageSuccess() {
        return messageSuccess;
    }

    public void setMessageSuccess(String messageSuccess) {
        this.messageSuccess = messageSuccess;
    }

    public boolean getAnonymous() {
        return this.anonymous;
    }

    public String add() {
        System.out.println("entered add()");
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

    public void changePassword() {
        User oldUser = getUser(getId());
        if (oldUser != null && oldUser.getPassword().equals(oldPassword)) {
            Session session = HibernateUtil.getHibernateSession();
            Transaction transaction = null;
            oldUser.setPassword(newPassword);
            try {
                transaction = session.beginTransaction();
                session.update(oldUser);
                transaction.commit();
                this.message = "Successfully changed password";
                this.messageSuccess = "success";
            } catch (Exception e) {
                this.message = "Something went wrong, password change failed!";
                this.messageSuccess = "fail";
                e.printStackTrace();
            }
        } else {
            this.message = "Password incorrect";
            this.messageSuccess = "fail";
        }
    }

    public void deleteUser(String id) {
        if (isAdmin()) {
            Session session = HibernateUtil.getHibernateSession();
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                String hql = "delete User where id = :id";
                Query q = session.createQuery(hql).setParameter("id", id);
                q.executeUpdate();
                transaction.commit();
                this.message = "User successfully deleted";
                this.messageSuccess = "success";
            } catch (PersistenceException e) {
                if (transaction != null) transaction.rollback();
                this.message = "Something went wrong, user deletion failed!";
                this.messageSuccess = "fail";
            }
        } else {
            this.message = "Only admin can delete other users";
            this.messageSuccess = "fail";
        }
    }

    public String deleteAccount() {
        if (currentUser == null || !currentUser.getId().equals(getId())) {
            currentUser = getUser(getId());
        }
        if (currentUser != null && currentUser.getPassword().equals(this.oldPassword)) {
            Session session = HibernateUtil.getHibernateSession();
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.remove(currentUser);
                transaction.commit();
                this.anonymous = true;
            } catch (PersistenceException e) {
                if (transaction != null) transaction.rollback();
                this.message = "Something went wrong, account deletion failed!";
                this.messageSuccess = "fail";
                return null;
            }
            return "index.xhtml?faces-redirect=true";
        } else {
            this.message = "Password did not match";
            this.messageSuccess = "fail";
            return null;
        }
    }

    public List<User> getUsersList() {
        User user = getUser(getId());
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
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
    }

    private User getUser(String id) {
        Session session = HibernateUtil.getHibernateSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> query = builder.createQuery(User.class);
            Root<User> root = query.from(User.class);
            query.select(root).where(builder.equal(root.get("id"), id));
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
