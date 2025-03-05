package org.savand.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class XSSPreventionFilter implements Filter {

    private final ObjectMapper objectMapper;

    static class XSSRequestWrapper extends HttpServletRequestWrapper {

        private final String principalName;

        // Avoid anything between script tags
        private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
        // Avoid anything in a src='...' type of expression
        private static final Pattern SRC_PATTERN = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        // Remove any lonesome </script> tag
        private static final Pattern LONESOME_CLOSE_TAG_SCRIPT_PATTERN = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
        // Remove any lonesome <script ...> tag
        private static final Pattern LONESOME_OPEN_TAG_SCRIPT_PATTERN = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        // Avoid eval(...) expressions
        private static final Pattern EVAL_PATTERN = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        // Avoid expression(...) expressions
        private static final Pattern EXPRESSION_PATTERN = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        // Avoid onload= expressions
        private static final Pattern ONLOAD_SCRIPT_PATTERN = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        // Avoid vbscript:... expressions
        private static final Pattern VBSCRIPT_PATTERN = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
        // Avoid javascript:... expressions
        private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);

        private static final List<Pattern> SANITIZER_PATTERNS = List.of(SCRIPT_PATTERN, SRC_PATTERN, LONESOME_CLOSE_TAG_SCRIPT_PATTERN,
                LONESOME_OPEN_TAG_SCRIPT_PATTERN, EVAL_PATTERN, EXPRESSION_PATTERN, ONLOAD_SCRIPT_PATTERN, VBSCRIPT_PATTERN,
                JAVASCRIPT_PATTERN);
        private static final String NULL_CHARACTER = "\0";

        private Map<String, String[]> sanitizedQueryString;

        public XSSRequestWrapper(HttpServletRequest request, String principalName) {
            super(request);
            this.principalName = principalName;
        }

        @Override
        public String getParameter(String name) {

            String parameter = null;

            String[] vals = getParameterMap().get(name);

            if (vals != null && vals.length > 0) {
                parameter = vals[0];
            }

            return parameter;
        }

        @Override
        public String[] getParameterValues(String name) {
            return getParameterMap().get(name);
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(getParameterMap().keySet());
        }

        @Override
        public Map<String, String[]> getParameterMap() {

            if (sanitizedQueryString == null) {

                Map<String, String[]> res = new HashMap<>();

                Map<String, String[]> originalQueryString = super.getParameterMap();

                if (originalQueryString != null) {

                    for (String key : originalQueryString.keySet()) {

                        String[] rawVals = originalQueryString.get(key);

                        String[] snzVals = new String[rawVals.length];

                        for (int i = 0; i < rawVals.length; i++) {

                            snzVals[i] = stripXSS(rawVals[i]);

                            log.trace("Sanitized: " + rawVals[i] + " to " + snzVals[i]);

                            if (!rawVals[i].equals(snzVals[i])) {
                                log.warn("Sanitized: " + rawVals[i] + " to " + snzVals[i]);
                                throw new SecurityException(String.format("You are caught on the malicious input [%s]. U'll be punished, %s", rawVals[i], principalName));
                            }

                        }

                        res.put(stripXSS(key), stripWhitespace(snzVals));
                    }
                }

                sanitizedQueryString = res;
            }

            return sanitizedQueryString;
        }

        private String[] stripWhitespace(String[] snzVals) {

            String[] stripVals = new String[snzVals.length];

            for (int i = 0; i < snzVals.length; i++) {
                stripVals[i] = snzVals[i].strip();
            }

            return stripVals;
        }


        /**
         * Removes all the potentially malicious characters from a string
         *
         * @param value the raw string
         * @return the sanitized string
         */
        private String stripXSS(final String value) {

            String cleanValue = null;

            if (value != null) {

                // Avoid null characters
                cleanValue = value.replace(NULL_CHARACTER, StringUtils.EMPTY);

                for (Pattern pattern : SANITIZER_PATTERNS) {
                    cleanValue = pattern.matcher(cleanValue).replaceAll(StringUtils.EMPTY);
                }

            }

            return cleanValue;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        String user = "an unknown user";

//        HttpServletRequest req = (HttpServletRequest) request;
//
//        JwtAuthenticationToken principal = (JwtAuthenticationToken) req.getUserPrincipal();
//
//        if (principal != null) {
//            user = (String) principal.getTokenAttributes().get("nickname");
//        }

        log.info("Received call from the address: [{}], the user: [{}], to [{}] with parameters [{}]",
                request.getRemoteAddr(),
                user,
                ((HttpServletRequest) request).getRequestURI(),
                objectMapper.writeValueAsString(request.getParameterMap()));

        XSSRequestWrapper wrapper = new XSSRequestWrapper((HttpServletRequest) request, user);

        chain.doFilter(wrapper, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("XSSPreventionFilter: init()");
    }

    @Override
    public void destroy() {
        log.info("XSSPreventionFilter: destroy()");
    }

}
