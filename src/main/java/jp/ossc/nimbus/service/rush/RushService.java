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
package jp.ossc.nimbus.service.rush;

import java.util.*;
import java.io.*;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.beans.dataset.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.io.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.keepalive.Cluster;
import jp.ossc.nimbus.service.publish.ServerConnectionFactory;
import jp.ossc.nimbus.service.publish.ServerConnection;
import jp.ossc.nimbus.service.publish.RequestServerConnection;
import jp.ossc.nimbus.service.publish.RequestMessageListener;
import jp.ossc.nimbus.service.publish.Message;
import jp.ossc.nimbus.service.publish.MessageReceiver;
import jp.ossc.nimbus.service.publish.RequestConnectionFactoryService;
import jp.ossc.nimbus.util.converter.*;
import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * ラッシュサービス。<p>
 *
 * @author M.Takata
 */
public class RushService extends ServiceBase implements RushServiceMBean{
    
    private int clientSize = 1;
    private int roopCount;
    private long rushTime;
    private long requestInterval = 0;
    private long roopInterval = 0;
    private long roopTime = 0;
    private int connectStepSize = 0;
    private long connectStepInterval = 0;
    private double roopPerSecond;
    private double requestPerSecond;
    private File roopLock;
    private File requestLock;
    private ServiceName rushClientFactoryServiceName;
    private ServiceName clusterServiceName;
    private ServiceName requestConnectionFactoryServiceName;
    private String subject = "Rush";
    private int rushMemberSize = 1;
    private Request connectRequest;
    private int connectRetryCount = 0;
    private long connectRetryInterval = 0;
    private List requests;
    private Request closeRequest;
    private boolean isStartRushOnStart;
    
    private Cluster cluster;
    private ServerConnectionFactory requestConnectionFactory;
    private MessageReceiver messageReceiver;
    private boolean isRushing;
    private Daemon[] rushThreads;
    
    public void setClientSize(int size){
        clientSize = size;
    }
    public int getClientSize(){
        return clientSize;
    }
    
    public void setRoopCount(int count){
        roopCount = count;
    }
    public int getRoopCount(){
        return roopCount;
    }
    
    public void setRushTime(long time){
        rushTime = time;
    }
    public long getRushTime(){
        return rushTime;
    }
    
    public void setRequestInterval(long interval){
        requestInterval = interval;
    }
    public long getRequestInterval(){
        return requestInterval;
    }
    
    public void setRoopInterval(long interval){
        roopInterval = interval;
    }
    public long getRoopInterval(){
        return roopInterval;
    }
    
    public void setRoopTime(long time){
        roopTime = time;
    }
    public long getRoopTime(){
        return roopTime;
    }
    
    public void setConnectStepSize(int size){
        connectStepSize = size;
    }
    public int getConnectStepSize(){
        return connectStepSize;
    }
    
    public void setConnectStepInterval(long interval){
        connectStepInterval = interval;
    }
    public long getConnectStepInterval(){
        return connectStepInterval;
    }
    
    public void setRoopPerSecond(double rps){
        this.roopPerSecond = rps;
    }
    public double getRoopPerSecond(){
        return roopPerSecond;
    }
    
    public void setRequestPerSecond(double tps){
        this.requestPerSecond = tps;
    }
    public double getRequestPerSecond(){
        return requestPerSecond;
    }
    
    public void setRoopLock(File file){
        roopLock = file;
    }
    public File getRoopLock(){
        return roopLock;
    }
    
    public void setRequestLock(File file){
        requestLock = file;
    }
    public File getRequestLock(){
        return requestLock;
    }
    
    public void setRushClientFactoryServiceName(ServiceName name){
        rushClientFactoryServiceName = name;
    }
    public ServiceName getRushClientFactoryServiceName(){
        return rushClientFactoryServiceName;
    }
    
    public void setClusterServiceName(ServiceName name){
        clusterServiceName = name;
    }
    public ServiceName getClusterServiceName(){
        return clusterServiceName;
    }
    
