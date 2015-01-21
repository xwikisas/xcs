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
import org.xwiki.job.Job;
import org.xwiki.job.JobException;
import org.xwiki.job.JobExecutor;
import org.xwiki.job.JobStatusStore;
import org.xwiki.job.event.status.JobStatus;

import com.xwiki.xcs.wikiflavour.WikiCreationRequest;
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

    @Inject
    private JobStatusStore jobStatusStore;

    @Override
    public Job createWiki(WikiCreationRequest request) throws WikiFlavourException
    {
        try {
            request.setId(getJobId(request.getWikiId()));
            return jobExecutor.execute(WikiFlavourJob.JOB_TYPE, request);
        } catch (JobException e) {
            throw new WikiFlavourException("Failed to create a new flavoured wiki.", e);
        }
    }

    @Override
    public JobStatus getJobStatus(String wikiId)
    {
        List<String> jobId = getJobId(wikiId);
        Job job = jobExecutor.getJob(jobId);
        if (job != null) {
            return job.getStatus();
        } else {
            return jobStatusStore.getJobStatus(jobId);
        }
    }

    private List<String> getJobId(String wikiId)
    {
        return Arrays.asList(WikiFlavourJob.JOB_ID_PREFIX, "createandinstall", wikiId);
    }
}
