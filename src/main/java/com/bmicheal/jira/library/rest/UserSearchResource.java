package com.bmicheal.jira.library.rest;

import com.atlassian.jira.bc.user.search.UserSearchParams;
import com.atlassian.jira.bc.user.search.UserSearchService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * A resource of message.
 */
@Path("/user")
public class UserSearchResource {

    @ComponentImport
    final private UserSearchService userSearchService;


    @Inject
    public UserSearchResource(UserSearchService userSearchService) {
        this.userSearchService = userSearchService;
    }

    @GET
    @Path("/health")
    @Produces({MediaType.APPLICATION_JSON})
    @AnonymousAllowed
    public Response health() {
        return Response.ok("ok").build();
    }

    @GET
    @Path("/search")
    @Produces({MediaType.APPLICATION_JSON})
    public Response searchUsers(@QueryParam("query") final String userQuery,
                                @Context HttpServletRequest request ) {
        List<UserSearchResourceModel> users = findUsers(userQuery);
        return Response.ok(users).build();
    }

    private List<UserSearchResourceModel> findUsers(String query) {
        List<UserSearchResourceModel> userSearchResourceModels = new ArrayList<UserSearchResourceModel>();
        UserSearchParams searchParams = UserSearchParams.builder()
                .includeActive(true)
                .sorted(true)
                .build();
        List<String> users = userSearchService.findUserNames(query, searchParams);
        if (users != null) {
            for (String user : users) {
                userSearchResourceModels.add(new UserSearchResourceModel(user, user));
            }
        }
        return userSearchResourceModels;
    }
}