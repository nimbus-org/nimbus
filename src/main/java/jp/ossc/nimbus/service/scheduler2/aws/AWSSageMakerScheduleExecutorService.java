/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2003 The Nimbus Project. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE NIMBUS PROJECT ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE NIMBUS PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of the Nimbus Project.
 */
package jp.ossc.nimbus.service.scheduler2.aws;

import java.util.*;

import jp.ossc.nimbus.service.scheduler2.*;
import jp.ossc.nimbus.util.converter.BeanJSONConverter;
import jp.ossc.nimbus.util.converter.DateFormatConverter;

import com.amazonaws.waiters.WaiterParameters;
import com.amazonaws.waiters.PollingStrategy;
import com.amazonaws.waiters.PollingStrategyContext;
import com.amazonaws.waiters.MaxAttemptsRetryStrategy;
import com.amazonaws.waiters.FixedDelayStrategy;
import com.amazonaws.services.sagemaker.AmazonSageMakerClient;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.sagemaker.model.*;

/**
 * AWS SageMakerを呼び出すスケジュール実行。<p>
 *
 * @author M.Takata
 */
public class AWSSageMakerScheduleExecutorService extends AWSWebServiceScheduleExecutorService implements AWSSageMakerScheduleExecutorServiceMBean{
    
    protected int waitPollingInterval = 1;
    protected PollingStrategy pollingStrategy;
    protected Map executeScheduleMap;
    
    {
        type = DEFAULT_EXECUTOR_TYPE;
    }
    
    public void setWaitPollingInterval(int interval){
        waitPollingInterval = interval;
    }
    public int getWaitPollingInterval(){
        return waitPollingInterval;
    }
    
    public void setPollingStrategy(PollingStrategy strategy){
        pollingStrategy = strategy;
    }
    
    public void createService() throws Exception{
        super.createService();
        executeScheduleMap = Collections.synchronizedMap(new HashMap());
    }
    
    public void startService() throws Exception{
        super.startService();
        
        BeanJSONConverter beanJSONConverter = new BeanJSONConverter();
        DateFormatConverter dfc = new DateFormatConverter();
        dfc.setFormat("yyyy/MM/dd HH:mm:ss.SSS");
        dfc.setConvertType(DateFormatConverter.DATE_TO_STRING);
        beanJSONConverter.setFormatConverter(java.util.Date.class, dfc);
        addAutoInputConvertMappings(beanJSONConverter);
        addAutoOutputConvertMappings(beanJSONConverter);
        
        if(pollingStrategy == null){
            pollingStrategy = new PollingStrategy(
                new PollingStrategy.RetryStrategy(){
                    public boolean shouldRetry(PollingStrategyContext pollingStrategyContext){
                        return true;
                    }
                },
                new FixedDelayStrategy(waitPollingInterval)
            );
        }
    }
    
    public void destroyService() throws Exception{
        super.destroyService();
        executeScheduleMap = null;
    }
    
    protected Schedule executeInternal(Schedule schedule) throws Throwable{
        executeScheduleMap.put(schedule.getId(), schedule);
        try{
            Schedule result = super.executeInternal(schedule);
            AmazonSageMakerClient client = (AmazonSageMakerClient)webServiceClient;
            AmazonWebServiceRequest request = (AmazonWebServiceRequest)schedule.getInput();
            if(request instanceof DeleteEndpointRequest){
                client.waiters().endpointDeleted().run(
                    new WaiterParameters()
                        .withPollingStrategy(pollingStrategy)
                        .withRequest(
                            setupRequest(
                                new DescribeEndpointRequest()
                                    .withEndpointName(((DeleteEndpointRequest)request).getEndpointName())
                            )
                        )
                );
            }else if(request instanceof CreateEndpointRequest){
                client.waiters().endpointInService().run(
                    new WaiterParameters()
                        .withPollingStrategy(pollingStrategy)
                        .withRequest(
                            setupRequest(
                                new DescribeEndpointRequest()
                                    .withEndpointName(((CreateEndpointRequest)request).getEndpointName())
                            )
                        )
                );
            }else if(request instanceof CreateNotebookInstanceRequest){
                client.waiters().notebookInstanceInService().run(
                    new WaiterParameters()
                        .withPollingStrategy(pollingStrategy)
                        .withRequest(
                            setupRequest(
                                new DescribeNotebookInstanceRequest()
                                    .withNotebookInstanceName(((CreateNotebookInstanceRequest)request).getNotebookInstanceName())
                            )
                        )
                );
            }else if(request instanceof DeleteNotebookInstanceRequest){
                client.waiters().notebookInstanceDeleted().run(
                    new WaiterParameters()
                        .withPollingStrategy(pollingStrategy)
                        .withRequest(
                            setupRequest(
                                new DescribeNotebookInstanceRequest()
                                    .withNotebookInstanceName(((DeleteNotebookInstanceRequest)request).getNotebookInstanceName())
                            )
                        )
                );
            }else if(request instanceof StopNotebookInstanceRequest){
                client.waiters().notebookInstanceStopped().run(
                    new WaiterParameters()
                        .withPollingStrategy(pollingStrategy)
                        .withRequest(
                            setupRequest(
                                new DescribeNotebookInstanceRequest()
                                    .withNotebookInstanceName(((StopNotebookInstanceRequest)request).getNotebookInstanceName())
                            )
                        )
                );
            }else if(request instanceof CreateTransformJobRequest){
                client.waiters().transformJobCompletedOrStopped().run(
                    new WaiterParameters()
                        .withPollingStrategy(pollingStrategy)
                        .withRequest(
                            setupRequest(
                                new DescribeTransformJobRequest()
                                    .withTransformJobName(((CreateTransformJobRequest)request).getTransformJobName())
                            )
                        )
                );
            }else if(request instanceof CreateTrainingJobRequest){
                client.waiters().trainingJobCompletedOrStopped().run(
                    new WaiterParameters()
                        .withPollingStrategy(pollingStrategy)
                        .withRequest(
                            setupRequest(
                                new DescribeTrainingJobRequest()
                                    .withTrainingJobName(((CreateTrainingJobRequest)request).getTrainingJobName())
                            )
                        )
                );
            }
            return result;
        }finally{
            executeScheduleMap.remove(schedule.getId());
        }
    }
    
