package com.bmicheal.jira.library.impl;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.TransitionOptions;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActors;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import com.bmicheal.jira.library.api.LibraryService;
import com.bmicheal.jira.library.util.EmailSendUtil;
import com.bmicheal.jira.library.util.SlackUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;


@ExportAsService({LibraryService.class})
@Named


public class LibraryServiceImpl implements LibraryService {

    private final Logger log = LoggerFactory.getLogger(LibraryServiceImpl.class);
    private final EmailSendUtil emailSendUtil = new EmailSendUtil();
    private final SlackUtil slackUtil = new SlackUtil();

    @ComponentImport
    private final IssueService issueService;

    @ComponentImport
    protected final JiraAuthenticationContext jiraAuthenticationContext;

    @ComponentImport
    private final ProjectRoleManager projectRoleManager;

    @ComponentImport
    private final ProjectManager projectManager;


    @Inject
    public LibraryServiceImpl(IssueService issueService, JiraAuthenticationContext jiraAuthenticationContext, ProjectRoleManager projectRoleManager, ProjectManager projectManager) {
        this.issueService = issueService;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.projectRoleManager = projectRoleManager;
        this.projectManager = projectManager;
    }


    //_______________________________________ LIBRARY FUNCTIONS ________________________________________________________

    public String ft_message(){
        return "OMG";
    }

    /** Transaction to Status ID
     * use workflow id for actionId
     * */
    public void ft_statusTransition(Issue issue, int actionId) {
        ApplicationUser user = jiraAuthenticationContext.getLoggedInUser();
        IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
        TransitionOptions transitionOptions = new TransitionOptions.Builder()
                .skipConditions()
                .skipPermissions()
                .skipValidators()
                .build();

        IssueService.TransitionValidationResult validationResult = issueService.validateTransition(user, issue.getId(), actionId, issueInputParameters, transitionOptions);

        if (validationResult.isValid()) {
            IssueService.IssueResult issueResult = issueService.transition(user, validationResult);
            if (!issueResult.isValid())
                log.warn("Failed to transition task {}, issuerResult not valid, errors: {}", issue.getKey(), issueResult.getErrorCollection());
            else
                log.info("Transaction {} with issue {} DONE", actionId, issue.getKey());
        }
        else
            log.warn("Failed to transition task {}, validationResult not valid, errors: {}", issue.getKey(), validationResult.getErrorCollection());

    }

    /** send Email to Approver
     * use workflow id for actionId
     * */
    public void ft_emailNotifyApprove(Issue issue, String email) {
        emailSendUtil.emailNotifyApprove(issue, email);
    }

    public void ft_emailNotifyTransact(Issue issue, String email, String newStatus) {
        emailSendUtil.emailNotifyTransact(issue, email, newStatus);
    }

    public void ft_slackNotify(String email, String text) {
        slackUtil.sendMessage(email, text);
    }


    public Iterable<ApplicationUser> ft_getHeadOfCampus(String campusFieldValue) {
        Project project = projectManager.getProjectObjByKey("PUR21");

        String projectRoleNameCampus;

        if (Objects.equals(campusFieldValue, "Москва"))
            projectRoleNameCampus = "PUR Head of the Campus MSK";
        else if (Objects.equals(campusFieldValue, "Казань"))
            projectRoleNameCampus = "PUR Head of the Campus KZN";
        else if (Objects.equals(campusFieldValue, "Новосибирск"))
            projectRoleNameCampus = "PUR Head of the Campus NSK";
        else
            projectRoleNameCampus = "Administrators";

        ProjectRole projectRoleHeadCampus = projectRoleManager.getProjectRole(projectRoleNameCampus);
        ProjectRoleActors actorsHeadCampus = projectRoleManager.getProjectRoleActors(projectRoleHeadCampus, project);

        return actorsHeadCampus.getUsers();
    }

