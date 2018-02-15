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
package jp.ossc.nimbus.service.server;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.util.*;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.daemon.Daemon;
import jp.ossc.nimbus.daemon.DaemonRunnableAdaptor;
import jp.ossc.nimbus.daemon.DaemonControl;
import jp.ossc.nimbus.service.queue.QueueHandlerContainer;
import jp.ossc.nimbus.service.sequence.Sequence;
import jp.ossc.nimbus.util.net.ServerSocketFactory;
import jp.ossc.nimbus.util.net.SocketFactory;

/**
 * デフォルトのサーバサービス。<p>
 * TCPソケット通信を行いリクエストを受け付け、{@link QueueHandlerContainer}に{@link RequestContext}を投入して、処理を依頼し、応答を返す。<br>
 * 
 * @author M.Takata
 */
public class DefaultServerService extends ServiceBase implements DefaultServerServiceMBean{
    
    private static final long serialVersionUID = 3768227629065502757L;
    
    private String hostName;
    private int port = 10000;
    private boolean isReuseAddress = true;
    private int receiveBufferSize;
    private int soTimeout;
    private boolean isHandleAccept;
    
    private ServiceName queueHandlerContainerServiceName;
    private QueueHandlerContainer queueHandlerContainer;
    
    private ServiceName sequenceServiceName;
    private Sequence sequence;
    
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    
    private Class requestClass = Request.class;
    private Class responseClass = Response.class;
    
    private ServiceName serverSocketFactoryServiceName;
    private ServerSocketFactory serverSocketFactory;
    
    private ServiceName socketFactoryServiceName;
    private SocketFactory socketFactory;
    
    private Daemon dispatchDaemon;
    
    // DefaultServerServiceMBeanのJavaDoc
    public void setHostName(String name){
        hostName = name;
    }
    // DefaultServerServiceMBeanのJavaDoc
    public String getHostName(){
        return hostName;
    }
    
    // DefaultServerServiceMBeanのJavaDoc
    public void setPort(int port){
        this.port = port;
    }
    // DefaultServerServiceMBeanのJavaDoc
    public int getPort(){
        return port;
    }
    
    // DefaultServerServiceMBeanのJavaDoc
    public void setReuseAddress(boolean isReuse){
        isReuseAddress = isReuse;
    }
    // DefaultServerServiceMBeanのJavaDoc
    public boolean isReuseAddress(){
        return isReuseAddress;
    }
    
    // DefaultServerServiceMBeanのJavaDoc
    public void setReceiveBufferSize(int size){
        receiveBufferSize = size;
    }
    // DefaultServerServiceMBeanのJavaDoc
    public int getReceiveBufferSize(){
        return receiveBufferSize;
    }
    
    // DefaultServerServiceMBeanのJavaDoc
    public void setSoTimeout(int timeout){
        soTimeout = timeout;
    }
    // DefaultServerServiceMBeanのJavaDoc
    public int getSoTimeout(){
        return soTimeout;
    }
    
    // DefaultServerServiceMBeanのJavaDoc
    public void setQueueHandlerContainerServiceName(ServiceName name){
        queueHandlerContainerServiceName = name;
    }
    // DefaultServerServiceMBeanのJavaDoc
    public ServiceName getQueueHandlerContainerServiceName(){
        return queueHandlerContainerServiceName;
    }
    
    // DefaultServerServiceMBeanのJavaDoc
    public void setSequenceServiceName(ServiceName name){
        sequenceServiceName = name;
    }
    // DefaultServerServiceMBeanのJavaDoc
    public ServiceName getSequenceServiceName(){
        return sequenceServiceName;
    }
    
    // DefaultServerServiceMBeanのJavaDoc
    public void setServerSocketFactoryServiceName(ServiceName name){
        serverSocketFactoryServiceName = name;
    }
    // DefaultServerServiceMBeanのJavaDoc
    public ServiceName getServerSocketFactoryServiceName(){
        return serverSocketFactoryServiceName;
    }
    
    // DefaultServerServiceMBeanのJavaDoc
    public void setSocketFactoryServiceName(ServiceName name){
        socketFactoryServiceName = name;
    }
    // DefaultServerServiceMBeanのJavaDoc
    public ServiceName getSocketFactoryServiceName(){
        return socketFactoryServiceName;
    }
    
    // DefaultServerServiceMBeanのJavaDoc
    public void setHandleAccept(boolean isHandle){
        isHandleAccept = isHandle;
    }
    // DefaultServerServiceMBeanのJavaDoc
    public boolean isHandleAccept(){
        return isHandleAccept;
    }
    
