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
package com.xwikisas.xcs.tools.dependenciespackager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Developer;
import org.apache.maven.model.License;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.extension.DefaultExtensionAuthor;
import org.xwiki.extension.DefaultExtensionDependency;
import org.xwiki.extension.ExtensionId;
import org.xwiki.extension.ExtensionLicense;
import org.xwiki.extension.ExtensionLicenseManager;
import org.xwiki.extension.LocalExtension;
import org.xwiki.extension.repository.InstalledExtensionRepository;
import org.xwiki.extension.repository.LocalExtensionRepository;
import org.xwiki.extension.repository.LocalExtensionRepositoryException;
import org.xwiki.extension.repository.internal.local.DefaultLocalExtension;
import org.xwiki.extension.version.internal.DefaultVersionConstraint;
import org.xwiki.properties.ConverterManager;

/**
 * @version $Id: $
 */
@Mojo(name = "package-extensions", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class PackageExtensionsMojo extends AbstractMojo
{
    private static final String XAR_TYPE = "xar";

    private static final String JAR_TYPE = "jar";

    private static final String JAR_DIRECTORY = "WEB-INF/lib/";

    public static final String MPKEYPREFIX = "xwiki.extension.";

    public static final String MPNAME_NAME = "name";

    public static final String MPNAME_SUMMARY = "summary";

    public static final String MPNAME_WEBSITE = "website";

    public static final String MPNAME_FEATURES = "features";

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${project.build.directory}/data/", readonly = true)
    private File xwikiDataDir;

    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession session;

    @Parameter
    private String[] excludes;

    @Parameter
    private String excludeFromWAR;

    @Parameter
    private String extensionOnMainWiki;

    @Component(role = ProjectBuilder.class)
    protected ProjectBuilder projectBuilder;

    @Component
    private DependencyGraphBuilder graphBuilder;

    private ComponentManager xwikiComponentManager = org.xwiki.environment.System.initialize();

    private LocalExtensionRepository localExtensionRepository;

    // TODO: replace this by a a method which look to the POM of the WAR
    private Collection<String> getJarsIncludedInWar() throws IOException, MojoExecutionException
    {
        Collection<String> jars = new ArrayList<>();

        // Get the XCS Web artifact
        Artifact web = mavenProject.getArtifactMap().get(excludeFromWAR);

        // Open the war and list all the jars
        ZipFile zipFile = new ZipFile(web.getFile());
        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            String entryName = entries.nextElement().getName();
            if (entryName.startsWith(JAR_DIRECTORY) && entryName.endsWith(".jar")) {
                jars.add(entryName.substring(JAR_DIRECTORY.length()));
            }
        }
        zipFile.close();

        return jars;
    }

    private boolean isJarForbidden(Artifact artifact, Collection<String> jarsIncludedInWar)
    {
        for (String jar : jarsIncludedInWar) {
            if (jar.startsWith(artifact.getArtifactId())) {
                return true;
            }
        }
        return false;
    }

    private boolean includeArtifacts(Artifact artifact, Collection<String> jarsInWar)
    {
        if (!JAR_TYPE.equals(artifact.getType()) && !XAR_TYPE.equals(artifact.getType())) {
            return false;
        }

        if (!"runtime".equals(artifact.getScope()) && !"compile".equals(artifact.getScope())) {
            return false;
        }

        if (isJarForbidden(artifact, jarsInWar)) {
            return false;
        }

        String id = String.format("%s:%s", artifact.getGroupId(), artifact.getArtifactId());
        for (String exclude : excludes) {
            if (exclude.equals(id)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void execute() throws MojoExecutionException
    {
        System.setProperty("xwiki.data.dir", this.xwikiDataDir.getAbsolutePath());

        try {
            localExtensionRepository = xwikiComponentManager.getInstance(LocalExtensionRepository.class);

            // Identify artifacts that are already bundled in the WAR
            Collection<String> jarsInWar = getJarsIncludedInWar();

            // Put all dependencies in the local repository (in /data/extensions)
            for (Artifact artifact : mavenProject.getArtifacts()) {
                if (includeArtifacts(artifact, jarsInWar)) {
                    installArtifact(artifact);
                }
            }

            // Now mark extensions used by the main wiki as installed
            Collection<Artifact> mainWikiArtifacts = getMainWikiArtifacts();
            for (Artifact artifact : mainWikiArtifacts) {
                if (includeArtifacts(artifact, jarsInWar)) {
                    registerExtensionOnMainWiki(
                            new ExtensionId(artifact.getGroupId()+":"+artifact.getArtifactId(), artifact.getVersion()));
                }
            }

        } catch (ComponentLookupException | DependencyGraphBuilderException | LocalExtensionRepositoryException
                | IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: replace this by a method which looks to the POM of the extension
    private Collection<Artifact> getMainWikiArtifacts() throws DependencyGraphBuilderException
    {
        // Get main UI
        Artifact mainWikiUI = mavenProject.getArtifactMap().get(extensionOnMainWiki);
        // If no artifact was configured for main wiki, return an empty list of artifacts to mark as installed
        if (mainWikiUI == null) {
            return Collections.<Artifact>emptyList();
        }

        ProjectBuildingRequest request = new DefaultProjectBuildingRequest();
        request.setRepositorySession(session.getRepositorySession());
        request.setProject(mavenProject);

        DependencyNode rootNode = graphBuilder.buildDependencyGraph(request, null);

        Collection<Artifact> artifactsToInstall = new ArrayList<>();
        walk(rootNode, mainWikiUI, false, artifactsToInstall);

        return artifactsToInstall;
    }

    // Walk to the tree and collect the artifacts to install
    private void walk(DependencyNode node, Artifact artifactToInclude, boolean includeMe,
            Collection<Artifact> artifactsToInstall)
    {
        boolean includeMeNow = includeMe;
        if (includeMe || artifactToInclude.equals(node.getArtifact())) {
            includeMeNow = true;
            artifactsToInstall.add(node.getArtifact());
        }

        for (DependencyNode child : node.getChildren()) {
            walk(child, artifactToInclude, includeMeNow, artifactsToInstall);
        }

    }

    private void installArtifact(Artifact artifact) throws MojoExecutionException,
            ComponentLookupException, LocalExtensionRepositoryException
    {
        MavenProject project = getMavenProject(artifact);

        // Convert the current artifact to an XWiki Extension
        DefaultLocalExtension extension = new DefaultLocalExtension(null,
                new ExtensionId(artifact.getGroupId() + ':' + artifact.getArtifactId(), artifact.getBaseVersion()),
                    artifact.getType());
        toExtension(extension, project.getModel());
        extension.setFile(artifact.getFile());

        // Install the extension
        getLog().info(String.format("Copying dependency [%s:%s, %s].", artifact.getGroupId(),
                artifact.getArtifactId(), artifact.getVersion()));
        localExtensionRepository.storeExtension(extension);
    }

    // Todo: this code comes form xwiki-platform-tool-packager-plugin. We should put it in commons.
    private MavenProject getMavenProject(Artifact artifact) throws MojoExecutionException
    {
        try {
            ProjectBuildingRequest request =
                    new DefaultProjectBuildingRequest(this.session.getProjectBuildingRequest())
                            // We don't want to execute any plugin here
                            .setProcessPlugins(false)
                            // It's not this plugin job to validate this pom.xml
                            .setValidationLevel(ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL)
                            // Use the repositories configured for the built project instead of the default Maven ones
                            .setRemoteRepositories(this.session.getCurrentProject().getRemoteArtifactRepositories());
            // Note: build() will automatically get the POM artifact corresponding to the passed artifact.
            ProjectBuildingResult result = this.projectBuilder.build(artifact, request);
            return result.getProject();
        } catch (ProjectBuildingException e) {
            throw new MojoExecutionException(String.format("Failed to build project for [%s]", artifact), e);
        }
    }

    // Todo: this code comes form xwiki-platform-tool-packager-plugin. We should put it in commons.
    private void toExtension(DefaultLocalExtension extension, Model model) throws ComponentLookupException
    {
        extension.setName(getPropertyString(model, MPNAME_NAME, model.getName()));
        extension.setSummary(getPropertyString(model, MPNAME_SUMMARY, model.getDescription()));
        extension.setWebsite(getPropertyString(model, MPNAME_WEBSITE, model.getUrl()));

        // authors
        for (Developer developer : model.getDevelopers()) {
            URL authorURL = null;
            if (developer.getUrl() != null) {
                try {
                    authorURL = new URL(developer.getUrl());
                } catch (MalformedURLException e) {
                    // TODO: log ?
                }
            }

            extension.addAuthor(new DefaultExtensionAuthor(StringUtils.defaultIfBlank(developer.getName(),
                    developer.getId()), authorURL));
        }

        // licenses
        if (!model.getLicenses().isEmpty()) {
            ExtensionLicenseManager licenseManager = xwikiComponentManager.getInstance(ExtensionLicenseManager.class);
            for (License license : model.getLicenses()) {
                extension.addLicense(getExtensionLicense(license, licenseManager));
            }
        }

        // features
        String featuresString = getProperty(model, MPNAME_FEATURES);
        if (StringUtils.isNotBlank(featuresString)) {
            featuresString = featuresString.replaceAll("[\r\n]", "");
            ConverterManager converter = xwikiComponentManager.getInstance(ConverterManager.class);
            extension.setFeatures(converter.<Collection<String>>convert(List.class, featuresString));
        }

        // dependencies
        for (Dependency mavenDependency : model.getDependencies()) {
            if (!mavenDependency.isOptional()
                    && (mavenDependency.getScope().equals("compile") || mavenDependency.getScope().equals("runtime"))) {
                extension.addDependency(new DefaultExtensionDependency(mavenDependency.getGroupId() + ':'
                        + mavenDependency.getArtifactId(), new DefaultVersionConstraint(mavenDependency.getVersion())));
            }
        }
    }

    // Todo: this code comes form xwiki-platform-tool-packager-plugin. We should put it in commons.
    private String getProperty(Model model, String propertyName)
    {
        return model.getProperties().getProperty(MPKEYPREFIX + propertyName);
    }

    // Todo: this code comes form xwiki-platform-tool-packager-plugin. We should put it in commons.
    private String getPropertyString(Model model, String propertyName, String def)
    {
        return StringUtils.defaultString(getProperty(model, propertyName), def);
    }

    // Todo: this code comes form xwiki-platform-tool-packager-plugin. We should put it in commons.
    private ExtensionLicense getExtensionLicense(License license, ExtensionLicenseManager licenseManager)
    {
        if (license.getName() == null) {
            return new ExtensionLicense("noname", null);
        }

        return createLicenseByName(license.getName(), licenseManager);
    }

    // Todo: this code comes form xwiki-platform-tool-packager-plugin. We should put it in commons.
    private ExtensionLicense createLicenseByName(String name, ExtensionLicenseManager licenseManager)
    {
        ExtensionLicense extensionLicense = licenseManager.getLicense(name);

        return extensionLicense != null ? extensionLicense : new ExtensionLicense(name, null);
    }

    // Todo: this code comes form xwiki-platform-tool-packager-plugin. We should put it in commons.
    private void registerExtensionOnMainWiki(ExtensionId extensionId)
    {
        // Register the package as extension if it's one
        try {
            LocalExtensionRepository localRepository =
                    xwikiComponentManager.getInstance(LocalExtensionRepository.class);

            LocalExtension localExtension = localRepository.getLocalExtension(extensionId);

            InstalledExtensionRepository installedRepository =
                    xwikiComponentManager.getInstance(InstalledExtensionRepository.class);

            String namespace = "wiki:xwiki";

            // Register the extension as installed
            getLog().info(String.format("Register extension [%s] to the main wiki.", extensionId.getId()));
            installedRepository.installExtension(localExtension, namespace, false);

        } catch (Exception e) {
            getLog().error(String.format("Failed to register extension [%s].", extensionId), e);
        }
    }
}
