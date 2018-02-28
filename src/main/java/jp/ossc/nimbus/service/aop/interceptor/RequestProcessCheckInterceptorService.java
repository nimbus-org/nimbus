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
package jp.ossc.nimbus.service.aop.interceptor;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.log.*;

/**
 * リクエスト処理チェックインターセプタ。<p>
 * リクエスト中のスレッドを監視したり、中断したり制御する事ができる。<br>
 * また、リクエスト中のスレッドの経過時間をチェックして閾値を超えるとログ出力を行う。<br>
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="RequestProcessCheckInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.RequestProcessCheckInterceptorService"&gt;
 *             &lt;attribute name="Threshold"&gt;
 *                 30000=MESSAGE_001
 *                 60000=MESSAGE_002
 *             &lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class RequestProcessCheckInterceptorService extends ServiceBase
 implements Interceptor, DaemonRunnable, Serializable,
            RequestProcessCheckInterceptorServiceMBean{
    
    private static final long serialVersionUID = -3680171846086643525L;
    
    protected ServiceName reportingLoggerServiceName;
    protected Logger reportingLogger;
    
    protected Map thresholdMap;
    protected List reportList;
    protected transient Set requests;
    protected long checkInterval = 1000l;
    protected transient Daemon checker;
    
    // RequestProcessCheckInterceptorServiceMBeanのJavaDoc
    public void setReportingLoggerServiceName(ServiceName name){
        reportingLoggerServiceName = name;
    }
    // RequestProcessCheckInterceptorServiceMBeanのJavaDoc
    public ServiceName getReportingLoggerServiceName(){
        return reportingLoggerServiceName;
    }
    
    // RequestProcessCheckInterceptorServiceMBeanのJavaDoc
    public void setThreshold(Map threshold){
        thresholdMap = threshold;
    }
    // RequestProcessCheckInterceptorServiceMBeanのJavaDoc
    public Map getThreshold(){
        return thresholdMap;
    }
    
    // RequestProcessCheckInterceptorServiceMBeanのJavaDoc
    public void setCheckInterval(long interval){
        if(interval <= 0){
            throw new IllegalArgumentException("CheckInterval must be larger than 0.");
        }
        checkInterval = interval;
    }
    
    // RequestProcessCheckInterceptorServiceMBeanのJavaDoc
    public long getCheckInterval(){
        return checkInterval;
    }
    
    // RequestProcessCheckInterceptorServiceMBeanのJavaDoc
    public String displayCurrentReport(){
        final StringWriter buf = new StringWriter();
        final PrintWriter pw = new PrintWriter(buf);
        final long now = System.currentTimeMillis();
        final Iterator itr = requests.iterator();
        while(itr.hasNext()){
            final Request request = (Request)itr.next();
            if(request.isEnd){
                continue;
            }
            pw.println('{');
            Object[] reports = createReport(
                request,
                now - request.time
            );
            pw.print("startTime=");
            pw.println(reports[0]);
            pw.print("processTime=");
            pw.println(reports[1]);
            pw.print("thread=");
            pw.println(reports[2]);
            pw.print("context=");
            pw.println(reports[3]);
            pw.print("stacktrace=");
            pw.println(reports[4]);
            pw.println('}');
        }
        pw.flush();
        return buf.toString();
    }
    
    // RequestProcessCheckInterceptorServiceMBeanのJavaDoc
    public void suspendChecker(){
        if(checker != null){
            checker.suspend();
        }
    }
    
    // RequestProcessCheckInterceptorServiceMBeanのJavaDoc
    public void resumeChecker(){
        if(checker != null){
            checker.resume();
        }
    }
    
    // RequestProcessCheckInterceptorServiceMBeanのJavaDoc
    public boolean interruptRequest(String groupName, String threadName){
        boolean isInterrupt = false;
        final Iterator itr = requests.iterator();
        while(itr.hasNext()){
            final Request request = (Request)itr.next();
            if(request.isEnd || request.thread == null){
                continue;
            }
            if(groupName.equals(request.thread.getThreadGroup().getName())
                 && threadName.equals(request.thread.getName())){
                if(!request.isEnd){
                    request.thread.interrupt();
                    isInterrupt = true;
                }
            }
        }
        return isInterrupt;
    }
    
    // RequestProcessCheckInterceptorServiceMBeanのJavaDoc
    public boolean removeRequest(String groupName, String threadName){
        boolean isRemove = false;
        final Iterator itr = requests.iterator();
        while(itr.hasNext()){
            final Request request = (Request)itr.next();
            if(request.isEnd || request.thread == null){
                continue;
            }
            if(groupName.equals(request.thread.getThreadGroup().getName())
                 && threadName.equals(request.thread.getName())){
                itr.remove();
                isRemove = true;
            }
        }
        return isRemove;
    }
    
    // RequestProcessCheckInterceptorServiceMBeanのJavaDoc
    public void clearRequest(){
        requests.clear();
    }
    
    // RequestProcessCheckInterceptorServiceMBeanのJavaDoc
    public int getRequestCount(){
        return requests == null ? 0 : requests.size();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        requests = new ConcurrentSkipListSet();
        if(thresholdMap != null){
            List list = new ArrayList();
            final Iterator entries = thresholdMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                long threshold = -1;
                try{
                    threshold = Long.parseLong(entry.getKey().toString().trim());
                }catch(NumberFormatException e){
                }
                if(threshold < 0){
                    throw new IllegalArgumentException("Threshold must be 'threshold performance[ms]=MessageId' : " + entry.getKey());
                }
                list.add(new Report(threshold, entry.getValue().toString().trim()));
            }
            Collections.sort(list);
            reportList = list;
            checker = new Daemon(this);
            checker.setName(
                "Nimbus PerformanceCheckInterceptorDaemon "
                    + getServiceNameObject()
            );
            checker.setDaemon(true);
            checker.start();
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(checker != null){
            checker.stop();
            checker = null;
        }
    }
    
    /**
     * ログ出力を行う{@link Logger}を設定する。<p>
     *
     * @param log Logger
     */
    public void setReportingLogger(Logger log){
        reportingLogger = logger;
    }
    
    /**
     * ログ出力を行う{@link Logger}を取得する。<p>
     *
     * @return Logger
     */
    public Logger getReportingLogger(){
        if(reportingLogger != null){
            return reportingLogger;
        }else if(reportingLoggerServiceName != null){
            try{
                return (Logger)ServiceManagerFactory
                    .getServiceObject(reportingLoggerServiceName);
            }catch(ServiceNotFoundException e){
                return super.getLogger();
            }
        }else{
            return super.getLogger();
        }
    }
    
    /**
     * リクエスト情報をスタックして、次のインターセプタを呼び出す。<p>
     * サービスが開始されていない場合は、何もせずに次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invoke(
        InvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        if(getState() != STARTED){
            return chain.invokeNext(context);
        }
        Request request = new Request(context);
        try{
            requests.add(request);
            return chain.invokeNext(context);
        }finally{
            request.isEnd = true;
            requests.remove(request);
        }
    }
    
    // DaemonRunnableのJavaDoc
    public boolean onStart(){
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
    public Object provide(DaemonControl ctrl) throws Throwable{
        ctrl.sleep(checkInterval, true);
        return requests.size() == 0 ? null : requests;
    }
    
    // DaemonRunnableのJavaDoc
    public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
        final Set requestSet = (Set)paramObj;
        if(requestSet == null || requestSet.size() == 0){
            return;
        }
        final long now = System.currentTimeMillis();
        final Iterator itr = requestSet.iterator();
        boolean isTimeout = false;
        while(itr.hasNext()){
            final Request request = (Request)itr.next();
            if(request.isEnd){
                continue;
            }
            for(int i = reportList.size(); --i >= 0;){
                Report report = (Report)reportList.get(i);
                if(now - request.time > report.threshold){
                    isTimeout = true;
                    final long reportTime = request.getReportTime(report);
                    if(!request.isEnd && (reportTime == -1 || (report.reportInterval >= 0 && reportTime + report.reportInterval <= now))){
                        getReportingLogger().write(
                            report.messageId,
                            createReport(request, now - request.time)
                        );
                        request.report(report);
                        break;
                    }
                }
            }
            if(!isTimeout){
                break;
            }
        }
    }
    
    /**
     * ログに出力する埋め込みパラメータを生成する。<p>
     *
     * @param request リクエスト情報
     * @param performance 経過時間[ms]
     * @return ログに出力する埋め込みパラメータ配列
     */
    protected Object[] createReport(Request request, long performance){
        String stackTrace = null;
        if(request.thread != null){
            try{
                final StackTraceElement[] elements = (StackTraceElement[])Thread.class.getMethod(
                    "getStackTrace",
                    (Class[])null
                ).invoke(
                    request.thread,
                    (Object[])null
                );
                final StringWriter buf = new StringWriter();
                final PrintWriter pw = new PrintWriter(buf);
                for(int i = 0; i < elements.length; i++){
                    pw.println(elements[i]);
                }
                pw.flush();
                stackTrace = buf.toString();
            }catch(NoSuchMethodException e){
            }catch(IllegalAccessException e){
            }catch(java.lang.reflect.InvocationTargetException e){
            }
        }
        return new Object[]{
            new Date(request.time),
            new Long(performance),
            request.thread,
            request.context,
            stackTrace
        };
    }
    
    // DaemonRunnableのJavaDoc
    public void garbage(){
    }
    
    private void readObject(ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        requests = new ConcurrentSkipListSet();
        checker = new Daemon(this);
        checker.setName(
            "Nimbus PerformanceCheckInterceptorDaemon "
                + getServiceNameObject()
        );
        checker.setDaemon(true);
        checker.start();
    }
    
    /**
     * リクエスト情報。<p>
     *
     * @author M.Takata
     */
    protected static class Request implements Comparable{
        /** スレッド */
        public final Thread thread;
        /** 実行コンテキスト */
        public final InvocationContext context;
        /** 開始時刻 */
        public final long time;
        /** 終了フラグ */
        public boolean isEnd = false;
        protected Map reports;
        
        /**
         * インスタンスを生成する。<p>
         *
         * @param context 実行コンテキスト
         */
        public Request(InvocationContext context){
            this.context = context;
            thread = Thread.currentThread();
            time = System.currentTimeMillis();
        }
        public int compareTo(Object o){
            final Request r = (Request)o;
            final long diff = time - r.time;
            return diff == 0 ? 0 : (diff > 0 ? 1 : -1);
        }
        
        public void report(Report report){
            if(reports == null){
                reports = new HashMap();
            }
            reports.put(report, new Long(System.currentTimeMillis()));
        }
        
        public long getReportTime(Report report){
            return reports == null ? -1l : reports.containsKey(report) ? ((Long)reports.get(report)).longValue() : -1l;
        }
    }
    
    /**
     * レポート情報。<p>
     *
     * @author M.Takata
     */
    protected static class Report implements Serializable, Comparable{
        private static final long serialVersionUID = 8777262126828754237L;
        /** 閾値 */
        public long threshold;
        /** メッセージID */
        public final String messageId;
        /** 報告間隔 */
        public long reportInterval = 0;
        
        public Report(long threshold, String report) throws Exception{
            this.threshold = threshold;
            String[] args = report.split(",");
            this.messageId = args[0].trim();
            if(args.length > 1){
                reportInterval = Long.parseLong(args[1].trim());
            }
        }
        public int compareTo(Object o){
            final Report r = (Report)o;
            final long diff = threshold - r.threshold;
            return diff == 0 ? 0 : (diff > 0 ? 1 : -1);
        }
    }
}