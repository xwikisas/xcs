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
package com.xwiki.xcs.wikiflavour.script;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.extension.ExtensionId;
import org.xwiki.job.Job;
import org.xwiki.script.service.ScriptService;

import com.xwiki.xcs.wikiflavour.WikiCreatorWithFlavour;
import com.xwiki.xcs.wikiflavour.WikiFlavourException;

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

    /**
     * Asynchronously create a wiki with a flavour.
     *
     * @param wikiId id of the wiki to create
     * @param wikiAlias alias of the wiki to create
     * @param extensionId id of the extension to install
     * @param extensionVersion version of the extension to install
     * @param failOnExist fail the wiki creation if a database called wikiId already exists
     * @return the job
     */
    public Job createWikiWithFlavour(String wikiId, String wikiAlias, String extensionId, String extensionVersion,
        boolean failOnExist)
    {
        try {
            return wikiCreatorWithFlavour.createWikiWithFlavour(wikiId, wikiAlias,
                new ExtensionId(extensionId, extensionVersion), failOnExist);
        } catch (WikiFlavourException e) {
            // Todo
        }

        return null;
    }

    /**
     * @param wikiId id of the wiki
     * @return the job corresponding to the creation of the wiki
     */
    public Job getJob(String wikiId)
    {
        return wikiCreatorWithFlavour.getJob(wikiId);
    }
}
