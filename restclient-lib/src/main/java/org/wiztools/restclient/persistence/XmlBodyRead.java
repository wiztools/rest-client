package org.wiztools.restclient.persistence;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import nu.xom.Element;
import nu.xom.Elements;
import org.wiztools.appupdate.Version;
import org.wiztools.appupdate.VersionImpl;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.bean.ContentType;
import org.wiztools.restclient.bean.ContentTypeBean;
import org.wiztools.restclient.bean.MultipartMode;
import org.wiztools.restclient.bean.MultipartSubtype;
import org.wiztools.restclient.bean.ReqEntity;
import org.wiztools.restclient.bean.ReqEntityBasePart;
import org.wiztools.restclient.bean.ReqEntityByteArrayBean;
import org.wiztools.restclient.bean.ReqEntityFileBean;
import org.wiztools.restclient.bean.ReqEntityFilePartBean;
import org.wiztools.restclient.bean.ReqEntityMultipartBean;
import org.wiztools.restclient.bean.ReqEntityPart;
import org.wiztools.restclient.bean.ReqEntityStringBean;
import org.wiztools.restclient.bean.ReqEntityStringPartBean;
import org.wiztools.restclient.bean.ReqEntityUrlStreamBean;
import org.wiztools.restclient.util.Util;

/**
 *
 * @author subwiz
 */
public class XmlBodyRead {
    private final Version readVersion;
    
    private static final Version VERSION_SINCE_PART_CONTENT = new VersionImpl("3.5");
    
    XmlBodyRead(String version) {
        readVersion = new VersionImpl(version);
    }
    
    ReqEntity getReqEntity(Element eEntity) {
        Elements eChildren = eEntity.getChildElements();
        for(int i=0; i<eChildren.size(); i++) {
            Element e = eChildren.get(i);
            final String name = e.getLocalName();
            if("string".equals(name)) {
                ContentType ct = getContentType(e);
                String body = e.getValue();
                return new ReqEntityStringBean(body, ct);
            }
            else if("file".equals(name)) {
                ContentType ct = getContentType(e);
                String filePath = e.getValue();
                return new ReqEntityFileBean(new File(filePath), ct);
            }
            else if("byte-array".equals(name)) {
                ContentType ct = getContentType(e);
                byte[] body = Util.base64decodeByteArray(e.getValue());
                return new ReqEntityByteArrayBean(body, ct);
            }
            else if("url-stream".equals(name)) {
                try {
                    ContentType ct = getContentType(e);
                    URL url = new URL(e.getValue());
                    return new ReqEntityUrlStreamBean(ct, url);
                }
                catch(MalformedURLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else if("multipart".equals(name)) {
                return getMultipart(e);
            }
            else {
                throw new XMLException("Unsupported element encountered inside <body>: " + name);
            }
        }
        return null;
    }
    
    private ReqEntityMultipartBean getMultipart(Element e) {
        final String subTypeStr = e.getAttributeValue("subtype");
        final MultipartSubtype subType = subTypeStr!=null?
                MultipartSubtype.valueOf(subTypeStr): MultipartSubtype.FORM_DATA;
        final String mode = e.getAttributeValue("mode");
        MultipartMode format = StringUtil.isNotEmpty(mode)?
                MultipartMode.valueOf(mode): null;
        List<ReqEntityPart> parts = getMultipartParts(e);
        return new ReqEntityMultipartBean(parts, format, subType);
    }
    
    private List<ReqEntityPart> getMultipartParts(Element e) {
        List<ReqEntityPart> parts = new ArrayList<>();
        Elements children = e.getChildElements();
        for(int i=0; i<children.size(); i++) {
            ReqEntityPart part = getMultipartPart(children.get(i));
            parts.add(part);
        }
        return parts;
    }
    
    private String getPartValue(Element e) {
        if(readVersion.isLessThan(VERSION_SINCE_PART_CONTENT)) {
            return e.getValue();
        }
        else {
            Element eContent = e.getChildElements("content").get(0);
            return eContent.getValue();
        }
    }
    
    private ReqEntityPart getMultipartPart(Element e) {
        final String name = e.getLocalName();
        
        final String partName = e.getAttributeValue("name");
        final ContentType ct = getContentType(e);
        
        Elements eFields = null;
        if(e.getChildElements("fields").size() > 0) {
            eFields = e.getChildElements("fields").get(0).getChildElements("field");
        }
        
        if("string".equals(name)) {
            String partBody = getPartValue(e);
            ReqEntityStringPartBean out = new ReqEntityStringPartBean(partName, ct, partBody);
            addFields(eFields, out);
            return out;
        }
        else if("file".equals(name)) {
            File file = new File(getPartValue(e));
            String fileName = e.getAttributeValue("filename");
            
            // filename: backward-compatibility:
            fileName = StringUtil.isEmpty(fileName)? file.getName(): fileName;
            
            return new ReqEntityFilePartBean(partName, fileName, ct, file);
        }
        else {
            throw new XMLException("Unsupported element encountered inside <multipart>: " + name);
        }
    }
    
    private void addFields(Elements eFields, ReqEntityBasePart part) {
        if(eFields == null) {
            return;
        }
        
        for(int i=0; i<eFields.size(); i++) {
            Element eField = eFields.get(i);
            
            String name = eField.getChildElements("name").get(0).getValue();
            String value = eField.getChildElements("value").get(0).getValue();
            
            part.addField(name, value);
        }
    }
    
    private static ContentType getContentType(Element e) {
        String contentType = e.getAttributeValue("content-type");
        String charsetStr = e.getAttributeValue("charset");
        if(StringUtil.isNotEmpty(contentType)) {
            return new ContentTypeBean(contentType,
                (charsetStr!=null? Charset.forName(charsetStr): null));
        }
        else {
            return null;
        }
    }
}
