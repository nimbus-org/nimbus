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
package jp.ossc.nimbus.service.aop.interceptor.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.aop.InterceptorChain;
import jp.ossc.nimbus.service.aop.ServletFilterInvocationContext;
import jp.ossc.nimbus.service.aop.interceptor.servlet.HttpServletRequestURLConvertInterceptorService.ResourcePath.ParameterPath;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.journal.Journal;

/**
 * HttpServletRequest URL変換インターセプタ。
 * <p>
 * resourcePathsに指定されたパス表記にマッチしたリクエストに対して、パス階層の一部をパラメータとして扱う。
 * パラメータとして扱うパスは{パラメータ名}として記載することで、パラメータパスとして認識される。
 * パラメータとして扱われるパスの値はThreadContext、Jounalに登録され、パス上から除外される。 ex)
 * resourcePath=/{ScreenID}/action/.*\\.bf
 * 実際のサーブレットパス=/Screen001/action/sample.bf 後続処理でのサーブレットパス=/action/sample.bf
 *
 * @author M.Ishida
 */
public class HttpServletRequestURLConvertInterceptorService extends ServletFilterInterceptorService implements
        HttpServletRequestURLConvertInterceptorServiceMBean {

    private static final long serialVersionUID = 8599129621419714729L;

    protected ServiceName threadContextServiceName;
    protected ServiceName journalServiceName;

    protected Context threadContext;
    protected Journal journal;

    protected String[] resourcePaths;

    protected List resourcePathList;

    public void setThreadContextServiceName(ServiceName name) {
        threadContextServiceName = name;
    }

    public ServiceName getThreadContextServiceName() {
        return threadContextServiceName;
    }

    public void setJournalServiceName(ServiceName name) {
        journalServiceName = name;
    }

    public ServiceName getJournalServiceName() {
        return journalServiceName;
    }

    public void setResourcePaths(String[] paths) {
        resourcePaths = paths;
    }

    public String[] getResourcePaths() {
        return resourcePaths;
    }

    /**
     * サービスの開始処理を行う。
     * <p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception {
        if (resourcePaths == null) {
            throw new IllegalArgumentException("ResourcePaths must be specified.");
        }
        resourcePathList = new ArrayList();
        for (int i = 0; i < resourcePaths.length; i++) {
            ResourcePath rp = new ResourcePath(resourcePaths[i]);
            resourcePathList.add(rp);
        }
        if (threadContextServiceName != null) {
            threadContext = (Context) ServiceManagerFactory.getServiceObject(threadContextServiceName);
        }
        if (journalServiceName != null) {
            journal = (Journal) ServiceManagerFactory.getServiceObject(journalServiceName);
        }
    }

    /**
     * resourcePathに設定された内容に従い、URI内の一部をパラメータとして判断する。
     * <p>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、
     *                本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても
     *                、呼び出し元には伝播されない。
     */
    public Object invokeFilter(ServletFilterInvocationContext context, InterceptorChain chain) throws Throwable {
        if (getState() == STARTED) {
            final HttpServletRequest request = (HttpServletRequest) context.getServletRequest();
            String uri = request.getRequestURI();
            for (int i = 0; i < resourcePathList.size(); i++) {
                ResourcePath rp = (ResourcePath) resourcePathList.get(i);
                if (rp.isPatternMatch(uri)) {
                    Map paramMap = new HashMap();
                    rp.parseParameter(uri, paramMap);
                    Iterator itr = paramMap.entrySet().iterator();
                    while (itr.hasNext()) {
                        Entry entry = (Entry) itr.next();
                        if (journal != null && journal.isStartJournal()) {
                            journal.addInfo((String) entry.getKey(), entry.getValue());
                        }
                        if (threadContext != null) {
                            threadContext.put((String) entry.getKey(), entry.getValue());
                        }
                    }
                    int index = 1;
                    List paramIndexList = rp.getParameterPathIndex();
                    String servletPath = request.getServletPath();
                    if (servletPath != null && !"".equals(servletPath)) {
                        StringBuilder sb = new StringBuilder();
                        List list = ResourcePath.splitPath(servletPath);
                        for (int j = 0; j < list.size(); j++) {
                            boolean isAdd = true;
                            for (int k = 0; k < paramIndexList.size(); k++) {
                                if (index == ((Integer) paramIndexList.get(k)).intValue()) {
                                    isAdd = false;
                                }
                            }
                            if (isAdd) {
                                sb.append(list.get(j));
                            }
                            index++;
                        }
                        servletPath = sb.toString();
                    }
                    String pathInfo = request.getPathInfo();
                    if (pathInfo != null && !"".equals(pathInfo)) {
                        StringBuilder sb = new StringBuilder();
                        List list = ResourcePath.splitPath(pathInfo);
                        for (int j = 0; j < list.size(); j++) {
                            boolean isAdd = true;
                            for (int k = 0; k < paramIndexList.size(); k++) {
                                if (index == ((Integer) paramIndexList.get(k)).intValue()) {
                                    isAdd = false;
                                }
                            }
                            if (isAdd) {
                                sb.append(list.get(j));
                            }
                            index++;
                        }
                        pathInfo = sb.toString();
                    }
                    URLConvertHttpServletRequestWrapper servletRequestWrapper = new URLConvertHttpServletRequestWrapper(
                            request, servletPath, pathInfo, paramMap);
                    context.setServletRequest(servletRequestWrapper);
                    break;
                }
            }
        }
        return chain.invokeNext(context);
    }

    public class URLConvertHttpServletRequestWrapper extends HttpServletRequestWrapper {

        public URLConvertHttpServletRequestWrapper(HttpServletRequest request, String servletPath, String pathInfo,
                Map map) {
            super(request);
            parameterMap = map;
            this.servletPath = servletPath;
            this.pathInfo = pathInfo;
        }

        private Map parameterMap;
        private String servletPath;
        private String pathInfo;

        public Map getParameterMap() {
            return parameterMap;
        }

        public String getRequestURI() {
            StringBuilder uri = new StringBuilder();
            uri.append(getContextPath());
            if(getServletPath() != null){
                uri.append(getServletPath());
            }
            if(getPathInfo() != null){
                uri.append(getPathInfo());
            }
            return uri.toString();
        }

        public StringBuffer getRequestURL() {
            StringBuffer url = new StringBuffer();
            url.append(getScheme());
            url.append("://").append(getLocalAddr());
            url.append(":").append(getLocalPort());
            url.append(getRequestURI());
            return url;
        }

        public String getServletPath() {
            return servletPath;
        }

        public String getPathInfo() {
            return pathInfo;
        }

    }

    public static class ResourcePath {

        protected String path;
        protected List pathElements = new ArrayList();
        protected List parameterPathIndex;

        public ResourcePath(String path) throws IllegalArgumentException {
            if (path.length() == 0) {
                throw new IllegalArgumentException("empty path : path=" + path);
            }
            this.path = path;
            List elements = splitPath(path);
            for (int i = 0; i < elements.size(); i++) {
                String element = (String) elements.get(i);
                if (ParameterPath.isParameterPath(element)) {
                    if (parameterPathIndex == null) {
                        parameterPathIndex = new ArrayList();
                    }
                    parameterPathIndex.add(new Integer(pathElements.size()));
                    pathElements.add(new ParameterPath(element));
                } else {
                    pathElements.add(element);
                }
            }
        }

        public String getPath() {
            return path;
        }

        public List getPathElementList() {
            return pathElements;
        }

        public int getParameterPathSize() {
            return parameterPathIndex == null ? 0 : parameterPathIndex.size();
        }

        public ParameterPath getParameterPathIndex(int index) {
            return parameterPathIndex == null || parameterPathIndex.size() <= index ? null
                    : (ParameterPath) pathElements.get(((Integer) parameterPathIndex.get(index)).intValue());
        }

        public static List splitPath(String path) {
            String[] paths = path.split("/");
            List result = new ArrayList();
            for (int i = 0; i < paths.length; i++) {
                String p = (String) paths[i];
                if (p.length() == 0) {
                    continue;
                }
                result.add("/" + p);
            }
            return result;
        }

        public boolean hasParameterPath() {
            return parameterPathIndex != null;
        }

        public List getParameterPathIndex() {
            return parameterPathIndex;
        }

        public boolean isPatternMatch(String path) {
            List paths = splitPath(path);
            if (paths.size() < pathElements.size()) {
                return false;
            }
            for (int i = 0; i < pathElements.size(); i++) {
                Object peo = pathElements.get(i);
                if (peo instanceof ParameterPath) {
                    continue;
                }
                String pe = (String) peo;
                String p = (String) paths.get(i);
                if (i == pathElements.size() - 1) {
                    StringBuilder sb = new StringBuilder(p);
                    for (int j = i; j < paths.size(); j++) {
                        sb.append(paths.get(j));
                    }
                    p = sb.toString();
                }
                if (!pe.equals(p)) {
                    Pattern pattern = Pattern.compile(pe);
                    Matcher m = pattern.matcher(p);
                    if (!m.find()) {
                        return false;
                    }
                }
            }
            return true;
        }

        public String parseParameter(String path, Map result) throws IndexOutOfBoundsException {
            if (!hasParameterPath()) {
                return path;
            }
            List paths = splitPath(path);
            for (int i = 0; i < parameterPathIndex.size(); i++) {
                Integer index = (Integer) parameterPathIndex.get(i);
                ParameterPath pp = (ParameterPath) pathElements.get(index.intValue());
                result = pp.parseParameter((String) paths.get(index.intValue()), result);
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < paths.size(); i++) {
                String p = (String) paths.get(i);
                boolean isAdd = true;
                for (int j = 0; j < parameterPathIndex.size(); j++) {
                    Integer index = (Integer) parameterPathIndex.get(j);
                    if (i == index.intValue()) {
                        isAdd = false;
                    }
                }
                if (isAdd) {
                    sb.append(p);
                }
            }
            return sb.toString();
        }

        public String toString() {
            return path;
        }

        public static class ParameterPath {

            protected static final Pattern PARAMETER_PATTERN = Pattern.compile("\\{.+?\\}");

            protected final String path;
            protected final int paramCount;
            protected List paramElements = new ArrayList();

            public ParameterPath(String path) throws IllegalArgumentException {
                this.path = path;
                Matcher m = PARAMETER_PATTERN.matcher(path);
                int offset = 0;
                int count = 0;
                while (m.find()) {
                    if (offset == 0 && m.start() != offset) {
                        paramElements.add(path.substring(offset, m.start()));
                    } else {
                        throw new IllegalArgumentException("deletemer not exists. path=" + path);
                    }
                    paramElements.add(new ParameterElement(m.group()));
                    count++;
                    offset = m.end();
                }
                if (offset != path.length()) {
                    paramElements.add(path.substring(offset));
                }
                paramCount = count;
            }

            public String getPath() {
                return path;
            }

            public int getParameterCount() {
                return paramCount;
            }

            public List getParameterElementList() {
                return paramElements;
            }

            public Map parseParameter(String path, Map result) throws IndexOutOfBoundsException {
                if (result == null) {
                    result = new LinkedHashMap();
                }
                int offset = 0;
                ParameterElement prePe = null;
                for (int i = 0; i < paramElements.size(); i++) {
                    Object element = paramElements.get(i);
                    if (prePe == null) {
                        if (element instanceof String) {
                            offset += ((String) element).length();
                        } else {
                            prePe = (ParameterElement) element;
                        }
                    } else {
                        result.put(prePe.name, path.substring(offset, path.indexOf((String) element, offset)));
                        offset += ((String) element).length();
                        prePe = null;
                    }
                }
                if (prePe != null) {
                    result.put(prePe.name, path.substring(offset));
                }
                return result;
            }

            public String toString() {
                return path;
            }

            public int hashCode() {
                return path.hashCode();
            }

            public boolean equals(Object obj) {
                if (obj == null) {
                    return false;
                }
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof ParameterPath)) {
                    return false;
                }
                return path.equals(((ParameterPath) obj).path);
            }

            public static boolean isParameterPath(String path) {
                Matcher m = PARAMETER_PATTERN.matcher(path);
                return m.find();
            }

            protected static class ParameterElement {
                protected final String name;

                public ParameterElement(String element) {
                    name = element.substring(1, element.length() - 1);
                }

                public String getName() {
                    return name;
                }
            }
        }
    }
}
