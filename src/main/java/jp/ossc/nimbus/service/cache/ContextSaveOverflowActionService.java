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
package jp.ossc.nimbus.service.cache;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.context.SharedContext;
import jp.ossc.nimbus.service.context.DistributedSharedContext;

/**
 * コンテキスト永続化あふれ動作サービス。<p>
 * キャッシュからあふれたキャッシュオブジェクトをコンテキストの永続化機能（{@link Context#save(Object)}）を使って永続化して、削除するあふれ動作である。<br>
 *
 * @author M.Takata
 */
public class ContextSaveOverflowActionService extends ServiceBase
 implements OverflowAction, CacheRemoveListener, java.io.Serializable,
            ContextSaveOverflowActionServiceMBean{
    
    private static final long serialVersionUID = 7753212558317658253L;
    
    private ServiceName contextServiceName;
    private Context context;
    private SharedContext sharedContext;
    private DistributedSharedContext distSharedContext;
    
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }
    
    public void setContext(Context context){
        this.context = context;
    }
    public Context getContext(){
        return context;
    }
    
    public void startService() throws Exception{
        if(contextServiceName != null){
            context = (Context)ServiceManagerFactory.getServiceObject(contextServiceName);
        }
        if(context == null){
            throw new IllegalArgumentException("Context is null.");
        }
        if(context instanceof SharedContext){
            sharedContext = (SharedContext)context;
        }
        if(context instanceof DistributedSharedContext){
            distSharedContext = (DistributedSharedContext)context;
        }
    }
    
    // OverflowActionのJavaDoc
    public void setOverflowController(OverflowController controller){
    }
    
    // CacheRemoveListenerのJavaDoc
    public void removed(CachedReference ref){
        if(ref == null){
            return;
        }
        KeyCachedReference keyRef = (KeyCachedReference)ref;
        boolean isRemove = false;
        if(sharedContext != null){
            if(!sharedContext.isClient()){
                if(distSharedContext != null){
                    final int index = distSharedContext.getDataNodeIndex(keyRef.getKey());
                    isRemove = !distSharedContext.isClient(index) && distSharedContext.isMain(index);
                }else{
                    isRemove = sharedContext.isMain();
                }
            }
        }else{
            isRemove = true;
        }
        if(isRemove){
            try{
                context.save(keyRef.getKey());
            }catch(Exception e){
                getLogger().write("CSOA_00001", keyRef.getKey(), e);
            }
        }
    }
    
    /**
     * あふれたキャッシュオブジェクトを永続化して、キャッシュから削除する。<p>
     *
     * @param validator あふれ検証を行ったOverflowValidator
     * @param algorithm あふれキャッシュ参照を決定したOverflowAlgorithm
     * @param ref あふれたキャッシュ参照
     */
    public void action(
        OverflowValidator validator,
        OverflowAlgorithm algorithm,
        CachedReference ref
    ){
        if(ref == null){
            return;
        }
        KeyCachedReference keyRef = (KeyCachedReference)ref;
        boolean isSave = false;
        if(sharedContext != null){
            if(!sharedContext.isClient()){
                if(distSharedContext != null){
                    final int index = distSharedContext.getDataNodeIndex(keyRef.getKey());
                    isSave = !distSharedContext.isClient(index) && distSharedContext.isMain(index);
                }else{
                    isSave = sharedContext.isMain();
                }
            }
        }else{
            isSave = true;
        }
        keyRef.addCacheRemoveListener(this);
        if(isSave){
            try{
                context.save(keyRef.getKey());
                ref.remove(this);
            }catch(Exception e){
                getLogger().write("CSOA_00001", keyRef.getKey(), e);
            }
        }else{
            ref.remove(this);
        }
        if(validator != null){
            validator.remove(ref);
        }
        if(algorithm != null){
            algorithm.remove(ref);
        }
    }
    
    // OverflowActionのJavaDoc
    public void reset(){
    }
}
