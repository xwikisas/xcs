/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xwikisas.xcs.test.ui;

import java.util.List;

import org.contrib.wikiflavor.tests.po.CreateFlavoredWikiPage;
import org.contrib.wikiflavor.tests.po.Flavor;
import org.contrib.wikiflavor.tests.po.Template;
import org.contrib.wikiflavor.tests.po.WikiCreationProvisioningPage;
import org.junit.Rule;
import org.junit.Test;
import org.xwiki.test.ui.AbstractTest;
import org.xwiki.test.ui.AdminAuthenticationRule;
import org.xwiki.test.ui.po.editor.WikiEditPage;
import org.xwiki.wiki.test.po.CreateWikiPageStepProvisioning;
import org.xwiki.wiki.test.po.CreateWikiPageStepUser;
import org.xwiki.wiki.test.po.WikiHomePage;
import org.xwiki.wiki.test.po.WikiIndexPage;
import org.xwiki.wiki.test.po.WikiLink;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @version $Id: $
 */
public class WikiFlavorTest extends AbstractTest
{
    @Rule
    public AdminAuthenticationRule adminAuthenticationRule =
            new AdminAuthenticationRule(getUtil(), getDriver());

    @Test
    public void verifyInstalledFlavors() throws Exception
    {
        // Create a subwiki
        WikiIndexPage wikiIndexPage = WikiIndexPage.gotoPage();
        wikiIndexPage.createWiki();
        CreateFlavoredWikiPage createFlavoredWikiPage = new CreateFlavoredWikiPage();

        // Get the list of flavors
        List<Flavor> flavors = createFlavoredWikiPage.getFlavors();
        assertEquals(3, flavors.size());

        // Verify that the flavor is the one we just have created
        // We expect the 3 following flavors:
        Flavor standardWiki = new Flavor();
        standardWiki.setName("Standard Wiki");
        standardWiki.setDescription("Basic wiki with default applications installed");
        standardWiki.setExtensionId("com.xwikisas:xcs-ui-wiki");
        Flavor knowledgeBase = new Flavor();
        knowledgeBase.setName("Knowledge Base");
        knowledgeBase.setDescription("Allows you to collect, organize and share information about one or several topics");
        knowledgeBase.setExtensionId("com.xwikisas:xcs-ui-knowledgebase");
        Flavor workspace = new Flavor();
        workspace.setName("Workspace");
        workspace.setDescription("Features collaborative applications allowing you to collaborate on a project");
        workspace.setExtensionId("com.xwikisas:xcs-ui-workspace");
        assertTrue(flavors.contains(standardWiki));
        assertTrue(flavors.contains(knowledgeBase));
        assertTrue(flavors.contains(workspace));
    }

    @Test
    public void verifyDefaultTemplates() throws Exception
    {
        // Create a subwiki
        WikiIndexPage wikiIndexPage = WikiIndexPage.gotoPage();
        wikiIndexPage.createWiki();
        CreateFlavoredWikiPage createFlavoredWikiPage = new CreateFlavoredWikiPage();

        // Get the list of templates
        List<Template> templates = createFlavoredWikiPage.getTemplates();
        assertEquals(0, templates.size());
    }
    

