package org.wiztools.restclient.bean;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author subwiz
 */
public class ReqEntityMultipartBean implements ReqEntityMultipart {
    
    private final List<ReqEntityPart> parts;

    public ReqEntityMultipartBean(List<ReqEntityPart> parts) {
        this.parts = Collections.unmodifiableList(parts);
    }

    @Override
    public List<ReqEntityPart> getBody() {
        return parts;
    }

    @Override
    public Object clone() {
        return null;
    }
}
