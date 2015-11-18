/**
 * 
 */
package com.github.ansell.javaemailtest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * Test of Java email functionality using Commons Email.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ArducropEmailTest {

	private static final long LIMIT = 10000;

	public static void main(String... args)
		throws Exception
	{
		Path sourceDir = Paths.get("/", "home", "peter", "Downloads", "ArduCrop 3G v2_CottonInfo Data 2014");
		Files.list(sourceDir).forEach(p -> {
			try {
				final String string = p.getFileName().toString();
				String pId = string.substring(string.indexOf("_") + 1);
				pId = pId.substring(0, pId.indexOf(".TXT"));
				System.out.println(p + " -> " + pId);
				List<String> allLines = Files.readAllLines(p);

				// Split the long files based on lines to ensure that email does not
				// reject them for being too large
				for (long i = 0; i < allLines.size(); i += LIMIT) {

					System.getProperties().setProperty("mail.smtp.port", "25");
					Email email = new SimpleEmail();
					email.setHostName("smtp.csiro.au");
					email.setSmtpPort(25);
					email.setFrom("peter.ansell@csiro.au");
					email.setSubject("PHEN_ID=" + pId);
					String nextMessage = allLines.stream().skip(i).limit(LIMIT).collect(Collectors.joining("\r\n"));
					System.out.println(nextMessage.length());
					if (i == 0) {
						// System.out.println(nextMessage);
					}
					email.setMsg(nextMessage);
					email.addTo("3G@arducrop.org");
					email.send();
				}
			}
			catch (IOException | EmailException e) {
				throw new RuntimeException(e);
			}
		});
	}

}
