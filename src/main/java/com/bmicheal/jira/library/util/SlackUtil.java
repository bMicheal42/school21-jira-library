package com.bmicheal.jira.library.util;

import com.bmicheal.jira.library.impl.LibraryServiceImpl;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.users.UsersLookupByEmailRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.users.UsersLookupByEmailResponse;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SlackUtil {
    private final Logger log = LoggerFactory.getLogger(LibraryServiceImpl.class);
    Slack slack = Slack.getInstance();
    String token = "xoxp-1548829621681-1973748268822-3987004662406-b6f83b73da6ceda8e9c97b4619fc29aa";
    MethodsClient methods = slack.methods(token);

    public void sendMessage(String email, String text) { //no channel more
        try {
            UsersLookupByEmailRequest usersRequest = UsersLookupByEmailRequest.builder()
                    .email(email)
                    .build();
            UsersLookupByEmailResponse  usersResponse = methods.usersLookupByEmail(usersRequest);

            String userSlackId = usersResponse.getUser().getId();
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(userSlackId) // Use a channel ID `C1234567` is preferable
                .text(text)
                .build();
            ChatPostMessageResponse response = methods.chatPostMessage(request);
            log.warn("result {}", response);
        } catch (IOException | SlackApiException e) {
            log.error("error: {}", e.getMessage(), e);
        }
    }

}



