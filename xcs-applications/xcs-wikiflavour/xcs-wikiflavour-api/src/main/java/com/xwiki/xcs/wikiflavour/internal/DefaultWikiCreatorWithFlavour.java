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

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.extension.ExtensionId;
import org.xwiki.job.Job;
import org.xwiki.job.JobException;
import org.xwiki.job.JobExecutor;

import com.xwiki.xcs.wikiflavour.WikiCreatorWithFlavour;
import com.xwiki.xcs.wikiflavour.WikiFlavourException;

/**
 * Default implementation for {@link com.xwiki.xcs.wikiflavour.WikiCreatorWithFlavour}.
 *
 * @version $Id: $
 * @since 2015-1-M1
 */
@Component
@Singleton
public class DefaultWikiCreatorWithFlavour implements WikiCreatorWithFlavour
{
    @Inject
    private JobExecutor jobExecutor;

    @Override
    public Job createWikiWithFlavour(String wikiId, String wikiAlias, ExtensionId extensionId, boolean failOnExist)
            throws WikiFlavourException
    {
        try {
            return jobExecutor.execute(WikiFlavourJob.JOBTYPE, new WikiFlavourJobRequest(getJobId(wikiId), wikiId,
                wikiAlias, extensionId, failOnExist));
        } catch (JobException e) {
            throw new WikiFlavourException("Failed to create a new flavoured wiki.", e);
        }
    }

    @Override
    public Job getJob(String wikiId)
    {
        return jobExecutor.getJob(getJobId(wikiId));
    }

    private List<String> getJobId(String wikiId)
    {
        return Arrays.asList(WikiFlavourJob.JOBID_PREFIX, "createandinstall", wikiId);
    }
}
