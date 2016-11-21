/*
 * This file is part of OLE-Viewer. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.ole.viewer;

import de.dfki.resc28.ole.viewer.services.CORSFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import de.dfki.resc28.ole.viewer.services.Service;



@ApplicationPath("/")
public class Server extends Application 
{
	public static String fBaseURI;

//	public Server(@Context ServletContext servletContext) throws URISyntaxException, IOException
//	{
//		configure();
//	}

	@Override
    public Set<Object> getSingletons() 
    {	
		Service service = new Service();
                CORSFilter corsFilter = new CORSFilter();
		return new HashSet<Object>(Arrays.asList(service, corsFilter));
    }

//	public static synchronized void configure() 
//    {
//        try 
//        {
//            String configFile = System.getProperty("oleviewer.configuration");
//            java.io.InputStream is;
//
//            if (configFile != null) 
//            {
//                is = new java.io.FileInputStream(configFile);
//                System.out.format("Loading OLE-Viewer configuration from %s ...%n", configFile);
//            } 
//            else 
//            {
//                is = Server.class.getClassLoader().getResourceAsStream("oleviewer.properties");
//                System.out.println("Loading OLE-Viewer configuration from internal resource file ...");
//            }
//
//            java.util.Properties p = new Properties();
//            p.load(is);
//
//            Server.fBaseURI = getProperty(p, "baseURI", "oleviewer.baseURI");
//        } 
//        catch (Exception e) 
//        {
//            e.printStackTrace();
//        }
//    }
//
//    public static String getProperty(java.util.Properties p, String key, String sysKey) 
//    {
//        String value = System.getProperty(sysKey);
//        if (value != null) 
//        {
//            return value;
//        }
//        return p.getProperty(key);
//    }
}
