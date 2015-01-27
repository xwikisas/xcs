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
import javax.inject.Named;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.InstantiationStrategy;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;
import org.xwiki.wiki.manager.WikiManagerException;
import org.xwiki.wiki.internal.manager.WikiCopier;

import com.xwikisas.xcs.wikiflavor.WikiCreationRequest;
import com.xwikisas.xcs.wikiflavor.WikiCreationStep;
import com.xwikisas.xcs.wikiflavor.WikiFlavorException;
import com.xwikisas.xcs.wikiflavor.internal.ExtensionInstaller;

/**
 * Step that provision a new empty wiki with a template or an extension.
 *
 * @version $Id: $
 * @since 2015-1-M1
 */
@Component
@InstantiationStrategy(ComponentInstantiationStrategy.PER_LOOKUP)
@Named("provision")
public class ProvisionWikiStep implements WikiCreationStep
{
    @Inject
    private WikiCopier wikiCopier;

    @Inject
    private ExtensionInstaller extensionInstaller;

    @Override
    public void execute(WikiCreationRequest request) throws WikiFlavorException
    {
        try {
            switch (request.getWikiSource()) {
                case EXTENSION:
                    extensionInstaller.installExtension(request.getWikiId(), request.getExtensionId());
                    break;
                case TEMPLATE:
                    wikiCopier.copyDocuments(request.getTemplateId(), request.getWikiId(), false);
                    break;
                default:
                    // No source is defined, we let the wiki empty and DW will do the rest
                    break;
            }
        } catch (WikiManagerException|WikiFlavorException e) {
            throw new WikiFlavorException(String.format("Failed to provision the wiki [%s]", request.getWikiId()), e);
        }
    }

    @Override
    public int getOrder()
    {
        return 3000;
    }
}