    public void setRushMemberSize(int size){
        rushMemberSize = size;
    }
    public int getRushMemberSize(){
        return rushMemberSize;
    }
    
    public void setRequestConnectionFactoryServiceName(ServiceName name){
        requestConnectionFactoryServiceName = name;
    }
    public ServiceName getRequestConnectionFactoryServiceName(){
        return requestConnectionFactoryServiceName;
    }
    
    public void setSubject(String subject){
        this.subject = subject;
    }
    public String getSubject(){
        return subject;
    }
    
    public boolean isStartRushOnStart(){
        return isStartRushOnStart;
    }
    public void setStartRushOnStart(boolean isStart){
        isStartRushOnStart = isStart;
    }
    
    public void setConnectRequest(Request request){
        connectRequest = request;
    }
    
    public void setRequests(Request[] requests){
        this.requests.clear();
        for(int i = 0; i < requests.length; i++){
            this.requests.add(requests[i]);
        }
    }
    
    public void addRequest(Request request){
        requests.add(request);
    }
    
    public void setCloseRequest(Request request){
        closeRequest = request;
    }
    
    public void createService() throws Exception{
        requests = new LinkedList();
    }
    
    public void startService() throws Exception{
        
        if(rushClientFactoryServiceName == null){
            throw new IllegalArgumentException("RushClientFactoryServiceName is null");
        }
        
        if(requestConnectionFactoryServiceName != null){
            requestConnectionFactory = (ServerConnectionFactory)ServiceManagerFactory.getServiceObject(requestConnectionFactoryServiceName);
            messageReceiver = (MessageReceiver)ServiceManagerFactory.getServiceObject(requestConnectionFactoryServiceName);
            if(clusterServiceName == null){
                throw new IllegalArgumentException("ClusterServiceName is null");
            }
            cluster = (Cluster)ServiceManagerFactory.getServiceObject(clusterServiceName);
        }
    }
    
    protected void postStartService() throws Exception{
        super.postStartService();
        if(isStartRushOnStart){
            startRush();
        }
    }
    
    public void stopService() throws Exception{
        stopRush();
    }
    
    public void startRush() throws Exception{
        if(getState() != STARTED || isRushing){
            return;
        }
        isRushing = true;
        
        try{
            rushThreads = new Daemon[clientSize];
            for(int i = 0; i < clientSize; i++){
                RushClient client = (RushClient)ServiceManagerFactory.getServiceObject(rushClientFactoryServiceName);
                client.setId(i);
                if(messageReceiver != null){
                    client.setNodeId(messageReceiver.getId());
                }
                if(i == 0){
                    int requestId = 0;
                    if(connectRequest != null){
                        connectRequest.init(client, requestId++);
                    }
                    if(requests != null){
                        Iterator itr = requests.iterator();
                        while(itr.hasNext()){
                            ((Request)itr.next()).init(client, requestId++);
                        }
                    }
                    if(closeRequest != null){
                        closeRequest.init(client, requestId++);
                    }
                }
                rushThreads[i] = new Daemon(
                    new RushTask(client)
                );
                rushThreads[i].setName(
                    getServiceName() + " Rush thread[" + i + "]"
                );
            }
            RequestServerConnection connection = null;
            if(requestConnectionFactory != null){
                connection = (RequestServerConnection)requestConnectionFactory.getServerConnection();
            }
            SynchronizeMonitor monitor = null;
            if(connection != null){
                if(cluster.isMain()){
                    messageReceiver.addSubject(new MyMessageListener(connection), subject);
                }else{
                    monitor = new WaitSynchronizeMonitor();
                    monitor.initMonitor();
                    messageReceiver.addSubject(new MyMessageListener(connection, monitor), subject);
                    getLogger().write("RS___00001", messageReceiver.getId());
                    try{
                        monitor.waitMonitor();
                    }catch(InterruptedException e){
                    }
                    monitor.releaseMonitor();
                }
            }
            if(connectStepSize > 0){
                int connectSize = 0;
                for(int i = 0; i < rushThreads.length; i++){
                    if(connectSize >= connectStepSize){
                        connectSize = 0;
                        try{
                            Thread.sleep(connectStepInterval);
                        }catch(InterruptedException e){
                        }
                        if(!isRushing){
                            return;
                        }
                    }
                    rushThreads[i].start();
                    connectSize++;
                }
            }else{
                for(int i = 0; i < rushThreads.length; i++){
                    rushThreads[i].start();
                }
            }
            
            if(connection != null && cluster.isMain()){
                int memberSize = 0;
                Message message = null;
                Set clients = null;
                do{
                    try{
                        Thread.sleep(1000l);
                    }catch(InterruptedException e){
                    }
                    message = connection.createMessage(subject, null);
                    clients = connection.getReceiveClientIds(message);
                }while(clients.size() < rushMemberSize - 1);
                Iterator itr = clients.iterator();
                while(itr.hasNext()){
                    Object clientId = itr.next();
                    message = connection.createMessage(subject, null);
                    message.addDestinationId(clientId);
                    getLogger().write("RS___00006", clientId);
                    connection.request(message, 1, 0);
                    getLogger().write("RS___00007", clientId);
                }
            }else if(monitor != null){
                monitor.notifyMonitor();
            }
            
            if(rushThreads != null){
                synchronized(rushThreads){
                    if(rushThreads != null){
                        try{
                            for(int i = 0; i < rushThreads.length; i++){
                                if(rushThreads[i].getDaemonThread() != null){
                                    rushThreads[i].getDaemonThread().join();
                                }
                            }
                            rushThreads = null;
                        }catch(InterruptedException e){
                        }
                    }
                }
            }
        }finally{
            isRushing = false;
        }
    }
    
