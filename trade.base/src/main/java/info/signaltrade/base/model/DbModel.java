package info.signaltrade.base.model;

/**
 * Database Model Class
 */
public class DbModel
{
    private String dbUser;
    private String dbPwd;
    private String dbUrl;
    private String dbDriver     = "com.mysql.jdbc.Driver";
    private int dbMinConnection = 5;
    private int dbMaxConnection = 20;

    /**
     * Constructor by default
     */
    public DbModel() {}

    /**
     * Constructor with params
     *
     * @param url   - connect url
     * @param user  - db user
     * @param pwd   - db password
     */
    public DbModel(String url, String user, String pwd) {
        dbUrl   = url;
        dbUser  = user;
        dbPwd   = pwd;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPwd() {
        return dbPwd;
    }

    public void setDbPwd(String dbPwd) {
        this.dbPwd = dbPwd;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public int getDbMinConnection() {
        return dbMinConnection;
    }

    public void setDbMinConnection(int dbMinConnection) {
        this.dbMinConnection = dbMinConnection;
    }

    public int getDbMaxConnection() {
        return dbMaxConnection;
    }

    public void setDbMaxConnection(int dbMaxConnection) {
        this.dbMaxConnection = dbMaxConnection;
    }
}
