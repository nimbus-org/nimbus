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
import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.ossc.nimbus.core.ServiceBase;

/**
 * {@link RetryEvaluateTestAction リトライ評価テストアクション}実装サービス。<p>
 * テストアクションを連鎖させて実行して、実行結果の評価が失敗した場合に、リトライするテストアクションである。<br>
 * 
 * @author M.Ishida
 */
public class RetryEvaluateTestActionService extends ServiceBase implements RetryEvaluateTestActionServiceMBean, RetryEvaluateTestAction, TestActionEstimation{
    
    private static final long serialVersionUID = 2193587152227926994L;
    
    protected long defaultInterval;
    protected int defaultRetryCount;
    protected int retryMarkIndex;
    
    protected EvaluateTestAction endEvaluateTestAction;
    
    protected List actionList;
    
    public void setDefaultInterval(long interval){
        defaultInterval = interval;
    }
    public long getDefaultInterval(){
        return defaultInterval;
    }
    
    public void setDefaultRetryCount(int count){
        defaultRetryCount = count;
    }
    public int getDefaultRetryCount(){
        return defaultRetryCount;
    }
    
    public void addTestAction(TestAction action) {
        actionList.add(new TestActionContext(action, -1));
    }
    
    public void addEvaluateTestAction(EvaluateTestAction action, int type) {
        actionList.add(new TestActionContext(action, type));
    }
    
    public void setEndEvaluateTestAction(EvaluateTestAction action) {
        endEvaluateTestAction = action;
    }
    
    public void setRetryMarkIndex(int index){
        retryMarkIndex = index;
    }
    public int getRetryMarkIndex(){
        return retryMarkIndex;
    }
    
    public void createService() throws Exception {
        actionList = new ArrayList();
    }
    
    public void startService() throws Exception {
        if (endEvaluateTestAction == null) {
            throw new IllegalArgumentException("EndEvaluateTestAction is null.");
        }
        actionList.add(new TestActionContext(endEvaluateTestAction, -1));
        if(retryMarkIndex > actionList.size()){
            throw new IllegalArgumentException("RetryMarkIndex is illegal.");
        }
    }
    
    public boolean execute(TestContext context, String actionId, Reader[] resources, long paramInterval, int paramRetryCount) throws Exception {
        if (resources.length != actionList.size()) {
            throw new IllegalArgumentException("ResourceFile count is illegal. Actions count=" + actionList.size() + " ResourceFiles count="
                    + resources.length);
        }
        long interval = defaultInterval;
        int maxRetryCount = defaultRetryCount;
        if (paramInterval > 0) {
            interval = paramInterval;
        }
        if (paramRetryCount > 0) {
            maxRetryCount = paramRetryCount;
        }
        int retryCount = 0;
        boolean isBreak = false;
        boolean result = true;
        int retryIndex = 0;
        while (!isBreak) {
            Object preResult = null;
            for (int i = retryIndex, iMax = actionList.size(); i < iMax; i++) {
                if(i >= retryMarkIndex){
                    retryIndex = retryMarkIndex;
                }
                TestActionContext actionContext = (TestActionContext) actionList.get(i);
                Object action = actionContext.getAction();
                String childActionId = actionId + '_' + (i + 1);
                resources[i].reset();
                if (action instanceof ChainTestAction.TestActionProcess) {
                    Object ret = ((ChainTestAction.TestActionProcess) action).execute(context, childActionId, preResult, resources[i]);
                    context.setTestActionResult(childActionId, ret);
                    preResult = ret;
                }else if (action instanceof TestAction) {
                    Object ret = ((TestAction) action).execute(context, childActionId, resources[i]);
                    context.setTestActionResult(childActionId, ret);
                    preResult = ret;
                } else {
                    boolean actionResult = ((EvaluateTestAction) action).execute(context, childActionId, resources[i]);
                    if (i == iMax - 1) {
                        if (actionResult) {
                            result = true;
                            isBreak = true;
                            break;
                        } else {
                            if (retryCount < maxRetryCount) {
                                retryCount++;
                                Thread.sleep(interval);
                                break;
                            } else {
                                result = false;
                                isBreak = true;
                                break;
                            }
                        }
                        
                    } else {
                        if (!actionResult) {
                            if (actionContext.getType() == NG_TYPE_RETRY) {
                                if (retryCount < maxRetryCount) {
                                    retryCount++;
                                    Thread.sleep(interval);
                                    break;
                                } else {
                                    result = false;
                                    isBreak = true;
                                    break;
                                }
                            } else if (actionContext.getType() == NG_TYPE_RETURN) {
                                result = false;
                                isBreak = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    
    private class TestActionContext {
        
        private Object action;
        private int type;
        
        public TestActionContext(Object action, int type) {
            this.action = action;
            this.type = type;
        }
        
        public Object getAction() {
            return action;
        }
        
        public int getType() {
            return type;
        }
    }
    
    public double getExpectedCost(){
        if(actionList == null || actionList.size() == 0){
            return 0d;
        }
        double result = 0d;
        for(int i = 0; i < actionList.size(); i++){
            Object action = actionList.get(i);
            if(!(action instanceof TestActionEstimation)){
                continue;
            }
            result += ((TestActionEstimation)action).getExpectedCost();
        }
        return result;
    }
}
