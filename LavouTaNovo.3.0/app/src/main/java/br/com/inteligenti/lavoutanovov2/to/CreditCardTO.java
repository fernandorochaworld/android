package br.com.inteligenti.lavoutanovov2.to;

public class CreditCardTO {
    private String cardNumber;
    private String name;
    private String month;
    private String year;
    private String cvv;
    private String token;
    private String sessionId;
    private String codgSenderHash;
    private String codgBrand;
    private String codgStatus;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCodgSenderHash() {
        return codgSenderHash;
    }

    public void setCodgSenderHash(String codgSenderHash) {
        this.codgSenderHash = codgSenderHash;
    }

    public String getCodgBrand() {
        return codgBrand;
    }

    public void setCodgBrand(String codgBrand) {
        this.codgBrand = codgBrand;
    }

    public String getCodgStatus() {
        return codgStatus;
    }

    public void setCodgStatus(String codgStatus) {
        this.codgStatus = codgStatus;
    }
}
