package org.hobbit.core.service.docker.example;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.aksw.jena_sparql_api.core.FluentQueryExecutionFactory;
import org.aksw.jena_sparql_api.core.connection.QueryExecutionFactorySparqlQueryConnection;
import org.aksw.jena_sparql_api.core.connection.SparqlQueryConnectionJsa;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.rdfconnection.RDFConnectionModular;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.WebContent;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.hobbit.core.service.docker.api.DockerService;
import org.hobbit.core.service.docker.api.DockerServiceSystem;
import org.hobbit.core.service.docker.impl.docker_client.DockerServiceSystemDockerClient;

import com.google.common.collect.ImmutableMap;
import com.spotify.docker.client.exceptions.DockerCertificateException;

public class MainDockerServiceExampleVirtuoso {
	public static void main(String[] args) throws DockerCertificateException, Exception {

		try (DockerServiceSystem<?> dss = DockerServiceSystemDockerClient.create(true, Collections.emptyMap(), Collections.emptySet())) {

			DockerService ds = dss.create("docker-service-example", "tenforce/virtuoso", ImmutableMap.<String, String>builder()
					.put("SPARQL_UPDATE", "true")
					.put("DEFAULT_GRAPH", "http://www.example.org/")
					.build());
			
			try {
				ds.startAsync().awaitRunning();

				// Give the sparql endpoint 5 seconds to come up
				// TODO Add another example for wrapping with health checks
				Thread.sleep(10000);
				
				String sparqlApiBase = "http://" + ds.getContainerId() + ":8890/";
				String sparqlEndpoint = sparqlApiBase + "sparql";

				dss.findServiceByName("docker-service-example");
				
				
				try(RDFConnection rawConn = RDFConnectionFactory.connect(sparqlEndpoint)) {
					
					// Wrap the connection to use a different content type for queries...
					// Jena rejects some of Virtuoso's json output
					@SuppressWarnings("resource")
					RDFConnection conn =
							new RDFConnectionModular(new SparqlQueryConnectionJsa(
									FluentQueryExecutionFactory
										.from(new QueryExecutionFactorySparqlQueryConnection(rawConn))
										.config()
										.withPostProcessor(qe -> {
											if(qe instanceof QueryEngineHTTP) {
												((QueryEngineHTTP)qe).setSelectContentType(WebContent.contentTypeResultsXML);
											}
										})
										.end()
										.create()
										), rawConn, rawConn);

					
					conn.update("PREFIX eg: <http://www.example.org/> INSERT DATA { GRAPH <http://www.example.org/> { eg:s eg:p eg:o } }");

					Model model = conn.queryConstruct("CONSTRUCT FROM <http://www.example.org/> WHERE { ?s ?p ?o }");
					RDFDataMgr.write(System.out, model, RDFFormat.TURTLE_PRETTY);
				}
				
			} finally {
				ds.stopAsync().awaitTerminated(30, TimeUnit.SECONDS);
			}
			
			
		}
		
	}
}