    public Iterable<ApplicationUser> ft_getHighLevelBoss(String depFieldValue, String campusFieldValue) {
        Project project = projectManager.getProjectObjByKey("PUR21");

        String projectRoleNameDep = null;

        if (Objects.equals(depFieldValue, "ADM"))
            projectRoleNameDep = "PUR Head of ADM";

        else if (Objects.equals(depFieldValue, "ADM коммерческие интенсивы"))
            projectRoleNameDep = "PUR Head of ADM comm";

        else if (Objects.equals(depFieldValue, "КБ") || Objects.equals(depFieldValue, "IT - КБ"))
            projectRoleNameDep = "PUR CISO";

        else if (Objects.equals(depFieldValue, "HR"))
            projectRoleNameDep = "PUR Head of HR";

        else if (Objects.equals(depFieldValue, "Product - коммерция") || Objects.equals(depFieldValue, "Product - основа") || Objects.equals(depFieldValue, "Методологи - основа") || Objects.equals(depFieldValue, "Методологи - ДПО"))
            projectRoleNameDep = "PUR CPO";

        else if (Objects.equals(depFieldValue, "IT - HQ") || Objects.equals(depFieldValue, "IT - DEV") || Objects.equals(depFieldValue, "IT - OPS") || Objects.equals(depFieldValue, "IT - Платформа") || Objects.equals(depFieldValue, "IT - Support") || Objects.equals(depFieldValue, "IT - Коммерция"))
            projectRoleNameDep = "PUR Head of Bocal";

        else if (Objects.equals(depFieldValue, "АХО")) {
            if (Objects.equals(campusFieldValue, "Москва"))
                projectRoleNameDep = "PUR COO";
            else if (Objects.equals(campusFieldValue, "Казань"))
                projectRoleNameDep = "PUR Head of the Campus KZN";
            else if (Objects.equals(campusFieldValue, "Новосибирск"))
                projectRoleNameDep = "PUR Head of the Campus NSK";
        }

        else if (Objects.equals(depFieldValue, "Коммерция") || Objects.equals(depFieldValue, "Коммерция - Франшиза") || Objects.equals(depFieldValue, "Коммерция - ДПО") || Objects.equals(depFieldValue, "Стажировки"))
            projectRoleNameDep = "PUR Head of Development";

        else if (Objects.equals(depFieldValue, "Финансы"))
            projectRoleNameDep = "PUR COO";

        else if (Objects.equals(depFieldValue, "Маркетинг"))
            projectRoleNameDep = "PUR Head of Marketing";

        ProjectRole projectRoleDep = projectRoleManager.getProjectRole(projectRoleNameDep);
        ProjectRoleActors actorsHeadDep = projectRoleManager.getProjectRoleActors(projectRoleDep, project);
        return actorsHeadDep.getUsers();
    }


