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
package com.xwikisas.xcs.test.po.tour;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.xwiki.contrib.tour.test.po.PageWithTour;

/**
 * Extends {@link PageWithTour} from the tour application page objects to fix broken methods (their dependencies version
 * are different).
 *
 * TODO: Remove this class when it becomes not needed.
 *
 * @version $Id: $
 * @since 0.2
 */
public class XCSPageWithTour extends PageWithTour
{
    public static XCSPageWithTour gotoPage(String space, String page)
    {
        getUtil().gotoPage(space, page);
        return new XCSPageWithTour();
    }
    
    @Override
    public boolean isTourDisplayed()
    {
        return getDriver().hasElement(By.className("tour"));
    }

    @Override
    public String getStepTitle()
    {
        // It's actually the same code than the superclass, but we need to recompile it because getDriver() have not
        // the same signature in 7.4.x than in 6.4.x.
        return getDriver().findElement(By.className("popover-title")).getText();
    }

    @Override
    public String getStepDescription()
    {
        // It's actually the same code than the superclass, but we need to recompile it because getDriver() have not
        // the same signature in 7.4.x than in 6.4.x.
        return getDriver().findElement(By.className("popover-content")).getText();
    }
    
    @Override
    public boolean hasPreviousStep()
    {
        return getDriver().hasElementWithoutWaiting(By.xpath("//button[@data-role='prev']"));
    }

    @Override
    public boolean hasNextStep()
    {
        return getDriver().hasElementWithoutWaiting(By.xpath("//button[@data-role='next']"));
    }

    @Override
    public boolean hasEndButton()
    {
        return getDriver().hasElementWithoutWaiting(
                By.xpath("//div[contains(@class, 'popover-navigation')]//button[@data-role='end']"));
    }

    private String getStepId()
    {
        return getDriver().findElement(By.className("tour")).getAttribute("id");
    }

    @Override
    public void previousStep()
    {
        // Get the current step id
        String stepId = getStepId();
        // Click
        getDriver().findElement(By.xpath("//button[@data-role='prev']")).click();
        // Wait until current state disappears
        getDriver().waitUntilCondition(
                ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(stepId))));
        // Wait until new step appears
        getDriver().waitUntilCondition(ExpectedConditions.presenceOfElementLocated(By.className("tour")));
    }

    @Override
    public void nextStep()
    {
        // Get the current step id
        String stepId = getStepId();
        // Click
        getDriver().findElement(By.xpath("//button[@data-role='next']")).click();
        // Wait until current state disappears
        getDriver().waitUntilCondition(
                ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(stepId))));
        // Wait until new step appear
        getDriver().waitUntilCondition(ExpectedConditions.presenceOfElementLocated(By.className("tour")));
    }

    @Override
    public void end()
    {
        getDriver().findElement(
                By.xpath("//div[contains(@class, 'popover-navigation')]//button[@data-role='end']")).click();
        getDriver().waitUntilCondition(
                ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("tour"))));
    }

    @Override
    public void close()
    {
        getDriver().findElement(
                By.xpath("//button[@data-role='end' and contains(@class, 'btn-default')]")).click();
        getDriver().waitUntilCondition(
                ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("tour"))));
    }
    
    @Override
    public boolean hasResumeButton()
    {
        return getDriver().hasElementWithoutWaiting(By.id("tourResume"));
    }

    @Override
    public void resume()
    {
        getDriver().findElement(By.id("tourResume")).click();
        getDriver().waitUntilCondition(ExpectedConditions.presenceOfElementLocated(By.className("tour")));
    }
}
