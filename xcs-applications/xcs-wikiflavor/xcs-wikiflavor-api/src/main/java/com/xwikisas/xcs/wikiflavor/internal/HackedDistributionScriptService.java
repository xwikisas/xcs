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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.bridge.DocumentAccessBridge;
import org.xwiki.component.annotation.Component;
import org.xwiki.extension.ExtensionId;
import org.xwiki.extension.distribution.internal.DistributionScriptService;
import org.xwiki.extension.repository.CoreExtensionRepository;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.text.StringUtils;
import org.xwiki.wiki.descriptor.WikiDescriptorManager;

import com.xpn.xwiki.XWikiContext;

/**
 * Component that tells to the Distribution Wizard which extension it should install on flavored wikis.
 *
 * @version $Id: $
 * @since 2015-1-M1
 */
@Component
@Named("distribution")
@Singleton
public class HackedDistributionScriptService extends DistributionScriptService
{
    private final static String WIKI_FLAVOR_CODE_SPACE = "WikiFlavorsCode";

    /**
     * The repository with core modules provided by the platform.
     */
    @Inject
    private CoreExtensionRepository coreExtensionRepository;

    @Inject
    private DocumentAccessBridge documentAccessBridge;

    @Inject
    private WikiDescriptorManager wikiDescriptorManager;

    /**
     * @return the recommended user interface for {@link #getDistributionExtension()}
     */
    @Override
    public ExtensionId getUIExtensionId()
    {
        XWikiContext xcontext = this.xcontextProvider.get();

        // If it is the main wiki, return the main UI.
        if (xcontext.isMainWiki()) {
            return this.distributionManager.getMainUIExtensionId();
        }

        String currentWiki = wikiDescriptorManager.getCurrentWikiId();
        String extensionId = (String) documentAccessBridge.getProperty(
                new DocumentReference(currentWiki, WIKI_FLAVOR_CODE_SPACE, "MainExtensionId"),
                new DocumentReference(currentWiki, WIKI_FLAVOR_CODE_SPACE, "MainExtensionIdClass"),
                "extensionId");
        if (StringUtils.isNotEmpty(extensionId)) {
            // Todo: Here we suppose we use the same version than the wiki.
            // Todo: But it could be something else for non XCS flavors...
            return new ExtensionId(extensionId, xcontext.getWiki().getVersion());
        } else {
            // Otherwise, it is the normal extension id
            return super.getUIExtensionId();
        }
    }
}