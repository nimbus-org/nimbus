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
package jp.ossc.nimbus.util.net;

import java.util.*;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.Externalizable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import jp.ossc.nimbus.beans.*;

/**
 * ソケットファクトリ。<p>
 * このファクトリから生成されるソケットは、{@link #setSocketProperty(String, Object)}で、予め設定されたプロパティが設定される。<br>
 *
 * @author M.Takata
 */
public class SocketFactory extends javax.net.SocketFactory implements Externalizable, java.rmi.server.RMIClientSocketFactory{
    
    public static final String DEFAULT_HOST_PROPERTY_NAME = SocketFactory.class.getName() + ".host";
    public static final String DEFAULT_LOCAL_HOST_PROPERTY_NAME = SocketFactory.class.getName() + ".localHost";
    
    protected javax.net.SocketFactory socketFactory;
    protected Map socketProperties;
    protected String hostPropertyName = DEFAULT_HOST_PROPERTY_NAME;
    protected String localHostPropertyName = DEFAULT_LOCAL_HOST_PROPERTY_NAME;
    protected int connectionTimeout;
    
    public static javax.net.SocketFactory getDefault(){
        return new SocketFactory();
    }
    
    protected String getHost(String host){
        final String replaceHost = System.getProperty(hostPropertyName + "." + host);
        return replaceHost == null ? host : replaceHost;
    }
    protected InetAddress getHostAddress(InetAddress host){
        String replaceHost = System.getProperty(hostPropertyName + "." + host.getHostAddress());
        if(replaceHost == null){
            replaceHost = System.getProperty(hostPropertyName + "." + host.getHostName());
        }
        try{
            return replaceHost == null ? host : InetAddress.getByName(replaceHost);
        }catch(UnknownHostException e){
            return host;
        }
    }
    protected String getLocalHost(String host){
        final String replaceHost = System.getProperty(localHostPropertyName);
        return replaceHost == null ? host : replaceHost;
    }
    protected InetAddress getLocalHostAddress(InetAddress host){
        final String replaceHost = System.getProperty(localHostPropertyName);
        try{
            return replaceHost == null ? host : InetAddress.getByName(replaceHost);
        }catch(UnknownHostException e){
            return host;
        }
    }
    
    public Socket createSocket() throws IOException{
        Socket socket = null;
        if(socketFactory == null){
            socket = new Socket();
        }else{
            socket = socketFactory.createSocket();
        }
        return applySocketProperties(socket);
    }
    
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException{
        Socket socket = createSocket();
        InetAddress localHost = getLocalHostAddress(null);
        if(localHost != null){
            socket.bind(new InetSocketAddress(localHost, 0));
        }
        if(connectionTimeout > 0){
            socket.connect(new InetSocketAddress(getHost(host), port), connectionTimeout);
        }else{
            socket.connect(new InetSocketAddress(getHost(host), port));
        }
        return socket;
    }
    
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException, UnknownHostException{
        Socket socket = createSocket();
        InetAddress localHost = getLocalHostAddress(localAddress);
        if(localHost != null){
            socket.bind(new InetSocketAddress(localHost, localPort));
        }
        if(connectionTimeout > 0){
            socket.connect(new InetSocketAddress(getHost(host), port), connectionTimeout);
        }else{
            socket.connect(new InetSocketAddress(getHost(host), port));
        }
        return socket;
    }
    
    public Socket createSocket(InetAddress host, int port) throws IOException{
        Socket socket = createSocket();
        InetAddress localHost = getLocalHostAddress(null);
        if(localHost != null){
            socket.bind(new InetSocketAddress(localHost, 0));
        }
        if(connectionTimeout > 0){
            socket.connect(new InetSocketAddress(getHostAddress(host), port), connectionTimeout);
        }else{
            socket.connect(new InetSocketAddress(getHostAddress(host), port));
        }
        return socket;
    }
    
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException{
        Socket socket = createSocket();
        InetAddress localHost = getLocalHostAddress(localAddress);
        if(localHost != null){
            socket.bind(new InetSocketAddress(localHost, localPort));
        }
        if(connectionTimeout > 0){
            socket.connect(new InetSocketAddress(address, port), connectionTimeout);
        }else{
            socket.connect(new InetSocketAddress(address, port));
        }
        return socket;
    }
    
