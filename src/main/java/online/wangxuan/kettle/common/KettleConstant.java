package online.wangxuan.kettle.common;

import online.wangxuan.kettle.service.impl.KettleApiServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by wangxuan on 2017/6/9.
 */
public final class KettleConstant {

    private static final Logger logger = LoggerFactory.getLogger(KettleConstant.class);

    // 资源库配置
    public final static String name;
    public final static String username;
    public final static String password;
    public final static String type;
    public final static String access;
    public final static String host;
    public final static String db;
    public final static String port;
    public final static String user;
    public final static String pass;

    // carte服务器
    public final static String cartName;
    public final static String carteHostname;
    public final static String cartePort;
    public final static String cartUsername;
    public final static String cartPassword;


    static {
        Properties prop = new Properties();
        try {
            InputStream inputStream = KettleApiServiceImpl.class.getClassLoader().
                    getResourceAsStream("kettle.properties");
            prop.load(inputStream);
        } catch (Exception e) {
            logger.info("读取配置文件错误");
        }

        name = prop.getProperty("name");
        username = prop.getProperty("username");
        password = prop.getProperty("password");
        type = prop.getProperty("type");
        access = prop.getProperty("access");
        host = prop.getProperty("host");
        db = prop.getProperty("db");
        port = prop.getProperty("port");
        user = prop.getProperty("user");
        pass = prop.getProperty("pass");

        cartName = prop.getProperty("cartName");
        carteHostname = prop.getProperty("carteHostname");
        cartePort = prop.getProperty("cartePort");
        cartUsername = prop.getProperty("cartUsername");
        cartPassword = prop.getProperty("cartPassword");
    }
}
