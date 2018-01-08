/*
 * This file is part of OLE-Viewer. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.ole.viewer.services;

import de.dfki.resc28.ole.viewer.util.ProxyConfigurator;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shared.PrefixMapping;

import de.dfki.resc28.ole.viewer.vocabularies.ADMS;
import java.util.Map;
import javax.ws.rs.ServerErrorException;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.apache.jena.riot.RDFLanguages;

@Path("/api")
public class Service {

    @GET
    @Path("/visualize")
//	@Produces(MediaType.APPLICATION_JSON)
    public Response generateSource(@DefaultValue("http://ole-frontend/repo") @QueryParam("uri") String oleUri) {

        try {
            JsonArray elements = new JsonArray();
            Model modelToDisplay = ModelFactory.createDefaultModel();

            URI rdfUri = new URI(oleUri);
            CloseableHttpClient client = ProxyConfigurator.createHttpClient();
            HttpGet req = new HttpGet(rdfUri);
            req.setHeader("Accept", "text/turtle");
            try {
                CloseableHttpResponse response = client.execute(req);

                if (response.getStatusLine().getStatusCode() == 200) {
                    RDFDataMgr.read(modelToDisplay, response.getEntity().getContent(), oleUri, RDFLanguages.contentTypeToLang(response.getEntity().getContentType().getValue()));
                } else {
                    final HttpEntity _entity = response.getEntity();
                    final ContentType _contentType = ContentType.getOrDefault(_entity);
                    final String contentStr = _entity != null ? EntityUtils.toString(_entity) : "";

                    System.err.println("Request to OLE failed: " + oleUri);
                    System.err.println("Response Content-Type: " + _contentType);
                    System.err.println("Response Content: " + contentStr);

                    JsonObject error = new JsonObject();
                    error.put("message", "Request to OLE service " + oleUri + " failed");
                    error.put("response.statusCode", Integer.toString(response.getStatusLine().getStatusCode()));
                    error.put("response.status", response.getStatusLine().getReasonPhrase());
                    error.put("response.contentType", _contentType.toString());
                    error.put("response.content", contentStr);

                    return Response.status(Response.Status.BAD_GATEWAY)
                            .entity(error.toString())
                            .type(MediaType.APPLICATION_JSON).build();
                }
            } finally {
                client.close();
            }
            PrefixMapping pm = PrefixMapping.Factory.create();
            final Map<String, String> prefixMap = modelToDisplay.getNsPrefixMap();
            prefixMap.put("adms", "http://www.w3.org/ns/adms#");
            prefixMap.put("dcat", "http://www.w3.org/ns/dcat#");
            prefixMap.put("dcterms", "http://purl.org/dc/terms/");
            prefixMap.put("foaf", "http://xmlns.com/foaf/0.1/");
            prefixMap.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            prefixMap.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
            prefixMap.put("skos", "http://www.w3.org/2004/02/skos/core#");
            prefixMap.put("xsd", "http://www.w3.org/2001/XMLSchema#");
            prefixMap.put("owl", "http://www.w3.org/2002/07/owl#");
            pm.setNsPrefixes(prefixMap);

            modelToDisplay.removeAll(null, ADMS.includedAsset, (RDFNode) null);

            // generate cytoscape nodes for all resources in modelToDispla
            Set<RDFNode> nodeSet = modelToDisplay.listObjects().toSet();
            ResIterator subjects = modelToDisplay.listSubjects();
            while (subjects.hasNext()) {
                nodeSet.add(subjects.next());
            }

            Iterator<RDFNode> nodes = nodeSet.iterator();
            while (nodes.hasNext()) {
                try {
                    RDFNode rdfNode = nodes.next();
                    JsonObject node = new JsonObject();

                    if (rdfNode.isURIResource()) {
                        String uri = ((Resource) rdfNode).getURI();

                        node.put("position", "");
                        node.put("group", "nodes");
                        node.put("removed", false);
                        node.put("selected", false);
                        node.put("selectable", true);
                        node.put("locked", false);
                        node.put("grabbed", false);
                        node.put("grabbable", true);
                        node.put("classes", "");

                        JsonObject data = new JsonObject();
                        data.put("id", rdfNode.hashCode());
                        data.put("uri", uri);
                        data.put("name", pm.shortForm(uri));
                        data.put("nodeType", "uriNode");

                        node.put("data", data);
                    } else if (rdfNode.isAnon()) {
                        node.put("position", "");
                        node.put("group", "nodes");
                        node.put("removed", false);
                        node.put("selected", false);
                        node.put("selectable", true);
                        node.put("locked", false);
                        node.put("grabbed", false);
                        node.put("grabbable", true);
                        node.put("", "blankNode");

                        JsonObject data = new JsonObject();
                        data.put("id", rdfNode.hashCode());
                        data.put("nodeType", "blankNode");

                        node.put("data", data);
                    } else {
                        node.put("position", "");
                        node.put("group", "nodes");
                        node.put("removed", false);
                        node.put("selected", false);
                        node.put("selectable", true);
                        node.put("locked", false);
                        node.put("grabbed", false);
                        node.put("grabbable", true);
                        node.put("classes", "");

                        JsonObject data = new JsonObject();
                        data.put("id", rdfNode.hashCode());
                        Literal value = rdfNode.asLiteral();
                        RDFDatatype valueType = value.getDatatype();
                        data.put("value", value.getString());
                        data.put("nodeType", "literalNode");
                        data.put("valueType", pm.shortForm(valueType.getURI()));

                        node.put("data", data);
                    }

                    elements.add(node);
                } catch (Exception e) {
                    // do nothing
                }
            }

            // generate cytoscape edges for all triples in modelToDispla
            StmtIterator triples = modelToDisplay.listStatements();
            while (triples.hasNext()) {
                Statement triple = triples.next();

                String uri = triple.getPredicate().getURI();

                JsonObject edge = new JsonObject();
                edge.put("group", "edges");
                edge.put("removed", "false");
                edge.put("selected", "false");
                edge.put("selectable", "true");
                edge.put("locked", "false");
                edge.put("grabbed", "false");
                edge.put("grabbable", "true");
                edge.put("classes", "autorotate");

                JsonObject data = new JsonObject();
                data.put("source", triple.getSubject().hashCode());
                data.put("target", triple.getObject().hashCode());
                data.put("id", triple.hashCode());
                data.put("uri", uri);
                data.put("name", pm.shortForm(uri));
                edge.put("data", data);

                elements.add(edge);
            }

            return Response.ok(elements.toString(), MediaType.APPLICATION_JSON).build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new WebApplicationException("Invalid uri!", Status.BAD_REQUEST);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            throw new WebApplicationException("Remote service not available.", Status.REQUEST_TIMEOUT);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException("Something went wrong.", Status.INTERNAL_SERVER_ERROR);
        }

    }
}
