package io.ampersand8.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;

@ManagedBean(name = "messageBean")
@RequestScoped
public class MessageBean implements Serializable {
    private String message;
    private String messageSuccess;

    public String getMessage() {
        return message;
    }

    public String getMessageSuccess() {
        return messageSuccess;
    }

    public void send(String message, String messageSuccess) {
        this.message = message;
        this.messageSuccess = messageSuccess;
    }
}