package pt.unl.fct.di.apdc.firstwebapp.resources;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
@Path("/cookie")
public class DisplayCookieResource {
	@GET
	@Path("/info")
    public String getCookieInfo(@CookieParam ("session::apdc") Cookie cookie) {
        return cookie.getName();
    }
	@GET
	@Path("/infoPostman")
    public Response getCookieInfoPostman(@CookieParam ("session::apdc") Cookie cookie) {
        return Response.ok().entity(cookie.getValue()).build();
    }
}
