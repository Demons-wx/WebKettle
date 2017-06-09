package online.wangxuan.kettle.service.impl;

import online.wangxuan.kettle.dto.output.job.KettleJobOutput;
import online.wangxuan.kettle.exception.BusinessException;
import online.wangxuan.kettle.service.KettleApiService;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.repository.kdr.delegates.metastore.KettleDatabaseRepositoryMetaStore;
import org.pentaho.di.www.SlaveServerJobStatus;
import org.pentaho.metastore.api.IMetaStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static online.wangxuan.kettle.common.KettleConstant.*;
import static online.wangxuan.kettle.common.ErrorCode.*;

/**
 * Created by wangxuan on 2017/6/8.
 */
@Service
public class KettleApiServiceImpl implements KettleApiService {

    private Logger logger = LoggerFactory.getLogger(KettleApiServiceImpl.class);
    private KettleDatabaseRepository repository;

    @Override
    public List<KettleJobOutput> listJobs() throws BusinessException {
        try {
            connect();
            List<KettleJobOutput> jobOutputList = new ArrayList<>();
            // 获取根目录
            RepositoryDirectoryInterface root = loadRootDir();
            // 加载作业列表
            listKettleJobs(root, jobOutputList);
            // 关闭事务
            closeReadTransaction();

            return jobOutputList;
        } catch (BusinessException e) {
            throw new BusinessException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            logger.info("获取作业列表失败", e);
            throw new BusinessException(ERROR_LOAD_JOBS.getCode(), ERROR_LOAD_JOBS.getMsg() );
        }
    }

    @Override
    public Result runJobLocal(String jobName, String jobPath) {
        try {
            connect();
            RepositoryDirectoryInterface jobDir = loadJobDir(jobPath);

            JobMeta jobMeta = repository.loadJob(repository.getJobId(jobName, jobDir), null);
            Job job = new Job(repository, jobMeta);
            Result jobExecuteResult = job.execute(0, null);
            return jobExecuteResult;
        } catch (BusinessException e) {
            throw new BusinessException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            logger.info("本地执行job发生异常", e);
            throw new BusinessException(ERROR_RUN_JOB_LOCAL.getCode(), ERROR_RUN_JOB_LOCAL.getMsg() );
        }

    }

    @Override
    public String runJobRemote(String jobName, String jobPath) {
        try {
            connect();
            // job所在目录
            RepositoryDirectoryInterface jobDir = loadJobDir(jobPath);
            // job元数据
            JobMeta jobMeta = repository.loadJob(jobName, jobDir, null, null);
            // job执行参数
            JobExecutionConfiguration jobExeConf = new JobExecutionConfiguration();
            jobExeConf.setExecutingLocally(false);
            jobExeConf.setExecutingRemotely(true);
            jobExeConf.setRepository(repository);
            // 远程执行job的carte服务器
            jobExeConf.setRemoteServer(getSlaveServer());

            IMetaStore metaStore = new KettleDatabaseRepositoryMetaStore(repository);

            String carteObjId = Job.sendToSlaveServer(jobMeta, jobExeConf, repository, metaStore);

            return carteObjId;
        } catch (BusinessException e) {
            throw new BusinessException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            logger.info("远程执行job发生异常", e);
            throw new BusinessException(ERROR_RUN_JOB_REMOTE.getCode(), ERROR_RUN_JOB_REMOTE.getMsg() );
        }
    }

    @Override
    public SlaveServerJobStatus getRemoteJobStatus(String jobName, String carteJobId) {
        try {
            connect();
            SlaveServer carte = getSlaveServer();
            SlaveServerJobStatus jobStatus = carte.getJobStatus(jobName, carteJobId, 0);
            return jobStatus;
        } catch (BusinessException e) {
            throw new BusinessException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            logger.info("获取job执行日志异常", e);
            throw new BusinessException(ERROR_GET_JOB_STATUS.getCode(), ERROR_GET_JOB_STATUS.getMsg() );
        }
    }

    // ======================== 连接资源库与释放连接 ========================
    private void connect() throws BusinessException {
        try {
            // 创建资源库对象
            repository = new KettleDatabaseRepository();
            // 创建资源库的数据库对象
            DatabaseMeta databaseMeta = new DatabaseMeta(name, type, access, host, db, port, user, pass);
            // 资源库元对象
            KettleDatabaseRepositoryMeta repositoryMeta = new KettleDatabaseRepositoryMeta(null, name, null, databaseMeta);
            repository.init(repositoryMeta);
            // 连接资源库
            repository.connect(username, password);
        } catch (KettleException e) {
            logger.info("连接资源库失败", e);
            throw new BusinessException(ERROR_CONNECT_REOP.getCode(), ERROR_CONNECT_REOP.getMsg());
        }
    }

    private void disconnect() {
        repository.disconnect();
    }

    private void closeReadTransaction() throws BusinessException {
        try {
            repository.connectionDelegate.closeReadTransaction();
        } catch (KettleException e) {
            logger.info("关闭transaction失败", e);
            throw new BusinessException(ERROR_CLOSE_TRANSACTION.getCode(), ERROR_CLOSE_TRANSACTION.getMsg());
        }

    }

    /**
     * 获取carte服务器
     * @return
     */
    private static SlaveServer getSlaveServer() {
        return new SlaveServer(cartName, carteHostname, cartePort, cartUsername, cartPassword);
    }

    // ========================= 私有方法 ============================

    /**
     * 加载作业列表
     * @param node
     * @param jobOutputList
     */
    private void listKettleJobs(RepositoryDirectoryInterface node, List<KettleJobOutput> jobOutputList) {
        try {
            ObjectId dirId = node.getObjectId();
            List<RepositoryElementMetaInterface> repoJobElems = repository.getJobObjects(dirId, false);
            for(RepositoryElementMetaInterface repoJobElem : repoJobElems) {
                String jobName = repoJobElem.getName();
                Date modifiedDate = repoJobElem.getModifiedDate();
                jobOutputList.add(KettleJobOutput.newjJob(jobName, node.getPath(), modifiedDate));
            }
            List<RepositoryDirectoryInterface> children = node.getChildren();
            for(RepositoryDirectoryInterface child : children) {
                listKettleJobs(child, jobOutputList);
            }
        } catch (KettleException e) {
            throw new BusinessException(ERROR_LOAD_JOBELEM.getCode(), ERROR_LOAD_JOBELEM.getMsg());
        }
    }

    /**
     * 获取根目录
     * @return
     */
    private RepositoryDirectoryInterface loadRootDir() {
        try {
            return repository.loadRepositoryDirectoryTree().findRoot();
        } catch (KettleException e) {
            logger.info("获取根目录失败", e);
            throw new BusinessException(ERROR_LOAD_ROOTDIR.getCode(), ERROR_LOAD_ROOTDIR.getMsg());
        }
    }

    /**
     * 通过job路径获取RDI
     * @param jobPath
     * @return
     */
    private RepositoryDirectoryInterface loadJobDir(String jobPath) {
        try {
            RepositoryDirectoryInterface directoryInterface = null;
            if (jobPath == null || jobPath.isEmpty()) {
                directoryInterface = repository.loadRepositoryDirectoryTree().findRoot();
            }
            directoryInterface = repository.loadRepositoryDirectoryTree().findDirectory(jobPath);
            return directoryInterface;
        } catch (KettleException e) {
            logger.info("获取作业目录失败", e);
            throw new BusinessException(ERROR_LOAD_JOBDIR.getCode(), ERROR_LOAD_JOBDIR.getMsg());
        }
    }
}
