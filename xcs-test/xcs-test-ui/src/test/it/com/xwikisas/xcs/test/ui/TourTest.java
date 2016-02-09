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

import org.contrib.wikiflavor.tests.po.CreateFlavoredWikiPage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.xwiki.contrib.tour.test.po.PageWithTour;
import org.xwiki.test.ui.AbstractTest;
import org.xwiki.test.ui.AdminAuthenticationRule;
import org.xwiki.wiki.test.po.CreateWikiPageStepUser;
import org.xwiki.wiki.test.po.WikiCreationPage;
import org.xwiki.wiki.test.po.WikiHomePage;
import org.xwiki.wiki.test.po.WikiIndexPage;

import com.xwikisas.xcs.test.po.tour.XCSPageWithTour;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @version $Id: $
 * @since 2016-1-M1 
 */
public class TourTest extends AbstractTest
{
    @Rule
    public AdminAuthenticationRule adminAuthenticationRule = new AdminAuthenticationRule(getUtil());

    @Before
    public void setUp()
    {
        System.out.println("Browser dimmensions:" + getDriver().manage().window().getSize());
        getDriver().manage().window().setSize(new Dimension(1280, 1024));
        System.out.println("Browser dimmensions (after manual maximize()):" + getDriver().manage().window().getSize());
    }
    
    @Test
    public void testMainTour() throws Exception
    {
        PageWithTour homePage = XCSPageWithTour.gotoPage("Main", "WebHome");
        assertTrue(homePage.isTourDisplayed() || homePage.hasResumeButton());
        if (!homePage.isTourDisplayed()) {
            homePage.resume();
        }
        assertTrue(homePage.isTourDisplayed());
        
        // Step 1
        assertEquals("Welcome to XCS", homePage.getStepTitle());
        assertTrue(homePage.getStepDescription().startsWith("In the next couple of minutes we will navigate together"));
        assertTrue(homePage.hasNextStep());
        assertFalse(homePage.hasPreviousStep());
        assertFalse(homePage.hasEndButton());
        
        // Step 2
        homePage.nextStep();
        assertEquals("Wiki Header", homePage.getStepTitle());
        assertTrue(homePage.getStepDescription().startsWith(
                "This allow access to the Search feature, the Watchlist activators and your User Profile."));
        assertTrue(homePage.hasNextStep());
        assertTrue(homePage.hasPreviousStep());
        assertFalse(homePage.hasEndButton());

        // Step 3
        homePage.nextStep();
        assertEquals("Page Menu", homePage.getStepTitle());
        assertTrue(homePage.getStepDescription()
                .startsWith("This menu contains page related actions, allowing users with appropriate rights"));
        assertTrue(homePage.hasNextStep());
        assertTrue(homePage.hasPreviousStep());
        assertFalse(homePage.hasEndButton());

        // Step 4
        homePage.nextStep();
        assertEquals("Welcome message", homePage.getStepTitle());
        assertTrue(homePage.getStepDescription().startsWith(
                "The Welcome gadget is used to display important messages."));
        assertTrue(homePage.hasNextStep());
        assertTrue(homePage.hasPreviousStep());
        assertFalse(homePage.hasEndButton());

        // Step 5
        homePage.nextStep();
        assertEquals("Wikis", homePage.getStepTitle());
        assertTrue(homePage.getStepDescription()
                .startsWith("This is the list of available wikis. As Administrator you can create new wikis to"));
        assertFalse(homePage.hasNextStep());
        assertTrue(homePage.hasPreviousStep());
        assertTrue(homePage.hasEndButton());
        
        // Close it
        homePage.close();
        assertFalse(homePage.isTourDisplayed());
        assertTrue(homePage.hasResumeButton());
    }
    
