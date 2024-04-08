package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import pt.unl.fct.di.apdc.firstwebapp.resources.LogoutResource;

@Path("/utils")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
public class ComputationResource { 
	
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public ComputationResource() {} //nothing to be done here @GET

	@GET
	@Path("/register")
	@Produces(MediaType.TEXT_PLAIN)
	public Response register() throws IOException {
			return Response.temporaryRedirect(URI.create("/register/register.html")).build();
	}
	@GET
	@Path("/functionalities")
	@Produces(MediaType.TEXT_PLAIN)
	public Response goBack() throws IOException {
			return Response.temporaryRedirect(URI.create("/index.html")).build();
	}
	@GET
	@Path("/login")
	@Produces(MediaType.TEXT_PLAIN)
	public Response login(@CookieParam("session::apdc") Cookie cookie) throws IOException {
			return Response.temporaryRedirect(URI.create("/login/login.html")).build();
	}
	@GET
	@Path("/logout")
	@Produces(MediaType.TEXT_PLAIN)
	public Response logout(@CookieParam("session::apdc") Cookie cookie) throws IOException {
		String value = cookie.getValue();
		String[] valueSplit = value.split("\\.");
		if (System.currentTimeMillis() > (Long.valueOf(valueSplit[2]) + Long.valueOf(valueSplit[3]))) {
			return Response.status(Status.NOT_ACCEPTABLE).entity("Cookie expired. Redirecting...").build();
		}
		String username = valueSplit[0];
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user = null;
		try {
			user = datastore.get(userKey);
		}
		catch (DatastoreException e)
		{
			
		}
		if (user == null)
		{
			return Response.status(Status.NOT_ACCEPTABLE).entity("You have been deleted. Redirecting...").build();
		}
		if (!user.getString("signature").equals(valueSplit[valueSplit.length-1]))
		{
			return Response.status(Status.NOT_ACCEPTABLE).entity("The cookie has been changed mysteriously. Redirecting...").build();
		}
		if (user.getString("state").equals("INACTIVE"))
		{
			return Response.status(Status.NOT_ACCEPTABLE).entity("Someone has changed the state of yours to inactive. Redirecting...").build();
		}
		if (user.getString("password_changed").equals("true"))
		{
			user = Entity.newBuilder(user).set("password_changed", "false").build();
			datastore.put(user);
			return Response.status(Status.NOT_ACCEPTABLE).entity("A user might have changed your password").build();
		}
		NewCookie newCookie = new NewCookie("session::apdc", null, "/", null, "comment", 0, false, true);
		return Response.temporaryRedirect(URI.create("/index.html")).cookie(newCookie).build();
	}
	@GET
	@Path("/profile")
	@Produces(MediaType.TEXT_PLAIN)
	public Response goToProfile() throws IOException {
			return Response.temporaryRedirect(URI.create("/profile/profile.html")).build();
	}
	@GET
	@Path("/remove")
	@Produces(MediaType.TEXT_PLAIN)
	public Response removeUser() throws IOException {
			return Response.temporaryRedirect(URI.create("/remove/remove.html")).build();
	}
	@GET
	@Path("/status")
	@Produces(MediaType.TEXT_PLAIN)
	public Response statusUser() throws IOException {
			return Response.temporaryRedirect(URI.create("/status/status.html")).build();
	}
	@GET
	@Path("/list")
	@Produces(MediaType.TEXT_PLAIN)
	public Response listUser() throws IOException {
			return Response.temporaryRedirect(URI.create("/list/list.html")).build();
	}
	@GET
	@Path("/attribute")
	@Produces(MediaType.TEXT_PLAIN)
	public Response changeAttributeUser() throws IOException {
			return Response.temporaryRedirect(URI.create("/attribute/attribute.html")).build();
	}
	@GET
	@Path("/password")
	@Produces(MediaType.TEXT_PLAIN)
	public Response changePasswordUser() throws IOException {
			return Response.temporaryRedirect(URI.create("/password/password.html")).build();
	}
	@GET
	@Path("/role")
	@Produces(MediaType.TEXT_PLAIN)
	public Response changeUserRole() throws IOException {
			return Response.temporaryRedirect(URI.create("/role/role.html")).build();
	}
}