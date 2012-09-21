package com.rackspace.papi.components.translation.xslt.handlerchain;

import com.rackspace.papi.components.translation.xslt.StyleSheetInfo;
import com.rackspace.papi.components.translation.xslt.XsltParameter;
import com.rackspace.papi.components.translation.xslt.XsltChain;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class XsltHandlerChainBuilderTest {

    public static class WhenBuildingChains {

        private static SAXTransformerFactory factory;
        private XsltHandlerChainBuilder builder;

        @BeforeClass
        public static void before() {
            System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
            factory = (SAXTransformerFactory) TransformerFactory.newInstance();
        }

        @Before
        public void setUp() {
            builder = new XsltHandlerChainBuilder(factory);
        }

        @Test
        public void shouldHandleEmptySetOfStyles() {
            XsltChain<Templates> chain = builder.build();

            assertNotNull("Should build an empty filter chain", chain);
            assertEquals("Should have no handlers", 0, chain.getFilters().size());
        }

        @Test
        public void shouldHandleStyleSheetList() {
            XsltChain<Templates> chain = builder.build(new StyleSheetInfo("", "classpath:///style.xsl"));

            assertNotNull("Should build a filter chain", chain);
            assertEquals("Should have 1 handler", 1, chain.getFilters().size());
        }

    }
    
    public static class WhenExecutingChains {
        private static SAXTransformerFactory factory;
        private XsltHandlerChainBuilder builder;
        private static final String params = "<params><param name='p1' value='pv1'/><param name='p2' value='pv2'/></params>";
        private static final String headers = "<headers><header name='h1' value='hv1'/><header name='h2' value='hv2'/></headers>";
        private ByteArrayOutputStream headersOutput;
        private ByteArrayOutputStream queryOutput;
        private ByteArrayOutputStream output;
        private ByteArrayInputStream headersInput;
        private ByteArrayInputStream queryInput;
        private InputStream body;

        @BeforeClass
        public static void before() {
            System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
            factory = (SAXTransformerFactory) TransformerFactory.newInstance();
        }

        @Before
        public void setUp() {
            builder = new XsltHandlerChainBuilder(factory);
            headersOutput = new ByteArrayOutputStream();
            queryOutput = new ByteArrayOutputStream();
            output = new ByteArrayOutputStream();
            headersInput = new ByteArrayInputStream(headers.getBytes());
            queryInput = new ByteArrayInputStream(params.getBytes());
            body = getClass().getResourceAsStream("/empty.xml");
        }

        @Test
        public void shouldUseInputOutputStreams() {
            List<XsltParameter> inputs = new ArrayList<XsltParameter>();

            inputs.add(new XsltParameter("headers", headersInput));
            inputs.add(new XsltParameter("query", queryInput));

            List<XsltParameter<? extends OutputStream>> outputs = new ArrayList<XsltParameter<? extends OutputStream>>();
            outputs.add(new XsltParameter<OutputStream>("headers.html", headersOutput));
            outputs.add(new XsltParameter<OutputStream>("query.html", queryOutput));
            
            XsltChain<Templates> chain = builder.build(new StyleSheetInfo("", "classpath:///style.xsl"));
            chain.executeChain(body, output, inputs, outputs);
            
            String headersResult = headersOutput.toString();
            String queryResult = queryOutput.toString();
            String outResult = output.toString();
            
            assertTrue("Should have header output", headersResult.length() > 0);
            assertTrue("Should have query output", queryResult.length() > 0);
            assertTrue("Shoudl have main output", outResult.length() > 0);
            
        }
        
    }
}
