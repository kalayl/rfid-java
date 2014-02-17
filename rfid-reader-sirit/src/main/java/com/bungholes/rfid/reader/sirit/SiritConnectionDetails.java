package com.bungholes.rfid.reader.sirit;

public class SiritConnectionDetails {

    private String ip;
    private String login;
    private String password;

    public SiritConnectionDetails() {
    }

    public SiritConnectionDetails(String ip, String login, String password) {
        this.ip = ip;
        this.login = login;
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "SiritConnectionDetails{" +
                "ip='" + ip + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SiritConnectionDetails that = (SiritConnectionDetails) o;

        if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;
        if (login != null ? !login.equals(that.login) : that.login != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
