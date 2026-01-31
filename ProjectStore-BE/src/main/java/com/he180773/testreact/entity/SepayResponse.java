package com.he180773.testreact.entity;

import java.util.List;

public class SepayResponse {
    private int status;
    private Object error;
    private Object messages;
    private List<SepayTransaction> transactions;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public Object getMessages() {
        return messages;
    }

    public void setMessages(Object messages) {
        this.messages = messages;
    }

    public List<SepayTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<SepayTransaction> transactions) {
        this.transactions = transactions;
    }
}