    public boolean controlState(String id, int cntrolState) throws ScheduleStateControlException{
        Schedule schedule = (Schedule)executeScheduleMap.get(id);
        if(schedule != null && cntrolState == Schedule.CONTROL_STATE_ABORT){
            AmazonSageMakerClient client = (AmazonSageMakerClient)webServiceClient;
            AmazonWebServiceRequest request = (AmazonWebServiceRequest)schedule.getInput();
            if(request instanceof CreateTrainingJobRequest){
                try{
                    client.stopTrainingJob(
                        (StopTrainingJobRequest)setupRequest(
                            new StopTrainingJobRequest()
                                .withTrainingJobName(((CreateTrainingJobRequest)request).getTrainingJobName())
                        )
                    );
                }catch(Exception e){
                    throw new ScheduleStateControlException(e);
                }
                return true;
            }else if(request instanceof CreateTransformJobRequest){
                try{
                    client.stopTransformJob(
                        (StopTransformJobRequest)setupRequest(
                            new StopTransformJobRequest()
                                .withTransformJobName(((CreateTransformJobRequest)request).getTransformJobName())
                        )
                    );
                }catch(Exception e){
                    throw new ScheduleStateControlException(e);
                }
                return true;
            }else if(request instanceof CreateCompilationJobRequest){
                try{
                    client.stopCompilationJob(
                        (StopCompilationJobRequest)setupRequest(
                            new StopCompilationJobRequest()
                                .withCompilationJobName(((CreateCompilationJobRequest)request).getCompilationJobName())
                        )
                    );
                }catch(Exception e){
                    throw new ScheduleStateControlException(e);
                }
                return true;
            }else if(request instanceof CreateHyperParameterTuningJobRequest){
                try{
                    client.stopHyperParameterTuningJob(
                        (StopHyperParameterTuningJobRequest)setupRequest(
                            new StopHyperParameterTuningJobRequest()
                                .withHyperParameterTuningJobName(((CreateHyperParameterTuningJobRequest)request).getHyperParameterTuningJobName())
                        )
                    );
                }catch(Exception e){
                    throw new ScheduleStateControlException(e);
                }
                return true;
            }else if(request instanceof CreateLabelingJobRequest){
                try{
                    client.stopLabelingJob(
                        (StopLabelingJobRequest)setupRequest(
                            new StopLabelingJobRequest()
                                .withLabelingJobName(((CreateLabelingJobRequest)request).getLabelingJobName())
                        )
                    );
                }catch(Exception e){
                    throw new ScheduleStateControlException(e);
                }
                return true;
            }else if(request instanceof CreateNotebookInstanceRequest){
                try{
                    client.stopNotebookInstance(
                        (StopNotebookInstanceRequest)setupRequest(
                            new StopNotebookInstanceRequest()
                                .withNotebookInstanceName((((CreateNotebookInstanceRequest)request).getNotebookInstanceName()))
                        )
                    );
                }catch(Exception e){
                    throw new ScheduleStateControlException(e);
                }
                return true;
            }
        }
        return false;
    }
}