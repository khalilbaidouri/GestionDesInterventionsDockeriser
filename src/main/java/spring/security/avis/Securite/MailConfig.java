////package spring.security.avis.Securite;
////
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.mail.javamail.JavaMailSender;
////import org.springframework.mail.javamail.JavaMailSenderImpl;
////
////import java.util.Properties;
////
////@Configuration
////public class MailConfig {
////
////    @Bean
////    public JavaMailSender javaMailSender() {
////        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
////
////        // Configuration SMTP de Gmail (à adapter si tu utilises un autre fournisseur)
////        mailSender.setHost("smtp.gmail.com");
////        mailSender.setPort(587);
////        mailSender.setUsername("khalilbaidouri2020@gmail.com");  // ton email
////        mailSender.setPassword("lwua zbfc uxwd uszp");     // mot de passe ou application password si 2FA activé
////
////        Properties props = mailSender.getJavaMailProperties();
////        props.put("mail.transport.protocol", "smtp");
////        props.put("mail.smtp.auth", "true");
////        props.put("mail.smtp.starttls.enable", "true");
////        props.put("mail.debug", "true"); // active le debug pour voir le log des mails
////
////        return mailSender;
////    }
////}
//
//
//
//package spring.security.avis.Securite;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
//import java.util.Properties;
//
//@Configuration
//public class MailConfig {
//
//    @Bean
//    public JavaMailSender javaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//
//        // Utilisation des variables d'environnement Railway
//        mailSender.setHost("smtp.gmail.com"); // Gmail SMTP
//        mailSender.setPort(587);
//        mailSender.setUsername(System.getenv("MAIL_USERNAME"));  // ex: khalilbaidouri2020@gmail.com
//        mailSender.setPassword(System.getenv("MAIL_PASSWORD"));  // mot de passe d'application Gmail
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.debug", "true"); // active le debug pour voir les logs SMTP
//
//        return mailSender;
//    }
//}
package spring.security.avis.Securite;

import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Configuration
public class MailConfig {

    @Autowired
    private JavaMailSender javaMailSender; // Spring Boot injectera la configuration depuis application.properties

    // Si tu veux un bean personnalisé, tu peux faire ceci :
    @Bean
    public JavaMailSender getJavaMailSender() {
        return javaMailSender;
    }
}