    public void stopRush(){
        if(!isRushing){
            return;
        }
        isRushing = false;
        if(rushThreads != null){
            for(int i = 0; i < rushThreads.length; i++){
                if(rushThreads[i] != null){
                    rushThreads[i].stopNoWait();
                }
            }
            if(rushThreads != null){
                synchronized(rushThreads){
                    if(rushThreads != null){
                        try{
                            for(int i = 0; i < rushThreads.length; i++){
                                if(rushThreads[i].getDaemonThread() != null){
                                    rushThreads[i].getDaemonThread().join();
                                }
                            }
                            rushThreads = null;
                        }catch(InterruptedException e){
                        }
                    }
                }
            }
        }
    }
    
    public class MyMessageListener implements RequestMessageListener{
        private ServerConnection connection;
        private SynchronizeMonitor monitor;
        
        public MyMessageListener(ServerConnection connection){
            this.connection = connection;
        }
        
        public MyMessageListener(ServerConnection connection, SynchronizeMonitor monitor){
            this.connection = connection;
            this.monitor = monitor;
        }
        
        public void onMessage(Message message){
        }
        
        public Message onRequestMessage(Object sourceId, int sequence, Message message, String responseSubject, String responseKey){
            if(monitor == null){
                return null;
            }
            getLogger().write("RS___00003", sourceId);
            monitor.initMonitor();
            monitor.notifyMonitor();
            try{
                monitor.waitMonitor();
            }catch(InterruptedException e){
            }
            monitor.releaseMonitor();
            getLogger().write("RS___00004", sourceId);
            try{
                return connection.createMessage(responseSubject, responseKey);
            }catch(Exception e){
                getLogger().write("RS___00005", sourceId, e);
                return null;
            }
        }
    }
    
    private class RushTask implements DaemonRunnable{
        
        private final RushClient client;
        private Random randomSeed;
        private int count = 0;
        private long startTime;
        private long processTime = 0;
        private int totalRequestCount = 0;
        private final List myRequests = new ArrayList();
        
        public RushTask(RushClient client){
            this.client = client;
        }
        
