package com.bmicheal.jira.library.impl;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;

public class MyPluginComponentImpl {

    @ComponentImport
    private PageBuilderService pageBuilderService;
}
