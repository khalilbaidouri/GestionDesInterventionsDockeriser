package spring.security.avis.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.security.avis.Enum.StatutNotification;
import spring.security.avis.Enum.TypeNotification;
import spring.security.avis.entity.Notification;
import spring.security.avis.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByDestinataire(User destinataire);

    List<Notification> findByDestinataireId(Long destinataireId);

    List<Notification> findByStatut(StatutNotification statut);

    List<Notification> findByType(TypeNotification type);

    //List<Notification> findBySite(Site site);

    @Query("SELECT n FROM Notification n WHERE n.destinataire.id = :userId AND n.statut = 'NON_LUE' ORDER BY n.dateEnvoi DESC")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE n.dateEnvoi BETWEEN :debut AND :fin")
    List<Notification> findByDateEnvoiBetween(@Param("debut") LocalDateTime debut,
                                              @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.destinataire.id = :userId AND n.statut = 'NON_LUE'")
    long countUnreadByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE n.type = 'URGENCE' AND n.statut = 'NON_LUE'")
    List<Notification> findUrgentUnreadNotifications();

    @Modifying
    @Query("UPDATE Notification n SET n.statut = 'LUE', n.dateLecture = :dateLecture WHERE n.id = :id")
    void markAsRead(@Param("id") Long id, @Param("dateLecture") LocalDateTime dateLecture);

    @Modifying
    @Query("UPDATE Notification n SET n.statut = 'LUE', n.dateLecture = :dateLecture WHERE n.destinataire.id = :userId AND n.statut = 'NON_LUE'")
    void markAllAsReadForUser(@Param("userId") Long userId, @Param("dateLecture") LocalDateTime dateLecture);

    @Query("SELECT n FROM Notification n WHERE n.destinataire.id = :userId AND n.type = :type ORDER BY n.dateEnvoi DESC")
    List<Notification> findByUserIdAndType(@Param("userId") Long userId, @Param("type") TypeNotification type);
}
