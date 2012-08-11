package org.wiztools.restclient.ui.resbody;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.wiztools.restclient.ui.RESTView;

/**
 *
 * @author subwiz
 */
public class ResBodyBinaryPanel extends AbstractResBody {
    @Inject private RESTView view;
    
    @PostConstruct
    protected void init() {
        // 
    }

    @Override
    public void clearBody() {
        // 
    }
}
