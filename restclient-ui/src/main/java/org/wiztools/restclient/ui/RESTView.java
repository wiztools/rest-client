package org.wiztools.restclient.ui;

import com.google.inject.ImplementedBy;
import java.awt.Font;
import org.wiztools.restclient.View;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.bean.Response;

/**
 *
 * @author subwiz
 */
@ImplementedBy(RESTViewImpl.class)
public interface RESTView extends View {
    int BORDER_WIDTH = 5;

    void disableBody();

    void enableBody();

    Request getLastRequest();

    Response getLastResponse();

    Request getRequestFromUI();

    Font getTextAreaFont();

    String getUrl();

    void runClonedRequestTest(Request request, Response response);

    void setStatusMessage(final String msg);

    void setTextAreaFont(final Font f);

    void setUrl(String url);

    void showError(final String error);

    void showMessage(final String title, final String message);
    
}
