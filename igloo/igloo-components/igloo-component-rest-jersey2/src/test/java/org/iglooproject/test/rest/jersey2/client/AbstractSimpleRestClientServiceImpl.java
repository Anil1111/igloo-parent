package org.iglooproject.test.rest.jersey2.client;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

public abstract class AbstractSimpleRestClientServiceImpl {
	
	private final URI targetUri;
	
	protected AbstractSimpleRestClientServiceImpl(String schemeAndHost, int port, String path) {
		this(
				UriBuilder.fromUri(schemeAndHost)
				.port(port)
				.path(path)
				.build()
		);
	}
	
	protected AbstractSimpleRestClientServiceImpl(URI remoteServiceUri) {
		this.targetUri = remoteServiceUri;
	}
	
	protected Client createJerseyClient() {
		return ClientBuilder.newBuilder()
				.register(MultiPartFeature.class)
				.register(JacksonJsonProvider.class)
				.build();
	}
	
	protected URI getTargetUri() {
		return targetUri;
	}
	
	protected WebTarget getTarget() {
		return createJerseyClient().target(targetUri);
	}
}