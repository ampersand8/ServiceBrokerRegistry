package com.swisscom.beans;

import com.swisscom.model.Broker;
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
import javax.faces.bean.SessionScoped;
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
@SessionScoped
public class BrokerBean implements Serializable {

    final static Logger logger = Logger.getLogger(BrokerBean.class);

    private String id;
    private String name;
    private String url;
    private String username;
    private String password;

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    private static final String PAGESUCCESSREGISTER = "brokers?faces-redirect=true&success=brokerRegister";
    private static final String PAGEFAILEDREGISTER = "brokers?failed=brokerRegister";

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
        if (getCatalog(broker)) {
            //List<Service> services = getCatalog(broker);
            //logger.info(services);
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
        } else {
            return PAGEFAILEDREGISTER;
        }
    }

    public List<Broker> getList() {
        testReadingJson();
        Session session = HibernateUtil.getSessionFactory().openSession();
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
        } finally {
            session.close();
        }
    }

    private void testReadingJson() {
        String testString = "{\"services\":[{\"id\":\"781e8f8c-c753-4a93-95eb-17c1f745b229\",\"name\":\"redisent\",\"description\":\"Redis Enterprise in-memory data structure store v3.2.3\",\"bindable\":true,\"plans\":[{\"id\":\"ea4b1b7d-3060-4ac6-836b-e134de0e7d9b\",\"name\":\"large\",\"description\":\"Redis Sentinel Cluster with 3 data bearing nodes with 8 GB memory, 8 GB storage and unlimited concurrent connections\",\"free\":false,\"metadata\":{\"displayName\":\"Large\",\"nodes\":\"3\",\"highAvailability\":true,\"dedicatedService\":true,\"maximumConcurrentConnections\":\"Unlimited\",\"storageCapacity\":\"8 GB\",\"memory\":\"8 GB\"},\"containerParams\":null},{\"id\":\"ebe11e59-5261-4939-ac8f-0a35c3850b4e\",\"name\":\"xlarge\",\"description\":\"Redis Sentinel Cluster with 3 data bearing nodes with 16 GB memory, 16 GB storage and unlimited concurrent connections\",\"free\":false,\"metadata\":{\"highAvailability\":true,\"displayName\":\"X-Large\",\"storageCapacity\":\"16 GB\",\"memory\":\"16 GB\",\"dedicatedService\":true,\"maximumConcurrentConnections\":\"Unlimited\",\"nodes\":\"3\"},\"containerParams\":null},{\"id\":\"7b71cf85-0e50-4509-af04-eafd3a6ad141\",\"name\":\"xxlarge\",\"description\":\"Redis Sentinel Cluster with 3 data bearing nodes with 32 GB memory, 32 GB storage, unlimited concurrent connections\",\"free\":false,\"metadata\":{\"highAvailability\":true,\"displayName\":\"XX-Large\",\"maximumConcurrentConnections\":\"Unlimited\",\"storageCapacity\":\"32 GB\",\"dedicatedService\":true,\"memory\":\"32 GB\",\"nodes\":\"3\"},\"containerParams\":null}],\"tags\":[\"redis\"],\"requires\":[],\"metadata\":{\"version\":\"3.2.3\",\"displayName\":\"Redis Enterprise\"},\"dashboard_client\":null}]}";
        JSONObject obj = new JSONObject(testString);
        JSONArray services = obj.getJSONArray("services");
        for (int i = 0; i < services.length(); i++) {
            JSONObject service = services.getJSONObject(i);
            JSONArray plans = service.getJSONArray("plans");
            for (int j = 0; j < plans.length(); j++) {
                JSONObject plan = plans.getJSONObject(j);
                System.out.println(plan.getString("id"));
            }
            System.out.println(service.getString("id"));
        }
        // System.out.println(obj.getJSONArray("services"));
    }

    private boolean getCatalog(Broker broker) {
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
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            // JSONObject obj = new JSONObject(result);
            System.out.println("testing: " + result);

            logger.info(result);

            return response.getStatusLine().getStatusCode() == 200;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
