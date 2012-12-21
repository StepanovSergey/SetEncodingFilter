/**
 * To apply this filter you need to add filter tag in web.xml 
 * Example:
 * <code>
 * <filter> 
 * 	<filter-name>SetCharFilter</filter-name>
 *      <filter-class>package.subpackage.SetEncodingFilter</filter-class>
 *      <init-param> 
 *      	<param-name>encoding</param-name>
 *         	<param-value>UTF-8</param-value> 
 *      </init-param> 
 * </filter>
 * <filter-mapping> 
 * 	<filter-name>SetEncodingFilter</filter-name>
 *      <servlet-name>MyServletName</servlet-name> 
 * </filter-mapping>
 * </code> 
 * Instead of <servlet-name> you can use <url-pattern>/*</url-pattern>
 */
package by.stepanov.sergey.filter;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * This class provides filter that changes encoding to UTF-8
 * 
 * @author Siarhei_Stsiapanau
 * 
 * 
 */
public class SetEncodingFilter implements Filter {
    private static final boolean debug = false;
    private String encoding = "UTF-8";
    private FilterConfig filterConfig = null;

    /**
     * What to do before processing this filter
     * 
     * @param request
     *            current request
     * @param response
     *            current response
     * @throws IOException
     *             if something wrong
     * @throws ServletException
     *             if something wrong
     */
    private void doBeforeProcessing(ServletRequest request,
	    ServletResponse response) throws IOException, ServletException {
	if (debug) {
	    log("EncodingFilter:DoBeforeProcessing");
	}
    }

    /**
     * What to do after processing this filter
     * 
     * @param request
     *            current request
     * @param response
     *            current response
     * @throws IOException
     *             if something wrong
     * @throws ServletException
     *             if something wrong
     */
    private void doAfterProcessing(ServletRequest request,
	    ServletResponse response) throws IOException, ServletException {
	if (debug) {
	    log("EncodingFilter:DoAfterProcessing");
	}
    }

    /**
     * Filter processing. Change request encoding to encoding setted in web.xml
     * 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
	    FilterChain chain) throws IOException, ServletException {
	if (debug) {
	    log("EncodingFilter:doFilter()");
	}
	doBeforeProcessing(request, response);
	if (request.getCharacterEncoding() == null) {
	    if (encoding != null) {
		request.setCharacterEncoding(encoding);
	    }
	}
	Throwable problem = null;
	try {
	    chain.doFilter(request, response);
	} catch (Throwable t) {
	    problem = t;
	}
	doAfterProcessing(request, response);
	if (problem != null) {
	    if (problem instanceof ServletException) {
		throw (ServletException) problem;
	    }
	    if (problem instanceof IOException) {
		throw (IOException) problem;
	    }
	    sendProcessingError(problem, response);
	}
    }

    /**
     * Set filter config
     * 
     * @return filter config
     */
    public FilterConfig getFilterConfig() {
	return (this.filterConfig);
    }

    /**
     * Get filter config
     * 
     * @param filterConfig
     *            filter config to set
     */
    public void setFilterConfig(FilterConfig filterConfig) {
	this.filterConfig = filterConfig;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
    }

    /**
     * Init this filter
     * 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) {
	this.filterConfig = filterConfig;
	if (filterConfig != null) {

	    System.out.println("encoding: " + encoding);
	    if (debug) {
		log("EncodingFilter:Initializing filter");
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	if (filterConfig == null) {
	    return ("EncodingFilter()");
	}
	StringBuffer sb = new StringBuffer("EncodingFilter(");
	sb.append(filterConfig);
	sb.append(")");
	return (sb.toString());
    }

    /**
     * Prints error if error occurred
     * 
     * @param t
     *            error throwable
     * @param response
     *            current response
     */
    private void sendProcessingError(Throwable t, ServletResponse response) {
	String stackTrace = getStackTrace(t);

	if (stackTrace != null && !stackTrace.equals("")) {
	    try {
		response.setContentType("text/html");
		PrintStream ps = new PrintStream(response.getOutputStream());
		PrintWriter pw = new PrintWriter(ps);
		pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); // NOI18N
		pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
		pw.print(stackTrace);
		pw.print("</pre></body>\n</html>"); // NOI18N
		pw.close();
		ps.close();
		response.getOutputStream().close();
	    } catch (Exception ex) {
	    }
	} else {
	    try {
		PrintStream ps = new PrintStream(response.getOutputStream());
		t.printStackTrace(ps);
		ps.close();
		response.getOutputStream().close();
	    } catch (Exception ex) {
	    }
	}
    }

    /**
     * Get stack trace of throwable
     * 
     * @param t
     *            throwable to get stack trace
     * @return string representation of stack trace
     */
    public static String getStackTrace(Throwable t) {
	String stackTrace = null;
	try {
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    t.printStackTrace(pw);
	    pw.close();
	    sw.close();
	    stackTrace = sw.getBuffer().toString();
	} catch (Exception ex) {
	}
	return stackTrace;
    }

    /**
     * Print message to log
     * 
     * @param msg
     *            message to print
     */
    public void log(String msg) {
	filterConfig.getServletContext().log(msg);
    }
}
