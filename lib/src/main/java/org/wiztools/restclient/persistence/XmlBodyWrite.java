package org.wiztools.restclient.persistence;

import java.util.List;
import nu.xom.Attribute;
import nu.xom.Element;
import org.wiztools.commons.MultiValueMap;
import org.wiztools.commons.StringUtil;
import org.wiztools.restclient.bean.*;
import org.wiztools.restclient.util.Util;

/**
 *
 * @author subwiz
 */
class XmlBodyWrite {
    
    Element getReqEntity(ReqEntity bean) {
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
        else if(bean instanceof ReqEntityUrlStream) {
            ReqEntityUrlStream entity = (ReqEntityUrlStream) bean;
            
            Element eUrlStream = new Element("url-stream");
            addContentTypeCharsetAttribute(entity.getContentType(), eUrlStream);
            eUrlStream.appendChild(entity.getUrl().toString());
            
            eBody.appendChild(eUrlStream);
        }
        else if(bean instanceof ReqEntityMultipart) {
            ReqEntityMultipart entity = (ReqEntityMultipart) bean;
            
            Element eMultipart = new Element("multipart");
            
            eMultipart.addAttribute(
                    new Attribute("subtype", entity.getSubtype().name()));
            
            eMultipart.addAttribute(
                    new Attribute("mode", entity.getMode().name()));
            
            List<ReqEntityPart> parts = entity.getBody();
            for(ReqEntityPart part: parts) {
                if(part instanceof ReqEntityStringPart) {
                    ReqEntityStringPart p = (ReqEntityStringPart) part;
                    
                    Element ePart = new Element("string");
                    addContentTypeCharsetAttribute(p.getContentType(), ePart);
                    ePart.addAttribute(new Attribute("name", p.getName()));
                    
                    Element eContent = new Element("content");
                    eContent.appendChild(p.getPart());
                    ePart.appendChild(eContent);
                    
                    Element eFields = new Element("fields");
                    MultiValueMap<String, String> fields = p.getFields();
                    for(String k: fields.keySet()) {
                        for(String value: fields.get(k)) {
                            Element eField = new Element("field");

                            Element eName = new Element("name");
                            eName.appendChild(k);
                            eField.appendChild(eName);
                            
                            Element eValue = new Element("value");
                            eValue.appendChild(value);
                            eField.appendChild(eValue);

                            eFields.appendChild(eField);
                        }
                    }
                    
                    eMultipart.appendChild(ePart);
                }
                else if(part instanceof ReqEntityFilePart) {
                    ReqEntityFilePart p = (ReqEntityFilePart) part;
                    
                    Element ePart = new Element("file");
                    addContentTypeCharsetAttribute(p.getContentType(), ePart);
                    ePart.addAttribute(new Attribute("name", p.getName()));
                    { // filename: backward compatibility!
                        String fileName = p.getFilename();
                        if(StringUtil.isNotEmpty(fileName)) {
                            ePart.addAttribute(new Attribute("filename", p.getFilename()));
                        }
                        else {
                            ePart.addAttribute(new Attribute("filename", p.getPart().getName()));
                        }
                    }
                    Element eContent = new Element("content");
                    eContent.appendChild(p.getPart().getAbsolutePath());
                    
                    ePart.appendChild(eContent);
                    
                    eMultipart.appendChild(ePart);
                }
            }
            
            eBody.appendChild(eMultipart);
        }
        
        return eBody;
    }
    
    private static void addContentTypeCharsetAttribute(ContentType c, Element e) {
        if(c != null) {
            e.addAttribute(new Attribute("content-type", c.getContentType()));
            if(c.getCharset() != null) {
                e.addAttribute(new Attribute("charset", c.getCharset().name()));
            }
        }
    }
}
