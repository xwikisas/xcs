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
package com.xwikisas.xcs.wikiflavour.script;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.extension.ExtensionId;
import org.xwiki.job.Job;
import org.xwiki.job.event.status.JobStatus;
import org.xwiki.script.service.ScriptService;

import com.xwikisas.xcs.wikiflavour.Flavour;
import com.xwikisas.xcs.wikiflavour.WikiCreationRequest;
import com.xwikisas.xcs.wikiflavour.WikiCreatorWithFlavour;
import com.xwikisas.xcs.wikiflavour.WikiFlavourException;
import com.xwikisas.xcs.wikiflavour.WikiFlavourManager;

/**
 * Script services for the creation of flavoured wikis.
 *
 * @version $Id: $
 * @since 2015-1-M1
 */
@Component
@Singleton
@Named("wikiflavour")
public class WikiFlavourScriptServices implements ScriptService
{
    @Inject
    private WikiCreatorWithFlavour wikiCreatorWithFlavour;

    @Inject
    private WikiFlavourManager wikiFlavourManager;

    /**
     * Asynchronously create a wiki with a flavour.
     *
     * @param request creation wiki request containing all information about the wiki to create
     * @return the job that creates the wiki
     */
    public Job createWiki(WikiCreationRequest request)
    {
        try {
            if (request.getExtensionId() != null) {
                if (!isAuthorizedFlavour(request.getExtensionId())) {
                    // The extension id is not an authorized flavour, we do not install it
                    return null;
                }
            }
            return wikiCreatorWithFlavour.createWiki(request);
        } catch (WikiFlavourException e) {
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
        return wikiCreatorWithFlavour.getJobStatus(wikiId);
    }

    /**
     * @return a new request for the creation of a new wiki
     */
    public WikiCreationRequest newWikiCreationRequest()
    {
        return new WikiCreationRequest();
    }

    /**
     * @return the list of available flavours
     */
    public List<Flavour> getFlavours()
    {
        try {
            return wikiFlavourManager.getFlavours();
        } catch (WikiFlavourException e) {
            // Todo
        }

        return null;
    }

    private boolean isAuthorizedFlavour(ExtensionId extensionId) throws WikiFlavourException
    {
        for (Flavour flavour : wikiFlavourManager.getFlavours()) {
            if (flavour.getExtensionId().equals(extensionId.getId())) {
                return true;
            }
        }
        return false;
    }
}
