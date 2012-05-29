package com.rackspace.papi.components.identity.ip;


import com.rackspace.papi.components.identity.ip.config.IpIdentityConfig;
import com.rackspace.papi.filter.FilterConfigHelper;
import com.rackspace.papi.filter.logic.impl.FilterLogicHandlerDelegate;
import com.rackspace.papi.service.config.ConfigurationService;
import com.rackspace.papi.service.context.ServletContextHelper;

import javax.servlet.*;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpIdentityFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(IpIdentityFilter.class);
    private static String DEFAULT_CONFIG = "ip-identity.cfg.xml";
    private String config;
    private IpIdentityHandlerFactory handlerFactory;
    private ConfigurationService configurationManager;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        new FilterLogicHandlerDelegate(request, response, chain).doFilter(handlerFactory.newHandler());
    }

    @Override
    public void destroy() {
        configurationManager.unsubscribeFrom(config, handlerFactory);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        config = new FilterConfigHelper(filterConfig).getFilterConfig(DEFAULT_CONFIG);
        LOG.info("Initializing filter using config " + config);
        configurationManager = ServletContextHelper.getInstance().getPowerApiContext(filterConfig.getServletContext()).configurationService();
        handlerFactory = new IpIdentityHandlerFactory();

        configurationManager.subscribeTo(config, handlerFactory, IpIdentityConfig.class);
    }
}
