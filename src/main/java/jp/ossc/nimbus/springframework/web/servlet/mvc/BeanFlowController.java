package jp.ossc.nimbus.springframework.web.servlet.mvc;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.sun.jmx.snmp.ThreadContext;

import jp.ossc.nimbus.beans.dataset.DataSet;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.aop.interceptor.servlet.StreamExchangeInterceptorServiceMBean;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory;
import jp.ossc.nimbus.service.context.ThreadContextService;

/**
 * アノテーションを使わずにコントローラを実装するサンプル。
 *
 * @author nakashima
 *
 */
public class BeanFlowController extends AbstractController{
    // AbstractControllerはGETとPOSTしかサポートしてない

    private String requestObjectAttributeName=
            StreamExchangeInterceptorServiceMBean.DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME;
    private String reponseObjectContextKey=
            StreamExchangeInterceptorServiceMBean.DEFAULT_RESPONSE_OBJECT_CONTEXT_KEY;

    private ServiceName beanflowInvokerFactoryServiceName=new ServiceName("WebServer.Servlet", "BeanFlowInvokerFactory");
    private BeanFlowInvokerFactory beanflowInvokerFactory = (BeanFlowInvokerFactory)ServiceManagerFactory
       .getServiceObject(beanflowInvokerFactoryServiceName);

    private ServiceName threadContextServiceName=new ServiceName("WebServer.Log", "ThreadContext");
    private ThreadContextService threadContext = (ThreadContextService)ServiceManagerFactory
       .getServiceObject(threadContextServiceName);

    // 以下getterとsetter
    // xml configからこのControllerにinjectionする口
    public String getRequestObjectAttributeName() {
        return requestObjectAttributeName;
    }



    public void setRequestObjectAttributeName(String requestObjectAttributeName) {
        this.requestObjectAttributeName = requestObjectAttributeName;
    }



    public String getReponseObjectContextKey() {
        return reponseObjectContextKey;
    }



    public void setReponseObjectContextKey(String reponseObjectContextKey) {
        this.reponseObjectContextKey = reponseObjectContextKey;
    }



    public ServiceName getBeanflowInvokerFactoryServiceName() {
        return beanflowInvokerFactoryServiceName;
    }



    public void setBeanflowInvokerFactoryServiceName(ServiceName beanflowInvokerFactoryServiceName) {
        this.beanflowInvokerFactoryServiceName = beanflowInvokerFactoryServiceName;
    }



    public BeanFlowInvokerFactory getBeanflowInvokerFactory() {
        return beanflowInvokerFactory;
    }



    public void setBeanflowInvokerFactory(BeanFlowInvokerFactory beanflowInvokerFactory) {
        this.beanflowInvokerFactory = beanflowInvokerFactory;
    }



    public ServiceName getThreadContextServiceName() {
        return threadContextServiceName;
    }



    public void setThreadContextServiceName(ServiceName threadContextServiceName) {
        this.threadContextServiceName = threadContextServiceName;
    }



    public ThreadContextService getThreadContext() {
        return threadContext;
    }



    public void setThreadContext(ThreadContextService threadContext) {
        this.threadContext = threadContext;
    }


    /**
     * AbstractControllerを実装するとspringに自前コントローラーをアノテーションなしで組み込める。
     *
     *
     * @param request mvcから渡されるRequest
     * @param response urlパスで指定されるbeanflow名
     * @return ModelAndViewはページ遷移がある場合に利用。RESTの場合は不要。
     *         nimbusのinterceptorでDTO⇔HTTPR/Rオブジェクトをさばくなら、以下のサンプルの通りでよい。
     * @throws Exception ここに上がってきた例外は全て上へスローする
     */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String beanflowName = getBeanFlowName(request);
        DataSet in = (DataSet)request.getAttribute(requestObjectAttributeName);
        DataSet ret = (DataSet) beanflowInvokerFactory.createFlow(beanflowName).invokeFlow(in);
        threadContext.put(reponseObjectContextKey, ret);

        return null;
    }

    // BeanFlowSelectorServiceからコピペ。ちゃんと作るならサービスアロケート。
    private String getBeanFlowName(HttpServletRequest req) {
        String flowName = req.getServletPath();
        if(req.getPathInfo() != null){
            flowName = flowName + req.getPathInfo();
        }
        if(flowName == null || flowName.length() == 0){
            return null;
        }
/*        if(flowName.endsWith(beanFlowPathPostfix)){
            flowName = flowName.substring(
                0,
                flowName.length() - beanFlowPathPostfix.length()
            );
        }
*/        if(flowName.length() == 0){
            return null;
        }
        return flowName;
    }
}
