package pt.unl.fct.di.apdc.firstwebapp.resources;

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
import pt.unl.fct.di.apdc.firstwebapp.util.RemoveData;

import com.google.cloud.datastore.*;

@Path("/remove")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RemoveResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public RemoveResource() {

	}
	@POST
	@Path("/v2")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doRemoveV4(RemoveData data, @CookieParam("session::apdc") Cookie cookie, @Context HttpServletRequest request, @Context HttpHeaders headers) {
		if (cookie == null)
		{
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		String[] valueSplit = cookie.getValue().split(".");
		String username = valueSplit[0];
		Key userTheChangerKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity userTheChanger;
		try {
			userTheChanger = datastore.get(userTheChangerKey);
		}
		catch (DatastoreException e)
		{
			cookie = null;
			return Response.status(Status.NOT_ACCEPTABLE).entity("The user you want to remove does not exist.").cookie(new NewCookie(cookie)).build();
		}
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity user;
		try {
			user = datastore.get(userKey);
		}
		catch (DatastoreException e)
		{
			return Response.status(Status.NOT_ACCEPTABLE).entity("You have been deleted.").build();
		}
		if (user.getString("state").equals("INACTIVE"))
		{
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		String userRole = user.getString("role");
		String userTheChangerRole = userTheChanger.getString("role");
		if (DataValidation.convertRole(userRole) >= DataValidation.convertRole(userTheChangerRole))
		{
			if (userTheChangerRole.equals(DataValidation.SU) || userTheChangerRole.equals(DataValidation.USER))
			{
				if (userTheChanger.getString("username").equals(username))
				{
					cookie = null;
					datastore.delete(userKey);
					return Response.status(Status.NOT_ACCEPTABLE).build();
				}
				datastore.delete(userKey);
				return Response.ok().cookie(new NewCookie(cookie)).build();
			}
			return Response.status(Status.NOT_MODIFIED).cookie(new NewCookie(cookie)).build();
		}
		if (userRole.equals(DataValidation.GA) && DataValidation.convertRole(userTheChangerRole) < DataValidation.convertRole(userRole))
		{
			datastore.delete(userKey);
			return Response.ok().cookie(new NewCookie(cookie)).build();
		}
		return Response.status(Status.NOT_MODIFIED).cookie(new NewCookie(cookie)).build();
	}
}