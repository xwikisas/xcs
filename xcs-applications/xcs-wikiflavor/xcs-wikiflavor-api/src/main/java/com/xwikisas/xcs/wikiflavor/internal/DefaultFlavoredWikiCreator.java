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
package com.xwikisas.xcs.wikiflavor.internal;

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

import com.xwikisas.xcs.wikiflavor.WikiCreationRequest;
import com.xwikisas.xcs.wikiflavor.FlavoredWikiCreator;
import com.xwikisas.xcs.wikiflavor.WikiFlavorException;

/**
 * Default implementation for {@link com.xwikisas.xcs.wikiflavor.FlavoredWikiCreator}.
 *
 * @version $Id: $
 * @since 2015-1-M1
 */
@Component
@Singleton
public class DefaultFlavoredWikiCreator implements FlavoredWikiCreator
{
    @Inject
    private JobExecutor jobExecutor;

    @Inject
    private JobStatusStore jobStatusStore;

    @Override
    public Job createWiki(WikiCreationRequest request) throws WikiFlavorException
    {
        try {
            request.setId(getJobId(request.getWikiId()));
            return jobExecutor.execute(WikiFlavorJob.JOB_TYPE, request);
        } catch (JobException e) {
            throw new WikiFlavorException("Failed to create a new flavored wiki.", e);
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
        return Arrays.asList(WikiFlavorJob.JOB_ID_PREFIX, "createandinstall", wikiId);
    }
}
