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
package com.xwikisas.xcs.wikiflavour.internal;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;
import org.xwiki.security.authorization.AuthorizationManager;
import org.xwiki.security.authorization.Right;
import org.xwiki.wiki.descriptor.WikiDescriptorManager;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xwikisas.xcs.wikiflavour.Flavour;
import com.xwikisas.xcs.wikiflavour.WikiFlavourException;
import com.xwikisas.xcs.wikiflavour.WikiFlavourManager;

/**
 * Default implementation for {@link com.xwikisas.xcs.wikiflavour.WikiFlavourManager}.
 *
 * @version $Id: $
 * @since 2015-1-M1
 */
@Component
@Singleton
public class DefaultWikiFlavourManager implements WikiFlavourManager
{
    @Inject
    private QueryManager queryManager;

    @Inject
    private WikiDescriptorManager wikiDescriptorManager;

    @Inject
    private AuthorizationManager authorizationManager;

    @Inject
    private Provider<XWikiContext> xcontextProvider;

    @Inject
    private Logger logger;

    @Override
    public List<Flavour> getFlavours() throws WikiFlavourException
    {
        List<Flavour> results = new ArrayList<>();
        try {
            String mainWikiId = wikiDescriptorManager.getMainWikiId();
            XWikiContext xcontext = xcontextProvider.get();
            XWiki xwiki = xcontext.getWiki();

            // Query to get all flavour documents
            Query query = queryManager.createQuery(
                    "SELECT obj.extensionId, doc.name, obj.nameTranslationKey, obj.descriptionTranslationKey, obj.icon "
                    + "FROM Document doc, doc.object(WikiFlavoursCode.WikiFlavoursClass) obj "
                    + "WHERE doc.space = 'WikiFlavours'",
                    Query.XWQL).setWiki(mainWikiId);
            for (Object[] result : query.<Object[]>execute()) {
                Flavour flavour = new Flavour((String)result[0], (String)result[1], (String)result[2],
                        (String) result[3], (String) result[4]);

                // Check that the flavour document have been saved with programming right
                DocumentReference documentReference = new DocumentReference(mainWikiId, "WikiFlavours",
                    flavour.getName());
                try {
                    XWikiDocument document = xwiki.getDocument(documentReference, xcontext);
                    if (authorizationManager.hasAccess(Right.PROGRAM, document.getAuthorReference(),
                            new WikiReference(mainWikiId))) {
                        results.add(flavour);
                    }
                } catch (XWikiException e) {
                    logger.warn("Unable to read the Wiki Flavour Document [{}]", documentReference, e);
                }
            }

        } catch (QueryException e) {
            String errorMessage = "Failed to get the list of the flavours.";
            logger.error(errorMessage, e);
            throw new WikiFlavourException(errorMessage, e);
        }

        return results;
    }
}
