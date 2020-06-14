package cse.maven_webmail.control;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

/**
 * XSS 공격을 막기 위해 필터처리를 합니다.
 * @author 이병길
 */
public class XssFilter implements Filter {
    /**
     * request를 필터링 하는 클래스입니다. 다른 곳에선 쓰이지 않으므로 이너클래스로 구현하였습니다.
     */
    private static class FilteredRequest extends HttpServletRequestWrapper {
        /**
         * 생성자로서 리퀘스트 객체를 초기화합니다.
         * @param request 체인할 리퀘스트
         */
        public FilteredRequest(ServletRequest request) {
            super((HttpServletRequest) request);
        }

        /**
         * 기존의 getParameterValues를 바꿔치기합니다.
         * @param parameter 파라미터
         * @return 값
         */
        public String[] getParameterValues(String parameter) {
            String[] values = super.getParameterValues(parameter);
            if (values == null) {
                return new String[0];
            }
            for (int i = 0; i < values.length; i++) {
                if (values[i] != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = 0; j < values[i].length(); j++) { // 순회하면서 값을 변경합니다.
                        char c = values[i].charAt(j);
                        switch (c) {
                            case '<':
                                stringBuilder.append("&lt;");
                                break;
                            case '>':
                                stringBuilder.append("&gt;");
                                break;
                            case '&':
                                stringBuilder.append("&amp;");
                                break;
                            case 34:
                                stringBuilder.append("&qout;");
                                break;
                            case 39:
                                stringBuilder.append("&apos;");
                                break;
                            default:
                                stringBuilder.append(c);
                                break;
                        }
                    }
                    values[i] = stringBuilder.toString();
                } else {
                    values[i] = null;
                }

            }
            return values;
        }

        /**
         * 기존의 getParameter 함수를 바꿔치기 합니다.
         * @param paramName 파라미터
         * @return 값
         */
        public String getParameter(String paramName) {
            String value = super.getParameter(paramName);
            if (value == null) {
                return null;
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                switch (c) {
                    case '<':
                        stringBuilder.append("&lt;");
                        break;
                    case '>':
                        stringBuilder.append("&gt;");
                        break;
                    case '&':
                        stringBuilder.append("&amp;");
                        break;
                    case 34:
                        stringBuilder.append("&qout;");
                        break;
                    case 39:
                        stringBuilder.append("&apos;");
                        break;
                    default:
                        stringBuilder.append(c);
                        break;
                }
            }
            value = stringBuilder.toString();
            return value;
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(new FilteredRequest(servletRequest), servletResponse);
    }
}
