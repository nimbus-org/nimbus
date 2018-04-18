package jp.ossc.nimbus.springframework.web.servlet.mvc;

import javax.management.ServiceNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.beans.dataset.DataSet;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.aop.interceptor.servlet.StreamExchangeInterceptorServiceMBean;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.servlet.BeanFlowSelector;

/**
 * Spring mvcに使用するBeanFlow用のコントローラ。
 * {@link jp.ossc.nimbus.beans.dataset.DataSet}をDTOとした汎用コントローラ。
 * 使い方
 *   Restで使う場合
 *     ・処理の流れ:上りStream(Jsonなど)⇒{@link jp.ossc.nimbus.beans.dataset.DataSet}(Beanflowの入力)⇒{@link jp.ossc.nimbus.beans.dataset.DataSet}(Beanflowの出力)⇒Stream(Jsonなど)⇒下り
 *     ・Beanflowの開発:{@link jp.ossc.nimbus.beans.dataset.DataSet}を受け、{@link jp.ossc.nimbus.beans.dataset.DataSet}を返すBeanflowを実装
 *   JSPで使う場合
 *     ・上りJson⇒DataSet(Beanflowの入力)⇒{@link org.springframework.web.servlet.ModelAndView}(Beanflowの出力)⇒JSP⇒下り
 *     ・Beanflowの開発:{@link jp.ossc.nimbus.beans.dataset.DataSet}を受け、{@link org.springframework.web.servlet.ModelAndView}を返すBeanflowを実装
 * @author nakashima
 *
 */
public class DataSetStreamBeanFlowController extends AbstractController{
    // AbstractControllerを実装するとspringに自前コントローラーをアノテーションなしで組み込める。

    // TODO:requestのチェックをここでできるようにするか？
    //      AbstractController#checkRequestが動く。メソッドのチェックのみ実行される。
    //      AbstractController#supportedMethods==nullなら素通りするだけ。

    // TODO: DTOがPOJOの場合、コントローラーにPOJOクラスを宣言しなければならないので、共通のControllerにならない？

//  private ServiceName beanflowInvokerFactoryServiceName=new ServiceName("WebServer.Servlet", "BeanFlowInvokerFactory");
//  private BeanFlowInvokerFactory beanflowInvokerFactory = (BeanFlowInvokerFactory)ServiceManagerFactory
//     .getServiceObject(beanflowInvokerFactoryServiceName);
//
//  private ServiceName threadContextServiceName=new ServiceName("WebServer.Log", "ThreadContext");
//  private ThreadContextService threadContext = (ThreadContextService)ServiceManagerFactory
//     .getServiceObject(threadContextServiceName);

    private static final String MODE_JSP="MODE_JSP";
    private static final String MODE_STREAM="MODE_STREAM";

    private String mode = null;

    private ServiceNameEditor sne = new ServiceNameEditor();

    private String requestObjectAttributeName=
            StreamExchangeInterceptorServiceMBean.DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME;
    private String reponseObjectContextKey=
            StreamExchangeInterceptorServiceMBean.DEFAULT_RESPONSE_OBJECT_CONTEXT_KEY;

    private String beanflowInvokerFactoryServiceName;
    private BeanFlowInvokerFactory beanflowInvokerFactory;

    private String threadContextServiceName;
    private Context threadContext;

    private String beanFlowSelectorServiceName;
    private BeanFlowSelector beanFlowSelector;

    // サービス名からサービスを取得する共通処理
    protected Object getServiceObject(String serviceName) throws ServiceNotFoundException {
        if(serviceName == null) {
            return null;
        }

        sne.setAsText(serviceName);
        ServiceName sn = (ServiceName) sne.getValue();
        Object service = ServiceManagerFactory.getServiceObject(sn);
        if(service == null) {
            throw new ServiceNotFoundException(serviceName + "is not found.");
        }

        return service;
    }

    /**
     * 初期化
     * サービス名からサービスを読み込む。必要なサービスが設定されているかチェックする。
     * @throws Exception beanflowInvokerFactory, beanFlowSelectorが取得できない場合, mode==MODE_STREAMかつthreadContextが取得できない場合
     */
    public void init() throws Exception {
        {
            Object service = getServiceObject(beanflowInvokerFactoryServiceName);
            if(service != null) {
                beanflowInvokerFactory = (BeanFlowInvokerFactory) service;
            }
        }

        {
            Object service = getServiceObject(threadContextServiceName);
            if(service != null) {
                threadContext = (Context) service;
            }
        }

        {
            Object service = getServiceObject(beanFlowSelectorServiceName);
            if(service != null) {
                beanFlowSelector = (BeanFlowSelector) service;
            }
        }

        if(beanflowInvokerFactory == null) {
            throw new Exception("BeanflowInvokerFactory must be set.");
        }

        if(beanFlowSelector == null) {
            throw new Exception("BeanFlowSelector must be set.");
        }

        // threadContextはMODE_STREAMの場合に必須
        if(MODE_STREAM.equals(mode) && threadContext == null) {
            throw new Exception("ThreadContext must be set When you use REST mode.");
        }
    }

    /**
     * R/Rの内部処理。Beanflowを実行する。
     *
     * @param request
     * @param response
     * @return Beanflowの戻り値がDataSetの場合null,Beanflowの戻り値がModelAndViewの場合はそれ
     * @throws Exception STREAMモードor指定なし かつ 戻り値の型がDataSet出ない場合, JSPモードor指定なし かつ 戻り値の型がModelAndViewでない場合, それ以外は呼び出し先の例外をそのままスロー
     */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // AbstractControllerを実装するとspringに自前コントローラーをアノテーションなしで組み込める。
        // @param request mvcから生のHttpServletRequestが渡される
        // @param response mvcから生のHttpServletResponseが渡される
        // @return ModelAndViewはページ遷移がある場合に利用。nullを返すと何もされない。
        String beanflowName = beanFlowSelector.selectBeanFlow(request);