    /**
     * NATするホスト名を指定するプロパティ名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_HOST_PROPERTY_NAME}。<br>
     * {プロパティ名}.{NAT対象ホスト名}={NAT後のホスト名}で指定する。<br>
     *
     * @param name プロパティ名
     */
    public void setHostPropertyName(String name){
        hostPropertyName = name;
    }
    
    /**
     * NATするホスト名を指定するプロパティ名を取得する。<p>
     *
     * @return プロパティ名
     */
    public String getHostPropertyName(){
        return hostPropertyName;
    }
    
    /**
     * マルチホームでのローカルホスト名を指定するプロパティ名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_LOCAL_HOST_PROPERTY_NAME}。<br>
     *
     * @param name プロパティ名
     */
    public void setLocalHostPropertyName(String name){
        localHostPropertyName = name;
    }
    
    /**
     * マルチホームでのローカルホスト名を指定するプロパティ名を取得する。<p>
     *
     * @return プロパティ名
     */
    public String getLocalHostPropertyName(){
        return localHostPropertyName;
    }
    
    /**
     * javax.net.SocketFactoryを設定する。<p>
     *
     * @param factory javax.net.SocketFactory
     */
    public void setSocketFactory(javax.net.SocketFactory factory){
        socketFactory = factory;
    }
    
    /**
     * {@link Socket}にプロパティを設定する。<p>
     *
     * @param props プロパティマップ
     */
    public void setSocketProperties(Map props){
        if(props == null || props.size() == 0){
            if(socketProperties != null){
                socketProperties = null;
            }
            return;
        }
        final Iterator names = props.keySet().iterator();
        while(names.hasNext()){
            String name = (String)names.next();
            setSocketProperty(name, props.get(name));
        }
    }
    
    /**
     * {@link Socket}にプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param value 値
     */
    public void setSocketProperty(String name, Object value){
        if(socketProperties == null){
            socketProperties = new LinkedHashMap();
        }
        final Property prop = PropertyFactory.createProperty(name);
        socketProperties.put(prop, value);
    }
    
    /**
     * {@link Socket}のプロパティを取得する。<p>
     *
     * @param name プロパティ名
     * @return 値
     */
    public Object getSocketProperty(String name){
        if(socketProperties == null){
            return null;
        }
        final Iterator props = socketProperties.keySet().iterator();
        while(props.hasNext()){
            final Property prop = (Property)props.next();
            if(prop.getPropertyName().equals(name)){
                return socketProperties.get(prop);
            }
        }
        return null;
    }
    
    public void setConnectionTimeout(int millisecond){
        connectionTimeout = millisecond;
    }
    public int getConnectionTimeout(){
        return connectionTimeout;
    }
    
    public Socket applySocketProperties(
        Socket socket
    ) throws IOException{
        try{
            if(socketProperties != null && socketProperties.size() != 0){
                final Iterator props = socketProperties.keySet().iterator();
                while(props.hasNext()){
                    final Property prop = (Property)props.next();
                    prop.setProperty(socket, socketProperties.get(prop));
                }
            }
        }catch(InvocationTargetException e){
            Throwable target = e.getTargetException();
            if(target instanceof IOException){
                throw (IOException)target;
            }else if(target instanceof RuntimeException){
                throw (RuntimeException)target;
            }else if(target instanceof Error){
                throw (Error)target;
            }else{
                throw new UndeclaredThrowableException(target);
            }
        }catch(NoSuchPropertyException e){
            throw new UndeclaredThrowableException(e);
        }
        return socket;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        out.writeObject(socketProperties);
        out.writeObject(socketFactory);
        out.writeObject(DEFAULT_HOST_PROPERTY_NAME.equals(hostPropertyName) ? null : hostPropertyName);
        out.writeObject(DEFAULT_LOCAL_HOST_PROPERTY_NAME.equals(localHostPropertyName) ? null : localHostPropertyName);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        socketProperties = (Map)in.readObject();
        socketFactory = (javax.net.SocketFactory)in.readObject();
        hostPropertyName = (String)in.readObject();
        if(hostPropertyName == null){
            hostPropertyName = DEFAULT_HOST_PROPERTY_NAME;
        }
        localHostPropertyName = (String)in.readObject();
        if(localHostPropertyName == null){
            localHostPropertyName = DEFAULT_LOCAL_HOST_PROPERTY_NAME;
        }
    }
}