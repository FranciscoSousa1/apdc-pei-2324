package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/utils")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
public class ComputationResource { 

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
