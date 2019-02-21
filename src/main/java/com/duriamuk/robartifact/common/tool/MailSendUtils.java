package com.duriamuk.robartifact.common.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-04 20:26
 */
public class MailSendUtils {
    private static final Logger logger = LoggerFactory.getLogger(MailSendUtils.class);
    private static final String HOST;
    private static final String FROM_MAIL;
    private static final String PWD;
    private static final String FROM_NAME;
    private static final String TO_NAME;
    private static final Properties properties = new Properties();

    static {
        InputStream in = MailSendUtils.class.getClassLoader().getResourceAsStream("META-INF/conf/mail.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HOST = properties.getProperty("host");
        FROM_MAIL = properties.getProperty("fromMail");
        PWD = properties.getProperty("pwd");
        FROM_NAME = properties.getProperty("fromName");
        TO_NAME = properties.getProperty("toName");
    }

    public static void sendHtmlMessage(String toEmail, String subject, String content)
            throws MessagingException, UnsupportedEncodingException {
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.port", "465");
        Session mailSession = Session.getDefaultInstance(props);

        InternetAddress fromAddress = new InternetAddress(FROM_MAIL, FROM_NAME);
        InternetAddress toAddress = new InternetAddress(toEmail, TO_NAME);
        MimeMessage message = new MimeMessage(mailSession);
        message.setFrom(fromAddress);
        message.addRecipient(Message.RecipientType.TO, toAddress);
        message.setSentDate(new Date());
        message.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
        message.setContent(content, "text/html;charset=UTF-8");

        Transport transport = mailSession.getTransport("smtp");
        transport.connect(HOST, FROM_MAIL, PWD);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}
