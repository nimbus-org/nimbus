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

import jp.ossc.nimbus.core.Service;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link ChainTestAction 連鎖テストアクション}実装サービス。<p>
 * テストアクションを連鎖させて実行するテストアクションである。<br>
 * 
 * @author M.Ishida
 */
public class ChainTestActionService extends ServiceBase implements ChainTestActionServiceMBean, ChainTestAction, TestActionEstimation {
    
    private static final long serialVersionUID = -6007344125808912954L;
    
    protected ServiceName[] actionServiceNames;
    protected Object[] actions;
    
    public ServiceName[] getActionServiceNames() {
        return actionServiceNames;
    }
    
    public void setActionServiceNames(ServiceName[] serviceNames) {
        actionServiceNames = serviceNames;
    }
    
    public Object[] getActions() {
        return actions;
    }
    
    public void setActions(Service[] actions) {
        this.actions = actions;
    }
    
    public void startService() throws Exception {
        if (actionServiceNames != null) {
            actions = new Object[actionServiceNames.length];
            for (int i = 0; i < actionServiceNames.length; i++) {
                Object action = ServiceManagerFactory.getServiceObject(actionServiceNames[i]);
                if (!(action instanceof TestAction) && !(action instanceof TestActionProcess)) {
                    throw new IllegalArgumentException("ActionServiceNames[" + i + "] is not TestAction or TestActionProcess. ServiceName=" + actionServiceNames[i]);
                }
                actions[i] = action;
            }
        }
        if (actions == null || actions.length == 0) {
            throw new IllegalArgumentException("Actions is null.");
        }
    }
    
    public Object execute(TestContext context, String actionId, Reader[] resources) throws Exception {
        if (resources.length != actions.length) {
            throw new IllegalArgumentException("ResourceFile count is illegal. Actions count=" + actions.length + " ResourceFiles count="
                    + resources.length);
        }
        Object result = null;
        for (int i = 0; i < actions.length; i++) {
            String childActionId = actionId + '_' + (i + 1);
            resources[i].reset();
            if (actions[i] instanceof ChainTestAction.TestActionProcess) {
                result = ((TestActionProcess) actions[i]).execute(context, childActionId, result, resources[i]);
            } else {
                result = ((TestAction) actions[i]).execute(context, childActionId, resources[i]);
            }
            context.setTestActionResult(childActionId, result);
        }
        return result;
    }

    public double getExpectedCost(){
        if(actions == null || actions.length == 0){
            return Double.NaN;
        }
        double result = Double.NaN;
        for(int i = 0; i < actions.length; i++){
            if(!(actions[i] instanceof TestActionEstimation)){
                continue;
            }
            double d = ((TestActionEstimation)actions[i]).getExpectedCost();
            if(!Double.isNaN(d)){
                if(Double.isNaN(result)){
                   result = ((TestActionEstimation)actions[i]).getExpectedCost();
                } else {
                    result += ((TestActionEstimation)actions[i]).getExpectedCost();
                }
            }
        }
        return result;
    }
}
