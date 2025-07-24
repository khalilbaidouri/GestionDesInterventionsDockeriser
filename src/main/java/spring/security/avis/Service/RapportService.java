package spring.security.avis.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import spring.security.avis.entity.Intervention;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class RapportService {

    public byte[] generateRapportPdfFromIntervention(Intervention intervention) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();

            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Fonts
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font bodyFont = new Font(Font.FontFamily.HELVETICA, 12);

            // Titre
            document.add(new Paragraph("Rapport d'Intervention", titleFont));
            document.add(new Paragraph(" "));

            // Détails de l'intervention
            document.add(new Paragraph("Informations Générales :", subTitleFont));
            document.add(new Paragraph("ID: " + intervention.getId(), bodyFont));
            document.add(new Paragraph("Statut: " + intervention.getStatut(), bodyFont));
            if (intervention.getDateDebut() != null)
                document.add(new Paragraph("Début: " + intervention.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), bodyFont));
            if (intervention.getDateFin() != null)
                document.add(new Paragraph("Fin: " + intervention.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), bodyFont));
            document.add(new Paragraph("Lieu: " + intervention.getLocalisation(), bodyFont));
            document.add(new Paragraph("Description: " + intervention.getDescription(), bodyFont));
            document.add(new Paragraph(" "));

            // Ingénieur
            if (intervention.getIngenieur() != null) {
                document.add(new Paragraph("Ingénieur Responsable :", subTitleFont));
                document.add(new Paragraph("Nom: " + intervention.getIngenieur().getNom(), bodyFont));
                document.add(new Paragraph("Email: " + intervention.getIngenieur().getEmail(), bodyFont));
                document.add(new Paragraph(" "));
            }

            // Rapport final
            if (intervention.getRapport() != null) {
                document.add(new Paragraph("Contenu du Rapport :", subTitleFont));
                document.add(new Paragraph(intervention.getRapport().getContenu(), bodyFont));
                document.add(new Paragraph("Date Rapport: " + intervention.getRapport().getDateCreation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), bodyFont));
                document.add(new Paragraph("Statut Rapport: " + intervention.getRapport().getStatut(), bodyFont));
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport PDF", e);
        }
    }
}
