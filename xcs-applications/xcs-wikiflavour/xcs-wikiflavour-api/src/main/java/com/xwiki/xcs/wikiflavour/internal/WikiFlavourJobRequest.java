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

import java.util.List;

import org.xwiki.extension.ExtensionId;
import org.xwiki.job.AbstractRequest;

/**
 * @version $Id: $
 */
public class WikiFlavourJobRequest extends AbstractRequest
{
    /**
     * Name of the property that stores the id of the wiki to create.
     */
    public static final String PROPERTY_WIKI_ID = "wikiflavour.wikiId";

    /**
     * Name of the property that stores the alias of the wiki to create.
     */
    public static final String PROPERTY_WIKI_ALIAS = "wikiflavour.wikiAlias";

    /**
     * Name of the property that stores the id of the extension to install.
     */
    public static final String PROPERTY_EXTENSION_ID = "wikiflavour.extensionId";

    /**
     * Name of the property that stores if the job must fail when the database already exists.
     */
    public static final String PROPERTY_FAIL_ON_EXIST = "wikiflavour.failOnExist";

    /**
     * Constructor.
     *
     * @param id id of the job request
     * @param wikiId id of the wiki to create
     * @param alias alias of the wiki to create
     * @param extensionId id of the extension to install
     * @param failOnExist makes the job fail when the database already exists
     */
    public WikiFlavourJobRequest(List<String> id, String wikiId, String alias, ExtensionId extensionId,
        boolean failOnExist)
    {
        setId(id);
        setWikiId(wikiId);
        setWikiAlias(alias);
        setExtensionId(extensionId);
        setFailOnExist(failOnExist);
    }

    /**
     * @param alias alias of the wiki to provision
     */
    public void setWikiAlias(String alias)
    {
        setProperty(PROPERTY_WIKI_ALIAS, alias);
    }

    /**
     * @return the alias of the wiki to provision
     */
    public String getWikiAlias()
    {
        return getProperty(PROPERTY_WIKI_ALIAS);
    }

    /**
     * @param wikiId if of the wiki to provision
     */
    public void setWikiId(String wikiId)
    {
        setProperty(PROPERTY_WIKI_ID, wikiId);
    }

    /**
     * @return the id of the wiki to provision
     */
    public String getWikiId()
    {
        return getProperty(PROPERTY_WIKI_ID);
    }

    /**
     * @param extensionId id of the extension to install
     */
    public void setExtensionId(ExtensionId extensionId)
    {
        setProperty(PROPERTY_EXTENSION_ID, extensionId);
    }

    /**
     * @return the id of the extension to install
     */
    public ExtensionId getExtensionId()
    {
        return getProperty(PROPERTY_EXTENSION_ID);
    }

    /**
     * @param failOnExist makes the job fail if the database already exists
     */
    public void setFailOnExist(boolean failOnExist)
    {
        setProperty(PROPERTY_FAIL_ON_EXIST, failOnExist);
    }

    /**
     * @return the namespace of the extension to install
     */
    public boolean getFailOnExists()
    {
        return getProperty(PROPERTY_FAIL_ON_EXIST);
    }
}
