package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;
import pt.unl.fct.di.apdc.firstwebapp.util.*;

import com.google.cloud.datastore.*;
import com.google.cloud.datastore.Entity.Builder;
import com.google.cloud.datastore.StructuredQuery.*;
import com.google.cloud.Timestamp;

@Path("/attribute")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class AttributesResource {

	private final Gson g = new Gson();

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public AttributesResource() {

	}

	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLoginV2(LoginData data) {
		if (data.dataEmpty() || data.dataNull()) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity user = datastore.get(userKey);
		Key logKey = datastore.allocateId(datastore.newKeyFactory()
				.addAncestors(PathElement.of("UserLog", data.username)).setKind("UserLog").newKey());
		if (user != null) {
			/*
			 * if (!DigestUtils.sha512Hex(data.password).equals(user.getString("password")))
			 * { return Response.status(Status.FORBIDDEN).build(); }
			 */
			Entity log = Entity.newBuilder(logKey).set("user-login-time", Timestamp.now()).build();
			datastore.put(log);
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -1);
			Timestamp yesterday = Timestamp.of(calendar.getTime());
			Query<Entity> query = Query.newEntityQueryBuilder().setKind("UserLog")
					.setFilter(CompositeFilter.and(
							PropertyFilter.hasAncestor(datastore.newKeyFactory().setKind("User").newKey(data.username)),
							PropertyFilter.ge("user-login-time", yesterday)))
					.build();
			QueryResults<Entity> logs = datastore.run(query);
			List<Date> loginDates = new ArrayList<Date>();
			logs.forEachRemaining(userlog -> {
				loginDates.add(userlog.getTimestamp("user-login-time").toDate());
			});
			return Response.ok(g.toJson(loginDates)).build();
		}
		return Response.status(Status.FORBIDDEN).build();
	}

	@POST
	@Path("/user/pagination")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLoginV3(LoginData data, @Context HttpServletRequest request) {
		if (data.dataEmpty() || data.dataNull()) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity user = datastore.get(userKey);
		Key logKey = datastore.allocateId(datastore.newKeyFactory()
				.addAncestors(PathElement.of("UserLog", data.username)).setKind("UserLog").newKey());
		if (user != null) {
			/*
			 * if (!DigestUtils.sha512Hex(data.password).equals(user.getString("password")))
			 * { return Response.status(Status.FORBIDDEN).build(); }
			 */
			Entity log = Entity.newBuilder(logKey).set("user-login-time", Timestamp.now()).build();
			datastore.put(log);
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -1);
			Timestamp yesterday = Timestamp.of(calendar.getTime());
			String startCursor = request.getParameter("cursor");
			Query<Entity> query;
			if (startCursor != null) {

				query = Query.newEntityQueryBuilder().setKind("UserLog").setLimit(3)
						.setStartCursor(Cursor.fromUrlSafe(startCursor))
						.setFilter(CompositeFilter.and(
								PropertyFilter
										.hasAncestor(datastore.newKeyFactory().setKind("User").newKey(data.username)),
								PropertyFilter.ge("user-login-time", yesterday)))
						.setOrderBy(OrderBy.desc("user-login-time")).build();
			} else {
				query = Query.newEntityQueryBuilder().setKind("UserLog").setLimit(3)
						.setFilter(CompositeFilter.and(
								PropertyFilter
										.hasAncestor(datastore.newKeyFactory().setKind("User").newKey(data.username)),
								PropertyFilter.ge("user-login-time", yesterday)))
						.setOrderBy(OrderBy.desc("user-login-time")).build();
			}
			QueryResults<Entity> logs = datastore.run(query);
			List<Date> loginDates = new ArrayList<Date>();
			logs.forEachRemaining(userlog -> {
				loginDates.add(userlog.getTimestamp("user-login-time").toDate());
			});
			return Response.ok(g.toJson(loginDates) + logs.getCursorAfter().toUrlSafe()).build();
		}
		return Response.status(Status.FORBIDDEN).build();
	}

	@POST
	@Path("/password")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changePassword(UserPasswordChangerData data, @CookieParam("session::apdc") Cookie cookie,
			@Context HttpServletRequest request, @Context HttpHeaders headers) {
		// TODO e onde testas se a password q tás a receber é válida (aka de acordo com
		// o pattern)? Só vejo aí q testa se é vazia ou null
		// e se é igual à confirmation?
		if (cookie == null) {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		String value = cookie.getValue();
		String[] valueSplit = value.split("\\.");
		if (System.currentTimeMillis() > (Long.valueOf(valueSplit[2]) + Long.valueOf(valueSplit[3]))) {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		if (data.dataEmpty() || data.dataNull()) {
			return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
		}
		if (!DataValidation.passwordValidation(data.newPassword).contains("valid")
				|| !data.newPassword.equals(data.confirmation)) {
			return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
		}
		String username = valueSplit[0];
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user;
		try {
			user = datastore.get(userKey);
		} catch (DatastoreException e) {
			cookie = null;
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		if (user.getString("state").equals("INACTIVE")) {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		if (!data.password.equals(user.getString("password"))) {
			return Response.status(Status.FORBIDDEN).cookie(new NewCookie(cookie)).build();
		}
		user = Entity.newBuilder(user).set("password", data.newPassword).build();
		datastore.put(user);
		return Response.ok().cookie(new NewCookie(cookie)).build();
	}

	@POST
	@Path("/account")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeAccountAttributes(ChangeAttributesData data, @CookieParam("session::apdc") Cookie cookie,
			@Context HttpServletRequest request, @Context HttpHeaders headers) {
		if (cookie == null) {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		String value = cookie.getValue();
		String[] valueSplit = value.split("\\.");
		String username = valueSplit[0];
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user;
		try {
			user = datastore.get(userKey);
		} catch (DatastoreException e) {
			cookie = null;
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		if (user.getString("state").equals("INACTIVE")) {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		if (System.currentTimeMillis() > (Long.valueOf(valueSplit[2]) + Long.valueOf(valueSplit[3]))) {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		if (!ChangeAttributesData.dataValidation(data).contains("valid")) {
			return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
		}
		Key usernameToChangeKey = datastore.newKeyFactory().setKind("User").newKey(data.usernameToChange);
		Entity userToChange = null;
		try {
			userToChange = datastore.get(usernameToChangeKey);
		} catch (DatastoreException e) {
			return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
		}
		String userRole = user.getString("role");
		String userToChangeRole = userToChange.getString("role");
		if (DataValidation.convertRole(userRole) <= DataValidation.convertRole(userToChangeRole)) {
			return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
		}
		if (data.role != "") {
			if (!data.role.equals(DataValidation.USER) && !data.role.equals(DataValidation.GBO) && !data.role.equals(DataValidation.GA) && !data.role.equals(DataValidation.SU))
			{
				return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
			}
			if (userRole.equals(DataValidation.USER) || userRole.equals(DataValidation.GBO)) {
				return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
			}
			if (userRole.equals(DataValidation.GA) && DataValidation.convertRole(DataValidation.GA) <= DataValidation.convertRole(userToChangeRole)) {
				return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
			}
			if (userRole.equals(DataValidation.GA) && DataValidation.convertRole(DataValidation.GA) > DataValidation.convertRole(userToChangeRole)
					&& DataValidation.convertRole(data.role) >= DataValidation.convertRole(DataValidation.GA)) {
				return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
			}
		}
		if (data.state != "") {
			if (!data.state.equals("ACTIVE") && !data.state.equals("INACTIVE"))
			{
				return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
			}
			if (userRole.equals(DataValidation.USER)) {
				return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
			}
			if (DataValidation.convertRole(userRole) <= DataValidation.convertRole(userToChangeRole)) {
				return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
			}
		}
		boolean bool;
		if (DataValidation.convertRole(userRole) < DataValidation.convertRole(userToChangeRole)) {
			bool = makeEntity(data, userToChange, userRole);
			if (bool) {
				return Response.ok().cookie(new NewCookie(cookie)).build();
			}
			return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
		}
		if (DataValidation.convertRole(userRole) == DataValidation.convertRole(userToChangeRole)
				&& user == userToChange) {
			bool = makeEntity(data, userToChange, userToChangeRole);
			if (bool) {
				return Response.ok().cookie(new NewCookie(cookie)).build();
			}
			return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
		}
		return Response.status(Status.NOT_MODIFIED).cookie(new NewCookie(cookie)).build();
	}
	private boolean makeEntity(ChangeAttributesData data, Entity userChange, String role) {
		Builder updatedUserBuilder = Entity.newBuilder(userChange);
		if (data.taxIdentification != "" && data.taxIdentification != null
				&& DataValidation.taxIdentificationValidation(data.taxIdentification).contains("valid")) {
			updatedUserBuilder.set("taxIdentification", data.taxIdentification);
		} else if (data.taxIdentification != ""
				&& !DataValidation.taxIdentificationValidation(data.taxIdentification).contains("valid")) {
			return false;
		}
		if (data.telephone != "" && data.telephone != null
				&& DataValidation.telephoneValidation(data.telephone).contains("valid")) {
			updatedUserBuilder.set("telephone", data.telephone);
		} else if (data.telephone != "" && !DataValidation.telephoneValidation(data.telephone).contains("valid")) {
			return false;
		}
		if (data.ocupation != "" && data.ocupation != null) {
			updatedUserBuilder.set("ocupation", data.ocupation);
		}
		if (data.postalCode != "" && data.postalCode != null
				&& DataValidation.postalCodeValidation(data.postalCode).contains("valid")) {
			updatedUserBuilder.set("postalCode", data.postalCode);
		} else if (data.postalCode != "" && !DataValidation.postalCodeValidation(data.postalCode).contains("valid")) {
			return false;
		}
		if (data.password != "" && data.password != null
				&& DataValidation.passwordValidation(data.password).contains("valid")) {
			updatedUserBuilder.set("password", data.password);
		} else if (data.password != "" && !DataValidation.passwordValidation(data.password).contains("valid")) {
			return false;
		}
		if (data.visibility != "" && data.visibility != null) {
			updatedUserBuilder.set("visibility", data.visibility);
		}
		if (data.workplace != "" && data.workplace != null) {
			updatedUserBuilder.set("workplace", data.workplace);
		}
		if (data.address != "" && data.address != null) {
			updatedUserBuilder.set("address", data.address);
		} 
		if (role != DataValidation.USER) {
			if (data.email != "" && data.email != null
					&& DataValidation.emailValidation(data.email).contains("valid")) {
				updatedUserBuilder.set("email", data.email);
			} else if (data.email != "" && !DataValidation.emailValidation(data.email).contains("valid")) {
				return false;
			}
			if (data.name != "" && data.name != null && DataValidation.nameValidation(data.name).contains("valid")) {
				updatedUserBuilder.set("name", data.name);
			} else if (data.name != "" && !DataValidation.nameValidation(data.name).contains("valid")) {
				return false;
			}
			if (data.role != "" && data.role != null) {
				updatedUserBuilder.set("role", data.role);
			}
			if (data.state != "" && data.state != null) {
				updatedUserBuilder.set("state", data.state);
			}
		}
		Entity user = updatedUserBuilder.build();
		datastore.put(user);
		return true;
	}
	@POST
	@Path("/role")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeRole(RoleData data, @CookieParam("session::apdc") Cookie cookie,
			@Context HttpServletRequest request, @Context HttpHeaders headers) {
		// TODO e onde testas se a password q tás a receber é válida (aka de acordo com
		// o pattern)? Só vejo aí q testa se é vazia ou null
		// e se é igual à confirmation?
		if (cookie == null) {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		String value = cookie.getValue();
		String[] valueSplit = value.split("\\.");
		if (System.currentTimeMillis() > (Long.valueOf(valueSplit[2]) + Long.valueOf(valueSplit[3]))) {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		if (data.dataEmpty() || data.dataNull()) {
			return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
		}
		String username = valueSplit[0];
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user;
		try {
			user = datastore.get(userKey);
		} catch (DatastoreException e) {
			cookie = null;
			return Response.status(Status.NOT_ACCEPTABLE).cookie(new NewCookie(cookie)).build();
		}
		if (user.getString("state").equals("INACTIVE")) {
			return Response.status(Status.NOT_ACCEPTABLE).cookie(new NewCookie(cookie)).build();
		}
		Key usernameToChangeKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity userToChange = null;
		try {
			userToChange = datastore.get(usernameToChangeKey);
		} catch (DatastoreException e) {
			return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
		}
		if (!data.role.equals(DataValidation.USER) && !data.role.equals(DataValidation.GBO) && !data.role.equals(DataValidation.GA) && !data.role.equals(DataValidation.SU))
		{
			return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
		}
		String userRole = user.getString("role");
		String userToChangeRole = userToChange.getString("role");
		if (DataValidation.convertRole(userRole) == DataValidation.convertRole(userToChangeRole)) {
			if (userRole.equals(DataValidation.SU)) {
				userToChange = Entity.newBuilder(userToChange).set("role", data.role).build();
				datastore.put(userToChange);
				return Response.ok().cookie(new NewCookie(cookie)).build();
			}
			return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
		}
		if (userRole.equals(DataValidation.SU)) {
			userToChange = Entity.newBuilder(userToChange).set("role", data.role).build();
			datastore.put(userToChange);
			return Response.ok().cookie(new NewCookie(cookie)).build();
		}
		if (userRole.equals(DataValidation.GA) && DataValidation.convertRole(userRole) > DataValidation.convertRole(data.role)
				&& DataValidation.convertRole(userRole) > DataValidation.convertRole(userToChangeRole)) {
			userToChange = Entity.newBuilder(userToChange).set("role", data.role).build();
			datastore.put(userToChange);
			return Response.ok().cookie(new NewCookie(cookie)).build();
		}
		return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
	}

	@POST
	@Path("/status")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeStatus(StatusData data, @CookieParam("session::apdc") Cookie cookie,
			@Context HttpServletRequest request, @Context HttpHeaders headers) {
		// TODO e onde testas se a password q tás a receber é válida (aka de acordo com
		// o pattern)? Só vejo aí q testa se é vazia ou null
		// e se é igual à confirmation?
		if (cookie == null) {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		String value = cookie.getValue();
		String[] valueSplit = value.split("\\.");
		String username = valueSplit[0];
		if (System.currentTimeMillis() > (Long.valueOf(valueSplit[2]) + Long.valueOf(valueSplit[3]))) {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		if (data.dataEmpty() || data.dataNull()) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user = datastore.get(userKey);
		Key usernameToChangeKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity userToChange = null;
		try {
			userToChange = datastore.get(usernameToChangeKey);
		} catch (DatastoreException e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		String userRole = user.getString("role");
		String userToChangeRole = userToChange.getString("role");
		if (!data.state.equals("ACTIVE") && !data.state.equals("INACTIVE"))
		{
			return Response.status(Status.BAD_REQUEST).cookie(new NewCookie(cookie)).build();
		}
		if (DataValidation.convertRole(userRole) == DataValidation.convertRole(userToChangeRole)) {
			if (userRole.equals(DataValidation.SU)) {
				userToChange = Entity.newBuilder(userToChange).set("state", data.state).build();
				datastore.put(userToChange);
				return Response.ok().cookie(new NewCookie(cookie)).build();
			}
			return Response.status(Status.NOT_MODIFIED).build();
		}
		if (userRole.equals(DataValidation.SU)) {
			userToChange = Entity.newBuilder(userToChange).set("state", data.state).build();
			datastore.put(userToChange);
			return Response.ok().cookie(new NewCookie(cookie)).build();
		}
		if (userRole.equals(DataValidation.GA)
				&& DataValidation.convertRole(userToChangeRole) < DataValidation.convertRole(userRole)) {
			userToChange = Entity.newBuilder(userToChange).set("state", data.state).build();
			datastore.put(userToChange);
			return Response.ok().cookie(new NewCookie(cookie)).build();
		}
		if (userRole.equals(DataValidation.GBO) && userToChangeRole.equals(DataValidation.USER)) {
			userToChange = Entity.newBuilder(userToChange).set("state", data.state).build();
			datastore.put(userToChange);
			return Response.ok().cookie(new NewCookie(cookie)).build();
		}
		return Response.status(Status.NOT_MODIFIED).build();
	}
}
