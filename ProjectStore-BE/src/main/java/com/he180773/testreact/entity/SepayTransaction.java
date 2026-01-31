package com.he180773.testreact.entity;



public class SepayTransaction {
    private String id;
    private String bank_brand_name;
    private String account_number;
    private String transaction_date;
    private String amount_out;
    private String amount_in;
    private String accumulated;
    private String transaction_content;
    private String reference_number;
    private String code;
    private String sub_account;
    private String bank_account_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getBank_brand_name() {
        return bank_brand_name;
    }

    public void setBank_brand_name(String bank_brand_name) {
        this.bank_brand_name = bank_brand_name;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getAmount_out() {
        return amount_out;
    }

    public void setAmount_out(String amount_out) {
        this.amount_out = amount_out;
    }

    public String getAmount_in() {
        return amount_in;
    }

    public void setAmount_in(String amount_in) {
        this.amount_in = amount_in;
    }

    public String getTransaction_content() {
        return transaction_content;
    }

    public void setTransaction_content(String transaction_content) {
        this.transaction_content = transaction_content;
    }

    public String getAccumulated() {
        return accumulated;
    }

    public void setAccumulated(String accumulated) {
        this.accumulated = accumulated;
    }

    public String getReference_number() {
        return reference_number;
    }

    public void setReference_number(String reference_number) {
        this.reference_number = reference_number;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBank_account_id() {
        return bank_account_id;
    }

    public void setBank_account_id(String bank_account_id) {
        this.bank_account_id = bank_account_id;
    }

    public String getSub_account() {
        return sub_account;
    }

    public void setSub_account(String sub_account) {
        this.sub_account = sub_account;
    }
}


