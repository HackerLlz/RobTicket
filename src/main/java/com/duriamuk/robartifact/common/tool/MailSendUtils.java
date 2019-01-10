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

    private static final Properties properties = new Properties();

    static {
        InputStream in = MailSendUtils.class.getClassLoader().getResourceAsStream("static/mail.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendHtmlMessage(String to, String subject, String content)
                    throws MessagingException, UnsupportedEncodingException {
        String host = properties.getProperty("host");
        String from = properties.getProperty("from");
        String pwd = properties.getProperty("pwd");
        String fromName = properties.getProperty("fromName");
        String toName = properties.getProperty("toName");

        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");

        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
        "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.port", "465");
        Session mailSession = Session.getDefaultInstance(props);

        InternetAddress fromAddress = new InternetAddress(from, fromName);
        InternetAddress toAddress = new InternetAddress(to, toName);

        MimeMessage message = new MimeMessage(mailSession);
        message.setFrom(fromAddress);
        message.addRecipient(Message.RecipientType.TO, toAddress);
        message.setSentDate(new Date());
        message.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
        message.setContent(content, "text/html;charset=UTF-8");

        Transport transport = mailSession.getTransport("smtp");
        transport.connect(host, from, pwd);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}