        public boolean onStart(){
            myRequests.clear();
            count = 0;
            processTime = 0;
            if(requests != null && requests.size() != 0){
                Iterator itr = requests.iterator();
                while(itr.hasNext()){
                    Request request = (Request)itr.next();
                    if(request.count == 0){
                        continue;
                    }
                    myRequests.add(request);
                }
            }
            int retry = 0;
            while(true){
                try{
                    client.connect(connectRequest);
                    break;
                }catch(Exception e){
                    if(connectRetryCount > retry){
                        try{
                            if(connectRetryInterval > 0){
                                Thread.sleep(connectRetryInterval);
                            }
                            retry++;
                            continue;
                        }catch(InterruptedException e2){
                        }
                    }
                    getLogger().write(
                        "ERROR",
                        "接続に失敗しました。",
                        e
                    );
                    return false;
                }
            };
            startTime = System.currentTimeMillis();
            getLogger().write("RS___00002", client.getId());
            return true;
        }
        
        public boolean onStop(){return true;}
        
        public boolean onSuspend(){return true;}
        
        public boolean onResume(){return true;}
        
        public Object provide(DaemonControl ctrl) throws Throwable{
            if(isRushing
                && myRequests.size() > 0
                && ((roopCount > 0 && count < roopCount)
                    || (rushTime > 0 && processTime < rushTime))
            ){
                if(roopLock != null){
                    while(roopLock.exists()){
                        try{
                            Thread.sleep(1000);
                        }catch(InterruptedException e){
                            if(getState() != STARTED){
                                ctrl.setRunning(false);
                                return null;
                            }
                        }
                    }
                }
                final List currentRequests = new ArrayList();
                RandomGroup randomGroup = null;
                totalRequestCount = 0;
                for(int i = 0, imax = myRequests.size(); i < imax; i++){
                    Request req = (Request)myRequests.get(i);
                    req.resetCount();
                    totalRequestCount += req.sequenceCount > 0 ? (req.count * req.sequenceCount) : req.count;
                    if(req.randomGroup != null){
                        if(randomGroup == null
                            || !randomGroup.getName().equals(req.randomGroup)
                        ){
                            randomGroup = new RandomGroup(req.randomGroup);
                            currentRequests.add(randomGroup);
                        }
                        if(req.sequenceGroup != null){
                            RandomGroup.SequenceGroup sequenceGroup = randomGroup.getSequenceGroup(req.sequenceGroup);
                            sequenceGroup.add(req);
                        }else{
                            randomGroup.add(req);
                        }
                    }else{
                        randomGroup = null;
                        currentRequests.add(req);
                    }
                }
                return currentRequests;
            }else{
                ctrl.setRunning(false);
                return null;
            }
        }
        
