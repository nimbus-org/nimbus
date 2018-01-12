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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.InetAddress;

import jp.ossc.nimbus.beans.*;

/**
 * サーバソケットファクトリ。<p>
 * このファクトリから生成されるサーバソケットは、{@link #setServerSocketProperty(String, Object)}で、予め設定されたプロパティが設定される。<br>
 *
 * @author M.Takata
 */
public class ServerSocketFactory extends javax.net.ServerSocketFactory implements java.rmi.server.RMIServerSocketFactory{
    
    protected Map serverSocketProperties;
    protected Map socketProperties;
    
    public static javax.net.ServerSocketFactory getDefault(){
        return new ServerSocketFactory();
    }
    
    public java.net.ServerSocket createServerSocket() throws IOException{
        return applyServerSocketProperties(new ServerSocket());
    }
    
    public java.net.ServerSocket createServerSocket(int port) throws IOException{
        return applyServerSocketProperties(new ServerSocket(port));
    }
    
    public java.net.ServerSocket createServerSocket(int port, int backlog) throws IOException{
        return applyServerSocketProperties(new ServerSocket(port, backlog));
    }
    
    public java.net.ServerSocket createServerSocket(int port, int backlog, InetAddress bindAddr) throws IOException{
        return applyServerSocketProperties(new ServerSocket(port, backlog, bindAddr));
    }
    
    /**
     * java.net.ServerSocketにプロパティを設定する。<p>
     *
     * @param props プロパティマップ
     */
    public void setServerSocketProperties(Map props){
        if(props == null || props.size() == 0){
            if(serverSocketProperties != null){
                serverSocketProperties = null;
            }
            return;
        }
        final Iterator names = props.keySet().iterator();
        while(names.hasNext()){
            String name = (String)names.next();
            setServerSocketProperty(name, props.get(name));
        }
    }
    
    /**
     * java.net.ServerSocketにプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param value 値
     */
    public void setServerSocketProperty(String name, Object value){
        if(serverSocketProperties == null){
            serverSocketProperties = new LinkedHashMap();
        }
        final Property prop = PropertyFactory.createProperty(name);
        serverSocketProperties.put(prop, value);
    }
    
    /**
     * java.net.ServerSocketのプロパティを取得する。<p>
     *
     * @param name プロパティ名
     * @return 値
     */
    public Object getServerSocketProperty(String name){
        if(serverSocketProperties == null){
            return null;
        }
        final Iterator props = serverSocketProperties.keySet().iterator();
        while(props.hasNext()){
            final Property prop = (Property)props.next();
            if(prop.getPropertyName().equals(name)){
                return serverSocketProperties.get(prop);
            }
        }
        return null;
    }
    
    /**
     * java.net.Socketにプロパティを設定する。<p>
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
     * java.net.Socketにプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param value 値
     */
    public void setSocketProperty(String name, Object value){
        if(socketProperties == null){
            socketProperties = new LinkedHashMap();
        }
        socketProperties.put(name, value);
    }
    
    /**
     * java.net.Socketのプロパティを取得する。<p>
     *
     * @param name プロパティ名
     * @return 値
     */
    public Object getSocketProperty(String name){
        if(socketProperties == null){
            return null;
        }
        return socketProperties.get(name);
    }
    
    public java.net.ServerSocket applyServerSocketProperties(
        java.net.ServerSocket serverSocket
    ) throws IOException{
        try{
            if(serverSocket instanceof ServerSocket && socketProperties != null && socketProperties.size() != 0){
                final Iterator names = socketProperties.keySet().iterator();
                while(names.hasNext()){
                    final String name = (String)names.next();
                    ((ServerSocket)serverSocket).setSocketProperty(
                        name,
                        socketProperties.get(name)
                    );
                }
            }
            if(serverSocketProperties != null && serverSocketProperties.size() != 0){
                final Iterator props = serverSocketProperties.keySet().iterator();
                while(props.hasNext()){
                    final Property prop = (Property)props.next();
                    prop.setProperty(serverSocket, serverSocketProperties.get(prop));
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
        return serverSocket;
    }
}