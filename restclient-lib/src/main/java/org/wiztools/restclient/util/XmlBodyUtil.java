package org.wiztools.restclient.util;

import java.io.File;
import java.nio.charset.Charset;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import org.wiztools.restclient.XMLException;
import org.wiztools.restclient.bean.*;

/**
 *
 * @author subwiz
 */
class XmlBodyUtil {
    private XmlBodyUtil() {}
    
    static ReqEntity getReqEntity(Element eEntity) {
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
            else {
                throw new XMLException("Unsupported element encountered inside <body>: " + name);
            }
        }
        return null;
    }
    
    private static ContentType getContentType(Element e) {
        String contentType = e.getAttributeValue("content-type");
        String charsetStr = e.getAttributeValue("charset");
        return new ContentTypeBean(contentType,
                (charsetStr!=null? Charset.forName(charsetStr): null));
    }
    
    static Element getReqEntity(ReqEntity bean) {
        Element eBody = new Element("body");
        
        if(bean instanceof ReqEntityString) {
            ReqEntityString entity = (ReqEntityString) bean;
            
            Element eStringContent = new Element("string");
            addContentTypeCharsetAttribute(entity.getContentType(), eStringContent);
            eStringContent.appendChild(entity.getBody());
            
            eBody.appendChild(eStringContent);
        }
        else if(bean instanceof ReqEntityFile) {
            ReqEntityFile entity = (ReqEntityFile) bean;
            
            Element eFile = new Element("file");
            addContentTypeCharsetAttribute(entity.getContentType(), eFile);
            eFile.appendChild(entity.getBody().getAbsolutePath());
            
            eBody.appendChild(eFile);
        }
        else if(bean instanceof ReqEntityByteArray) {
            ReqEntityByteArray entity = (ReqEntityByteArray) bean;
            
            Element eByte = new Element("byte-array");
            addContentTypeCharsetAttribute(entity.getContentType(), eByte);
            eByte.appendChild(Util.base64encode(entity.getBody()));
            
            eBody.appendChild(eByte);
        }
        
        return eBody;
    }
    
    private static void addContentTypeCharsetAttribute(ContentType c, Element e) {
        e.addAttribute(new Attribute("content-type", c.getContentType()));
        if(c.getCharset() != null) {
            e.addAttribute(new Attribute("charset", c.getCharset().name()));
        }
    }
}
