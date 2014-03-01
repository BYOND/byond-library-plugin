package com.byond.maven.plugin.library;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.Test;

public class BasicIT {
	
	@Test
	public void testBasicPackage() throws Exception {
		File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/basic");
		assertNotNull(testDir);
        assertTrue(testDir.exists());
        Verifier verifier = new Verifier(testDir.getAbsolutePath());
        verifier.deleteDirectory("target");
        verifier.setAutoclean(false);
        verifier.executeGoal("install");
        verifier.assertArtifactPresent("com.byond.byond-library-plugin.test", "basic", "1.0", "zip");
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
	}
}
