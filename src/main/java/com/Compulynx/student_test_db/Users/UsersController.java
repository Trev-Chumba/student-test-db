package com.Compulynx.student_test_db.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
public class UsersController {
    @Autowired
    UsersRepository usersRepository;

    @Autowired
    AuthService authService;

    @CrossOrigin
    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<String> login(@RequestBody UsersWrapper usersWrapper ){
        String userName = usersWrapper.getUserName();
        String password = usersWrapper.getPassword();

        Users user = usersRepository.findByUserName(userName);

        if (user != null && user.getPassword().equals(password))
        {
            String token = authService.generateToken(userName);
            return ResponseEntity.ok().body("{\"message\": \"Logged in successfully\", \"token\": \"" + token + "\"}");

        }
        else {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"" + "Invalid Credentials" + "\"}");
        }
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<String> verifyToken(@RequestBody String token)
    {
        Boolean isValid = authService.validateToken(token);

        if (isValid)
        {
            return ResponseEntity.ok().body("{\"message\": \"Valid Token\", \"token\": \"" + token + "\"}");
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"" + "Invalid Credentials" + "\"}");
        }
    }
}
