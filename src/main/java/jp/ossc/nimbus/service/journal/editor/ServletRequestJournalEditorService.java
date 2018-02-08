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
package jp.ossc.nimbus.service.journal.editor;

import java.io.Serializable;
import java.util.*;
import javax.servlet.ServletRequest;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * ServletRequestオブジェクトをフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class ServletRequestJournalEditorService extends BlockJournalEditorServiceBase
 implements ServletRequestJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 4667997446381679299L;
    
    private static final String ATTRIBUTE_HEADER = "Attribute : ";
    private static final String PARAMETER_HEADER = "Parameter : ";
    private static final String CHARACTER_ENCODING_HEADER
         = "Character Encoding : ";
    private static final String CONTENT_LENGTH_HEADER = "Content Length : ";
    private static final String CONTENT_TYPE_HEADER = "Content Type : ";
    private static final String SENT_SERVER_HEADER
         = "Request Sent Server : ";
    private static final String RECEIVED_SERVER_HEADER
         = "Request Received Server : ";
    private static final String HOST_HEADER = "Host : ";
    private static final String PROTPCOL_HEADER = "Protocol : ";
    private static final String SCHEME_HEADER = "Scheme : ";
    private static final String LOCALE_HEADER = "Locale : ";
    
    private static final String ATTRIBUTE_SEPARATOR = " = ";
    private static final String PARAMETER_SEPARATOR = " = ";
    private static final String OPEN_BRACKET = "( ";
    private static final String CLOSE_BRACKET = " )";
    private static final String PORT_SEPARATOR = ":";
    
    private static final String HEADER = "[ServletRequest]";
    
    private static final String DEFAULT_SECRET_STRING = "******";
    
    private boolean isOutputSentServer = true;
    private boolean isOutputReceivedServer = true;
    private boolean isOutputHost = true;
    private boolean isOutputProtocol = true;
    private boolean isOutputScheme = true;
    private boolean isOutputLocale = true;
    private boolean isOutputContentType = true;
    private boolean isOutputContentLength = true;
    private boolean isOutputCharacterEncoding = true;
    private boolean isOutputAttributes = true;
    private boolean isOutputParameters = true;
    private boolean isOutputRemoteHost = true ;
    
    private String secretString = DEFAULT_SECRET_STRING;
    protected String[] secretAttributes;
    private Set secretAttributeSet;
    protected String[] secretParameters;
    private Set secretParameterSet;
    protected String[] enabledAttributes;
    private Set enabledAttributeSet;
    protected String[] enabledParameters;
    private Set enabledParameterSet;
    
    public ServletRequestJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setOutputSentServer(boolean isOutput){
        isOutputSentServer = isOutput;
    }
    
    public boolean isOutputSentServer(){
        return isOutputSentServer;
    }
    
    public void setOutputReceivedServer(boolean isOutput){
        isOutputReceivedServer = isOutput;
    }
    
    public boolean isOutputReceivedServer(){
        return isOutputReceivedServer;
    }
    
    public void setOutputHost(boolean isOutput){
        isOutputHost = isOutput;
    }
    
    public boolean isOutputHost(){
        return isOutputHost;
    }
    
    public void setOutputProtocol(boolean isOutput){
        isOutputProtocol = isOutput;
    }
    
    public boolean isOutputProtocol(){
        return isOutputProtocol;
    }
    
    public void setOutputScheme(boolean isOutput){
        isOutputScheme = isOutput;
    }
    
    public boolean isOutputScheme(){
        return isOutputScheme;
    }
    
    public void setOutputLocale(boolean isOutput){
        isOutputLocale = isOutput;
    }
    
    public boolean isOutputLocale(){
        return isOutputLocale;
    }
    
    public void setOutputContentType(boolean isOutput){
        isOutputContentType = isOutput;
    }
    
    public boolean isOutputContentType(){
        return isOutputContentType;
    }
    
    public void setOutputContentLength(boolean isOutput){
        isOutputContentLength = isOutput;
    }
    
    public boolean isOutputContentLength(){
        return isOutputContentLength;
    }
    
    public void setOutputCharacterEncoding(boolean isOutput){
        isOutputCharacterEncoding = isOutput;
    }
    
    public boolean isOutputCharacterEncoding(){
        return isOutputCharacterEncoding;
    }
    
    public void setOutputAttributes(boolean isOutput){
        isOutputAttributes = isOutput;
    }
    
    public boolean isOutputAttributes(){
        return isOutputAttributes;
    }
    
    public void setOutputParameters(boolean isOutput){
        isOutputParameters = isOutput;
    }
    
    public boolean isOutputParameters(){
        return isOutputParameters;
    }
    
    public void setSecretString(String str){
        secretString = str;
    }
    
    public String getSecretString(){
        return secretString;
    }
    
    public void setSecretAttributes(String[] names){
        secretAttributes = names;
    }
    
    public String[] getSecretAttributes(){
        return secretAttributes;
    }
    
    public void setEnabledAttributes(String[] names){
        enabledAttributes = names;
    }
    
    public String[] getEnabledAttributes(){
        return enabledAttributes;
    }
    
    public void setSecretParameters(String[] names){
        secretParameters = names;
    }
    
    public String[] getSecretParameters(){
        return secretParameters;
    }
    
    public void setEnabledParameters(String[] names){
        enabledParameters = names;
    }
    
    public String[] getEnabledParameters(){
        return enabledParameters;
    }
    
    public void createService() throws Exception{
        secretAttributeSet = new HashSet();
        secretParameterSet = new HashSet();
        enabledAttributeSet = new HashSet();
        enabledParameterSet = new HashSet();
    }
    
    public void startService() throws Exception{
        if(secretAttributes != null){
            for(int i = 0; i < secretAttributes.length; i++){
                secretAttributeSet.add(secretAttributes[i]);
            }
        }
        if(enabledAttributes != null){
            for(int i = 0; i < enabledAttributes.length; i++){
                enabledAttributeSet.add(enabledAttributes[i]);
            }
        }
        if(secretParameters != null){
            for(int i = 0; i < secretParameters.length; i++){
                secretParameterSet.add(secretParameters[i]);
            }
        }
        if(enabledParameters != null){
            for(int i = 0; i < enabledParameters.length; i++){
                enabledParameterSet.add(enabledParameters[i]);
            }
        }
    }
    
    public void stopService() throws Exception{
        secretAttributeSet.clear();
        enabledAttributeSet.clear();
        secretParameterSet.clear();
        enabledParameterSet.clear();
    }
    
    public void destroyService() throws Exception{
        secretAttributeSet = null;
        enabledAttributeSet = null;
        secretParameterSet = null;
        enabledParameterSet = null;
    }
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        final ServletRequest request = (ServletRequest)value;
        boolean isMake = false;
        if(isOutputSentServer()){
            makeSentServerFormat(finder, key, request, buf);
            isMake = true;
        }
        
        if(isOutputReceivedServer()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeReceivedServerFormat(finder, key, request, buf);
            isMake = true;
        }
        
        if(isOutputHost()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeHostFormat(finder, key, request, buf);
            isMake = true;
        }
        
        if(isOutputProtocol()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeProtocolFormat(finder, key, request, buf);
            isMake = true;
        }
        
        if(isOutputScheme()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeSchemeFormat(finder, key, request, buf);
            isMake = true;
        }
        
        if(isOutputLocale()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeLocaleFormat(finder, key, request, buf);
            isMake = true;
        }
        
        if(isOutputContentType()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeContentTypeFormat(finder, key, request, buf);
            isMake = true;
        }
        
        if(isOutputContentLength()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeContentLengthFormat(finder, key, request, buf);
            isMake = true;
        }
        
        if(isOutputCharacterEncoding()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeCharacterEncodingFormat(finder, key, request, buf);
            isMake = true;
        }
        
        if(isOutputAttributes()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeAttributesFormat(finder, key, request, buf);
            isMake = true;
        }
        
        if(isOutputParameters()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeParametersFormat(finder, key, request, buf);
            isMake = true;
        }
        return isMake;
    }
    
    protected StringBuilder makeAttributesFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuilder buf
    ){
        buf.append(ATTRIBUTE_HEADER);
        final Enumeration attrNames = request.getAttributeNames();
        if(attrNames.hasMoreElements()){
            buf.append(getLineSeparator());
        }else{
            buf.append(NULL_STRING);
            return buf;
        }
        final StringBuilder subBuf = new StringBuilder();
        while(attrNames.hasMoreElements()){
            final String name = (String)attrNames.nextElement();
            if(!enabledAttributeSet.isEmpty()
                 && !enabledAttributeSet.contains(name)){
                continue;
            }
            subBuf.append(name);
            subBuf.append(ATTRIBUTE_SEPARATOR);
            if(secretAttributeSet.contains(name)){
                subBuf.append(getSecretString());
            }else{
                makeObjectFormat(
                    finder,
                    null,
                    request.getAttribute(name),
                    subBuf
                );
            }
            if(attrNames.hasMoreElements()){
                subBuf.append(getLineSeparator());
            }
        }
        addIndent(subBuf);
        return buf.append(subBuf);
    }
    
    protected StringBuilder makeParametersFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuilder buf
    ){
        buf.append(PARAMETER_HEADER);
        final Enumeration paramNames = request.getParameterNames();
        if(paramNames.hasMoreElements()){
            buf.append(getLineSeparator());
        }else{
            buf.append(NULL_STRING);
            return buf;
        }
        final StringBuilder subBuf = new StringBuilder();
        while(paramNames.hasMoreElements()){
            final String name = (String)paramNames.nextElement();
            if(!enabledParameterSet.isEmpty()
                 && !enabledParameterSet.contains(name)){
                continue;
            }
            subBuf.append(name);
            subBuf.append(PARAMETER_SEPARATOR);
            if(secretParameterSet.contains(name)){
                subBuf.append(getSecretString());
            }else{
                makeObjectFormat(
                    finder,
                    null,
                    request.getParameterValues(name),
                    subBuf
                );
            }
            if(paramNames.hasMoreElements()){
                subBuf.append(getLineSeparator());
            }
        }
        addIndent(subBuf);
        return buf.append(subBuf);
    }
    
    protected StringBuilder makeCharacterEncodingFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuilder buf
    ){
        return buf.append(CHARACTER_ENCODING_HEADER)
            .append(request.getCharacterEncoding());
    }
    
    protected StringBuilder makeContentLengthFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuilder buf
    ){
        return buf.append(CONTENT_LENGTH_HEADER)
            .append(request.getContentLength());
    }
    
    protected StringBuilder makeContentTypeFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuilder buf
    ){
        return buf.append(CONTENT_TYPE_HEADER)
            .append(request.getContentType());
    }
    
    protected StringBuilder makeSentServerFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuilder buf
    ){
        buf.append(SENT_SERVER_HEADER);
        buf.append(request.getRemoteAddr());
        try{
            final int port = request.getRemotePort();
            buf.append(PORT_SEPARATOR);
            buf.append(port);
        }catch(NoSuchMethodError e){
        }catch(AbstractMethodError e){
        }
        if(isOutputRemoteHost) {
	        buf.append(OPEN_BRACKET)
	            .append(request.getRemoteHost())
	            .append(CLOSE_BRACKET);
        }
	    return buf;
    }
    
    protected StringBuilder makeReceivedServerFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuilder buf
    ){
        try{
            final String localAddr = request.getLocalAddr();
            buf.append(RECEIVED_SERVER_HEADER);
            buf.append(localAddr);
            final int localPort = request.getLocalPort();
            buf.append(PORT_SEPARATOR);
            buf.append(localPort);
            final String localName = request.getLocalName();
            buf.append(OPEN_BRACKET)
                .append(localName)
                .append(CLOSE_BRACKET);
        }catch(NoSuchMethodError e){
        }catch(AbstractMethodError e){
        }
        return buf;
    }
    
    protected StringBuilder makeHostFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuilder buf
    ){
        buf.append(HOST_HEADER);
        buf.append(request.getServerName());
        buf.append(PORT_SEPARATOR);
        buf.append(request.getServerPort());
        return buf;
    }
    
    protected StringBuilder makeProtocolFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuilder buf
    ){
        return buf.append(PROTPCOL_HEADER).append(request.getProtocol());
    }
    
    protected StringBuilder makeSchemeFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuilder buf
    ){
        return buf.append(SCHEME_HEADER).append(request.getScheme());
    }
    
    protected StringBuilder makeLocaleFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuilder buf
    ){
        buf.append(LOCALE_HEADER);
        makeObjectFormat(
            finder,
            null,
            request.getLocales(),
            buf
        );
        return buf;
    }

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.journal.editor.ServletRequestJournalEditorServiceMBean#isOutputRemoteHost()
	 */
	public boolean isOutputRemoteHost() {
		return this.isOutputRemoteHost;
	}

	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.journal.editor.ServletRequestJournalEditorServiceMBean#setOutputRemoteHost(boolean)
	 */
	public void setOutputRemoteHost(boolean isOutput) {
		this.isOutputRemoteHost = isOutput ;
	}
}