        DataSet in = (DataSet)request.getAttribute(requestObjectAttributeName);
        Object ret = beanflowInvokerFactory.createFlow(beanflowName).invokeFlow(in);


        if(ret instanceof DataSet) {
            if(MODE_JSP.equals(mode)) {
                throw new Exception("Beanflow return value is only Dataset when mode == MODE_STREAM or null.");
            }

            threadContext.put(reponseObjectContextKey, ret);
            return null;
        }

        if(ret instanceof ModelAndView) {
            if(MODE_STREAM.equals(mode)) {
                throw new Exception("Beanflow return value is only ModelAndView when mode == MODE_JSP or null.");
            }

            return (ModelAndView) ret;
        }

        // Spring mvcの例外ハンドラーを設定しなければ、ServletFilterまで例外があがる
        // TODO:Nimbus Filterでの例外ハンドルがこの例外のみを対象にハンドリングしたいことがあるか？
        //      何ができるわけでもないので、メッセージでわかるようにしておくくらいでいいのでは？
        throw new Exception("Beanflow return value is not DataSet and ModelAndView.");
    }

    // 以下getterとsetter
    // xml configからこのControllerにinjectionする口
    public BeanFlowInvokerFactory getBeanflowInvokerFactory() {
        return beanflowInvokerFactory;
    }

    public String getBeanflowInvokerFactoryServiceName() {
        return beanflowInvokerFactoryServiceName;
    }

    public BeanFlowSelector getBeanFlowSelector() {
        return beanFlowSelector;
    }


    public String getBeanFlowSelectorServiceName() {
        return beanFlowSelectorServiceName;
    }

    public String getMode() {
        return mode;
    }

    public String getReponseObjectContextKey() {
        return reponseObjectContextKey;
    }


    public String getRequestObjectAttributeName() {
        return requestObjectAttributeName;
    }

    public Context getThreadContext() {
        return threadContext;
    }

    public String getThreadContextServiceName() {
        return threadContextServiceName;
    }


    /**
     * デフォルトはnull.
     * beanflowInvokerFactoryServiceNameが指定されている場合, {@link #init()}時に上書かれる.
     * @param beanflowInvokerFactory
     */
    public void setBeanflowInvokerFactory(BeanFlowInvokerFactory beanflowInvokerFactory) {
        this.beanflowInvokerFactory = beanflowInvokerFactory;
    }



    /**
     * デフォルトはnull.
     * beanflowInvokerFactoryServiceNameが指定されている場合, {@link #init()}時にbeanflowInvokerFactoryを上書く.
     * @param beanflowInvokerFactoryServiceName
     */
    public void setBeanflowInvokerFactoryServiceName(String beanflowInvokerFactoryServiceName) {
        this.beanflowInvokerFactoryServiceName = beanflowInvokerFactoryServiceName;
    }



    /**
     * デフォルトはnull.
     * beanFlowSelectorServiceNameが指定されている場合, {@link #init()}時に上書かれる.
     * @param beanFlowSelector
     */
    public void setBeanFlowSelector(BeanFlowSelector beanFlowSelector) {
        this.beanFlowSelector = beanFlowSelector;
    }



    /**
     * デフォルトはnull.
     * beanFlowSelectorServiceNameが指定されている場合, {@link #init()}時にbeanFlowSelectorを上書く.
     * @param beanFlowSelectorServiceName
     */
    public void setBeanFlowSelectorServiceName(String beanFlowSelectorServiceName) {
        this.beanFlowSelectorServiceName = beanFlowSelectorServiceName;
    }


    /**
     * デフォルトはnull.
     * @param mode MODE_STREAM, MODE_JSP, null
     */
    public void setMode(String mode) {
        this.mode = mode;
    }



    /**
     * デフォルトは{@link StreamExchangeInterceptorServiceMBean#DEFAULT_RESPONSE_OBJECT_CONTEXT_KEY}.
     * Filterで利用する{@link StreamExchangeInterceptorServiceMBean}と合わせる.
     * @param reponseObjectContextKey ResponseにStreamを利用する場合にThreadContextに指定するkey名
     */
    public void setReponseObjectContextKey(String reponseObjectContextKey) {
        this.reponseObjectContextKey = reponseObjectContextKey;
    }



    /**
     * デフォルトは{@link StreamExchangeInterceptorServiceMBean#DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME}.
     * Filterで利用する{@link StreamExchangeInterceptorServiceMBean}と合わせる.
     * @param requestObjectAttributeName RequestにStreamを利用する場合にRequestのattributeに指定するkey名
     */
    public void setRequestObjectAttributeName(String requestObjectAttributeName) {
        this.requestObjectAttributeName = requestObjectAttributeName;
    }



    /**
     * デフォルトはnull.
     * threadContextServiceNameが指定されている場合, {@link #init()}時に上書かれる.
     * @param threadContext
     */
    public void setThreadContext(Context threadContext) {
        this.threadContext = threadContext;
    }



    /**
     * デフォルトはnull.
     * threadContextServiceNameが指定されている場合, {@link #init()}時にthreadContextを上書く.
     * @param threadContextServiceName
     */
    public void setThreadContextServiceName(String threadContextServiceName) {
        this.threadContextServiceName = threadContextServiceName;
    }
}
