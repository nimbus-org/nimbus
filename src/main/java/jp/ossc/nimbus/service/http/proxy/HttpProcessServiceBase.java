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
package jp.ossc.nimbus.service.http.proxy;

import java.io.*;
import java.util.*;
import java.net.*;
import javax.net.SocketFactory;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.util.converter.BASE64StringConverter;

/**
 * HTTPプロキシのリクエスト処理を行う抽象クラス。<p>
 * 
 * @author M.Takata
 */
public abstract class HttpProcessServiceBase extends ServiceBase
 implements HttpProcessServiceBaseMBean, Process{
    
    private static final long serialVersionUID = 7809414473826320613L;
    
    private static final String MSG_ID_00001 = "HPS__00001";
    private static final String MSG_ID_00002 = "HPS__00002";
    private static final String MSG_ID_00003 = "HPS__00003";
    
    protected boolean isRequestStreamInflate = true;
    protected int tunnelBufferSize = 1024;
    protected Set tunnelProxySet;
    protected ServiceName tunnelSocketFactoryServiceName;
    protected SocketFactory tunnelSocketFactory;
    protected String proxyHost;
    protected int proxyPort = 80;
    protected String proxyUser;
    protected String proxyPassword;
    
    // HttpProcessServiceBaseMBean のJavaDoc
    public void setRequestStreamInflate(boolean isInflate){
        isRequestStreamInflate = isInflate;
    }
    
    // HttpProcessServiceBaseMBean のJavaDoc
    public boolean isRequestStreamInflate(){
        return isRequestStreamInflate;
    }
    
    // HttpProcessServiceBaseMBean のJavaDoc
    public void setTunnelSocketFactoryServiceName(ServiceName name){
        tunnelSocketFactoryServiceName = name;
    }
    // HttpProcessServiceBaseMBean のJavaDoc
    public ServiceName getTunnelSocketFactoryServiceName(){
        return tunnelSocketFactoryServiceName;
    }
    
    // HttpProcessServiceBaseMBean のJavaDoc
    public void setTunnelBufferSize(int size){
        tunnelBufferSize = size;
    }
    // HttpProcessServiceBaseMBean のJavaDoc
    public int getTunnelBufferSize(){
        return tunnelBufferSize;
    }
    
    // HttpProcessServiceBaseMBean のJavaDoc
    public String getProxyHost(){
        return proxyHost;
    }
    // HttpProcessServiceBaseMBean のJavaDoc
    public void setProxyHost(String host){
        proxyHost = host;
    }
    
    // HttpProcessServiceBaseMBean のJavaDoc
    public int getProxyPort(){
        return proxyPort;
    }
    // HttpProcessServiceBaseMBean のJavaDoc
    public void setProxyPort(int port){
        proxyPort = port;
    }
    
    // HttpProcessServiceBaseMBean のJavaDoc
    public String getProxyUser(){
        return proxyUser;
    }
    // HttpProcessServiceBaseMBean のJavaDoc
    public void setProxyUser(String user){
        proxyUser = user;
    }
    
    // HttpProcessServiceBaseMBean のJavaDoc
    public String getProxyPassword(){
        return proxyPassword;
    }
    // HttpProcessServiceBaseMBean のJavaDoc
    public void setProxyPassword(String password){
        proxyPassword = password;
    }
    
    public void setTunnelSocketFactory(SocketFactory factory){
        tunnelSocketFactory = factory;
    }
    protected void preStartService() throws Exception{
        tunnelProxySet = Collections.synchronizedSet(new HashSet());
        if(tunnelSocketFactoryServiceName != null){
            tunnelSocketFactory = (SocketFactory)ServiceManagerFactory.getServiceObject(tunnelSocketFactoryServiceName);
        }
    }
    
    // ProcessのJavaDoc
    public void doProcess(Socket socket) throws Exception{
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        HttpRequest request = null;
        try{
            request = new HttpRequest(is);
            
            if("CONNECT".equals(request.header.method)){
                TunnelProxy tunnelProxy = null;
                try{
                    tunnelProxy = new TunnelProxy(socket, request.header.url);
                }catch(UnknownHostException e){
                    HttpResponse response = new HttpResponse();
                    response.setStatusCode(404);
                    response.setStatusMessage(e.getMessage());
                    response.writeResponse(request, os);
                    return;
                }catch(SocketTimeoutException e){
                    HttpResponse response = new HttpResponse();
                    response.setStatusCode(404);
                    response.setStatusMessage(e.getMessage());
                    response.writeResponse(request, os);
                    return;
                }
                if(proxyHost != null){
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(tunnelProxy.getServerSocket().getOutputStream()));
                    pw.print(request.header.method);
                    pw.print(' ');
                    pw.print(request.header.url);
                    pw.print(' ');
                    pw.print(request.header.version);
                    pw.print("\r\n");
                    Map headerMap = request.header.getHeaderMap();
                    Iterator entries = headerMap.entrySet().iterator();
                    while(entries.hasNext()){
                        Map.Entry entry = (Map.Entry)entries.next();
                        pw.print((String)entry.getKey());
                        pw.print(": ");
                        for(int i = 0, imax = ((String[])entry.getValue()).length; i < imax; i++){
                            pw.print(((String[])entry.getValue())[i]);
                            if(i != imax - 1){
                                pw.print("; ");
                            }
                        }
                        pw.print("\r\n");
                    }
                    if(proxyUser != null && proxyPassword != null){
                        pw.print("Proxy-Authorization: Basic ");
                        pw.print(BASE64StringConverter.encode(proxyUser + ':' + proxyPassword, null));
                        pw.print("\r\n");
                    }
                    pw.print("\r\n");
                    pw.flush();
                    InputStream sis = tunnelProxy.getServerSocket().getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    String responseLine = HttpRequest.RequestHeader.readLine(sis, baos);
                    int index1 = responseLine.indexOf(" ");
                    int index2 = responseLine.indexOf(" ", index1 + 1);
                    int statusCode = Integer.parseInt(responseLine.substring(index1 + 1, index2));
                    String statusMessage = responseLine.substring(index2 + 1);
                    if(200 > statusCode && statusCode <= 300){
                        HttpResponse response = new HttpResponse();
                        response.setStatusCode(statusCode);
                        response.setStatusMessage(statusMessage);
                        response.writeResponse(request, os);
                        return;
                    }
                }
                HttpResponse response = new HttpResponse();
                response.setDateHeader("Date", new Date());
                response.setStatusCode(200);
                response.writeResponse(request, os);
                tunnelProxySet.add(tunnelProxy);
                tunnelProxy.start();
                return;
            }
            
            if(request.body != null){
                request.body.setDecompress(isRequestStreamInflate());
            }
        }catch(Exception e){
            getLogger().write(MSG_ID_00001, e);
            HttpResponse response = new HttpResponse();
            response.setStatusCode(400);
            response.setStatusMessage(e.getMessage());
            PrintStream ps = new PrintStream(response.getOutputStream());
            e.printStackTrace(ps);
            ps.flush();
            response.writeResponse(request, os);
            return;
        }
        
        HttpResponse response = new HttpResponse();
        response.setVersion(request.getHeader().getVersion());
        
        try{
            doProcess(request, response);
        }catch(Exception e){
            getLogger().write(MSG_ID_00002, e);
            response.setStatusCode(500);
            response.setStatusMessage(e.getMessage());
            PrintStream ps = new PrintStream(response.getOutputStream());
            e.printStackTrace(ps);
            ps.flush();
            response.writeResponse(request, os);
            return;
        }
        
        try{
            response.writeResponse(request, os);
        }catch(Exception e){
            getLogger().write(MSG_ID_00003, request.header.url, e);
        }
    }
    
    /**
     * HTTPリクエストのプロキシ処理を行う。<p>
     *
     * @param request HTTPリクエスト
     * @param response HTTPレスポンス
     * @exception Exception HTTPリクエストの処理に失敗した場合
     */
    public abstract void doProcess(
        HttpRequest request,
        HttpResponse response
    ) throws Exception;
    
    protected static class ReadData{
        protected byte[] buffer;
        protected int length;
        public ReadData(int bufferSize){
            buffer = new byte[bufferSize];
        }
    }
    
    protected class TunnelProxy{
        private Socket clientSocket;
        private Socket serverSocket;
        private Daemon clientDaemon;
        private Daemon serverDaemon;
        public TunnelProxy(Socket clientSocket, String serverStr) throws Exception{
            this.clientSocket = clientSocket;
            final int index = serverStr.indexOf(":");
            if(index == -1 || index == serverStr.length() - 1){
                throw new Exception("Server port is unknown. server=" + serverStr);
            }
            final String host = serverStr.substring(0, index);
            final int port = Integer.parseInt(serverStr.substring(index + 1));
            if(proxyHost == null){
                serverSocket = tunnelSocketFactory == null ? new Socket(host, port) : tunnelSocketFactory.createSocket(host, port);
            }else{
                serverSocket = tunnelSocketFactory == null ? new Socket(proxyHost, proxyPort) : tunnelSocketFactory.createSocket(proxyHost, proxyPort);
            }
            clientDaemon = new Daemon(
                new SocketDaemonRunnable(
                    clientSocket.getInputStream(),
                    serverSocket.getOutputStream(),
                    tunnelBufferSize
                )
            );
            clientDaemon.setName(getServiceNameObject() + " TunnelProxy Client Daemon : " + clientSocket.getRemoteSocketAddress());
            serverDaemon = new Daemon(
                new SocketDaemonRunnable(
                    serverSocket.getInputStream(),
                    clientSocket.getOutputStream(),
                    tunnelBufferSize
                )
            );
            serverDaemon.setName(getServiceNameObject() + " TunnelProxy Server Daemon : " + clientSocket.getRemoteSocketAddress());
        }
        
        public Socket getServerSocket(){
            return serverSocket;
        }
        public Socket getClientSocket(){
            return clientSocket;
        }
        
        public void start() throws IOException{
            clientDaemon.start();
            serverDaemon.start();
        }
        
        public synchronized void stop(){
            clientDaemon.stopNoWait();
            serverDaemon.stopNoWait();
            try{
                clientSocket.close();
            }catch(IOException e){}
            try{
                serverSocket.close();
            }catch(IOException e){}
            tunnelProxySet.remove(TunnelProxy.this);
        }
        
        protected class SocketDaemonRunnable extends DaemonRunnableAdaptor{
            
            protected InputStream is;
            protected OutputStream os;
            protected ReadData data;
            
            public SocketDaemonRunnable(InputStream is, OutputStream os, int bufferSize){
                this.is = is;
                this.os = os;
                data = new ReadData(bufferSize);
            }
            
            public Object provide(DaemonControl ctrl) throws Throwable{
                try{
                    data.length = is.read(data.buffer);
                    if(data.length == -1){
                        TunnelProxy.this.stop();
                    }
                }catch(SocketTimeoutException e){
                    return null;
                }catch(IOException e){
                    TunnelProxy.this.stop();
                    return null;
                }
                return data;
            }
            
            public void consume(Object obj, DaemonControl ctrl) throws Throwable{
                ReadData data = (ReadData)obj;
                if(data == null || data.length < 0){
                    return;
                }
                try{
                    os.write(data.buffer, 0, data.length);
                    os.flush();
                }catch(IOException e){
                    TunnelProxy.this.stop();
                }
            }
        }
    }
}