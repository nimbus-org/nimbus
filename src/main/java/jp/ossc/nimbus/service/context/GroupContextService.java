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
package jp.ossc.nimbus.service.context;

import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * �O���[�v�R���e�L�X�g�B<p>
 * �����̃R���e�L�X�g���O���[�s���O���āA�P�̃R���e�L�X�g�Ƃ��āA�R���e�L�X�g��񂪎擾�ł���悤�ɂ���B<br>
 * �R���e�L�X�g���̒ǉ��̓T�|�[�g���Ȃ��B<br>
 * �ȉ��ɁA�T�[�r�X��`��������B<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="Context"
 *                  code="jp.ossc.nimbus.service.context.GroupContextService"&gt;
 *             &lt;attribute name="ContextServiceNames"&gt;
 *                 #Context1
 *                 #Context2
 *             &lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="Context1"
 *                  code="jp.ossc.nimbus.service.context.DefaultContextService"&gt;
 *             &lt;attribute name="HOME_PATH"&gt;/home&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="Context2"
 *                  code="jp.ossc.nimbus.service.context.DefaultContextService"&gt;
 *             &lt;attribute name="DOMAIN"&gt;nimbus.ossc.jp&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author H.Nakano
 */
public class GroupContextService extends ServiceBase
 implements Context, GroupContextServiceMBean, java.io.Serializable{
    
    private static final long serialVersionUID = -5282880238704983055L;
    
    /**
     * �O���[�s���O����R���e�L�X�g�T�[�r�X���z��B<p>
     */
    protected ServiceName[] contextServiceNames;
    
    protected Context[] contexts;
    
    // GroupContextServiceMBean��JavaDoc
    public void setContextServiceNames(ServiceName[] names){
        contextServiceNames = names;
    }
    
    // GroupContextServiceMBean��JavaDoc
    public ServiceName[] getContextServiceNames(){
        return contextServiceNames;
    }
    
    /**
     * Context��ݒ肷��B
     */
    public void setContexts(Context[] contexts) {
        this.contexts = contexts;
    }

    // Context��JavaDoc
    public Object get(Object key){
        Context context = null;
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    Context ctx = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    if(ctx.containsKey(key)){
                        context = ctx;
                        break;
                    }
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i=0; i<contexts.length; i++) {
                Context ctx = contexts[i];
                if(ctx.containsKey(key)){
                    context = ctx;
                    break;
                }
            }
        }
        return context == null ? null : context.get(key);
    }
    
    /**
     * �T�|�[�g���Ȃ��B<br>
     *
     * @param key �L�[
     * @param value �R���e�L�X�g���
     * @return �w�肳�ꂽ�L�[�Ɋ֘A�t�����Ă����R���e�L�X�g���B���݂��Ȃ��ꍇ�́Anull
     * @exception UnsupportedOperationException
     */
    public Object put(Object key, Object value){
        throw new UnsupportedOperationException();
    }
    
    /**
     * �R���e�L�X�g���̃L�[�W�����擾����B<p>
     * �A���A���̃L�[�W���́A�ύX�s�ł���B<br>
     *
     * @return �R���e�L�X�g���̃L�[�W��
     */
    public Set keySet(){
        final Set result = new HashSet();
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    result.addAll(context.keySet());
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                result.addAll(context.keySet());
            }
        }
        return Collections.unmodifiableSet(result);
    }
    
    /**
     * �R���e�L�X�g���̏W�����擾����B<p>
     * �A���A���̃R���e�L�X�g���̏W���́A�ύX�s�ł���B<br>
     *
     * @return �R���e�L�X�g���̏W��
     */
    public Collection values() {
        final Map result = new HashMap();
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    result.putAll(context);
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                result.putAll(context);
            }
        }
        return Collections.unmodifiableCollection(result.values());
    }
    
    /**
     * �S�ẴR���e�L�X�g�����擾����B<p>
     * �A���A���̃R���e�L�X�g���́A�ύX�s�ł���B<br>
     *
     * @return �R���e�L�X�g���
     */
    public Map all(){
        final Map result = new HashMap();
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    result.putAll(context);
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                result.putAll(context);
            }
        }
        return Collections.unmodifiableMap(result);
    }
    
    /**
     * �w�肳�ꂽ�L�[�Ɋ֘A�t����ꂽ�R���e�L�X�g�����폜����B<p>
     * �O���[�s���O����S�ẴR���e�L�X�g����폜���A�Ō�ɍ폜�����I�u�W�F�N�g��Ԃ��B<br>
     *
     * @param key �L�[
     * @return �폜���ꂽ�R���e�L�X�g���B�폜����R���e�L�X�g��񂪂Ȃ��ꍇ�́Anull
     */
    public Object remove(Object key) {
        Object result = null;
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    if(context.containsKey(key)){
                        result = context.remove(key);
                    }
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                if(context.containsKey(key)){
                    result = context.remove(key);
                }
            }
        }
        return result;
    }
    
    /**
     * �O���[�s���O����S�ẴR���e�L�X�g�����폜����B<p>
     */
    public void clear(){
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    context.clear();
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                context.clear();
            }
        }
    }
    
    // Context��JavaDoc
    public boolean isEmpty(){
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    if(!context.isEmpty()){
                        return false;
                    }
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                if(!context.isEmpty()){
                    return false;
                }
            }
        }
        return true;
    }
    
    // Context��JavaDoc
    public boolean containsKey(Object key){
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    if(context.containsKey(key)){
                        return true;
                    }
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                if(context.containsKey(key)){
                    return true;
                }
            }
        }
        return false;
    }
    
    // Context��JavaDoc
    public boolean containsValue(Object value) {
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    if(context.containsValue(value)){
                        return true;
                    }
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                if(context.containsValue(value)){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * �R���e�L�X�g���̃G���g���W�����擾����B<p>
     * �A���A�G���g���W���ɑ΂���ύX�͕s�ł���B<br>
     *
     * @return �R���e�L�X�g���̃G���g���W��
     */
    public Set entrySet(){
        final Map result = new HashMap();
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    final Object[] entries = context.entrySet().toArray();
                    for(int j = 0; j < entries.length; j++){
                        result.put(
                            ((Map.Entry)entries[j]).getKey(),
                            entries[j]
                        );
                    }
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i =0; i<contexts.length; i++){
                final Context context = contexts[i];
                final Object[] entries = context.entrySet().toArray();
                for(int j = 0; j < entries.length; j++){
                    result.put(
                        ((Map.Entry)entries[j]).getKey(),
                        entries[j]
                    );
                }
            }
        }
        return Collections.unmodifiableSet(new HashSet(result.values()));
    }
    
    // Context��JavaDoc
    public int size(){
        int size = 0;
        if(contextServiceNames != null){
            for(int i = 0; i < contextServiceNames.length; i++){
                try{
                    final Context context = (Context)ServiceManagerFactory
                        .getServiceObject(contextServiceNames[i]);
                    size += context.size();
                }catch(ServiceNotFoundException e){
                }
            }
        } else if(contexts != null) {
            for(int i = 0; i < contexts.length; i++){
                final Context context = contexts[i];
                size += context.size();
            }
        }
        return size;
    }
    
    /**
     * �T�|�[�g���Ȃ��B<p>
     *
     * @param t �R���e�L�X�g���Ƃ��Đݒ肷��}�b�v
     * @exception UnsupportedOperationException
     */
    public void putAll(Map t){
        throw new UnsupportedOperationException();
    }
    
    // Context��JavaDoc
    public Object get(String key){
        return get((Object)key);
    }
    
    // Context��JavaDoc
    public Object remove(String key) {
        return remove((Object)key);
    }
    
    // Context��JavaDoc
    public String list(){
        final StringBuilder buf = new StringBuilder();
        synchronized(this){
            for(Iterator ite = keySet().iterator(); ite.hasNext();){
                Object key = ite.next();
                buf.append(key);
                buf.append(" : ");
                buf.append(get(key));
                buf.append('\n');
            }
        }
        return buf.toString();
    }
    
    /**
     * �T�|�[�g���Ȃ��B<p>
     *
     * @exception UnsupportedOperationException �K��throw����
     */
    public void load() throws Exception{
        throw new UnsupportedOperationException();
    }
    
    /**
     * �T�|�[�g���Ȃ��B<p>
     *
     * @exception UnsupportedOperationException �K��throw����
     */
    public void loadKey() throws Exception{
        throw new UnsupportedOperationException();
    }
    
    /**
     * �T�|�[�g���Ȃ��B<p>
     *
     * @param key �L�[
     * @exception UnsupportedOperationException �K��throw����
     */
    public void load(Object key) throws Exception{
        throw new UnsupportedOperationException();
    }
    
    /**
     * �T�|�[�g���Ȃ��B<p>
     *
     * @exception UnsupportedOperationException �K��throw����
     */
    public void save() throws Exception{
        throw new UnsupportedOperationException();
    }
    
    /**
     * �T�|�[�g���Ȃ��B<p>
     *
     * @param key �L�[
     * @exception UnsupportedOperationException �K��throw����
     */
    public void save(Object key) throws Exception{
        throw new UnsupportedOperationException();
    }
}