        public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
            List currentRequests = (List)paramObj;
            if(currentRequests == null){
                return;
            }
            long myRoopProcessTime = roopTime;
            if(roopPerSecond > 0){
                myRoopProcessTime = (long)((double)(1000l * clientSize) / roopPerSecond);
            }
            int requestCount = 0;
            final long roopStartTime = System.currentTimeMillis();
            if(requestPerSecond > 0){
                myRoopProcessTime = (long)((double)(1000l * clientSize * totalRequestCount) / requestPerSecond);
            }
            RandomGroup randomGroup = null;
            while(isRushing && currentRequests.size() != 0){
                final Iterator itr = currentRequests.iterator();
                while(isRushing && itr.hasNext()){
                    Object req = itr.next();
                    if(req instanceof RandomGroup){
                        randomGroup = (RandomGroup)req;
                        if(randomSeed == null){
                            randomSeed = new Random();
                        }
                        req = randomGroup.random(randomSeed);
                    }else{
                        randomGroup = null;
                    }
                    if(req instanceof RandomGroup.SequenceGroup){
                        RandomGroup.SequenceGroup sequenceGroup = (RandomGroup.SequenceGroup)req;
                        boolean isError = false;
                        Iterator sequenceItr = sequenceGroup.iterator();
                        while(isRushing && sequenceItr.hasNext()){
                            Request request = (Request)sequenceItr.next();
                            if(request.sequenceCount > 0){
                                for(int i = 0, imax = request.sequenceCount; i < imax; i++){
                                    if(isError){
                                        if(i == imax - 1){
                                            request.countDown();
                                        }
                                    }else{
                                        isError |= !request(
                                            count,
                                            request,
                                            i == imax - 1,
                                            i,
                                            requestInterval > 0 ? requestInterval : calcTPSInterval(totalRequestCount, requestCount, myRoopProcessTime, System.currentTimeMillis() - roopStartTime)
                                        );
                                        requestCount++;
                                    }
                                }
                            }else{
                                if(isError){
                                    request.countDown();
                                }else{
                                    isError |= !request(
                                        count,
                                        request,
                                        requestInterval > 0 ? requestInterval : calcTPSInterval(totalRequestCount, requestCount, myRoopProcessTime, System.currentTimeMillis() - roopStartTime)
                                    );
                                    requestCount++;
                                }
                            }
                            if(!request.isRemainCount()){
                                sequenceItr.remove();
                                request.resetCount();
                            }
                        }
                        if(sequenceGroup.size() == 0){
                            randomGroup.remove(sequenceGroup);
                            if(randomGroup.size() == 0){
                                itr.remove();
                            }
                        }
                    }else{
                        Request request = (Request)req;
                        final long st = System.currentTimeMillis();
                        request(
                            count,
                            request,
                            requestInterval > 0 ? requestInterval : calcTPSInterval(totalRequestCount, requestCount, myRoopProcessTime, System.currentTimeMillis() - roopStartTime)
                        );
                        requestCount++;
                        if(!request.isRemainCount()){
                            if(randomGroup == null){
                                itr.remove();
                            }else{
                                randomGroup.remove(request);
                                if(randomGroup.size() == 0){
                                    itr.remove();
                                }
                            }
                            request.resetCount();
                        }
                    }
                    processTime = System.currentTimeMillis() - startTime;
                    if(rushTime > 0 && processTime >= rushTime){
                        ctrl.setRunning(false);
                        return;
                    }
                }
                processTime = System.currentTimeMillis() - startTime;
                if(rushTime > 0 && processTime >= rushTime){
                    ctrl.setRunning(false);
                    return;
                }
                if(isRushing && roopInterval > 0){
                    try{
                        Thread.sleep(roopInterval);
                    }catch(InterruptedException e){
                        if(getState() != STARTED){
                            return;
                        }
                    }
                }
            }
            processTime = System.currentTimeMillis() - startTime;
            if(rushTime > 0 && processTime >= rushTime){
                ctrl.setRunning(false);
                return;
            }
            if(myRoopProcessTime > 0){
                final long curRoopProcessTime = System.currentTimeMillis() - roopStartTime;
                if(isRushing && myRoopProcessTime > curRoopProcessTime){
                    Thread.sleep(myRoopProcessTime - curRoopProcessTime);
                }
            }
            
            count++;
            processTime = System.currentTimeMillis() - startTime;
        }
        
        public void garbage(){
            try{
                client.close(closeRequest);
            }catch(Exception e){
                getLogger().write(
                    "ERROR",
                    "切断に失敗しました。",
                    e
                );
            }
        }
        
        private long calcTPSInterval(int totalRequestCount, int requestCount, long roopTime, long currentRoopProcessTime){
            return (long)(((double)((requestCount + 1) * roopTime)) / (double)totalRequestCount) - currentRoopProcessTime;
        }
        
        private boolean request(
            int roopCount,
            Request request,
            long interval
        ) throws InterruptedException{
            return request(roopCount, request, true, request.currentCount(), interval);
        }
        
        private boolean request(
            int roopCount,
            Request request,
            boolean isCountDown,
            int count,
            long interval
        ) throws InterruptedException{
            if(isRushing && interval > 0){
                Thread.sleep(interval);
            }
            if(requestLock != null){
                while(isRushing && requestLock.exists()){
                    Thread.sleep(1000);
                }
            }
            try{
                client.request(roopCount, count, request);
            }catch(Exception e){
                getLogger().write(
                    "WARN",
                    "リクエストに失敗しました。request=" + request,
                    e
                );
                return false;
            }finally{
                if(isCountDown){
                    request.countDown();
                }
            }
            return true;
        }
        