    @Test
    public void createBasicWikiAndCreateWithTemplate() throws Exception
    {
        // Create a subwiki
        WikiIndexPage wikiIndexPage = WikiIndexPage.gotoPage();
        wikiIndexPage.createWiki();
        CreateFlavoredWikiPage createFlavoredWikiPage = new CreateFlavoredWikiPage();
        createFlavoredWikiPage.setPrettyName("My subwiki");
        createFlavoredWikiPage.setDescription("My subwiki which gonna be a template.");

        // Set the type of wiki we want
        createFlavoredWikiPage.setFlavorOrExtension("Standard Wiki");
        // This wiki will be a template
        createFlavoredWikiPage.setIsTemplate(true);

        // Step 2
        CreateWikiPageStepUser createWikiPageStepUser = createFlavoredWikiPage.goUserStep();
        createWikiPageStepUser.createWithTemplate();
        WikiCreationProvisioningPage wikiCreationProvisioningPage = new WikiCreationProvisioningPage();

        // Provisioning
        assertEquals("Wiki creation", wikiCreationProvisioningPage.getStepTitle());
        
        // The installation is quite long
        wikiCreationProvisioningPage.waitForFinalizeButton(60*3); // 3 minutes
        assertFalse(wikiCreationProvisioningPage.hasLogError());

        // Finalization
        WikiHomePage wikiHomePage = wikiCreationProvisioningPage.finalizeCreation();

        // Go to the created subwiki

        // Create a home page
        WikiEditPage editPage = wikiHomePage.editWiki();
        editPage.setTitle("My Template");
        editPage.clickSaveAndView();

        // Let's go to create a new subwiki
        wikiIndexPage = WikiIndexPage.gotoPage();
        wikiIndexPage.createWiki();
        createFlavoredWikiPage = new CreateFlavoredWikiPage();
        createFlavoredWikiPage.setPrettyName("My other subwiki");

        // Get the list of templates
        List<Template> templates = createFlavoredWikiPage.getTemplates();
        assertEquals(1, templates.size());

        // Verify that the template is the one we just have created
        Template myTemplate = templates.get(0);
        assertEquals("My subwiki (mysubwiki)", myTemplate.getName());
        assertEquals("My subwiki which gonna be a template.", myTemplate.getDescription());
        assertEquals("mysubwiki", myTemplate.getTemplateId());

        // Set the type of wiki we want
        createFlavoredWikiPage.setFlavorOrExtension("My subwiki (mysubwiki)");

        // Step 2
        createWikiPageStepUser = createFlavoredWikiPage.goUserStep();
        CreateWikiPageStepProvisioning createWikiPageStepProvisioning = createWikiPageStepUser.createWithTemplate();

        // Provisioning
        assertEquals("Wiki creation", createWikiPageStepProvisioning.getStepTitle());
        wikiCreationProvisioningPage.waitForFinalizeButton(60 * 2);
        assertFalse(wikiCreationProvisioningPage.hasLogError());
        wikiHomePage = createWikiPageStepProvisioning.finalizeCreation();

        // Go to the subwiki and check that it has correctly be created with the template
        assertEquals("My Template", wikiHomePage.getDocumentTitle());

        // Cleaning
        wikiHomePage.deleteWiki().confirm();
        wikiIndexPage = WikiIndexPage.gotoPage();
        wikiHomePage = wikiIndexPage.getWikiLink("My subwiki").click();
        wikiHomePage.deleteWiki().confirm();
    }

    @Test
    public void createWorkspace() throws Exception
    {
        // Create a subwiki
        WikiIndexPage wikiIndexPage = WikiIndexPage.gotoPage();
        wikiIndexPage.createWiki();
        CreateFlavoredWikiPage createFlavoredWikiPage = new CreateFlavoredWikiPage();
        createFlavoredWikiPage.setPrettyName("My Workspace");

        // Set the type of wiki we want
        createFlavoredWikiPage.setFlavorOrExtension("Workspace");
        
        // Step 2
        CreateWikiPageStepUser createWikiPageStepUser = createFlavoredWikiPage.goUserStep();
        createWikiPageStepUser.createWithTemplate();
        WikiCreationProvisioningPage wikiCreationProvisioningPage = new WikiCreationProvisioningPage();

        // Provisioning
        assertEquals("Wiki creation", wikiCreationProvisioningPage.getStepTitle());

        // The installation is quite long
        wikiCreationProvisioningPage.waitForFinalizeButton(60*3); // 3 minutes
        assertFalse(wikiCreationProvisioningPage.hasLogError());

        // Finalization
        wikiCreationProvisioningPage.finalizeCreation();

        // Go to the list of wikis
        wikiIndexPage = WikiIndexPage.gotoPage();
        WikiLink wikiLink = wikiIndexPage.getWikiLink("My Workspace");
        assertNotNull(wikiLink);
        assertEquals("My Workspace", wikiLink.getWikiName());

        // Cleaning
        wikiLink.click().deleteWiki().confirm();
    }

    @Test
    public void createKnowledgeBase() throws Exception
    {
        // Create a subwiki
        WikiIndexPage wikiIndexPage = WikiIndexPage.gotoPage();
        wikiIndexPage.createWiki();
        CreateFlavoredWikiPage createFlavoredWikiPage = new CreateFlavoredWikiPage();
        createFlavoredWikiPage.setPrettyName("My Knowledge Base");

        // Set the type of wiki we want
        createFlavoredWikiPage.setFlavorOrExtension("Knowledge Base");

        // Step 2
        CreateWikiPageStepUser createWikiPageStepUser = createFlavoredWikiPage.goUserStep();
        createWikiPageStepUser.createWithTemplate();
        WikiCreationProvisioningPage wikiCreationProvisioningPage = new WikiCreationProvisioningPage();

        // Provisioning
        assertEquals("Wiki creation", wikiCreationProvisioningPage.getStepTitle());

        // The installation is quite long
        wikiCreationProvisioningPage.waitForFinalizeButton(60*3); // 3 minutes
        assertFalse(wikiCreationProvisioningPage.hasLogError());

        // Finalization
        wikiCreationProvisioningPage.finalizeCreation();

        // Go to the list of wikis
        wikiIndexPage = WikiIndexPage.gotoPage();
        WikiLink wikiLink = wikiIndexPage.getWikiLink("My Knowledge Base");
        assertNotNull(wikiLink);
        assertEquals("My Knowledge Base", wikiLink.getWikiName());

        // Cleaning
        wikiLink.click().deleteWiki().confirm();
    }
}
