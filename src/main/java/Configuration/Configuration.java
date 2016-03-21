package Configuration;

/**
 * @author Robin Duda
 */
public class Configuration {
    public static final int MASTER_PORT = 9494;
    public static final int WEB_PORT = 6464;
    public static final String CONNECTION_STRING = "mongodb://localhost:27017/";
    public static final String DB_NAME = "vote";
    public static final String SERVER_NAME = "server.controller";
    public static final byte[] SERVER_SECRET = "!!!!!!!!!!!server_secret!!!!!!!!!!".getBytes();
    public static final byte[] CLIENT_SECRET = "!!!!!!!!!!!client_secret!!!!!!!!!!".getBytes();
}
