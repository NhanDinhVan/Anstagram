package Utils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import Network.UserServiceImpl;
import jakarta.websocket.Session;

public class EmailSender {

//	private static final String TEST_EMAIL = "nui20092004@gmail.com";

	public static void sendOtpEmail(String subject, String msg) throws Exception {

		NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
		Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, JSON_FACTORY))
				.setApplicationName("Test Mailer").build();

		// Encode as MIME message:
		Properties props = new Properties();
		javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, null);
		MimeMessage email = new MimeMessage(session);
		email.setFrom(new InternetAddress(UserServiceImpl.TEST_EMAIL));
		email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(UserServiceImpl.TEST_EMAIL));
		email.setSubject(subject);
		email.setText(msg);

		// Encode and wrap the MIME message into a gmail message
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		email.writeTo(buffer);
		byte[] rawMessageBytes = buffer.toByteArray();
		String encodeEmail = com.google.api.client.util.Base64.encodeBase64URLSafeString(rawMessageBytes);
		com.google.api.services.gmail.model.Message message = new com.google.api.services.gmail.model.Message();
		message.setRaw(encodeEmail);

		try {
			message = service.users().messages().send("me", message).execute();
			System.out.println("Message ID: " + message.getId());
			System.out.println(message.toPrettyString());

		} catch (GoogleJsonResponseException e) {
			GoogleJsonError error = e.getDetails();
			if (error.getCode() == 403) {
				System.err.println("Unable to send message: " + e.getDetails());
			} else {
				throw e;
			}
		}
	}

	public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, GsonFactory JSON_FACTORY)
			throws IOException {
		InputStream in = EmailSender.class.getResourceAsStream(
				"/client_secret_1016590761558-ong5rkbovbqbgvu48modi6rtjd8mnrg8.apps.googleusercontent.com.json");
		if (in == null) {
			throw new FileNotFoundException("Resource 'client_secret.json' not found in the classpath.");
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, java.util.Set.of(GmailScopes.GMAIL_SEND))
				.setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile())).setAccessType("offline")
				.build();

		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
		return credential;
	}
}
