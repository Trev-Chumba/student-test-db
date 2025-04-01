package com.Compulynx.student_test_db.Users;

import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;

@Component
public class UserConfig implements CommandLineRunner {

    private final UsersRepository usersRepository;

    public UserConfig(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public void run(String... args) {
        if(usersRepository.count() == 0)
        {
            usersRepository.save(new Users("user1", "user@2025"));
            usersRepository.save(new Users("user2", "user2@2025"));
            usersRepository.save(new Users("user3", "user3@2025"));
        }
        else {
            System.out.println("Database already contains users.");
        }
    }
}
