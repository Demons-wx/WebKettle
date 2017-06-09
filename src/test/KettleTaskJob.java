
import org.pentaho.di.core.Result;
import org.pentaho.di.www.SlaveServerJobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static online.wangxuan.kettle.utils.KettleUtil.*;

/**
 * Created by wangxuan on 2017/6/6.
 */
public class KettleTaskJob {

    private static Logger logger = LoggerFactory.getLogger(KettleTaskJob.class);

    public void run() throws Exception {
        logger.info("*****kettle定时任务运行开始******");
        // 调用本地转换
    //    String transFileName = "E:/kettle/demo/dbtrans.ktr";
    //    callNativeTrans(null, transFileName);
        // 调用数据库中的转换
    //    String transName = "dataSyn";
    //    callDatabaseTrans(transName);
        // 资源库中的作业与转换列表
        listJobAndTrans();
        String jobName = "tbSyn";
    //    callDatabaseJob(jobName);

        // 远程执行job
    //    runJobRemote(jobName, "/", initSlaveServer());
        logger.info("*****kettle定时任务运行结束******");
    }

    /**
     * 远程执行job并获取日志
     * @throws Exception
     */
    public void testRunJobRemoteAndGetJobStatus() throws Exception {
        String jobName = "tbSyn";
        String jobDirPath = "/";
        // 远程执行job
        String carteObjId = runJobRemote(jobName, jobDirPath, initSlaveServer());
        SlaveServerJobStatus jobStatus = null;
        Result result = null;
        while (true) {
         //   TimeUnit.MILLISECONDS.sleep(1000);
            jobStatus = getRemoteJobStatus(jobName, carteObjId, initSlaveServer());
            System.out.println("===========================日志开始==========================");
            System.out.println(jobStatus.getLoggingString());
            System.out.println("===========================日志结束==========================");
            if (!jobStatus.isRunning() && !jobStatus.isWaiting()) {
                break;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        KettleTaskJob taskJob = new KettleTaskJob();
    //    taskJob.testRunJobRemoteAndGetJobStatus();
        taskJob.run();
    }
}
