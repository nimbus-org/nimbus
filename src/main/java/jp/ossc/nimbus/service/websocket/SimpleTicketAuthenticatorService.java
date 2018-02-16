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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.service.connection.ConnectionFactoryException;
import jp.ossc.nimbus.util.crypt.CryptParameters;
import jp.ossc.nimbus.util.crypt.FalsifiedParameterException;
import jp.ossc.nimbus.util.crypt.OverLimitExpiresException;
import jp.ossc.nimbus.util.crypt.UnexpectedCryptException;

/**
 * 簡易認証サービス。
 * <p>
 *
 * @author M.Ishida
 */
public class SimpleTicketAuthenticatorService extends ServiceBase implements SimpleTicketAuthenticatorServiceMBean {

    private static final long serialVersionUID = 8021761307165570160L;

    protected String idKey = DEFAULT_ID_KEY;
    protected String ticketKey = DEFAULT_TICKET_KEY;

    protected byte[] key = DEFAULT_KEY;
    protected String algorithm = DEFAULT_ALGORITHM;
    protected String transformation = DEFAULT_TRANSFORMATION;
    protected int ivLength = DEFAULT_IVLENGTH;
    protected String provider;
    protected String hashKey = DEFAULT_HASHKEY;
    protected long overLimitTime = -1;

    private Map idAndTicketMapping;

    protected CryptParameters wsCipher;

    @Override
    public void setTicketMapping(String id, String ticket) {
        if (idAndTicketMapping == null) {
            idAndTicketMapping = new HashMap();
        }
        idAndTicketMapping.put(id, ticket);
    }

    @Override
    public String getTicketMapping(String id) {
        return idAndTicketMapping == null ? null : (String)idAndTicketMapping.get(id);
    }

    @Override
    public Map getTicketMappings() {
        return idAndTicketMapping;
    }

    @Override
    public String getIdKey() {
        return idKey;
    }

    @Override
    public void setIdKey(String key) {
        idKey = key;
    }

    @Override
    public String getTicketKey() {
        return ticketKey;
    }

    @Override
    public void setTicketKey(String key) {
        ticketKey = key;
    }

    @Override
    public byte[] getKey() {
        return key;
    }

    @Override
    public void setKey(byte[] keyBytes) {
        key = keyBytes;
    }

    @Override
    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public void setAlgorithm(String paramAlgorithm) {
        algorithm = paramAlgorithm;
    }

    @Override
    public String getTransformation() {
        return transformation;
    }

    @Override
    public void setTransformation(String paramTransformation) {
        transformation = paramTransformation;
    }

    @Override
    public int getIvLength() {
        return ivLength;
    }

    @Override
    public void setIvLength(int length) {
        ivLength = length;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public void setProvider(String paramProvider) {
        provider = paramProvider;
    }

    @Override
    public String getHashKey() {
        return hashKey;
    }

    @Override
    public void setHashKey(String hash) {
        hashKey = hash;
    }

    @Override
    public long getOverLimitTime() {
        return overLimitTime;
    }

    @Override
    public void setOverLimitTime(long time) {
        overLimitTime = time;
    }

    @Override
    public void startService() throws Exception {
        wsCipher = new CryptParameters(key, algorithm, transformation, ivLength, provider, hashKey);
    }

    public AuthResult login(HttpServletRequest req, HttpServletResponse res) throws AuthenticateException {

        // 結果オブジェクト
        AuthResult result = new AuthResult();

        String id = req.getParameter(idKey);
        result.setId(id);
        String ticket = req.getParameter(ticketKey);
        // ID,チケットがnullの場合はNG
        if (id == null) {
            throw new AuthenticateException("id is null");
        }
        if (ticket == null) {
            throw new AuthenticateException("ticket is null");
        }
        if (!ticket.equals(getTicketMapping(id))) {
            throw new AuthenticateException("Did not authenticated : " + id);
        }
        Map map = wsCipher.createParametersMap();
        map.put(idKey, id);
        map.put(ticketKey, ticket);
        String wsTicket = null;
        try {
            wsTicket = URLEncoder.encode(wsCipher.encrypt(null, map), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AuthenticateException(e);
        }
        result.setTicket(wsTicket);
        result.setResult(true);
        return result;
    }

    public boolean handshake(String id, String ticket) throws AuthenticateException {
        try {
            // ID,チケットがnullの場合はNG
            if (id == null) {
                throw new AuthenticateException("id is null");
            }
            if (ticket == null) {
                throw new AuthenticateException("ticket is null");
            }
            Map map = null;
            if (overLimitTime != -1) {
                map = wsCipher.decrypt(null, ticket, overLimitTime);
            } else {
                map = wsCipher.decrypt(null, ticket);
            }
            String mapId = (String) map.get(idKey);
            String mapTicket = (String) map.get(ticketKey);
            if (!id.equals(mapId)) {
                throw new AuthenticateException("ticket is incorrect value. id:" + id + " ticket:" + ticket);
            }
        } catch (OverLimitExpiresException e) {
            throw new AuthenticateException(e);
        } catch (FalsifiedParameterException e) {
            throw new AuthenticateException(e);
        }
        return true;
    }

    public void logout(String id, String ticket, boolean isForce) throws AuthenticateException {
        // DoNothing
    }

}
