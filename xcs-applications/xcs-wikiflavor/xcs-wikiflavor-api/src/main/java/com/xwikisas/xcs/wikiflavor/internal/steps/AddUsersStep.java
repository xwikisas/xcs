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
package com.xwikisas.xcs.wikiflavor.internal.steps;

import javax.inject.Inject;

import org.xwiki.wiki.user.WikiUserManager;
import org.xwiki.wiki.user.WikiUserManagerException;

import com.xwikisas.xcs.wikiflavor.WikiCreationRequest;
import com.xwikisas.xcs.wikiflavor.WikiCreationStep;
import com.xwikisas.xcs.wikiflavor.WikiFlavorException;

/**
 * Steps that add members to the wiki.
 * 
 * @version $Id: $
 * @since 2015-1-M1
 */
public class AddUsersStep implements WikiCreationStep
{
    @Inject
    private WikiUserManager wikiUserManager;

    @Override
    public void execute(WikiCreationRequest request) throws WikiFlavorException
    {
        try {
            String wikId = request.getWikiId();
            if (request.getMembers() != null) {
                wikiUserManager.addMembers(request.getMembers(), wikId);
            }
        } catch (WikiUserManagerException e) {
            throw new WikiFlavorException(
                String.format("Failed to add members to the wiki [%s]", request.getWikiId()), e);
        }
    }

    @Override
    public int getOrder()
    {
        return 4000;
    }
}
