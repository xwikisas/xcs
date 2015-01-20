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
package com.xwiki.xcs.wikiflavour.internal;

import javax.inject.Inject;
import javax.inject.Named;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.InstantiationStrategy;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;
import org.xwiki.job.event.status.PopLevelProgressEvent;
import org.xwiki.job.event.status.PushLevelProgressEvent;
import org.xwiki.job.event.status.StepProgressEvent;
import org.xwiki.job.internal.AbstractJob;
import org.xwiki.job.internal.DefaultJobStatus;
import org.xwiki.wiki.manager.WikiManager;
import org.xwiki.wiki.manager.WikiManagerException;

import com.xwiki.xcs.wikiflavour.WikiFlavourException;

/**
 * Job that create a wiki and install an extension in it.
 *
 * @version $Id: $
 * @since 2015-1-M1
 */
@Component
@InstantiationStrategy(ComponentInstantiationStrategy.PER_LOOKUP)
@Named(WikiFlavourJob.JOBTYPE)
public class WikiFlavourJob extends AbstractJob<WikiFlavourJobRequest, DefaultJobStatus<WikiFlavourJobRequest>>
{
    /**
     * The prefix put behind all job ids.
     */
    public static final String JOBID_PREFIX = "wikiflavour";

    public static final String JOBTYPE = "wikiflavourjob";

    @Inject
    private WikiManager wikiManager;

    @Inject
    private FlavourInstaller flavourInstaller;

    @Override
    protected void runInternal() throws Exception
    {
        WikiFlavourJobRequest request = getRequest();
        try {
            // There is 2 steps:
            // 1 - the creation of the wiki
            // 2 - the installation of the extension
            observationManager.notify(new PushLevelProgressEvent(2), this);

            // 1: create the wiki
            wikiManager.create(request.getWikiId(), request.getWikiAlias(), request.getFailOnExists());
            observationManager.notify(new StepProgressEvent(), this);

            // 2: install the extension
            flavourInstaller.installExtension(request.getWikiId(), request.getExtensionId()).join();

            // The job is done
            observationManager.notify(new PopLevelProgressEvent(), this);

        } catch (WikiManagerException e) {
            // Do something
        } catch (WikiFlavourException e) {
            // Do something too
        }
    }

    @Override
    public String getType()
    {
        return JOBTYPE;
    }
}
