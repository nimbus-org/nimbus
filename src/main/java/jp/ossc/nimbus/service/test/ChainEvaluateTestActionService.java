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
package jp.ossc.nimbus.service.test;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link ChainEvaluateTestAction 連鎖評価テストアクション}実装サービス。<p>
 * テストアクションを連鎖させて実行して、最終的に実行結果を評価するテストアクションである。<br>
 * 
 * @author M.Ishida
 */
public class ChainEvaluateTestActionService extends ServiceBase implements ChainEvaluateTestActionServiceMBean, ChainEvaluateTestAction, FileEvaluateTestAction, TestActionEstimation {
    
    private static final long serialVersionUID = 2163058114046456244L;
    
    protected ServiceName[] actionServiceNames;
    protected ServiceName endEvaluateTestActionName;
    
    protected List actionList;
    
    protected EvaluateTestAction endEvaluateTestAction;
    protected ChainEvaluateTestAction.EvaluateTestActionProcess endEvaluateTestActionProcess;
    
    protected String targetFileName;
    protected String evidenceFileName;
    
    public ServiceName[] getActionServiceNames() {
        return actionServiceNames;
    }
    
    public void setActionServiceNames(ServiceName[] serviceNames) {
        actionServiceNames = serviceNames;
    }
    
    public ServiceName getEndEvaluateTestActionServiceName() {
        return endEvaluateTestActionName;
    }
    
    public void setEndEvaluateTestActionServiceName(ServiceName name) {
        endEvaluateTestActionName = name;
    }
    
    public void setEndEvaluateTestAction(EvaluateTestAction action) {
        endEvaluateTestAction = action;
    }
    
    public void setEndEvaluateTestActionProcess(ChainEvaluateTestAction.EvaluateTestActionProcess action) {
        endEvaluateTestActionProcess = action;
    }
    
    public String getEvaluateTargetFileName(){
        return targetFileName;
    }
    
    public String getEvaluateEvidenceFileName(){
        return evidenceFileName;
    }
    
    public void createService() throws Exception {
        actionList = new ArrayList();
    }
    
    public void startService() throws Exception {
        if(endEvaluateTestActionName != null){
            Object action = ServiceManagerFactory.getServiceObject(endEvaluateTestActionName);
            if(action instanceof EvaluateTestAction){
                endEvaluateTestAction = (EvaluateTestAction)action;
            }else if(action instanceof ChainEvaluateTestAction.EvaluateTestActionProcess){
                endEvaluateTestActionProcess = (ChainEvaluateTestAction.EvaluateTestActionProcess)action;
            }else{
                throw new IllegalArgumentException("EndEvaluateTestAction is illegal. action=" + action.getClass());
            }
        }
        if (endEvaluateTestAction == null && endEvaluateTestActionProcess == null) {
            throw new IllegalArgumentException("EndEvaluateTestAction is null.");
        }
        if (actionServiceNames != null) {
            actionList.clear();
            for (int i = 0; i < actionServiceNames.length; i++) {
                Object action = ServiceManagerFactory.getServiceObject(actionServiceNames[i]);
                if (!(action instanceof TestAction)
                    && !(action instanceof EvaluateTestAction)
                    && !(action instanceof ChainTestAction.TestActionProcess)
                    && !(action instanceof ChainEvaluateTestAction.EvaluateTestActionProcess)
                ) {
                    throw new IllegalArgumentException("ActionServiceNames[" + i + "] is not TestAction. ServiceName=" + actionServiceNames[i]);
                }
                actionList.add(action);
            }
        }
        if(endEvaluateTestAction != null){
            actionList.add(endEvaluateTestAction);
        }else{
            actionList.add(endEvaluateTestActionProcess);
        }
    }
    
