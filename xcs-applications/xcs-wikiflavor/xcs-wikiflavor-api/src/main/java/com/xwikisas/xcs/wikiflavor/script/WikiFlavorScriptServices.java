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
package com.xwikisas.xcs.wikiflavor.script;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.extension.ExtensionId;
import org.xwiki.job.Job;
import org.xwiki.job.event.status.JobStatus;
import org.xwiki.script.service.ScriptService;

import com.xwikisas.xcs.wikiflavor.Flavor;
import com.xwikisas.xcs.wikiflavor.WikiCreationRequest;
import com.xwikisas.xcs.wikiflavor.FlavoredWikiCreator;
import com.xwikisas.xcs.wikiflavor.WikiFlavorException;
import com.xwikisas.xcs.wikiflavor.WikiFlavorManager;

/**
 * Script services for the creation of flavored wikis.
 *
 * @version $Id: $
 * @since 2015-1-M1
 */
@Component
@Singleton
@Named("wikiflavor")
public class WikiFlavorScriptServices implements ScriptService
{
    @Inject
    private FlavoredWikiCreator flavoredWikiCreator;

    @Inject
    private WikiFlavorManager wikiFlavorManager;

    /**
     * Asynchronously create a wiki with a flavor.
     *
     * @param request creation wiki request containing all information about the wiki to create
     * @return the job that creates the wiki
     */
    public Job createWiki(WikiCreationRequest request)
    {
        try {
            if (request.getExtensionId() != null) {
                if (!isAuthorizedFlavor(request.getExtensionId())) {
                    // The extension id is not an authorized flavor, we do not install it
                    return null;
                }
            }
            return flavoredWikiCreator.createWiki(request);
        } catch (WikiFlavorException e) {
            // Todo
        }

        return null;
    }

    /**
     * @param wikiId id of the wiki
     * @return the job status corresponding to the creation of the wiki
     */
    public JobStatus getJobStatus(String wikiId)
    {
        return flavoredWikiCreator.getJobStatus(wikiId);
    }

    /**
     * @return a new request for the creation of a new wiki
     */
    public WikiCreationRequest newWikiCreationRequest()
    {
        return new WikiCreationRequest();
    }

    /**
     * @return the list of available flavors
     */
    public List<Flavor> getFlavors()
    {
        try {
            return wikiFlavorManager.getFlavors();
        } catch (WikiFlavorException e) {
            // Todo
        }

        return null;
    }

    private boolean isAuthorizedFlavor(ExtensionId extensionId) throws WikiFlavorException
    {
        for (Flavor flavor : wikiFlavorManager.getFlavors()) {
            if (flavor.getExtensionId().equals(extensionId.getId())) {
                return true;
            }
        }
        return false;
    }
}
