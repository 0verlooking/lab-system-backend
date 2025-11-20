package com.example.labsystem.config;

import com.example.labsystem.domain.lab.Equipment;
import com.example.labsystem.domain.lab.EquipmentStatus;
import com.example.labsystem.domain.lab.Lab;
import com.example.labsystem.domain.user.User;
import com.example.labsystem.domain.user.UserRole;
import com.example.labsystem.repository.EquipmentRepository;
import com.example.labsystem.repository.LabRepository;
import com.example.labsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Ініціалізує базу даних тестовими даними при запуску додатку.
 * Застосовує патерн Command (CommandLineRunner).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LabRepository labRepository;
    private final EquipmentRepository equipmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Initializing test data...");

        initUsers();
        initLabs();
        initEquipment();

        log.info("Test data initialization completed!");
    }

    private void initUsers() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
            log.info("Created admin user: admin/admin123");
        }

        if (userRepository.findByUsername("student").isEmpty()) {
            User student = new User();
            student.setUsername("student");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setRole(UserRole.STUDENT);
            userRepository.save(student);
            log.info("Created student user: student/student123");
        }
    }

    private void initLabs() {
        if (labRepository.count() == 0) {
            Lab lab1 = new Lab();
            lab1.setName("Computer Lab A");
            lab1.setLocation("Building 1, Floor 2, Room 201");
            lab1.setCapacity(30);
            lab1.setDescription("Комп'ютерна лабораторія з 30 робочими місцями для програмування");
            labRepository.save(lab1);

            Lab lab2 = new Lab();
            lab2.setName("Physics Lab");
            lab2.setLocation("Building 3, Floor 1, Room 105");
            lab2.setCapacity(20);
            lab2.setDescription("Фізична лабораторія з обладнанням для проведення експериментів");
            labRepository.save(lab2);

            Lab lab3 = new Lab();
            lab3.setName("Chemistry Lab");
            lab3.setLocation("Building 2, Floor 3, Room 302");
            lab3.setCapacity(25);
            lab3.setDescription("Хімічна лабораторія з витяжними шафами та реактивами");
            labRepository.save(lab3);

            log.info("Created 3 labs");
        }
    }

    private void initEquipment() {
        if (equipmentRepository.count() == 0) {
            Lab lab1 = labRepository.findAll().get(0);

            Equipment eq1 = Equipment.builder()
                    .name("Dell OptiPlex 7090")
                    .inventoryNumber("PC-2024-001")
                    .status(EquipmentStatus.AVAILABLE)
                    .lab(lab1)
                    .build();
            equipmentRepository.save(eq1);

            Equipment eq2 = Equipment.builder()
                    .name("HP ProDesk 600")
                    .inventoryNumber("PC-2024-002")
                    .status(EquipmentStatus.AVAILABLE)
                    .lab(lab1)
                    .build();
            equipmentRepository.save(eq2);

            Equipment eq3 = Equipment.builder()
                    .name("Lenovo ThinkCentre")
                    .inventoryNumber("PC-2024-003")
                    .status(EquipmentStatus.MAINTENANCE)
                    .lab(lab1)
                    .build();
            equipmentRepository.save(eq3);

            log.info("Created 3 equipment items");
        }
    }
}