    public boolean execute(TestContext context, String actionId, Reader[] resources) throws Exception {
        if (resources.length != actionList.size()) {
            throw new IllegalArgumentException("ResourceFile count is illegal. Actions count=" + actionList.size() + " ResourceFiles count="
                    + resources.length);
        }
        
        boolean actionResult = true;
        Object preResult = null;
        boolean isPreEvaluateAction = false;
        for (int i = 0, iMax = actionList.size(); i < iMax; i++) {
            Object action = actionList.get(i);
            String childActionId = actionId + '_' + (i + 1);
            resources[i].reset();
            if(i == 0){
                if(action instanceof TestAction) {
                    preResult = ((TestAction) action).execute(context, childActionId, resources[i]);
                    isPreEvaluateAction = false;
                }else if(action instanceof EvaluateTestAction) {
                    actionResult = ((EvaluateTestAction) action).execute(context, childActionId, resources[i]);
                    preResult = null;
                    isPreEvaluateAction = true;
                }else if(action instanceof ChainTestAction.TestActionProcess) {
                    preResult = ((ChainTestAction.TestActionProcess) action).execute(context, childActionId, preResult, resources[i]);
                    isPreEvaluateAction = false;
                }else if(action instanceof ChainEvaluateTestAction.EvaluateTestActionProcess){
                    actionResult = ((ChainEvaluateTestAction.EvaluateTestActionProcess) action).execute(context, childActionId, preResult, resources[i]);
                    preResult = null;
                    isPreEvaluateAction = true;
                }
            }else if(i == iMax - 1){
                if(action instanceof ChainEvaluateTestAction.EvaluateTestActionProcess){
                    if(action instanceof EvaluateTestAction && isPreEvaluateAction){
                        actionResult = ((EvaluateTestAction) action).execute(context, childActionId, resources[i]);
                    }else{
                        actionResult = ((ChainEvaluateTestAction.EvaluateTestActionProcess) action).execute(context, childActionId, preResult, resources[i]);
                    }
                }else if(action instanceof EvaluateTestAction) {
                    actionResult = ((EvaluateTestAction) action).execute(context, childActionId, resources[i]);
                }
                if(action instanceof FileEvaluateTestAction){
                    targetFileName = ((FileEvaluateTestAction)action).getEvaluateTargetFileName();
                    evidenceFileName = ((FileEvaluateTestAction)action).getEvaluateEvidenceFileName();
                }
            }else{
                if(isPreEvaluateAction){
                    if(action instanceof TestAction) {
                        preResult = ((TestAction) action).execute(context, childActionId, resources[i]);
                        isPreEvaluateAction = false;
                    }else if(action instanceof EvaluateTestAction) {
                        actionResult = ((EvaluateTestAction) action).execute(context, childActionId, resources[i]);
                        preResult = null;
                        isPreEvaluateAction = true;
                    }else if(action instanceof ChainTestAction.TestActionProcess) {
                        preResult = ((ChainTestAction.TestActionProcess) action).execute(context, childActionId, preResult, resources[i]);
                        isPreEvaluateAction = false;
                    }else if(action instanceof ChainEvaluateTestAction.EvaluateTestActionProcess){
                        actionResult = ((ChainEvaluateTestAction.EvaluateTestActionProcess) action).execute(context, childActionId, preResult, resources[i]);
                        preResult = null;
                        isPreEvaluateAction = true;
                    }
                }else{
                    if(action instanceof ChainTestAction.TestActionProcess) {
                        preResult = ((ChainTestAction.TestActionProcess) action).execute(context, childActionId, preResult, resources[i]);
                        isPreEvaluateAction = false;
                    }else if(action instanceof ChainEvaluateTestAction.EvaluateTestActionProcess){
                        actionResult = ((ChainEvaluateTestAction.EvaluateTestActionProcess) action).execute(context, childActionId, preResult, resources[i]);
                        preResult = null;
                        isPreEvaluateAction = true;
                    }else if(action instanceof TestAction) {
                        preResult = ((TestAction) action).execute(context, childActionId, resources[i]);
                        isPreEvaluateAction = false;
                    }else if(action instanceof EvaluateTestAction) {
                        actionResult = ((EvaluateTestAction) action).execute(context, childActionId, resources[i]);
                        preResult = null;
                        isPreEvaluateAction = true;
                    }
                }
            }
            if(!isPreEvaluateAction){
                context.setTestActionResult(childActionId, preResult);
            }
            if (!actionResult) {
                return false;
            }
        }
        return actionResult;
    }

    public double getExpectedCost(){
        if(actionList == null || actionList.size() == 0){
            return Double.NaN;
        }
        double result = Double.NaN;
        for(int i = 0; i < actionList.size(); i++){
            Object action = actionList.get(i);
            if(!(action instanceof TestActionEstimation)){
                continue;
            }
            double d = ((TestActionEstimation)action).getExpectedCost();
            if(!Double.isNaN(d)){
                if(Double.isNaN(result)){
                   result = ((TestActionEstimation)action).getExpectedCost();
                } else {
                    result += ((TestActionEstimation)action).getExpectedCost();
                }
            }
        }
        return result;
    }
}