        private class RandomGroup{
            private final String name;
            private final List list = new ArrayList();
            private int totalCount;
            
            public RandomGroup(String name){
                this.name = name;
            }
            
            public String getName(){
                return name;
            }
            
            public void add(Object req){
                if(req instanceof Request){
                    totalCount += ((Request)req).count;
                }
                list.add(req);
            }
            
            public boolean remove(Object obj){
                return list.remove(obj);
            }
            
            public int size(){
                return list.size();
            }
            
            public SequenceGroup getSequenceGroup(String name){
                SequenceGroup sequenceGroup = null;
                if(list.size() == 0){
                    sequenceGroup = new SequenceGroup(name);
                    list.add(sequenceGroup);
                }else{
                    Object req = list.get(list.size() - 1);
                    if((req instanceof SequenceGroup)
                        && name.equals(((SequenceGroup)req).getName())
                    ){
                        sequenceGroup = (SequenceGroup)req;
                    }else{
                        sequenceGroup = new SequenceGroup(name);
                        list.add(sequenceGroup);
                    }
                }
                return sequenceGroup;
            }
            
            public Object random(Random randomSeed){
                Object req = null;
                final int random = randomSeed.nextInt(totalCount);
                int count = 0;
                for(int i = 0; i < list.size(); i++){
                    req = list.get(i);
                    if(req instanceof SequenceGroup){
                        SequenceGroup sequenceGroup = (SequenceGroup)req;
                        int maxCount = 0;
                        for(int j = 0; j < sequenceGroup.size(); j++){
                            Request request = sequenceGroup.get(j);
                            if(maxCount < request.remainCount()){
                                maxCount = request.remainCount();
                            }
                        }
                        count += maxCount;
                    }else{
                        Request request = (Request)req;
                        count += request.remainCount();
                    }
                    if(count > random){
                        break;
                    }
                }
                totalCount--;
                return req;
            }
            
            private class SequenceGroup{
                private final String name;
                private final List list = new ArrayList();
                private int count;
                
                public SequenceGroup(String name){
                    this.name = name;
                }
                
                public String getName(){
                    return name;
                }
                
                public void add(Request param){
                    if(count < param.count){
                        totalCount -= count;
                        count = param.count;
                        totalCount += count;
                    }
                    list.add(param);
                }
                
                public Request get(int i){
                    return (Request)list.get(i);
                }
                
                public void remove(int i){
                    list.remove(i);
                }
                
                public Iterator iterator(){
                    return list.iterator();
                }
                
