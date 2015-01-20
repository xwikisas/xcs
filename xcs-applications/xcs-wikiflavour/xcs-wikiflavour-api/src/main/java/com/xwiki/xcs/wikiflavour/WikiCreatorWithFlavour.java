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
package com.xwiki.xcs.wikiflavour;

import org.xwiki.component.annotation.Role;
import org.xwiki.extension.ExtensionId;
import org.xwiki.job.Job;

/**
 * Component to create a wiki and install a flavour in it.
 *
 * @version $Id: $
 * @since 2015-1-M1
 */
@Role
public interface WikiCreatorWithFlavour
{
    /**
     * Start an asynchronous wiki creation filled with a flavour (an extension).
     *
     * @param wikiId id of the wiki to create
     * @param wikiAlias default alias of the wiki to create
     * @param extensionId id of the flavour (extension) to install
     * @param failOnExist throw an exception if the database already exists
     * @throws WikiFlavourException if problem occurs
     *
     * @return the job of the whole process (wiki creation + flavour installation)
     */
    Job createWikiWithFlavour(String wikiId, String wikiAlias, ExtensionId extensionId, boolean failOnExist)
        throws WikiFlavourException;

    /**
     * @param wikiId id of the wiki
     * @return the job corresponding to the creation of the wiki
     */
    Job getJob(String wikiId);
}
