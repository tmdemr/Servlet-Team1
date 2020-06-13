package cse.maven_webmail.control;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

public class XssFilter implements Filter {
    private static class FilteredRequest extends HttpServletRequestWrapper {

        public FilteredRequest(ServletRequest request) {
            super((HttpServletRequest) request);
        }

        public String replace(String input) {
            return input.replace("&","&amp;").replace("<","&lt;")
                    .replace(">","&gt;").replace("\"","&#034;")
                    .replace("'","&#039;");
        }

        public String getParameter(String paramName) {
            String value = super.getParameter(paramName);
            return replace(value);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(new FilteredRequest(servletRequest), servletResponse);
    }

    @Override
    public void destroy() {

    }
}
