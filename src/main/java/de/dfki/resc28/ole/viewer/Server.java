/*
 * This file is part of OLE-Viewer. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.ole.viewer;

import de.dfki.resc28.ole.viewer.services.IndexService;
import de.dfki.resc28.ole.viewer.util.CORSFilter;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import de.dfki.resc28.ole.viewer.services.Service;
import de.dfki.resc28.ole.viewer.util.ForwardedHeaderFilter;
import de.dfki.resc28.ole.viewer.util.ProxyConfigurator;
import de.dfki.resc28.ole.viewer.util.WebAppExceptionMapper;
import java.util.HashMap;
import java.util.Map;
import org.glassfish.jersey.server.mvc.mustache.MustacheMvcFeature;

@ApplicationPath("/")
public class Server extends Application {

    static {
        ProxyConfigurator.initHttpClient();
    }

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(MustacheMvcFeature.class);
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        final Set<Object> singletons = new HashSet<Object>();
        singletons.add(new ForwardedHeaderFilter());
        singletons.add(new CORSFilter());
        // singletons.add(new WebAppExceptionMapper());
        singletons.add(new Service());
        singletons.add(new IndexService());
        return singletons;
    }

    @Override
    public Map<String, Object> getProperties() {
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(MustacheMvcFeature.TEMPLATE_BASE_PATH, "/WEB-INF/mustache");
        return properties;
    }
}
