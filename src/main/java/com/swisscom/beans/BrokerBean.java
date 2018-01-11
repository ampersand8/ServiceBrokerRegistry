package com.swisscom.beans;

import com.google.gson.Gson;
import com.swisscom.model.Broker;
import com.swisscom.model.Service;
import com.swisscom.model.dto.ServiceDto;
import com.swisscom.util.HibernateUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
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

@ManagedBean(name = "brokerBean")
@RequestScoped
public class BrokerBean implements Serializable {

    final static Logger logger = Logger.getLogger(BrokerBean.class);
    private String name;
    private String url;
    private String username;
    private String password;

    @ManagedProperty(value = "#{loginSessionBean}")
    private LoginSessionBean loginSessionBean;

    private static final String PAGESUCCESSREGISTER = "brokers?faces-redirect=true&success=brokerRegister";
    private static final String PAGEFAILEDREGISTER = "brokers?failed=brokerRegister";

    public LoginSessionBean getLoginSessionBean() {
        return loginSessionBean;
    }

    public void setLoginSessionBean(LoginSessionBean loginSessionBean) {
        this.loginSessionBean = loginSessionBean;
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
        Session session = HibernateUtil.getHibernateSession();
        Transaction transaction = null;
        Broker broker = new Broker(loginSessionBean.getId(), name, url, username, password);
        List<Service> services = parseJsonString(getCatalog(broker));
        if (!services.isEmpty()) {
            try {
                transaction = session.beginTransaction();
                for (Service service : services) {
                    service.setBroker(broker.getId());
                    broker.addService(service);
                }
                session.save(broker);
                transaction.commit();
            } catch (PersistenceException e) {
                if (transaction != null) transaction.rollback();
                e.printStackTrace();
                return PAGEFAILEDREGISTER;
            } finally {
                // session.close();
            }
            return PAGESUCCESSREGISTER;
        } else {
            return PAGEFAILEDREGISTER;
        }
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

    private List<Service> parseJsonString(String jsonString) {
        JSONObject obj = new JSONObject(jsonString);
        JSONArray jsonServices = obj.getJSONArray("services");
        Gson gson = new Gson();
        ArrayList<Service> services = new ArrayList<>();
        for (int i = 0; i < jsonServices.length(); i++) {
            JSONObject jsonService = jsonServices.getJSONObject(i);

            ServiceDto serviceDto = gson.fromJson(jsonService.toString(), ServiceDto.class);
            Service service = Service.makeServiceFromDto(serviceDto);
            System.out.println(service.getName());
            services.add(service);
        }
        return services;
    }

    private String getCatalog(Broker broker) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            String creds = broker.getUsername() + ":" + broker.getPassword();
            String encoding = Base64.getEncoder().encodeToString(creds.getBytes());
            request.addHeader("Authorization", "Basic " + encoding);

            HttpResponse response = client.execute(request);

            logger.info("Response Code : "
                    + response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), "UTF8"));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println("testing: " + result);

            logger.info(result);

            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
