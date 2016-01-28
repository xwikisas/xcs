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

import org.junit.Rule;
import org.junit.Test;
import org.xwiki.contrib.tour.test.po.PageWithTour;
import org.xwiki.test.ui.AbstractTest;
import org.xwiki.test.ui.AdminAuthenticationRule;

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
    
    @Test
    public void testMainTour() throws Exception
    {
        PageWithTour homePage = XCSPageWithTour.gotoPage("Main", "WebHome");
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
        assertTrue(homePage.getStepDescription()
                .startsWith("This allow access to your User Profile, Watchlist activators and Search."));
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
        assertTrue(homePage.getStepDescription()
                .startsWith("The Welcome message is used to display important messages."));
        assertTrue(homePage.hasNextStep());
        assertTrue(homePage.hasPreviousStep());
        assertFalse(homePage.hasEndButton());

        // Step 5
        homePage.nextStep();
        assertEquals("Wikis", homePage.getStepTitle());
        assertTrue(homePage.getStepDescription()
                .startsWith("This the list of available wikis. As Administrator you can create new wikis to"));
        assertFalse(homePage.hasNextStep());
        assertTrue(homePage.hasPreviousStep());
        assertTrue(homePage.hasEndButton());
        
        // Close it
        homePage.close();
        assertFalse(homePage.isTourDisplayed());
        assertTrue(homePage.hasResumeButton());
    }
}
