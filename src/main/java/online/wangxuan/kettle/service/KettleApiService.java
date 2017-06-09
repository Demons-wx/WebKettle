package online.wangxuan.kettle.service;

import online.wangxuan.kettle.dto.output.job.KettleJobOutput;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Result;
import org.pentaho.di.www.SlaveServerJobStatus;

import java.util.List;

/**
 * Created by wangxuan on 2017/6/8.
 */
public interface KettleApiService {

    /**
     * 获取作业列表
     * @return
     */
    public List<KettleJobOutput> listJobs();

    /**
     * 本地执行job
     * @param jobName
     * @return
     */
    public Result runJobLocal(String jobName, String jobPath);

    /**
     * 远程执行job
     * @param jobName 作业名称
     * @param jobPath 作业在资源库中的路径
     * @return job在carte服务器上唯一标识
     */
    public String runJobRemote(String jobName, String jobPath);

    /**
     * 获取job执行状态
     * @param jobName
     * @param carteJobId
     * @return
     */
    public SlaveServerJobStatus getRemoteJobStatus(String jobName, String carteJobId);
}
