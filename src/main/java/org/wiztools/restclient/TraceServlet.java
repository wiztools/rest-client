/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wiztools.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author subwiz
 */
public class TraceServlet extends HttpServlet {
    private void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("**RESTClient TraceServlet**");
        
        out.println("\n*Method*");
        out.println("\t" + request.getMethod());
        
        out.println("\n*Path Info*");
        out.println("\t" + request.getPathInfo());
        
        out.println("\n*Headers*");
        Enumeration eHeaders = request.getHeaderNames();
        while(eHeaders.hasMoreElements()){
            String headerName = (String)eHeaders.nextElement();
            String headerValue = request.getHeader(headerName);
            headerValue = headerValue.replaceAll("\n", "\n\t");
            out.println("\t" + headerName + ": " + request.getHeader(headerName));
        }
        
        out.println("\n*Query String*");
        out.println("\t" + request.getQueryString());
        
        out.println("\n*Parameters*");
        Enumeration eParams = request.getParameterNames();
        while(eParams.hasMoreElements()){
            String paramName = (String)eParams.nextElement();
            out.println("\t~Parameter Name: " + paramName);
            out.println("\t~Parameter Value:");
            String paramValue = request.getParameter(paramName);
            paramValue = paramValue.replaceAll("\n", "\n\t");
            out.println("\t" + paramValue);
        }
        
        out.println("\n*Body*");
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String str = null;
        while((str = br.readLine())!=null){
            out.println("\t" + str);
        }
        br.close();
        
        out.flush();
        out.close();
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        process(request, response);
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        process(request, response);
    }
    
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        process(request, response);
    }
    
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        process(request, response);
    }
    
    /*@Override
    public void doHead(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        System.out.println("doHead called!");
        process(request, response);
    }
    
    @Override
    public void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        process(request, response);
    }
    
    @Override
    public void doTrace(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        process(request, response);
    }*/
}
