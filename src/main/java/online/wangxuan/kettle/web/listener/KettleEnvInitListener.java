package online.wangxuan.kettle.web.listener;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by wangxuan on 2017/6/9.
 */
public class KettleEnvInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("==================== 初始化kettle环境 ===================");
        try {
            initKettleEnv();
        } catch (KettleException e) {
            System.out.print("kettle初始化失败");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    private void initKettleEnv() throws KettleException {
        KettleEnvironment.init();
    }
}
