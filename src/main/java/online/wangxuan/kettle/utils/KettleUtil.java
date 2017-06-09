package online.wangxuan.kettle.utils;

import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.repository.kdr.delegates.metastore.KettleDatabaseRepositoryMetaStore;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.www.SlaveServerJobStatus;
import org.pentaho.metastore.api.IMetaStore;

/**
 * Created by wangxuan on 2017/6/6.
 */
public class KettleUtil {

    private static KettleDatabaseRepository rep;

    private static void init() {
        try {
            // 初始化kettle环境
            KettleEnvironment.init();
            // 创建资源库对象
            rep = new KettleDatabaseRepository();
            // 创建资源库的数据库对象
            DatabaseMeta databaseMeta = new DatabaseMeta("swaptasktool_rep", "MYSQL", "Native",
                    "192.168.179.128", "swaptasktool_rep", "3306", "root", "mysql");
            // 资源库元对象
            KettleDatabaseRepositoryMeta repInfo = new KettleDatabaseRepositoryMeta(null, "swaptasktool_rep", null, databaseMeta);
            rep.init(repInfo);
            // 连接资源库
            rep.connect("admin", "admin");
        } catch (KettleException e) {
            throw new RuntimeException("连接资源库错误" ,e);
        }

    }

    public static SlaveServer initSlaveServer() {
        return new SlaveServer("carte", "192.168.179.1", "8888", "cluster", "cluster");
    }

    public static void callNativeTrans(String[] args, String transFileName) throws Exception {
        KettleEnvironment.init();
        EnvUtil.environmentInit();
        TransMeta transMeta = new TransMeta(transFileName);
        //转换
        Trans trans = new Trans(transMeta);
        //执行
        trans.execute(args);
        //等待结束
        trans.waitUntilFinished();
        //抛出异常
        if(trans.getErrors() > 0){
            throw new Exception("There are errors during transformation exception!(传输过程中发生异常)");
        }
    }

    public static void callDatabaseTrans(String transName) throws Exception {
        init();
        RepositoryDirectoryInterface root = rep.loadRepositoryDirectoryTree().findRoot();
        ObjectId rootId = root.getObjectId();

        TransMeta transMeta = rep.loadTransformation(rep.getTransformationID(transName, root), null);
        Trans trans = new Trans(transMeta);
        trans.execute(null);
        trans.waitUntilFinished();

        if(trans.getErrors() > 0){
            throw new Exception("There are errors during transformation exception!(传输过程中发生异常)");
        }
    }

    public static void callDatabaseJob(String jobName) throws Exception {
        init();
        RepositoryDirectoryInterface root = rep.loadRepositoryDirectoryTree().findRoot();
        ObjectId rootId = root.getObjectId();

        JobMeta jobMeta = rep.loadJob(rep.getJobId(jobName, root), null);
        Job job = new Job(rep, jobMeta);
     //   job.start();
        Result result = job.execute(0, null);
        String log = result.getLogText();
        if (job.getErrors() > 0) {
            throw new Exception("There are errors during job exception!(执行job发生异常)");
        }
    }

    public static void listJobAndTrans() throws Exception {

        init();
        RepositoryDirectoryInterface root = rep.loadRepositoryDirectoryTree().findRoot();
        ObjectId rootId = root.getObjectId();


        /*************** 目录 *****************/
        ObjectId[] subDirectoryIDs = rep.getSubDirectoryIDs(rootId);

        for (ObjectId objId : subDirectoryIDs) {
            RepositoryDirectoryInterface cunnrentNode = rep.findDirectory(objId);
            System.out.print(cunnrentNode.getName()+ " {");
            /*************** 作业 *****************/
            String[] jobNames = rep.getJobNames(objId, false);
            System.out.print("jobNames: [");
            for (String jobName : jobNames) {
                System.out.print(jobName + ",");
            }
            System.out.print("], ");

            /*************** 转换 *****************/
            String[] transNames = rep.getTransformationNames(objId, false);
            System.out.print("transNames: [");
            for (String trans : transNames) {
                System.out.print(trans + ",");
            }
            System.out.print("]");
            System.out.println(" }");
        }
    }

    public static void callNativeJob(String jobName) throws Exception {

        KettleEnvironment.init();
        JobMeta jobMeta = new JobMeta(jobName, null);
        Job job = new Job(null, jobMeta);
        job.start();
        job.waitUntilFinished();

        if (job.getErrors() > 0) {
            throw new Exception("There are errors during job exception!(执行job发生异常)");
        }
    }

    /**
     * 远程执行job
     * @param jobName
     * @param jobDirPath
     * @param server
     * @return
     * @throws Exception
     */
    public static String runJobRemote(String jobName, String jobDirPath, SlaveServer server) throws Exception {

        init();
        // job所在资源库目录
        RepositoryDirectoryInterface repoDirInterface = getDirInterface(jobDirPath);
        // job元数据
        JobMeta jobMeta = rep.loadJob(jobName, repoDirInterface, null, null);
        // job执行参数
        JobExecutionConfiguration jobExeConf = new JobExecutionConfiguration();
        jobExeConf.setExecutingLocally(false);
        jobExeConf.setExecutingRemotely(true);
        jobExeConf.setRepository(rep);

        // 远程执行job的服务器
        jobExeConf.setRemoteServer(server);

        IMetaStore metaStore = new KettleDatabaseRepositoryMetaStore(rep);
        String carteObjId = Job.sendToSlaveServer(jobMeta, jobExeConf, rep, metaStore);

        return carteObjId;
    }

    /**
     * 获取远程服务器执行job的结果
     * @param jobName
     * @param carteObjId
     * @param server
     * @return
     * @throws Exception
     */
    public static SlaveServerJobStatus getRemoteJobStatus(String jobName, String carteObjId, SlaveServer server) throws Exception {
        init();
        SlaveServerJobStatus serverJobStatus = server.getJobStatus(jobName, carteObjId, 0);
        return serverJobStatus;
    }

    private static RepositoryDirectoryInterface getDirInterface(String repoDirPath) throws KettleException {
        RepositoryDirectoryInterface repoDirInterface = null;
        if(repoDirPath == null || repoDirPath.isEmpty()) {
            repoDirInterface = rep.loadRepositoryDirectoryTree().findRoot();
        } else {
            repoDirInterface = rep.loadRepositoryDirectoryTree().findDirectory(repoDirPath);
        }
        return repoDirInterface;
    }
}
