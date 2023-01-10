package rso.itemscompare.authenticationservice.api.v1.resources;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import rso.itemscompare.authenticationservice.lib.AuthToken;
import rso.itemscompare.authenticationservice.lib.User;
import rso.itemscompare.authenticationservice.services.beans.AuthTokenBean;
import rso.itemscompare.authenticationservice.services.beans.UserBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

@ApplicationScoped
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationServiceResource {
    @Inject
    private UserBean userBean;
    @Inject
    private AuthTokenBean authTokenBean;

    @POST
    @Path("/authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(@HeaderParam("userEmail") String userEmail, @HeaderParam("token") String token) {
        User user;
        try {
            user = userBean.getUserByEmail(userEmail);
        } catch (NotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(buildErrorResponse("Specified user does not exist"))
                    .build();
        }
        try {
            AuthToken tokenObj = authTokenBean.getToken(user.getUserId());
            if (token.equals(tokenObj.getToken())) {
                return Response.status(Response.Status.OK).entity(true).build();
            }
            return Response.status(Response.Status.OK).entity(false).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.OK).entity(false).build();
        }
    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(@HeaderParam("user") String user, @HeaderParam("password") String password) {
        // check if email string is valid
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        if (!pattern.matcher(user).matches()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(buildErrorResponse("Invalid email specified")).build();
        }

        boolean alreadyExists = true;
        try {
            userBean.getUserByEmail(user);
        } catch (NotFoundException e) {
            alreadyExists = false;
        }

        if (alreadyExists) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(buildErrorResponse("User already exists")).build();
        }

        String hashedPassword = hashPassword(password);

        try {
            int numRowsAffected = userBean.addNewUser(user, hashedPassword);
            String errorMessage = null;
            if (numRowsAffected < 1) {
                errorMessage = "Failed to create new user";
            } else if (numRowsAffected > 1) {
                errorMessage = "More than one row(" + numRowsAffected + ") affected when creating user";
            }
            if (errorMessage != null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(buildErrorResponse(errorMessage))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(buildErrorResponse("Exception Occurred when trying to register: " + e.getMessage()))
                    .build();
        }

        return Response.status(Response.Status.CREATED).entity(true).build();
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(@HeaderParam("user") String userEmail, @HeaderParam("password") String password) {
        User user;

        try {
            user = userBean.getUserByEmail(userEmail);
        } catch (NotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(buildErrorResponse("Specified user does not exist"))
                    .build();
        }

        String userHashedPassword = user.getUserPassword();
        boolean isPasswordCorrect = verifyPassword(userHashedPassword, password);
        if (!isPasswordCorrect) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(buildErrorResponse("Incorrect password"))
                    .build();
        }

        String token = generateNewToken();
        int saveTokenResult = authTokenBean.saveTokenForUser(user.getUserId(), token);
        String errorMessage = null;
        Response.Status responseStatus = null;
        if (saveTokenResult == AuthTokenBean.ALREADY_LOGGED_IN) {
            token = authTokenBean.getToken(user.getUserId()).getToken();
        } else if (saveTokenResult < 1) {
            errorMessage = "Failed to login";
            responseStatus = Response.Status.INTERNAL_SERVER_ERROR;
        } else if (saveTokenResult > 1) {
            errorMessage = "More than one row(" + saveTokenResult + ") affected when trying to login";
            responseStatus = Response.Status.INTERNAL_SERVER_ERROR;
        }
        if (errorMessage != null) {
            return Response.status(responseStatus)
                    .entity(buildErrorResponse(errorMessage))
                    .build();
        }

        return Response.status(Response.Status.OK).entity(token).build();
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logout(@HeaderParam("user") String userEmail) {
        User user;

        try {
            user = userBean.getUserByEmail(userEmail);
        } catch (NotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(buildErrorResponse("Specified user does not exist"))
                    .build();
        }

        int deleteTokenResult = authTokenBean.deleteTokenForUser(user.getUserId());
        String errorMessage = null;
        Response.Status responseStatus = null;
        if (deleteTokenResult == AuthTokenBean.ALREADY_LOGGED_OUT) {
            return Response.status(Response.Status.OK).entity("Already logged out").build();
        } else if (deleteTokenResult < 1) {
            errorMessage = "Failed to log out user";
            responseStatus = Response.Status.INTERNAL_SERVER_ERROR;
        } else if (deleteTokenResult > 1) {
            errorMessage = "More than one row(" + deleteTokenResult + ") affected when trying to logout";
        }
        if (errorMessage != null) {
            return Response.status(responseStatus)
                    .entity(buildErrorResponse(errorMessage))
                    .build();
        }

        return Response.status(Response.Status.OK).entity(true).build();
    }

    private JsonObject buildErrorResponse(String message) {
        return Json.createObjectBuilder().add("Error", message).build();
    }

    private String hashPassword(String password) {
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 64);
        return argon2.hash(2,15*1024,1, password.toCharArray());
    }

    private boolean verifyPassword(String hash, String password) {
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 64);
        return argon2.verify(hash, password.toCharArray());
    }

    private String generateNewToken() {
        SecureRandom secureRandom = new SecureRandom();
        Base64.Encoder base64Encoder = Base64.getUrlEncoder();
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
