package com.byond.maven.plugin.library;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
public class PackageMojo extends AbstractMojo {
	private static final String[] DEFAULT_EXCLUDES = new String[] {};
	private static final String[] DEFAULT_INCLUDES = new String[] { "**/**" };
	
	@Component
	private ZipArchiver archiver;
	
	@Parameter(defaultValue = "${project.build.directory}", required = true)
	private File dmFilesDirectory;

	@Parameter
	private String[] excludes;

	@Parameter(property = "finalName", defaultValue = "${project.build.finalName}", required = true)
	private String finalName;

	@Parameter
	private String[] includes;
	
	@Component
	private MavenProject project;

	@Parameter(defaultValue = "${project.build.directory}", required = true)
	private File outputDirectory;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		File dmLibraryFile = createArchive();
		if (dmLibraryFile.exists()) {
			project.getArtifact().setFile(dmLibraryFile);
		}
	}
	
	protected File createArchive() throws MojoExecutionException {
		File libraryFile = getArtifactFile();
		archiver.setDestFile(libraryFile);
		if (dmFilesDirectory.exists()) {
			archiver.addDirectory(dmFilesDirectory, getIncludes(), getExcludes());
		}
		try {
			archiver.createArchive();
		} catch (IOException e) {
			throw new MojoExecutionException("Could not create DM library archive", e);
		}
		return libraryFile;
	}

	protected File getArtifactFile() {
		return new File(outputDirectory, finalName + ".zip");
	}
	
	protected String[] getExcludes() {
		if (excludes != null && excludes.length > 0) {
			return excludes;
		}
		return DEFAULT_EXCLUDES;
	}

	protected String[] getIncludes() {
		if (includes != null && includes.length > 0) {
			return includes;
		}
		return DEFAULT_INCLUDES;
	}
}