                public int size(){
                    return list.size();
                }
            }
        }
    }
    
    private static void usage(){
        System.out.println("コマンド使用方法：");
        System.out.println(" java jp.ossc.nimbus.service.rush.RushService [options]");
        System.out.println();
        System.out.println("[options]");
        System.out.println();
        System.out.println(" [-servicedir path filter]");
        System.out.println("  ラッシュサービスの起動に必要なサービス定義ファイルのディレクトリとサービス定義ファイルを特定するフィルタを指定します。");
        System.out.println();
        System.out.println(" [-servicepath paths]");
        System.out.println("  ラッシュサービスの起動に必要なサービス定義ファイルのパスを指定します。");
        System.out.println("  パスセパレータ区切りで複数指定可能です。");
        System.out.println();
        System.out.println(" [-servicename name]");
        System.out.println("  ラッシュサービスのサービス名を指定します。");
        System.out.println("  指定しない場合はNimbus#Rushとみなします。");
        System.out.println();
        System.out.println(" [-help]");
        System.out.println("  ヘルプを表示します。");
        System.out.println();
        System.out.println(" 使用例 : ");
        System.out.println("    java -classpath classes;lib/nimbus.jar jp.ossc.nimbus.service.rush.RushService -servicepath service-definition.xml");
    }
    
    private static List parsePaths(String paths){
        String pathSeparator = System.getProperty("path.separator");
        final List result = new ArrayList();
        if(paths == null || paths.length() == 0){
            return result;
        }
        if(paths.indexOf(pathSeparator) == -1){
            result.add(paths);
            return result;
        }
        String tmpPaths = paths;
        int index = -1;
        while((index = tmpPaths.indexOf(pathSeparator)) != -1){
            result.add(tmpPaths.substring(0, index));
            if(index != tmpPaths.length() - 1){
                tmpPaths = tmpPaths.substring(index + 1);
            }else{
                tmpPaths = null;
                break;
            }
        }
        if(tmpPaths != null && tmpPaths.length() != 0){
            result.add(tmpPaths);
        }
        return result;
    }
    
    public static void main(String[] args) throws Exception{
        
        if(args.length == 0 || (args.length != 0 && args[0].equals("-help"))){
            usage();
            if(args.length == 0){
                System.exit(-1);
            }
            return;
        }
        
        boolean option = false;
        boolean isServiceDir = false;
        String key = null;
        ServiceName serviceName = null;
        List serviceDirs = null;
        String serviceDir = null;
        List servicePaths = null;
        for(int i = 0; i < args.length; i++){
            if(option){
                if(key.equals("-servicename")){
                    ServiceNameEditor editor = new ServiceNameEditor();
                    editor.setAsText(args[i]);
                    serviceName = (ServiceName)editor.getValue();
                }else if(key.equals("-servicedir")){
                    if(serviceDirs == null){
                        serviceDirs = new ArrayList();
                    }
                    serviceDirs.add(new String[]{serviceDir, args[i]});
                }else if(key.equals("-servicepath")){
                    servicePaths = parsePaths(args[i]);
                }
                option = false;
                key = null;
            }else{
                if(args[i].equals("-servicename")
                     || args[i].equals("-servicepath")
                ){
                    option = true;
                    key = args[i];
                }else if(args[i].equals("-servicedir")){
                    isServiceDir = true;
                    key = args[i];
                }else if(args[i].equals("-help")){
                    usage();
                    return;
                }else if(isServiceDir){
                    isServiceDir = false;
                    option = true;
                    serviceDir = args[i];
                }
            }
        }
        if(serviceDirs != null || servicePaths != null){
            if(serviceDirs != null){
                for(int i = 0, imax = serviceDirs.size(); i < imax; i++){
                    String[] array = (String[])serviceDirs.get(i);
                    if(!ServiceManagerFactory.loadManagers(array[0], array[1])){
                        System.out.println("Service load error. path=" + array[0] + ", filter=" + array[1]);
                        Thread.sleep(1000);
                        System.exit(-1);
                    }
                }
            }
            if(servicePaths != null){
                for(int i = 0, imax = servicePaths.size(); i < imax; i++){
                    if(!ServiceManagerFactory.loadManager((String)servicePaths.get(i))){
                        System.out.println("Service load error." + servicePaths.get(i));
                        Thread.sleep(1000);
                        System.exit(-1);
                    }
                }
            }
            if(!ServiceManagerFactory.checkLoadManagerCompleted()){
                Thread.sleep(1000);
                System.exit(-1);
            }
        }
        if(serviceName == null){
            serviceName = new ServiceName("Nimbus", "Rush");
        }
        RushService rush = (RushService)ServiceManagerFactory.getServiceObject(serviceName);
        try{
            rush.startRush();
        }finally{
            if(servicePaths != null){
                for(int i = servicePaths.size(); --i >= 0;){
                    ServiceManagerFactory.unloadManager((String)servicePaths.get(i));
                }
            }
            if(serviceDirs != null){
                for(int i = serviceDirs.size(); --i >= 0;){
                    String[] array = (String[])serviceDirs.get(i);
                    ServiceManagerFactory.unloadManagers(array[0], array[1]);
                }
            }
        }
        Thread.sleep(1000);
    }
}
