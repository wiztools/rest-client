package org.wiztools.restclient.server;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 *
 * @author subwiz
 */
public class TraceServlet extends HttpServlet {
    private void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("**RESTClient TraceServlet**");

        out.println("\n*Method*");
        out.println("\t" + request.getMethod());

        out.println("\n*Path Info*");
        out.println("\t" + request.getPathInfo());

        out.println("\n*Headers*");
        Enumeration<String> eHeaders = request.getHeaderNames();
        while(eHeaders.hasMoreElements()){
            final String headerName = eHeaders.nextElement();
            Enumeration<String> eValues = request.getHeaders(headerName);
            while(eValues.hasMoreElements()) {
                String headerValue = eValues.nextElement();
                headerValue = headerValue.replaceAll("\n", "\n\t");
                out.println("\t" + headerName + ": " + headerValue);
            }
        }

        out.println("\n*Query String*");
        out.println("\t" + request.getQueryString());

        out.println("\n*Parameters*");
        Enumeration<String> eParams = request.getParameterNames();
        while(eParams.hasMoreElements()){
            String paramName = eParams.nextElement();
            String[] paramValues = request.getParameterValues(paramName);

            for(String paramValue: paramValues) {
                paramValue = paramValue.replaceAll("\n", "\n\t");

                out.println("\t~Parameter Name: " + paramName);
                out.println("\t~Parameter Value:");
                out.println("\t" + paramValue);
            }
        }

        out.println("\n*Body (First 100 characters only)*");
        String body = Util.inputStreamToString(request.getInputStream());
        String[] arr = body.split("\n");
        for(String s: arr) {
            out.print("\t");
            out.println(s);
        }

        out.println();

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
}
