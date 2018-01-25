package test.org.alien8.managers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import main.org.alien8.managers.LogManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

class LogManagerTest {

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		File folder = new File("logs");
		if(folder.isDirectory()) {
			if(folder.list().length == 0)
				folder.delete();
			else {
				File[] files = folder.listFiles();
				for(File f : files)
					f.delete();
				folder.delete();
			}
		}
	}

	@Test
	void testGetInstance() {
		LogManager lm = LogManager.getInstance();
		assertNotNull(lm);
	}

	@Test
	void testLog() {
		LogManager lm = LogManager.getInstance();
		for(int i = -1; i <= 7; i++)
			lm.log("Source"+i, i%5, "Test log #" + i);
	}

	@Test
	void testLogBanner() {
		LogManager lm = LogManager.getInstance();
		lm.logBanner("TEST BANNER");
	}

	@Test
	void testLogMeta() {
		LogManager lm = LogManager.getInstance();
		lm.logMeta("123456789 is a test log.");
	}
}
