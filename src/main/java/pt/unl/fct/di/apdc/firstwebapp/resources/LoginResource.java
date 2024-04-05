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

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public LoginResource() {

	}
	@POST
	@Path("/v2")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLoginV4(LoginData data, @CookieParam("session::apdc") Cookie cookie,
			@Context HttpServletRequest request, @Context HttpHeaders headers) {
		if (data.dataEmpty() || data.dataNull()) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity user;
		try {
			user = datastore.get(userKey);
		}
		catch (DatastoreException e)
		{
			cookie = null;
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		if (user != null) {
			if (!user.getString(DataValidation.STATE).equals(DataValidation.ACTIVE))
			{
				return Response.status(Status.NOT_ACCEPTABLE).build();
			}
			if (!data.password.equals(user.getString(DataValidation.PASSWORD))) {
				return Response.status(Status.FORBIDDEN).build();
			}
			NewCookie theCookie;
			if (cookie == null)
			{
				String id = UUID.randomUUID().toString();
				long currentTime = System.currentTimeMillis();
				String fields = data.username + "." + id + "." + currentTime + "." + 1000 * 60 * 60 * 2;
				String signature = SignatureUtils.calculateHMac(DataValidation.key, fields);
				String value = fields + "." + signature;
				theCookie = new NewCookie("session::apdc",  value, "/", null, "comment", 1000 * 60 * 60 * 2, false, true);
				return Response.ok().cookie(theCookie).build();
			}
			else {
				theCookie = new NewCookie("session::apdc", cookie.getValue(), "/", null, "comment", 1000 * 60 * 60 * 2, false, true);
				return Response.ok().cookie(theCookie).build();
			}
		}
		return Response.status(Status.FORBIDDEN).entity("Username not valid.").build();
	}
}
