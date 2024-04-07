package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import pt.unl.fct.di.apdc.firstwebapp.util.DataValidation;
import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.Authentication.SignatureUtils;
import com.google.cloud.datastore.*;

@Path("/logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogoutResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public LogoutResource() {

	}

	@POST
	@Path("/v2")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogout(@CookieParam("session:apdc") Cookie cookie) {
		String value = cookie.getValue();
		String[] valueSplit = value.split("\\.");
		String username = valueSplit[0];
		NewCookie theCookie = new NewCookie("session::apdc", cookie.getValue(), "/", null, "comment", 0, false, true);
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user = null;
		try {
			user = datastore.get(userKey);
		} catch (DatastoreException e) {

		}
		if (user == null) {

		}
		return Response.ok().build();
	}
}
