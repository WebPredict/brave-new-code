package wp.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CharsetFilter implements Filter{

	public void init(FilterConfig config) throws ServletException {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// iteration params in request and encode param from iso8859-1 to utf-8
		// ...
		// new String(param.getBytes("ISO-8859-1"), "utf-8");
		request.setCharacterEncoding("UTF-8");
		chain.doFilter(request,response);
	}
}