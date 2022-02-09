/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.NamingException;

/**
 *
 * @author edwin
 */
@Singleton
@LocalBean
public class SfccbdmqCorreosFacade {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Resource(name = "jdni/correoCbdmq")
    private Session jmscorreo;
    private final String correo = "informar@cmdmq.gob.ec";
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    public void enviarCorreo(String correo, String motivo, String cuerpo, File pdf, File xml) throws MessagingException, UnsupportedEncodingException {
        try {
            sendMail(correo, motivo, cuerpo, pdf, xml);
        } catch (NamingException ex) {
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    public void enviarCorreoFile(String correo, String motivo, String cuerpo, File pdf) throws MessagingException, UnsupportedEncodingException {
        try {
            sendMailFile(correo, motivo, cuerpo, pdf);
        } catch (NamingException ex) {
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    public void enviarCorreo(String correo, String motivo, String cuerpo) throws MessagingException, UnsupportedEncodingException {
        try {
            sendMail(correo, motivo, cuerpo);
        } catch (NamingException ex) {
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    public void sendMail(String email, String subject, String body, File pdf, File xml)
            throws NamingException, MessagingException, UnsupportedEncodingException {
        MimeMessage message = new MimeMessage(jmscorreo);
        message.setSubject(subject);

        message.setFrom(new InternetAddress(
                "informar@cmdmq.gob.ec",
                //                "documnetos@emseguridad.gob.ec",
                subject));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
//        message.setText(body);
        Multipart multipart = new MimeMultipart();

        String textContent = "Bienvenid@";

        BodyPart htmlPart = new MimeBodyPart();
//        String htmlContent = body;
        htmlPart.setContent(body, "text/html ; charset = UTF-8");
        multipart.addBodyPart(htmlPart);

//        Archivos
        DataSource dsXml = new FileDataSource(xml);
        DataSource dsPdf = new FileDataSource(pdf);
        // esto es para el pdf 
        htmlPart = new MimeBodyPart();
        htmlPart.setDataHandler(new DataHandler(dsPdf));
        htmlPart.setFileName(pdf.getName());
        multipart.addBodyPart(htmlPart);
        // para xml
        htmlPart = new MimeBodyPart();
        htmlPart.setDataHandler(new DataHandler(dsXml));
        htmlPart.setFileName(xml.getName());
        multipart.addBodyPart(htmlPart);
        // Fin archivos

//        multipart.addBodyPart(parteXml);
        message.setContent(multipart);
        Transport.send(message);
    }

    public void sendMail(String email, String subject, String body)
            throws NamingException, MessagingException, UnsupportedEncodingException {
        MimeMessage message = new MimeMessage(jmscorreo);
        message.setSubject(subject);

        message.setFrom(new InternetAddress(
                "informar@cbdmq.gob.ec",
                subject));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
//        message.setText(body);
        Multipart multipart = new MimeMultipart("alternative");
        MimeBodyPart textPart = new MimeBodyPart();
        String textContent = "Bienvenid@";
        textPart.setText(textContent);
        MimeBodyPart htmlPart = new MimeBodyPart();
        String htmlContent = body;
        htmlPart.setContent(htmlContent, "text/html; charset = UTF-8");
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(htmlPart);
        message.setContent(multipart);
        Transport.send(message);
    }

    public void sendCalendar(String email, String subject, String calendar, Date start, Date end,
            String quienEnvia, String quienRecibe, String descripcion, String resumen)
            throws NamingException, MessagingException, UnsupportedEncodingException {
        MimeMessage message = new MimeMessage(jmscorreo);
        message.setSubject(subject);

        message.setFrom(new InternetAddress(
                "informar@cmdmq.gob.ec",
                //                "documnetos@emseguridad.gob.ec",
                subject));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
//        message.setText(body);
        Multipart multipart = new MimeMultipart("alternative");
        //part 1,
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(resumen, "text/html; charset=utf-8");
        multipart.addBodyPart(messageBodyPart);
        // Part 2 - the vCal
        BodyPart calendarPart = new MimeBodyPart();
        String vCal = buildVcalendar(start, end, quienEnvia, quienRecibe, descripcion, resumen);

        calendarPart.addHeader("Content-Class", "url:content-classes:calendarmessage");
        calendarPart.setContent(vCal, "text/calendar;method=CANCEL; charset = UTF-8");

        multipart.addBodyPart(calendarPart);

//Put the multipart in message 
        message.setContent(multipart);

//send the message
        Transport.send(message);

    }

    public void sendMailFile(String email, String subject, String body, File pdf)
            throws NamingException, MessagingException, UnsupportedEncodingException {
        MimeMessage message = new MimeMessage(jmscorreo);
        message.setSubject(subject);

        message.setFrom(new InternetAddress(
                correo,
                subject));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
        Multipart multipart = new MimeMultipart();
        String textContent = "Bienvenid@";
        BodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(body, "text/html ; charset = ISO-8859-1");
        multipart.addBodyPart(htmlPart);
        DataSource dsPdf = new FileDataSource(pdf);
        // esto es para el pdf 
        htmlPart = new MimeBodyPart();
        htmlPart.setDataHandler(new DataHandler(dsPdf));
        htmlPart.setFileName(pdf.getName());
        multipart.addBodyPart(htmlPart);

        message.setContent(multipart);
        Transport.send(message);
    }

    private static SimpleDateFormat iCalendarDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmm'00'");

    private static String buildVcalendar(Date start, Date end, String quienEnvia, String quienRecibe, String descripcion, String resumen) {

        String calendarContent
                = "BEGIN:VCALENDAR\n"
                + "METHOD:PUBLISH\n"
                + "PRODID: //Microsoft Corporation//Outlook 14.0 MIMEDIR//EN\n"
                + "VERSION:2.0\n"
                + "BEGIN:VEVENT\n"
                + "DTSTAMP:" + iCalendarDateFormat.format(start) + "\n"
                + "DTSTART:" + iCalendarDateFormat.format(start) + "\n"
                + "DTEND:" + iCalendarDateFormat.format(end) + "\n"
                + "SUMMARY:" + resumen + "\n"
                + "UID:324567548245636767834564768564\n"
                + "ATTENDEE;ROLE=REQ-PARTICIPANT;PARTSTAT=ACCEPTED;RSVP=FALSE:MAILTO:" + quienRecibe + "\n"
                + "ORGANIZER:MAILTO:" + quienEnvia + "\n"
                + "DESCRIPTION:" + descripcion + "\n"
                + "SEQUENCE:0\n"
                + "PRIORITY:0\n"
                + "CLASS:PUBLIC\n"
                + "STATUS:CONFIRMED\n"
                + "TRANSP:OPAQUE\n"
                + "BEGIN:VALARM\n"
                + "ACTION:DISPLAY\n"
                + "DESCRIPTION:" + descripcion + "\n"
                + "TRIGGER;RELATED=START:-PT00H15M00S\n"
                + "END:VALARM\n"
                + "END:VEVENT\n"
                + "END:VCALENDAR";

        return calendarContent;
    }
}