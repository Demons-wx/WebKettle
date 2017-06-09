package online.wangxuan.kettle.web.controller;

import com.google.common.base.Strings;
import online.wangxuan.kettle.dto.input.job.KettleJobInput;
import online.wangxuan.kettle.dto.output.job.KettleJobOutput;
import online.wangxuan.kettle.dto.output.KettleResult;
import online.wangxuan.kettle.service.KettleApiService;
import org.pentaho.di.core.Result;
import org.pentaho.di.www.SlaveServerJobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wangxuan on 2017/6/9.
 */
@RestController
@RequestMapping("/job")
public class KettleJobController {

    @Autowired
    private KettleApiService kettleApiService;

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public KettleResult listKettleJobs() {
        List<KettleJobOutput> jobOutputList = kettleApiService.listJobs();
        return KettleResult.success(jobOutputList);
    }

    @ResponseBody
    @RequestMapping(value = "/runJobLocal", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public KettleResult runJobLocal(@RequestBody KettleJobInput jobInput) {
        Result jobExecuteResult = kettleApiService.runJobLocal(jobInput.getJobName(), jobInput.getJobPath());
        boolean result = jobExecuteResult.getResult();
        return KettleResult.success(result);
    }

    @ResponseBody
    @RequestMapping(value = "/runJobRemote", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public KettleResult runJobRemote(@RequestBody KettleJobInput jobInput) {
        String carteObjId = kettleApiService.runJobRemote(jobInput.getJobName(), jobInput.getJobPath());
        SlaveServerJobStatus jobStatus = kettleApiService.getRemoteJobStatus(jobInput.getJobName(), carteObjId);
        String jobLogs = jobStatus.getLoggingString();
        return KettleResult.success((Object) jobLogs);
    }

    @ResponseBody
    @RequestMapping(value = "/getJobLogs", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public KettleResult getJobLogs(@RequestBody KettleJobInput jobInput) {
        SlaveServerJobStatus jobStatus = kettleApiService.getRemoteJobStatus(jobInput.getJobName(),
                jobInput.getCarteObjId());
        String jobLogs = jobStatus.getLoggingString();
        return KettleResult.success((Object) jobLogs);
    }


    /**
     * comet4j  http://pengbaowei0311.iteye.com/blog/2302882
     */

}
