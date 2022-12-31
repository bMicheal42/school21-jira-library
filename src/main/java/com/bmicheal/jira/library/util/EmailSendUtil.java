package com.bmicheal.jira.library.util;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.server.SMTPMailServer;

import javax.inject.Named;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class EmailSendUtil {

    private final SMTPMailServer mailServer = MailFactory.getServerManager().getDefaultSMTPMailServer();
    private final Logger log = LoggerFactory.getLogger(EmailSendUtil.class);

    public void emailNotifyApprove(Issue issue, String email) {
        String url = String.format("https://jira.21-school.ru/browse/%s/", issue.getKey());

        String html = String.format("<body><html>" +
                "<p>Заявка на закупку " +
                "<a href=%s>%s" +
                "</a> ожидает твоего согласования." +
                "<br> <strong>Предмет заявки: </strong>%s" +
                "</p></body></html>", url, issue.getKey(), issue.getSummary());


        String subj = String.format("Заявка %s требует твоего согласования", issue.getKey());

        sendMail(email, subj, html);
    }

    public void emailNotifyTransact(Issue issue, String email, String newStatus) {
        String url = String.format("https://jira.21-school.ru/browse/%s/", issue.getKey());

        String html = String.format("<body><html>" +
                "<p>Заявка на закупку " +
                "<a href=%s>%s</a>" +
                "<br>" +
                "<strong>Предмет заявки: </strong>%s" +
                "<br>" +
                " перешла в статус " +
                "%s" +
                "</p></body></html>", url, issue.getKey(), issue.getSummary(), newStatus);

        String subj = String.format("Заявка %s сменила свой статус", issue.getKey());
        sendMail(email, subj, html);
    }

    private void sendMail(String to, String subject, String body) {
        Email email = new Email(to);
        email.setSubject(subject);
        email.setBody(body);
        email.setFromName("Jira (Закупки)");
        email.setMimeType("text/html");
        sendMail(email);
    }

    private void sendMail(Email email) {
        if (Objects.isNull(mailServer)) {
            log.warn("SMTP Mail Server is not found. Please configure new SMTP mail server.");
            return;
        }
        try {
            mailServer.send(email);
        } catch (MailException e) {
            log.error(e.getMessage(), e);
        }
    }
}