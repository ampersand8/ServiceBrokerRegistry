package io.ampersand8.beans;

import com.google.gson.Gson;
import io.ampersand8.model.Broker;
import io.ampersand8.model.Service;
import io.ampersand8.model.dto.ServiceDto;
import io.ampersand8.util.HibernateUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "formBrokerBean")
@RequestScoped
public class FormBrokerBean implements Serializable {

    final static Logger logger = Logger.getLogger(FormBrokerBean.class);
    private String name;
    private String url;
    private String username;
    private String password;
    private String title = "Register Broker";
    private String id;

    @ManagedProperty(value = "#{loginSessionBean}")
    private LoginSessionBean loginSessionBean;

    @ManagedProperty(value = "#{messageBean}")
    private MessageBean messageBean;

    private static final String PAGESUCCESSREGISTER = "brokers?faces-redirect=true";

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String add() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (params.get("requestBrokerId") == null) {
            Session session = HibernateUtil.getHibernateSession();
            Transaction transaction = null;
            Broker broker = new Broker(loginSessionBean.getId(), name, url, username, password);
            String catalog = getCatalog(broker);
            if (catalog == null) return null;
            List<Service> services = parseJsonString(catalog);
            if (services == null) return null;
            if (!services.isEmpty()) {
                try {
                    transaction = session.beginTransaction();
                    for (Service service : services) {
                        service.setBroker(broker);
                        broker.addService(service);
                    }
                    session.saveOrUpdate(broker);
                    transaction.commit();
                } catch (PersistenceException e) {
                    e.printStackTrace();
                    if (transaction != null) transaction.rollback();
                    messageBean.send("Could not save broker", "fail");
                    return null;
                }
                return PAGESUCCESSREGISTER;
            } else {
                messageBean.send("Cannot save broker without any services", "fail");
                return null;
            }
        } else {
            return this.update();
        }
    }

    public String update() {
        Broker brokerToUpdate = getBroker(this.id);
        brokerToUpdate.setName(this.name);
        brokerToUpdate.setOwner(loginSessionBean.getId());
        brokerToUpdate.setUrl(this.url);
        brokerToUpdate.setUsername(this.username);
        brokerToUpdate.setPassword(this.password);
        Session session = HibernateUtil.getHibernateSession();
        Transaction transaction = null;
        String catalog = getCatalog(brokerToUpdate);
        if (catalog == null) return null;
        List<Service> services = parseJsonString(catalog);
        if (services == null) return null;
        if (!services.isEmpty()) {
            try {
                transaction = session.beginTransaction();
                for (Service service : services) {
                    service.setBroker(brokerToUpdate);
                    brokerToUpdate.addService(service);
                }
                session.update(brokerToUpdate);
                transaction.commit();
            } catch (PersistenceException e) {
                e.printStackTrace();
                if (transaction != null) transaction.rollback();
                messageBean.send("Could not update broker", "fail");
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return PAGESUCCESSREGISTER;
        } else {
            messageBean.send("Cannot save broker without any services", "fail");
            return null;
        }
    }

    private List<Service> parseJsonString(String jsonString) {
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONArray jsonServices = obj.getJSONArray("services");
            Gson gson = new Gson();
            ArrayList<Service> services = new ArrayList<>();
            for (int i = 0; i < jsonServices.length(); i++) {
                JSONObject jsonService = jsonServices.getJSONObject(i);

                ServiceDto serviceDto = gson.fromJson(jsonService.toString(), ServiceDto.class);
                Service service = Service.makeServiceFromDto(serviceDto);
                services.add(service);
            }
            return services;
        } catch (JSONException e) {
            messageBean.send("Could not parse JSON<br/>" + e.getMessage(), "fail");
            return null;
        }
    }

    private String getCatalog(Broker broker) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(broker.getUrl());
            String creds = broker.getUsername() + ":" + broker.getPassword();
            String encoding = Base64.getEncoder().encodeToString(creds.getBytes());
            request.addHeader("Authorization", "Basic " + encoding);

            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 401) {
                messageBean.send("Authentication failed", "fail");
                return null;
            }
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), "UTF8"));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        } catch (ClientProtocolException e) {
            messageBean.send("Could not reach host", "fail");
            return null;
        } catch (Exception e) {
            messageBean.send("Something went wrong. Registering Broker failed", "fail");
            return null;
        }
    }

    public void onload() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String requestBrokerId = params.get("requestBrokerId");
        if (requestBrokerId != null && requestBrokerId.length() > 0) {
            this.title = "Edit Broker";
            Broker broker = getBroker(requestBrokerId);
            if (broker != null) {
                this.name = broker.getName();
                this.url = broker.getUrl();
                this.username = broker.getUsername();
                this.title += " " + broker.getName();
                this.id = broker.getId();
            }
        }
    }

    private Broker getBroker(String id) {
        Session session = HibernateUtil.getHibernateSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Broker> query = builder.createQuery(Broker.class);
            Root<Broker> root = query.from(Broker.class);
            query.select(root).where(builder.equal(root.get("id"), id));
            Query<Broker> q = session.createQuery(query);
            Broker broker = q.getSingleResult();
            transaction.commit();
            return broker;
        } catch (NoResultException e) {
            if (transaction != null) transaction.rollback();
            return null;
        }
    }
}
