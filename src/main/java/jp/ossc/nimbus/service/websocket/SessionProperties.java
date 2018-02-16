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
package jp.ossc.nimbus.service.websocket;

import java.util.Map;

import javax.websocket.Session;

/**
 * WebSocketのセッション上に情報を保持するためのクラス。
 * <p>
 *
 * @author M.Ishida
 */
public class SessionProperties implements java.io.Serializable {

    /**
     * SessionのUserPropertiesにSessionPropertyオブジェクトを格納する際のキー
     */
    public static final String SESSION_PROPERTY_KEY = "SessionProperty";

    private String id;
    private String ticket;
    private String webSocketSessionId;
    private String httpSessionId;
    private String path;
    private String ip;
    private String port;
    private Map headers;
    private Map parameterMap;
    private long pingRequestTime = -1;
    private long pingSendTime = -1;
    private long pongReceiveTime = -1;
    private long sendMessageCount = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getWebSocketSessionId() {
        return webSocketSessionId;
    }

    public void setWebSocketSessionId(String webSocketSessionId) {
        this.webSocketSessionId = webSocketSessionId;
    }

    public String getHttpSessionId() {
        return httpSessionId;
    }

    public void setHttpSessionId(String httpSessionId) {
        this.httpSessionId = httpSessionId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Map getHeaders() {
        return headers;
    }

    public void setHeaders(Map headers) {
        this.headers = headers;
    }

    public Map getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map parameterMap) {
        this.parameterMap = parameterMap;
    }

    public long getPingRequestTime() {
        return pingRequestTime;
    }

    public void setPingRequestTime(long time) {
        pingRequestTime = time;
    }

    public long getPingSendTime() {
        return pingSendTime;
    }

    public void setPingSendTime(long time) {
        pingSendTime = time;
    }

    public long getPongReceiveTime() {
        return pongReceiveTime;
    }

    public void setPongReceiveTime(long time) {
        pongReceiveTime = time;
    }

    public void addSendMessageCount() {
        sendMessageCount++;
    }

    public long getSendMessageCount() {
        return sendMessageCount;
    }

    public String toString() {
        return "[id:" + id + ", ticket:" + ticket + ", webSocketSessionId:" + webSocketSessionId + ", httpSessionId:"
                + httpSessionId + ", path:" + path + ", ip:" + ip + ", port:" + port + ", sendMessageCount:"
                + sendMessageCount + " PingSendTime:" + pingSendTime + " PongReceiveTime:" + pongReceiveTime + "]";
    }

    /**
     * SessionPropertyをSessionのUserPropertiesに格納する。
     * <p>
     *
     * @param session WebSocketセッション
     * @param prop SessionPropertyオブジェクト
     */
    public static void put(Session session, SessionProperties prop) {
        session.getUserProperties().put(SessionProperties.SESSION_PROPERTY_KEY, prop);
    }

    /**
     * SessionのUserPropertiesからSessionPropertyを取得する。
     * <p>
     *
     * @param session WebSocketセッション
     *
     * @return SessionPropertyオブジェクト
     */
    public static SessionProperties getSessionProperty(Session session) {
        if(session == null || session.getUserProperties() == null){
            return null;
        }
        return ((SessionProperties) session.getUserProperties().get(SessionProperties.SESSION_PROPERTY_KEY));
    }

}
