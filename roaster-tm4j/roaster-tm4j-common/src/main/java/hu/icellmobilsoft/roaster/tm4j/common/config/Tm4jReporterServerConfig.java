package hu.icellmobilsoft.roaster.tm4j.common.config;

public class Tm4jReporterServerConfig {
    private String baseUrl;
    private String userName;
    private String password;
    private String basicAuthToken;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBasicAuthToken() {
        return basicAuthToken;
    }

    public void setBasicAuthToken(String basicAuthToken) {
        this.basicAuthToken = basicAuthToken;
    }
}
