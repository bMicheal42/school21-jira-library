package it.com.bmicheal.jira.library.rest;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.bmicheal.jira.library.rest.UserSearchResource;
import com.bmicheal.jira.library.rest.UserSearchResourceModel;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;

public class UserSearchResourceFuncTest {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

//    @Test
//    public void messageIsValid() {
//
//        String baseUrl = System.getProperty("baseurl");
//        String resourceUrl = baseUrl + "/rest/usersearchresource/1.0/message";
//
//        RestClient client = new RestClient();
//        Resource resource = client.resource(resourceUrl);
//
//        UserSearchResourceModel message = resource.get(UserSearchResourceModel.class);
//
//        assertEquals("wrong message","Hello World",message.getMessage());
//    }
}
