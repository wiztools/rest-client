package org.wiztools.restclient.bean;

import java.util.Objects;
import org.wiztools.commons.CollectionsUtil;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.commons.MultiValueMapArrayList;

/**
 *
 * @author subwiz
 */
public abstract class ReqEntityBasePart implements ReqEntityPart {
    
    protected final String name;
    protected final ContentType contentType;
    protected final MultiValueMap<String, String> fields = new MultiValueMapArrayList<>();

    public ReqEntityBasePart(String name, ContentType contentType) {
        this.name = name;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ContentType getContentType() {
        return contentType;
    }
    
    public void addField(String key, String value) {
        fields.put(key, value);
    }

    @Override
    public MultiValueMap<String, String> getFields() {
        return CollectionsUtil.unmodifiableMultiValueMap(fields);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.contentType);
        hash = 79 * hash + Objects.hashCode(this.fields);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReqEntityBasePart other = (ReqEntityBasePart) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.contentType, other.contentType)) {
            return false;
        }
        if (!Objects.equals(this.fields, other.fields)) {
            return false;
        }
        return true;
    }
}
