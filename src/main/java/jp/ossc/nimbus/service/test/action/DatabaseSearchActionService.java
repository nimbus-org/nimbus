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
package jp.ossc.nimbus.service.test.action;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Connection;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.test.TestContext;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.connection.ConnectionFactory;
import jp.ossc.nimbus.service.connection.PersistentManager;

/**
 * �f�[�^�x�[�X����������e�X�g�A�N�V�����B<p>
 * ����̏ڍׂ́A{@link #execute(TestContext, String, Reader)}���Q�ƁB<br>
 * 
 * @author M.Takata
 */
public class DatabaseSearchActionService extends ServiceBase implements TestAction,TestActionEstimation, DatabaseSearchActionServiceMBean{
    
    private static final long serialVersionUID = 8193135412984495251L;
    
    protected ServiceName connectionFactoryServiceName;
    protected ConnectionFactory connectionFactory;
    protected ServiceName persistentManagerServiceName;
    protected PersistentManager persistentManager;
    protected double expectedCost = 0d;
    
    public void setConnectionFactoryServiceName(ServiceName name){
        connectionFactoryServiceName = name;
    }
    public ServiceName getConnectionFactoryServiceName(){
        return connectionFactoryServiceName;
    }
    
    public void setPersistentManagerServiceName(ServiceName name){
        persistentManagerServiceName = name;
    }
    public ServiceName getPersistentManagerServiceName(){
        return persistentManagerServiceName;
    }
    
    public void setConnectionFactory(ConnectionFactory factory){
        connectionFactory = factory;
    }
    
    public void setPersistentManager(PersistentManager manager){
        persistentManager = manager;
    }
    
    public void startService() throws Exception{
        if(connectionFactoryServiceName != null){
            connectionFactory = (ConnectionFactory)ServiceManagerFactory.getServiceObject(connectionFactoryServiceName);
        }
        if(connectionFactory == null){
            throw new IllegalArgumentException("ConnectionFactory is null.");
        }
        if(persistentManagerServiceName != null){
            persistentManager = (PersistentManager)ServiceManagerFactory.getServiceObject(persistentManagerServiceName);
        }
        if(persistentManager == null){
            throw new IllegalArgumentException("PersistentManager is null.");
        }
    }
    
    /**
     * ���\�[�X�̓��e��ǂݍ���ŁA�f�[�^�x�[�X�Ɍ����N�G���𔭍s����B<p>
     * ���\�[�X�̃t�H�[�}�b�g�́A�ȉ��B<br>
     * <pre>
     * inputId
     * outputId
     * searchQuery
     * </pre>
     * inputId�́A{@link PersistentManager}�ɓn�������N�G���ɑ΂�����̓I�u�W�F�N�g���w�肷����̂ŁA����e�X�g�P�[�X���ɁA����TestAction���O�ɁA���̓I�u�W�F�N�g��߂��e�X�g�A�N�V���������݂���ꍇ�́A���̃A�N�V����ID���w�肷��B�܂��A����V�i���I���ɁA����TestAction���O�ɁA���̓I�u�W�F�N�g��߂��e�X�g�A�N�V���������݂���ꍇ�́A�e�X�g�P�[�XID�ƃA�N�V����ID���J���}��؂�Ŏw�肷��B��s���w�肵���ꍇ�́Anull�B<br>
     * outputId�́A{@link PersistentManager}�ɓn�������N�G���ɑ΂���o�̓I�u�W�F�N�g���w�肷����̂ŁA����e�X�g�P�[�X���ɁA����TestAction���O�ɁA�o�̓I�u�W�F�N�g��߂��e�X�g�A�N�V���������݂���ꍇ�́A���̃A�N�V����ID���w�肷��B�܂��A����V�i���I���ɁA����TestAction���O�ɁA�o�̓I�u�W�F�N�g��߂��e�X�g�A�N�V���������݂���ꍇ�́A�e�X�g�P�[�XID�ƃA�N�V����ID���J���}��؂�Ŏw�肷��B��s���w�肵���ꍇ�́Anull�B<br>
     * searchQuery�́A{@link PersistentManager}�ɓn�������N�G�����w�肷��B<br>
     *
     * @param context �R���e�L�X�g
     * @param actionId �A�N�V����ID
     * @param resource ���\�[�X
     * @return �������ʃI�u�W�F�N�g
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        String searchQuery = null;
        Object input = null;
        Object output = null;
        try{
            final String inputId = br.readLine();
            if(inputId == null){
                throw new Exception("Unexpected EOF on inputId");
            }
            if(inputId.length() != 0){
                Object actionResult = null;
                if(inputId.indexOf(",") == -1){
                    actionResult = context.getTestActionResult(inputId);
                }else{
                    String[] ids = inputId.split(",");
                    if(ids.length != 2){
                        throw new Exception("Illegal inputId format. id=" + inputId);
                    }
                    actionResult = context.getTestActionResult(ids[0], ids[1]);
                }
                if(actionResult == null){
                    throw new Exception("TestActionResult not found. id=" + inputId);
                }
                input = actionResult;
            }
            final String outputId = br.readLine();
            if(outputId == null){
                throw new Exception("Unexpected EOF on outputId");
            }
            if(outputId.length() != 0){
                Object actionResult = null;
                if(outputId.indexOf(",") == -1){
                    actionResult = context.getTestActionResult(outputId);
                }else{
                    String[] ids = outputId.split(",");
                    if(ids.length != 2){
                        throw new Exception("Illegal outputId format. id=" + outputId);
                    }
                    actionResult = context.getTestActionResult(ids[0], ids[1]);
                }
                if(actionResult == null){
                    throw new Exception("TestActionResult not found. id=" + outputId);
                }
                output = actionResult;
            }
            String line = br.readLine();
            if(line == null){
                throw new Exception("Unexpected EOF on searchQuery");
            }
            final StringBuilder buf = new StringBuilder();
            do{
                line = line.trim();
                if(line.length() != 0){
                    if(buf.length() != 0){
                        buf.append(' ');
                    }
                    buf.append(line);
                }
            }while((line = br.readLine()) != null);
            if(buf.length() != 0){
                searchQuery = buf.toString().trim();
                buf.setLength(0);
            }
            if(searchQuery == null || searchQuery.length() == 0){
                throw new Exception("Unexpected EOF on searchQuery");
            }
        }finally{
            br.close();
            br = null;
        }
        Object result = null;
        Connection con = connectionFactory.getConnection();
        try{
            result = persistentManager.loadQuery(con, searchQuery, input, output);
        }finally{
            con.close();
        }
        return result;
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
}
