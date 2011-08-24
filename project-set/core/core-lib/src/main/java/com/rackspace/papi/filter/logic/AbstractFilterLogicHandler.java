package com.rackspace.papi.filter.logic;

import com.rackspace.papi.commons.util.servlet.http.ReadableHttpServletResponse;
import com.rackspace.papi.filter.logic.impl.SimplePassFilterDirector;
import javax.servlet.http.HttpServletRequest;

public class AbstractFilterLogicHandler implements FilterLogicHandler {

    @Override
    public FilterDirector handleRequest(HttpServletRequest request) {
        return SimplePassFilterDirector.getInstance();
    }

    @Override
    public FilterDirector handleResponse(HttpServletRequest request, ReadableHttpServletResponse response) {
        return SimplePassFilterDirector.getInstance();
    }
}