package com.rackspace.papi.service.context.impl;

import com.rackspace.papi.commons.util.thread.DestroyableThreadWrapper;
import com.rackspace.papi.service.context.ServiceContext;
import com.rackspace.papi.service.context.ServletContextHelper;
import com.rackspace.papi.service.event.PowerProxyEventKernel;
import com.rackspace.papi.service.event.common.EventService;
import com.rackspace.papi.service.threading.ThreadingService;
import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;

@Component("eventManagerServiceContext")
public class EventManagerServiceContext implements ServiceContext<EventService> {

    public static final String SERVICE_NAME = "powerapi:/kernel/event";
    
    private final EventService eventManager;
    private DestroyableThreadWrapper eventKernelThread;

    @Autowired
    public EventManagerServiceContext(@Qualifier("eventManager") EventService eventManager) {
       this.eventManager = eventManager;
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public EventService getService() {
        return eventManager;
    }
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final ThreadingService threadManager = ServletContextHelper.getInstance().getPowerApiContext(sce.getServletContext()).threadingService();

        final PowerProxyEventKernel eventKernel = new PowerProxyEventKernel(eventManager);
        eventKernelThread = new DestroyableThreadWrapper(threadManager.newThread(eventKernel, "Event Kernel Thread"), eventKernel);
        
        eventKernelThread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        eventKernelThread.destroy();
    }
}