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
package jp.ossc.nimbus.service.http.httpclient;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import jp.ossc.nimbus.service.http.RESTHttpRequest;

/**
 * Jakarta HttpClientを使ったRESTfulなHTTP PUTリクエスト。<p>
 *
 * @author M.Takata
 */
public class RESTPutHttpRequestImpl extends PutHttpRequestImpl implements RESTHttpRequest{
    
    /**
     * リクエストするリソースを特定するキーを追加する。<p>
     * URLに"/"区切りで指定されてキーを追加する。<br>
     *
     * @param key リソースを特定するキー
     */
    public void addKey(String key){
        StringBuffer buf = new StringBuffer(url);
        if(url.length() != 0 && url.charAt(url.length() - 1) != '/'){
            buf.append('/');
        }
        buf.append(key);
        url = buf.toString();
    }
    
    public void setKey(String name, String value){
        Pattern p = Pattern.compile("\\{.+?\\}");
        Matcher m = p.matcher(url);
        StringBuffer buf = new StringBuffer();
        int offset = 0;
        while(m.find()){
            buf.append(url.substring(offset, m.start()));
            String keyPath = m.group();
            String keyName = keyPath.substring(1, keyPath.length() - 1);
            if(keyName.equals(name)){
                buf.append(value);
            }else{
                buf.append(keyPath);
            }
            offset = m.end();
        }
        if(offset != url.length()){
            buf.append(url.substring(offset));
        }
        url = buf.toString();
    }
}
