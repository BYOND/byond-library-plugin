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

/**
 * Packages the target folder up as a BYOND DM library, with extra information required
 * to assist Dream Maker, and non-maven compilers.
 * 
 * @author Stephen001
 */
@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
public class PackageMojo extends AbstractMojo {
	private static final String[] DEFAULT_EXCLUDES = new String[] {};
	private static final String[] DEFAULT_INCLUDES = new String[] { "**/**" };
	
	@Component
	private ZipArchiver archiver;
	
	/**
	 * The directory we will be archiving up.
	 */
	@Parameter(defaultValue = "${project.build.directory}", required = true)
	private File dmFilesDirectory;

	/**
	 * A list of paths or patterns to exclude from the DM library archive. By default,
	 * nothing is excluded.
	 */
	@Parameter
	private String[] excludes;

	/**
	 * The final name of the archive.
	 */
	@Parameter(property = "finalName", defaultValue = "${project.build.finalName}", required = true)
	private String finalName;

	/**
	 * A list of paths or patterns to include from the DM library archive. By default,
	 * everything is included.
	 */
	@Parameter
	private String[] includes;
	
	@Component
	private MavenProject project;

	/**
	 * The output directory to write the archive to.
	 */
	@Parameter(defaultValue = "${project.build.directory}", required = true)
	private File outputDirectory;

	/**
	 * Executes this mojo, producing a DM library archive and setting it as the
	 * primary artifact of the project.
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		File dmLibraryFile = createArchive();
		if (dmLibraryFile.exists()) {
			project.getArtifact().setFile(dmLibraryFile);
		}
	}
	
	/**
	 * Creates a DM library archive from the provided target directory.
	 * 
	 * @return The (potentially empty) DM library archive.
	 * @throws MojoExecutionException If the archive could not be written.
	 */
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

	/**
	 * Returns a file representing the archive file we intend to create.
	 * 
	 * @return A file representing the archive file we intend to create.
	 */
	protected File getArtifactFile() {
		return new File(outputDirectory, finalName + ".zip");
	}
	
	/**
	 * Gets the list of exclude paths/patterns. If not defined, it defaults to nothing.
	 * 
	 * @return A list of exclude paths/patterns.
	 */
	protected String[] getExcludes() {
		if (excludes != null && excludes.length > 0) {
			return excludes;
		}
		return DEFAULT_EXCLUDES;
	}

	/**
	 * Gets the list of include paths/patterns. If not defined, it defaults to everything.
	 * 
	 * @return A list of include paths/patterns.
	 */
	protected String[] getIncludes() {
		if (includes != null && includes.length > 0) {
			return includes;
		}
		return DEFAULT_INCLUDES;
	}
}