    public void setServerSocketFactory(ServerSocketFactory factory){
        serverSocketFactory = factory;
    }
    public ServerSocketFactory getServerSocketFactory(){
        return serverSocketFactory;
    }
    
    public void setSocketFactory(SocketFactory factory){
        socketFactory = factory;
    }
    public SocketFactory getSocketFactory(){
        return socketFactory;
    }
    
    public void setRequestClass(Class clazz){
        requestClass = clazz;
    }
    public Class getRequestClass(){
        return requestClass;
    }
    public void setResponseClass(Class clazz){
        responseClass = clazz;
    }
    public Class getResponseClass(){
        return responseClass;
    }
    
    public void startService() throws Exception{
        if(queueHandlerContainerServiceName == null){
            throw new IllegalArgumentException("QueueHandlerContainerServiceName is null.");
        }else{
            queueHandlerContainer = (QueueHandlerContainer)ServiceManagerFactory.getServiceObject(queueHandlerContainerServiceName);
        }
        if(sequenceServiceName != null){
            sequence = (Sequence)ServiceManagerFactory.getServiceObject(sequenceServiceName);
        }
        
        Request request = (Request)requestClass.newInstance();
        
        Response response = (Response)responseClass.newInstance();
        
        dispatchDaemon = new Daemon(new DispatchDaemonRunnable());
        dispatchDaemon.setName("Nimbus TCP Server dispatch daemon " + getServiceNameObject());
        connect();
        dispatchDaemon.start();
    }
    
    private void connect() throws Exception{
        serverSocketChannel = ServerSocketChannel.open();
        if(serverSocketFactory != null){
            serverSocketFactory.applyServerSocketProperties(serverSocketChannel.socket());
        }
        serverSocketChannel.socket().setReuseAddress(isReuseAddress);
        if(receiveBufferSize > 0){
            serverSocketChannel.socket().setReceiveBufferSize(receiveBufferSize);
        }
        if(soTimeout > 0){
            serverSocketChannel.socket().setSoTimeout(soTimeout);
        }
        serverSocketChannel.socket().bind(
            hostName == null ? new InetSocketAddress(port) : new InetSocketAddress(hostName, port)
        );
        serverSocketChannel.configureBlocking(false);
        
        
        selector = Selector.open();
        serverSocketChannel.register(
            selector,
            SelectionKey.OP_ACCEPT
        );
    }
    
    private void close(){
        if(serverSocketChannel != null){
            try{
                serverSocketChannel.close();
            }catch(IOException e){
            }
        }
        if(selector != null){
            try{
                selector.close();
            }catch(IOException e){
            }
        }
    }
    
    public void stopService() throws Exception{
        dispatchDaemon.stop();
        close();
    }
    
    private class DispatchDaemonRunnable extends DaemonRunnableAdaptor{
        
