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
				.build();
		if (userRepo.findByEmail("ingenieur@gmail.com") == null) {
			userRepo.save(ingenieur);
		}
	}

}
