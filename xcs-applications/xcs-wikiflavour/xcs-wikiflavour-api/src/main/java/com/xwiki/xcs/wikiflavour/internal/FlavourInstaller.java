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

import java.util.Arrays;

import javax.inject.Inject;

import org.xwiki.component.annotation.Component;
import org.xwiki.extension.ExtensionId;
import org.xwiki.extension.job.InstallRequest;
import org.xwiki.extension.job.internal.InstallJob;
import org.xwiki.job.Job;
import org.xwiki.job.JobException;
import org.xwiki.job.JobExecutor;
import org.xwiki.model.reference.DocumentReference;

import com.xwiki.xcs.wikiflavour.WikiFlavourException;

/**
 * Component that install an extension on a wiki.
 *
 * @version $Id: $
 * @since 2015-1-M1
 */
@Component(roles = FlavourInstaller.class)
public class FlavourInstaller
{
    private static final String PROPERTY_USERREFERENCE = "user.reference";

    private static final DocumentReference SUPERADMIN_REFERENCE = new DocumentReference("xwiki", "XWiki", "superadmin");

    @Inject
    private JobExecutor jobExecutor;

    /**
     * Install an extension on a wiki.
     *
     * @param wikiId id of the wiki
     * @param extensionId id of the extension to install
     * @return the job of the installation
     * @throws WikiFlavourException if problem occurs
     */
    public Job installExtension(String wikiId, ExtensionId extensionId) throws WikiFlavourException
    {
        try {
            // Create the install request
            InstallRequest installRequest = new InstallRequest();
            installRequest.setId(Arrays.asList(WikiFlavourJob.JOBID_PREFIX, "install", wikiId));
            installRequest.addExtension(extensionId);
            installRequest.addNamespace("wiki:" + wikiId);
            // To avoid problem with
            installRequest.setProperty(PROPERTY_USERREFERENCE, SUPERADMIN_REFERENCE);
            // Start the job that install the extension
            return jobExecutor.execute(InstallJob.JOBTYPE, installRequest);
        } catch (JobException e) {
            throw new WikiFlavourException("Failed to install the flavour.", e);
        }
    }
}
