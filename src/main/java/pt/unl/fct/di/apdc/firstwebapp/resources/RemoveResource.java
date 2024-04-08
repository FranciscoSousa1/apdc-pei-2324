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

import pt.unl.fct.di.apdc.firstwebapp.Authentication.SignatureUtils;
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
	public Response doRemoveV4(RemoveData data, @CookieParam("session::apdc") Cookie cookie) {
		if (cookie == null)
		{
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		String[] valueSplit = cookie.getValue().split("\\.");
		String username = valueSplit[0];
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user;
		try {
			user = datastore.get(userKey);
		}
		catch (DatastoreException e)
		{
			cookie = null;
			return Response.status(Status.NOT_ACCEPTABLE).entity("You have been deleted.").build();
		}
		if (user.getString("password_changed").equals("true"))
		{
			user = Entity.newBuilder(user).set("password_changed", "false").build();
			datastore.put(user);
			return Response.status(Status.NOT_ACCEPTABLE).entity("A user might have changed your password").build();
		}
		String id = UUID.randomUUID().toString();
		long currentTime = System.currentTimeMillis();
		String fields = username+"."+ id +"."+currentTime+"."+1000*60*60*2;
		
		String signature = SignatureUtils.calculateHMac(DataValidation.key, fields);
		
		if(signature == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error while signing token. See logs.").build();
		}
		String theValue =  fields + "." + signature;
		NewCookie theCookie = new NewCookie("session::apdc", theValue, "/", null, "comment", 1000*60*60*2, false, true);
		Key userTheChangerKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity userTheChanger = null;
		try {
			userTheChanger = datastore.get(userTheChangerKey);
		}
		catch (DatastoreException e)
		{
			return Response.status(Status.NOT_ACCEPTABLE).entity("The user does not exist.").build();
		}
		if (!user.getString("signature").equals(valueSplit[valueSplit.length-1]))
		{
			return Response.status(Status.NOT_ACCEPTABLE).entity("The cookie has been changed mysteriously. Redirecting...").build();
		}
		if (user.getString("state").equals("INACTIVE"))
		{
			return Response.status(Status.NOT_ACCEPTABLE).entity("The state of yours has become inactive.").build();
		}
		String userRole = user.getString("role");
		String userTheChangerRole = userTheChanger.getString("role");
		if (userRole.equals(DataValidation.SU))
		{
			if (userTheChanger.getString("username").equals(username))
			{
				cookie = null;
				datastore.delete(userKey);
				return Response.ok().cookie(theCookie).build();
			}
			datastore.delete(userTheChangerKey);
			return Response.ok().cookie(theCookie).build();
		}
		if (userRole.equals(DataValidation.USER) && userTheChanger.getString("username").equals(username))
		{
			cookie = null;
			datastore.delete(userKey);
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		if (userRole.equals(DataValidation.GA) && DataValidation.convertRole(userTheChangerRole) < DataValidation.convertRole(userRole))
		{
			datastore.delete(userTheChangerKey);
			return Response.ok().cookie(theCookie).build();
		}
		return Response.status(Status.NOT_MODIFIED).build();
	}
}