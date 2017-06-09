package online.wangxuan.kettle.dto.input.job;

/**
 * Created by wangxuan on 2017/6/9.
 */
public class KettleJobInput {

    private String jobName;
    private String jobPath;
    private String carteObjId;

    public String getCarteObjId() {
        return carteObjId;
    }

    public void setCarteObjId(String carteObjId) {
        this.carteObjId = carteObjId;
    }

    public String getJobPath() {
        return jobPath;
    }

    public void setJobPath(String jobPath) {
        this.jobPath = jobPath;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
