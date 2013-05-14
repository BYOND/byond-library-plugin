package com.byond.maven.plugin.library;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        List<String> cliOptions = new ArrayList<String>();
        cliOptions.add("-N");
        verifier.executeGoal("package");
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
	}
}
