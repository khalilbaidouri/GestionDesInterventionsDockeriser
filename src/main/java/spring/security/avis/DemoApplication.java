package spring.security.avis;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import spring.security.avis.Enum.TypeRole;
import spring.security.avis.Repo.UserRepo;
import spring.security.avis.entity.Role;
import spring.security.avis.entity.User;

@SpringBootApplication
@EnableScheduling
@AllArgsConstructor
public class DemoApplication implements CommandLineRunner {
	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Role adminRole = Role.builder().libelle(TypeRole.ADMINISTRATEUR).build();
		User admin = User.builder()
				.active(true)
				.nom("admin")
				.prenom("admin")
				.password(passwordEncoder.encode("admin"))
				.email("admin@gmail.com")
				.role(adminRole)
				.matricule("a23182010")

				.build();
		if (userRepo.findByEmail("admin@gmail.com") == null) {
			userRepo.save(admin);
		}

		Role managerRole = Role.builder().libelle(TypeRole.CHEF_DE_DEPARTEMENT).build();
		User manager = User.builder()
				.active(true)
				.nom("manager")
				.prenom("manager")
				.password(passwordEncoder.encode("manager"))
				.email("manager@gmail.com")
				.role(managerRole)
				.matricule("a123456789")
				.build();
		if (userRepo.findByEmail("manager@gmail.com") == null) {
			userRepo.save(manager);
		}

		Role ingenieurRole = Role.builder().libelle(TypeRole.INGENIEUR).build();
		User ingenieur = User.builder()
				.active(true)
				.nom("ingenieur")
				.prenom("ingenieur")
				.password(passwordEncoder.encode("ingenieur"))
				.email("ingenieur@gmail.com")
				.role(ingenieurRole)
				.matricule("a329010923")
				.build();
		if (userRepo.findByEmail("ingenieur@gmail.com") == null) {
			userRepo.save(ingenieur);
		}
		Role chefDepartemantRole = Role.builder().libelle(TypeRole.CHEF_DE_DEPARTEMENT).build();
		User chefDepartemant = User.builder()
				.active(true)
				.nom("chefDepartemant")
				.prenom("chefDepartemant")
				.password(passwordEncoder.encode("chefDepartemant"))
				.email("chefDepartemant@gmail.com")
				.role(chefDepartemantRole)
				.matricule("a21830231")
				.build();
		if (userRepo.findByEmail("chefDepartemant@gmail.com") == null) {
			userRepo.save(chefDepartemant);
		}
	}

}