        public Object provide(DaemonControl ctrl) throws Throwable{
            try{
                int count = selector.select(1000);
                if(count > 0){
                    return selector.selectedKeys();
                }else{
                    return null;
                }
            }catch(Throwable e){
                getLogger().write("DSS__00001", getServiceNameObject(), e);
                close();
                try{
                    connect();
                }catch(IOException e2){
                    close();
                    getLogger().write("DSS__00002", getServiceNameObject(), e2);
                }
                return null;
            }
        }
        public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
            if(paramObj == null){
                return;
            }
            SelectionKey key = null;
            final Set selectedKeys = (Set)paramObj;
            try{
                final Iterator keyIterator = selectedKeys.iterator();
                while(keyIterator.hasNext()){
                    try{
                        key = (SelectionKey)keyIterator.next();
                        if(!key.isValid()){
                            key.cancel();
                        }else if(key.isAcceptable()){
                            final ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
                            final SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            if(socketFactory != null){
                                socketFactory.applySocketProperties(socketChannel.socket());
                            }
                            final ServantImpl servant = new ServantImpl(socketChannel);
                            if(isHandleAccept){
                                servant.accept(key);
                            }else{
                                socketChannel.register(
                                    key.selector(),
                                    SelectionKey.OP_READ,
                                    servant
                                );
                            }
                        }else{
                            if(key.isWritable()){
                                final ServantImpl servant = (ServantImpl)key.attachment();
                                servant.write(key);
                            }
                            if(key.isReadable()){
                                final ServantImpl servant = (ServantImpl)key.attachment();
                                if(servant.getSocketChannel().socket().isInputShutdown()){
                                    servant.close(true);
                                    key.cancel();
                                }else{
                                    servant.read(key);
                                }
                            }
                        }
                    }catch(CancelledKeyException e){
                    }catch(IOException e){
                        key.cancel();
                    }finally{
                        keyIterator.remove();
                    }
                }
            }catch(Throwable e){
                getLogger().write("ERROR", "SelectionKey handle error.", e);
            }
        }
    }
    
    private class ServantImpl implements Servant{
        protected SocketChannel socketChannel;
        protected List writeBuffers;
        protected Request request;
        protected boolean isFirstRequest = true;
        protected boolean isClosed;
        
        protected ServantImpl(SocketChannel channel){
            socketChannel = channel;
            writeBuffers = new ArrayList();
        }
        
        public SocketChannel getSocketChannel(){
            return socketChannel;
        }
        
        protected void accept(SelectionKey key) throws IOException{
            Request req = null;
            try{
                req = (Request)requestClass.newInstance();
                req.setAccept(true);
            }catch(InstantiationException e){
                throw new IOException(e.toString());
            }catch(IllegalAccessException e){
                throw new IOException(e.toString());
            }
            req.accept(socketChannel);
            Response response = null;
            try{
                response = (Response)responseClass.newInstance();
            }catch(InstantiationException e){
                throw new IOException(e.toString());
            }catch(IllegalAccessException e){
                throw new IOException(e.toString());
            }
            response.init(this, key);
            if(sequence != null){
                req.setRequestId(sequence.increment());
            }
            queueHandlerContainer.push(
                new RequestContextImpl(
                    req,
                    response
                )
            );
        }
        
        protected void read(SelectionKey key) throws IOException{
            Request req = request;
            if(req == null){
                try{
                    req = (Request)requestClass.newInstance();
                    req.setFirst(isFirstRequest);
                    if(isFirstRequest){
                        isFirstRequest = false;
                    }
                }catch(InstantiationException e){
                    throw new IOException(e.toString());
                }catch(IllegalAccessException e){
                    throw new IOException(e.toString());
                }
            }
            if(!req.read(socketChannel)){
                request = req;
                return;
            }
            request = null;
            Response response = null;
            try{
                response = (Response)responseClass.newInstance();
            }catch(InstantiationException e){
                throw new IOException(e.toString());
            }catch(IllegalAccessException e){
                throw new IOException(e.toString());
            }
            response.init(this, key);
            if(sequence != null){
                req.setRequestId(sequence.increment());
            }
            queueHandlerContainer.push(
                new RequestContextImpl(
                    req,
                    response
                )
            );
        }
        
        protected void write(SelectionKey key) throws IOException{
            if(socketChannel == null){
                return;
            }
            synchronized(writeBuffers){
                while(writeBuffers.size() > 0){
                    socketChannel.write((ByteBuffer)writeBuffers.remove(0));
                }
            }
            if(isClosed){
                close(true);
            }else{
                key.interestOps(SelectionKey.OP_READ);
            }
        }
        
        public void writeResponse(SelectionKey key, InputStream is) throws IOException{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            if(is != null){
                int readLen = 0;
                while((readLen = is.read(bytes)) > 0){
                    baos.write(bytes, 0, readLen);
                }
            }
            bytes = baos.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.put(bytes);
            buffer.flip();
            synchronized(writeBuffers){
                writeBuffers.add(buffer);
            }
            if(key.interestOps() != (SelectionKey.OP_READ | SelectionKey.OP_WRITE)){
                key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            }
        }
        
        public OutputStream getOutputStream(SelectionKey key){
            return new ResponseOutputStream(key);
        }
        
        public void close(boolean isForce){
            if(isForce){
                if(socketChannel != null){
                    try{
                        socketChannel.close();
                    }catch(IOException e){
                    }
                }
            }
            isClosed = true;
        }
        
        protected class ResponseOutputStream extends ByteArrayOutputStream{
            private SelectionKey key;
            public ResponseOutputStream(SelectionKey key){
                this.key = key;
            }
            public void flush() throws IOException{
                super.flush();
                byte[] bytes = toByteArray();
                ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
                buffer.put(bytes);
                buffer.flip();
                synchronized(writeBuffers){
                    writeBuffers.add(buffer);
                }
                if(key.interestOps() != (SelectionKey.OP_READ | SelectionKey.OP_WRITE)){
                    key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                }
            }
        }
    }
    
    private static class RequestContextImpl implements RequestContext{
        private final Request request;
        private final Response response;
        public RequestContextImpl(Request req, Response res){
            request = req;
            response = res;
        }
        public Request getRequest(){
            return request;
        }
        public Response getResponse(){
            return response;
        }
    }
}
