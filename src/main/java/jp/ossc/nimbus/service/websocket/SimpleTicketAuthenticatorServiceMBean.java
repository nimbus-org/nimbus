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

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link SimpleTicketAuthenticatorService}のMBeanインタフェース
 * <p>
 *
 * @author M.Ishida
 */
public interface SimpleTicketAuthenticatorServiceMBean extends ServiceBaseMBean, Authenticator {

    public static final String DEFAULT_ID_KEY = "id";

    public static final String DEFAULT_TICKET_KEY = "ticket";

    public static final byte[] DEFAULT_KEY = { 49, 113, 97, 122, 50, 119, 115, 120, 51, 101, 100, 99, 52, 114, 102,
            118, 53, 116, 103, 98, 54, 121, 104, 110, 55, 117, 106, 109, 56, 105, 107, 44 };
    public static final String DEFAULT_ALGORITHM = "AES";
    public static final String DEFAULT_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    public static final int DEFAULT_IVLENGTH = 16;
    public static final String DEFAULT_HASHKEY = "DefaultHashKey99";

    public String getIdKey();

    public void setIdKey(String key);

    public String getTicketKey();

    public void setTicketKey(String key);

    public byte[] getKey();

    public void setKey(byte[] keyBytes);

    public String getAlgorithm();

    public void setAlgorithm(String paramAlgorithm);

    public String getTransformation();

    public void setTransformation(String paramTransformation);

    public int getIvLength();

    public void setIvLength(int length);

    public String getProvider();

    public void setProvider(String paramProvider);

    public String getHashKey();

    public void setHashKey(String hash);

    public long getOverLimitTime();

    public void setOverLimitTime(long time);

    public void setTicketMapping(String id, String ticket);

    public String getTicketMapping(String id);

    public Map getTicketMappings();
}
