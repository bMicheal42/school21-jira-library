package com.bmicheal.jira.library.api;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

public interface LibraryService {
    void                        ft_statusTransition(Issue issue, int actionId);
    void                        ft_emailNotifyApprove(Issue issue, String email);
    void                        ft_emailNotifyTransact(Issue issue, String email, String newStatus);
    void                        ft_slackNotify(String email, String text);
    Iterable<ApplicationUser>   ft_getHeadOfCampus(String campusFieldValue);
    Iterable<ApplicationUser>   ft_getHighLevelBoss(String depFieldValue, String campusFieldValue);
    Iterable<ApplicationUser>   ft_getLowLevelBoss(String depFieldValue, String campusFieldValue);
    Iterable<ApplicationUser>   ft_getFinContr(String campusFieldValue);
    String                      ft_message();
}