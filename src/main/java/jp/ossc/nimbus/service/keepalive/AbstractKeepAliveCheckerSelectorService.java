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
package jp.ossc.nimbus.service.keepalive;

import java.util.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;

/**
 * {@link KeepAliveCheckerSelector}抽象クラス。<p>
 *
 * @author M.Takata
 */
public abstract class AbstractKeepAliveCheckerSelectorService
 extends ServiceBase
 implements KeepAliveCheckerSelector, DaemonRunnable, ClusterListener, 
            AbstractKeepAliveCheckerSelectorServiceMBean{
    
    private static final long serialVersionUID = 1452664941582086362L;
    
    /**
     * 選択可能な{@link KeepAliveChecker}サービスのサービス名配列。<p>
     */
    protected ServiceName[] selectableCheckerServiceNames;
    
    /**
     * 選択可能な{@link KeepAliveChecker}サービスのサービス名のリスト。<p>
     */
    protected List selectableCheckers;
    
    /**
     * 定期的に{@link KeepAliveChecker}に生存確認を行う間隔[ms]。<p>
     */
    protected long checkInterval = -1L;
    
    /**
     * 生存している{@link KeepAliveChecker}のサービス名またはUIDのリスト。<p>
     */
    protected List aliveCheckers;
    
    /**
     * 定期的に{@link KeepAliveChecker}に生存確認を行うデーモンスレッド。<p>
     */
    protected Daemon daemon;
    
    protected String aliveLogMessageId = DEFAULT_ALIVE_LOG_MSG_ID;
    
    protected String deadLogMessageId = DEFAULT_DEAD_LOG_MSG_ID;
    
    protected boolean isOutputAliveLogMessage = true;
    
    protected boolean isOutputDeadLogMessage = true;
    
    protected boolean isKeepOrder = false;
    
    protected ServiceName clusterServiceName;
    protected Cluster cluster;
    protected List clusterMembers;
    protected String clusterOptionKey;
    
    // AbstractKeepAliveCheckerSelectorServiceMBean のJavaDoc
    public void setSelectableCheckerServiceNames(ServiceName[] names){
        selectableCheckerServiceNames = names;
    }
    
    // AbstractKeepAliveCheckerSelectorServiceMBean のJavaDoc
    public ServiceName[] getSelectableCheckerServiceNames(){
        return selectableCheckerServiceNames;
    }
    
    // AbstractKeepAliveCheckerSelectorServiceMBean のJavaDoc
    public void setCheckInterval(long millis){
        checkInterval = millis;
    }
    // AbstractKeepAliveCheckerSelectorServiceMBean のJavaDoc
    public long getCheckInterval(){
        return checkInterval;
    }
    
    // AbstractKeepAliveCheckerSelectorServiceMBeanのJavaDoc
    public void setAliveLogMessageId(String id){
        aliveLogMessageId = id;
    }
    
    // AbstractKeepAliveCheckerSelectorServiceMBeanのJavaDoc
    public String getAliveLogMessageId(){
        return aliveLogMessageId;
    }
    
    // AbstractKeepAliveCheckerSelectorServiceMBeanのJavaDoc
    public void setDeadLogMessageId(String id){
        deadLogMessageId = id;
    }
    
    // AbstractKeepAliveCheckerSelectorServiceMBeanのJavaDoc
    public String getDeadLogMessageId(){
        return deadLogMessageId;
    }
    
    // AbstractKeepAliveCheckerSelectorServiceMBeanのJavaDoc
    public void setOutputAliveLogMessage(boolean isOutput){
        isOutputAliveLogMessage = isOutput;
    }
    
    // AbstractKeepAliveCheckerSelectorServiceMBeanのJavaDoc
    public boolean isOutputAliveLogMessage(){
        return isOutputAliveLogMessage;
    }
    
    // AbstractKeepAliveCheckerSelectorServiceMBeanのJavaDoc
    public void setOutputDeadLogMessage(boolean isOutput){
        isOutputDeadLogMessage = isOutput;
    }
    
    // AbstractKeepAliveCheckerSelectorServiceMBeanのJavaDoc
    public boolean isOutputDeadLogMessage(){
        return isOutputDeadLogMessage;
    }
    
    // AbstractKeepAliveCheckerSelectorServiceMBeanのJavaDoc
    public void setKeepOrder(boolean isKeep){
        isKeepOrder = isKeep;
    }
    
    // AbstractKeepAliveCheckerSelectorServiceMBeanのJavaDoc
    public boolean isKeepOrder(){
        return isKeepOrder;
    }
    
    // AbstractKeepAliveCheckerSelectorServiceMBeanのJavaDoc
    public void setClusterServiceName(ServiceName name){
        clusterServiceName = name;
    }
    // AbstractKeepAliveCheckerSelectorServiceMBeanのJavaDoc
    public ServiceName getClusterServiceName(){
        return clusterServiceName;
    }
    
    // AbstractKeepAliveCheckerSelectorServiceMBeanのJavaDoc
    public void setClusterOptionKey(String key){
        clusterOptionKey = key;
    }
    // AbstractKeepAliveCheckerSelectorServiceMBeanのJavaDoc
    public String getClusterOptionKey(){
        return clusterOptionKey;
    }
    
    public void createService() throws Exception{
        aliveCheckers = Collections.synchronizedList(new ArrayList());
        selectableCheckers = new ArrayList();
    }
    
    public void startService() throws Exception{
        if(clusterServiceName != null){
            cluster = (Cluster)ServiceManagerFactory.getServiceObject(clusterServiceName);
            cluster.addClusterListener(this);
        }else{
            if(selectableCheckerServiceNames != null){
                selectableCheckers.clear();
                for(int i = 0; i < selectableCheckerServiceNames.length; i++){
                    selectableCheckers.add(selectableCheckerServiceNames[i]);
                }
            }
        }
        if(checkInterval > 0){
            daemon = new Daemon(this);
            daemon.setName(
                "Nimbus KeepAliveCheckDaemon " + getServiceNameObject()
            );
            daemon.start();
        }
    }
    
    public void stopService() throws Exception{
        if(cluster != null){
            cluster.removeClusterListener(this);
            cluster = null;
        }
        if(daemon != null){
            // デーモン停止
            daemon.stop();
        }
        aliveCheckers.clear();
    }
    
    public void destroyService() throws Exception{
        aliveCheckers = null;
    }
    
    // KeepAliveCheckerSelector のJavaDoc
    public KeepAliveChecker[] getSelectableCheckers(){
        if(checkInterval <= 0){
            updateChekerStates(false);
        }
        if(cluster != null){
            List members = aliveCheckers;
            if(members == null || members.size() == 0){
                return new KeepAliveChecker[0];
            }
            List list = new ArrayList();
            for(int i = 0, imax = members.size(); i < imax; i++){
                ClusterUID uid = (ClusterUID)members.get(i);
                Object option = clusterOptionKey == null ? uid.getOption() : uid.getOption(clusterOptionKey);
                if(option != null && option instanceof KeepAliveChecker){
                    list.add(option);
                }
            }
            return (KeepAliveChecker[])list.toArray(new KeepAliveChecker[list.size()]);
        }else{
            ServiceName[] names = (ServiceName[])aliveCheckers.toArray(
                new ServiceName[aliveCheckers.size()]
            );
            final List list = new ArrayList();
            KeepAliveChecker checker = null;
            for(int i = 0; i < names.length; i++){
                try{
                    Service service = ServiceManagerFactory.getService(names[i]);
                    if(service.getState() != Service.STARTED){
                        continue;
                    }
                    checker = (KeepAliveChecker)ServiceManagerFactory
                        .getServiceObject(names[i]);
                    list.add(checker);
                }catch(ServiceNotFoundException e){
                    continue;
                }
            }
            return (KeepAliveChecker[])list.toArray(new KeepAliveChecker[list.size()]);
        }
    }
    
    public List getAliveCheckers(){
        return aliveCheckers;
    }
    
    protected void updateChekerStates(boolean init){
        if(cluster != null){
            List members = clusterMembers;
            if(members == null || members.size() == 0){
                return;
            }
            final List tmpAliveCheckers = Collections.synchronizedList(new ArrayList());
            for(int i = 0, imax = members.size(); i < imax; i++){
                ClusterUID uid = (ClusterUID)members.get(i);
                Object option = clusterOptionKey == null ? uid.getOption() : uid.getOption(clusterOptionKey);
                if(option == null || !(option instanceof KeepAliveChecker)){
                    continue;
                }
                KeepAliveChecker checker = (KeepAliveChecker)option;
                if(checker.isAlive()){
                    tmpAliveCheckers.add(uid);
                    if(!aliveCheckers.contains(uid)){
                        if(!init && isOutputAliveLogMessage){
                            getLogger().write(
                                aliveLogMessageId,
                                uid
                            );
                        }
                    }
                }else{
                    if(aliveCheckers.contains(uid)){
                        if(!init && isOutputDeadLogMessage){
                            getLogger().write(
                                deadLogMessageId,
                                uid
                            );
                        }
                    }
                }
            }
            aliveCheckers = tmpAliveCheckers;
        }else{
            if(aliveCheckers == null){
                return;
            }
            final List tmpAliveCheckers = Collections.synchronizedList(
                new ArrayList(aliveCheckers)
            );
            for(int i = 0; i < selectableCheckerServiceNames.length; i++){
                try{
                    Service service = ServiceManagerFactory
                        .getService(selectableCheckerServiceNames[i]);
                    final KeepAliveChecker checker
                         = (KeepAliveChecker)ServiceManagerFactory
                            .getServiceObject(selectableCheckerServiceNames[i]);
                    if(service.getState() == Service.STARTED
                        && checker.isAlive()){
                        if(!tmpAliveCheckers.contains(selectableCheckerServiceNames[i])){
                            tmpAliveCheckers.add(selectableCheckerServiceNames[i]);
                            if(!init && isOutputAliveLogMessage){
                                getLogger().write(
                                    aliveLogMessageId,
                                    selectableCheckerServiceNames[i]
                                );
                            }
                        }
                    }else{
                        if(tmpAliveCheckers.contains(selectableCheckerServiceNames[i])){
                            tmpAliveCheckers.remove(selectableCheckerServiceNames[i]);
                            if(!init && isOutputDeadLogMessage){
                                getLogger().write(
                                    deadLogMessageId,
                                    selectableCheckerServiceNames[i]
                                );
                            }
                        }
                    }
                }catch(ServiceNotFoundException e){
                    tmpAliveCheckers.remove(selectableCheckerServiceNames[i]);
                }
            }
            if(isKeepOrder){
                Collections.sort(
                    tmpAliveCheckers,
                    new SelectableCheckerComparator()
                );
            }
            aliveCheckers = tmpAliveCheckers;
        }
    }
    
    // ClusterListenerのJavaDoc
    public void memberInit(Object myId, List members){
        List tmpMembers = new ArrayList(members);
        if(isKeepOrder){
            Collections.sort(tmpMembers);
        }
        clusterMembers = tmpMembers;
        updateChekerStates(true);
    }
    
    // ClusterListenerのJavaDoc
    public void memberChange(List oldMembers, List newMembers){
        List addedMember = new ArrayList(newMembers);
        addedMember.removeAll(oldMembers);
        if(addedMember.size() != 0 && isOutputAliveLogMessage){
            for(int i = 0; i < addedMember.size(); i++){
                getLogger().write(aliveLogMessageId, addedMember.get(i));
            }
        }
        List removedMember = new ArrayList(oldMembers);
        removedMember.removeAll(newMembers);
        if(removedMember.size() != 0 && isOutputDeadLogMessage){
            for(int i = 0, imax = removedMember.size(); i < imax; i++){
                getLogger().write(
                    deadLogMessageId,
                    removedMember.get(i)
                );
            }
        }
        List tmpMembers = new ArrayList(newMembers);
        if(isKeepOrder){
            Collections.sort(tmpMembers);
        }
        clusterMembers = tmpMembers;
    }
    
    // ClusterListenerのJavaDoc
    public void changeMain() throws Exception{}
    
    // ClusterListenerのJavaDoc
    public void changeSub(){}
    
    // DaemonRunnableのJavaDoc
    public boolean onStart(){
        updateChekerStates(true);
        return true;
    }
    
    // DaemonRunnableのJavaDoc
    public boolean onStop(){
        return true;
    }
    
    // DaemonRunnableのJavaDoc
    public boolean onSuspend(){
        return true;
    }
    
    // DaemonRunnableのJavaDoc
    public boolean onResume(){
        return true;
    }
    
    // DaemonRunnableのJavaDoc
    public Object provide(DaemonControl ctrl) throws Exception{
        if(checkInterval >= 0){
            Thread.sleep(checkInterval);
        }else{
            ctrl.setRunning(false);
        }
        return null;
    }
    
    // DaemonRunnableのJavaDoc
    public void consume(Object paramObj, DaemonControl ctrl) throws Exception{
        updateChekerStates(false);
    }
    
    // DaemonRunnableのJavaDoc
    public void garbage(){
    }
    
    protected class SelectableCheckerComparator implements Comparator{
        public int compare(Object o1, Object o2){
            if(o1 == o2){
                return 0;
            }
            if(selectableCheckers == null || selectableCheckers.size() == 0){
                if(o1 != null && o1 instanceof Comparable){
                    return ((Comparable)o1).compareTo(o2);
                }else{
                    return 0;
                }
            }
            final int index1 = selectableCheckers.indexOf(o1);
            final int index2 = selectableCheckers.indexOf(o2);
            return index1 - index2;
        }
    }
}
