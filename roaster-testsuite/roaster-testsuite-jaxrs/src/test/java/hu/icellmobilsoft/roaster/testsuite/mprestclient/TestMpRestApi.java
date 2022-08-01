package hu.icellmobilsoft.roaster.testsuite.mprestclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseRequest;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * Sample TestMP Rest client API
 *
 * @author imre.scheffer
 * @since 0.8.0
 */
@RegisterRestClient
public interface TestMpRestApi {

    @POST
    @Path("/mp/rest/client/post")
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(value = { MediaType.APPLICATION_JSON })
    String testPost(BaseRequest baseRequest) throws BaseException;
}
