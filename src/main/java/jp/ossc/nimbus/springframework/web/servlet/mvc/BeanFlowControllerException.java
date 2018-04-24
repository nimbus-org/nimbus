package jp.ossc.nimbus.springframework.web.servlet.mvc;

/**
 * {@link BeanFlowController}にて発生した例外の印付け例外クラス。
 * @author Y.Nakashima
 *
 */
public class BeanFlowControllerException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * @see Exception#Exception()
     */
    public BeanFlowControllerException() {
        super();
    }

    /**
     * @see Exception#Exception(String)
     */
    public BeanFlowControllerException(String message) {
        super(message);
    }

    /**
     * @see Exception#Exception(String,Throwable)
     */
    public BeanFlowControllerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see Exception#Exception(Throwable)
     */
    public BeanFlowControllerException(Throwable cause) {
        super(cause);
    }

}
