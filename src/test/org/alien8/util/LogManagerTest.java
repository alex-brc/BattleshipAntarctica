package test.org.alien8.util;

import java.io.FileWriter;
import java.io.IOException;

import org.alien8.util.LogManager;
import org.junit.Test;

public class LogManagerTest {

	@Test
	public void testLog() throws IOException {
		// Test general usage
		for(LogManager.Scope scope : LogManager.Scope.values())
			LogManager.getInstance().log("TEST", scope, "TEST_LOG");
		
		// Test for IOException
		FileWriter blocker = new FileWriter(LogManager.getFilePath());
		blocker.write("BLOCKING");
		LogManager.getInstance().log("TEST", LogManager.Scope.CRITICAL, "TEST_LOG");
		blocker.close();
	}

	@Test
	public void testLogBanner() throws IOException {
		// Test general usage
		LogManager.getInstance().logBanner("TEST_BANNER");
		
		// Test for IOException
		FileWriter blocker = new FileWriter(LogManager.getFilePath());
		blocker.write("BLOCKING");
		LogManager.getInstance().logBanner("TEST_BANNER");
		blocker.close();
	}

	@Test
	public void testLogMeta() throws IOException {
		// Test general usage
		LogManager.getInstance().logMeta("TEST___________META");

		// Test for IOException	
		FileWriter blocker = new FileWriter(LogManager.getFilePath());
		blocker.write("BLOCKING");
		LogManager.getInstance().logMeta("TEST___________META");
		blocker.close();
	}

}
