package online.wangxuan.kettle.dto.output.job;


import java.util.Date;

/**
 * Created by wangxuan on 2017/6/9.
 */
public class KettleJobOutput {

    private String jobName;

    private String jobPath;

    private Date lastModifiedDate;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobPath() {
        return jobPath;
    }

    public void setJobPath(String jobPath) {
        this.jobPath = jobPath;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    private KettleJobOutput(String jobName, String jobPath, Date lastModifiedDate) {
        this.jobName = jobName;
        this.jobPath = jobPath;
        this.lastModifiedDate = lastModifiedDate;
    }

    public static KettleJobOutput newjJob(String jobName, String jobPath, Date lastModifiedDate) {
        return new KettleJobOutput(jobName, jobPath, lastModifiedDate);
    }
}