    @Test
    public void testWorkspaceTour() throws Exception
    {
        WikiIndexPage wikiIndexPage = WikiIndexPage.gotoPage();
        wikiIndexPage.createWiki();

        CreateFlavoredWikiPage createFlavoredWikiPage = new CreateFlavoredWikiPage();
        createFlavoredWikiPage.setFlavor("Workspace");
        createFlavoredWikiPage.setPrettyName("Workspace");
        createFlavoredWikiPage.setDescription("My workspace.");

        // Step 2
        CreateWikiPageStepUser createWikiPageStepUser = createFlavoredWikiPage.goUserStep();
        WikiCreationPage wikiCreationPage = createWikiPageStepUser.create();

        // Provisioning
        assertEquals("Wiki creation", wikiCreationPage.getStepTitle());

        // The installation is quite long
        wikiCreationPage.waitForFinalizeButton(60*3); // 3 minutes
        assertFalse(wikiCreationPage.hasLogError());

        // Finalization
        wikiCreationPage.finalizeCreation();

        // Go to the created subwiki
        PageWithTour workspaceHomePage = new XCSPageWithTour();
        
        // Tour step 1
        assertTrue(workspaceHomePage.isTourDisplayed());
        assertEquals("Applications Bar", workspaceHomePage.getStepTitle());
        assertTrue(workspaceHomePage.getStepDescription().startsWith(
                "The Applications Bar is the place to launch existing applications found on this wiki."));

        // Tour step 2
        workspaceHomePage.nextStep();
        assertEquals("Dashboard", workspaceHomePage.getStepTitle());
        assertTrue(workspaceHomePage.getStepDescription().startsWith(
                "The Dashboard is collecting recent applications and page events from the wiki."));
        
        // Tour step 3
        workspaceHomePage.nextStep();
        assertEquals("Join Wiki", workspaceHomePage.getStepTitle());
        assertTrue(workspaceHomePage.getStepDescription().startsWith(
                "In order to have rights to create new pages and applications entries on this wiki,"));

        // Tour step 4
        workspaceHomePage.nextStep();
        assertEquals("Breadcrumb", workspaceHomePage.getStepTitle());
        assertTrue(workspaceHomePage.getStepDescription().startsWith(
                "The breadcrumb allows you to identify where you are and navigate the hierarchy."));
        
        // End tour
        workspaceHomePage.end();
        
        // Delete the wiki
        WikiHomePage.gotoPage("workspace").deleteWiki().confirm("workspace");
    }

    @Test
    public void testKnowledgeBaseTour() throws Exception
    {
        WikiIndexPage wikiIndexPage = WikiIndexPage.gotoPage();
        wikiIndexPage.createWiki();

        CreateFlavoredWikiPage createFlavoredWikiPage = new CreateFlavoredWikiPage();
        createFlavoredWikiPage.setFlavor("Knowledge Base");
        createFlavoredWikiPage.setPrettyName("KB");
        createFlavoredWikiPage.setDescription("My KB.");

        // Step 2
        CreateWikiPageStepUser createWikiPageStepUser = createFlavoredWikiPage.goUserStep();
        WikiCreationPage wikiCreationPage = createWikiPageStepUser.create();

        // Provisioning
        assertEquals("Wiki creation", wikiCreationPage.getStepTitle());

        // The installation is quite long
        wikiCreationPage.waitForFinalizeButton(60*3); // 3 minutes
        assertFalse(wikiCreationPage.hasLogError());

        // Finalization
        wikiCreationPage.finalizeCreation();

        // Go to the created subwiki
        PageWithTour kbHomePage = new XCSPageWithTour();

        // Tour step 1
        assertTrue(kbHomePage.isTourDisplayed());
        assertEquals("Knowledge Base Flavor", kbHomePage.getStepTitle());
        assertTrue(kbHomePage.getStepDescription().startsWith(
                "This Flavors allows you to share knowledge across your team members by providing means for"));

        // Tour step 2
        kbHomePage.nextStep();
        assertEquals("Navigation", kbHomePage.getStepTitle());
        assertTrue(kbHomePage.getStepDescription().startsWith(
                "The Navigation Tree displays the wiki's hierarchy, containing pages and their children."));

        // Tour step 3
        kbHomePage.nextStep();
        assertEquals("Tag Cloud", kbHomePage.getStepTitle());
        assertTrue(kbHomePage.getStepDescription().startsWith(
                "The Tag Cloud provides easy access to related pages, previously tagged."));

        // Tour step 4
        kbHomePage.nextStep();
        assertEquals("Dashboard", kbHomePage.getStepTitle());
        assertTrue(kbHomePage.getStepDescription().startsWith(
                "The Dashboard is collecting recent applications and page events from the wiki."));

        // Tour step 5
        kbHomePage.nextStep();
        assertEquals("Join Wiki", kbHomePage.getStepTitle());
        assertTrue(kbHomePage.getStepDescription().startsWith(
                "In order to have rights to create new pages and applications entries on this wiki, you need to"));

        // Tour step 6
        kbHomePage.nextStep();
        assertEquals("Breadcrumb", kbHomePage.getStepTitle());
        assertTrue(kbHomePage.getStepDescription().startsWith(
                "The breadcrumb allows you to identify where you are and navigate the hierarchy."));

        // End tour
        kbHomePage.end();

        // Delete the wiki
        WikiHomePage.gotoPage("kb").deleteWiki().confirm("kb");
    }
}
