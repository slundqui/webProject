/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.nmt.cs.itweb;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Chen
 */
public class SessionCheckFilter implements Filter {
    
    /**
     * Init method for this filter
     */
    @Override
    public void init(FilterConfig filterConfig) {        
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;  

        if (req.getSession().getAttribute("LOGIN_USER") == null) {
            res.sendRedirect("login.jsp");
        }
        else {
            String userType = (String) req.getSession().getAttribute("LOGIN_USER");
            chain.doFilter(request, response);
        }
    }
    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {        
    }
}
