/*
 * This file is part of OLE. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.ole.viewer.services;

import de.dfki.resc28.ole.viewer.util.Config;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.server.mvc.Template;
import org.apache.jena.atlas.json.JsonObject;

/**
 *
 * @author Dmitri Rubinstein
 */
@Path("/")
public class IndexService {

    private final String configAsJson;
    private final String configJs;

    public IndexService() {
        JsonObject info = new JsonObject();
        for (Map.Entry<Object, Object> item : Config.PROPERTIES.entrySet()) {
            info.put(item.getKey().toString(), item.getValue().toString());
        }
        configAsJson = info.toString();

        String _configJs = "";
        if (Config.CONFIG_JS_PATH != null) {
            try {
                _configJs = new String(Files.readAllBytes(Paths.get(Config.CONFIG_JS_PATH)), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                Logger.getLogger(IndexService.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (Config.CONFIG_JS != null) {
            _configJs = Config.CONFIG_JS;
        }
        this.configJs = _configJs;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Template(name = "/index")
    public Map<String, Object> getIndex(@Context UriInfo uriInfo) {
        Map<String, Object> model = new HashMap<String, Object>();
        Map<String, Object> uriInfoModel = new HashMap<String, Object>();
        uriInfoModel.put("absolutePath", uriInfo.getAbsolutePath());
        model.put("uriInfo", uriInfoModel);
        model.put("configAsJson", configAsJson);
        model.put("config", Config.PROPERTIES);
        model.put("configJs", configJs);
        return model;
    }
}