    public Iterable<ApplicationUser> ft_getLowLevelBoss(String depFieldValue, String campusFieldValue) {
        Project project = projectManager.getProjectObjByKey("PUR21");

        String projectRoleNameCampus = null;

        if (Objects.equals(depFieldValue, "ADM")) {
            if (Objects.equals(campusFieldValue, "Москва"))
                projectRoleNameCampus = "PUR LB ADM MSK";
            else if (Objects.equals(campusFieldValue, "Казань"))
                projectRoleNameCampus = "PUR LB ADM KZN";
            else if (Objects.equals(campusFieldValue, "Новосибирск"))
                projectRoleNameCampus = "PUR LB ADM NSK";
        }

        else if (Objects.equals(depFieldValue, "ADM коммерческие интенсивы"))
            projectRoleNameCampus = "PUR LB ADM comm";

        else if (Objects.equals(depFieldValue, "HR"))
            projectRoleNameCampus = "PUR Head of HR";

        else if (Objects.equals(depFieldValue, "Product - основа"))
            projectRoleNameCampus = "PUR CPO"; // done

        else if (Objects.equals(depFieldValue, "Product - коммерция"))
            projectRoleNameCampus = "PUR LB PRODUCT COMM"; // done

        else if (Objects.equals(depFieldValue, "АХО")) {
            if (Objects.equals(campusFieldValue, "Москва"))
                projectRoleNameCampus = "PUR LB AXO MSK";
            else if (Objects.equals(campusFieldValue, "Казань"))
                projectRoleNameCampus = "PUR LB AXO KZN";
            else if (Objects.equals(campusFieldValue, "Новосибирск"))
                projectRoleNameCampus = "PUR LB AXO NSK";
        }

        else if (Objects.equals(depFieldValue, "IT - DEV"))
            projectRoleNameCampus = "PUR LB IT DEV";

        else if (Objects.equals(depFieldValue, "IT - OPS")) {
            if (Objects.equals(campusFieldValue, "Москва"))
                projectRoleNameCampus = "PUR LB IT OPS MSK";
            else if (Objects.equals(campusFieldValue, "Казань"))
                projectRoleNameCampus = "PUR LB IT OPS KZN";
            else if (Objects.equals(campusFieldValue, "Новосибирск"))
                projectRoleNameCampus = "PUR LB IT OPS NSK";
        }

        else if (Objects.equals(depFieldValue, "IT - КБ") || Objects.equals(depFieldValue, "КБ"))
            projectRoleNameCampus = "PUR CISO"; // done

        else if (Objects.equals(depFieldValue, "IT - Платформа"))
            projectRoleNameCampus = "PUR LB IT PLATFORM";

        else if (Objects.equals(depFieldValue, "IT - HQ"))
            projectRoleNameCampus = "PUR Head of Bocal"; // done

        else if (Objects.equals(depFieldValue, "IT - Support"))
            projectRoleNameCampus = "PUR LB IT SUPPORT";

        else if (Objects.equals(depFieldValue, "IT - Коммерция"))
            projectRoleNameCampus = "PUR LB IT COMMERCIAL";

        else if (Objects.equals(depFieldValue, "Коммерция - Франшиза"))
            projectRoleNameCampus = "PUR LB COM - franchises";

        else if (Objects.equals(depFieldValue, "Коммерция") || Objects.equals(depFieldValue, "Коммерция - ДПО"))
            projectRoleNameCampus = "PUR LB COM"; // done

        else if (Objects.equals(depFieldValue, "Финансы"))
            projectRoleNameCampus = "PUR LB FINANCE";

        else if (Objects.equals(depFieldValue, "Маркетинг"))
            projectRoleNameCampus = "PUR LB MARKETING";

        else if (Objects.equals(depFieldValue, "Методологи - основа"))
            projectRoleNameCampus = "PUR LB METHOD - OSNOVA";

        else if (Objects.equals(depFieldValue, "Методологи - ДПО"))
            projectRoleNameCampus = "PUR LB METHOD - DPO";

        else if (Objects.equals(depFieldValue, "Стажировки"))
            projectRoleNameCampus = "PUR LB INTERNSHIP";

        ProjectRole projectRoleDep = projectRoleManager.getProjectRole(projectRoleNameCampus);
        ProjectRoleActors actorsHeadDep = projectRoleManager.getProjectRoleActors(projectRoleDep, project);

        return actorsHeadDep.getUsers();
    }


    public Iterable<ApplicationUser> ft_getFinContr(String campusFieldValue) {
        Project project = projectManager.getProjectObjByKey("PUR21");
        String projectRoleName = null;
        if (Objects.equals(campusFieldValue, "Москва"))
            projectRoleName = "PUR Fin Controller MSK";
        else if (Objects.equals(campusFieldValue, "Казань"))
            projectRoleName = "PUR Fin Controller KZN";
        else if (Objects.equals(campusFieldValue, "Новосибирск"))
            projectRoleName = "PUR Fin Controller NSK";
        else
            projectRoleName = "Administrators";
        ProjectRole projectRole = projectRoleManager.getProjectRole(projectRoleName);
        ProjectRoleActors finContr = projectRoleManager.getProjectRoleActors(projectRole, project);

        return finContr.getUsers();
    }

}




