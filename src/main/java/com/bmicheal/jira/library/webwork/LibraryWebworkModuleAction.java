package com.bmicheal.jira.library.webwork;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.web.action.JiraWebActionSupport;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class LibraryWebworkModuleAction extends JiraWebActionSupport
{
    private static final Logger log = LoggerFactory.getLogger(LibraryWebworkModuleAction.class);

    @Inject
    private PageBuilderService pageBuilderService;

    @Override
    public String execute() throws Exception {
        pageBuilderService.assembler().resources().requireWebResource(
                "com.bmicheal.jira.library.jira-library:jira-library2-resources"
        ).requireWebResource(
                "com.bmicheal.jira.library.jira-library:jira-library2-resources--main-page"
        );

        return "success";
    }

    public void setPageBuilderService(PageBuilderService pageBuilderService) {
        this.pageBuilderService = pageBuilderService;
    }
}
