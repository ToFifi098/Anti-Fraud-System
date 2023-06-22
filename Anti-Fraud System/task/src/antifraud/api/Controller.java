package antifraud.api;

import antifraud.api.antifraud.Card;
import antifraud.api.antifraud.CardService;
import antifraud.api.authentication.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
public class Controller {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TransactionService transactionService;
    @Autowired
    CardService cardService;


    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity postTransaction(@RequestBody Transaction transaction){
        if(transaction.getIp() == null || transaction.getNumber() == null)
            return ResponseEntity.status(400).body(Map.of());
        Result result = transactionService.checkTransaction(transaction);
        if(result == null){
            return ResponseEntity.status(400).body(Map.of());
        }

        return ResponseEntity.status(200).body(result);
    }

    @PutMapping("/api/antifraud/transaction")
    public ResponseEntity setFeedback(@RequestBody Feedback feedback){
        Transaction transaction = transactionService.getTransactionById(feedback.getTransactionId());
        if(transaction != null){

            if(transaction.getFeedback() != null)
                return ResponseEntity.status(409).body(null);
            transaction.setFeedback(feedback.getFeedback());
            boolean update = transactionService.updateLimits(transaction);
            if(update) {
                transactionService.add(transaction);
                return ResponseEntity.status(200).body(transaction);
            }
            else
                return ResponseEntity.status(422).body(null);
        }
        return ResponseEntity.status(404).body(null);
    }

    @GetMapping("/api/antifraud/history")
    public ResponseEntity getAllTransactions(){
        return ResponseEntity.status(200).body(transactionService.getAllTransactions());
    }

    @GetMapping("/api/antifraud/history/{number}")
    public ResponseEntity getAllTransactionsByNumber(@PathVariable String number){
        if(!cardService.isValid(new Card(number)))
            return ResponseEntity.status(400).body(null);
        List<Transaction> output = transactionService.getAllTransactionsByNumber(number);
        if(output == null)
            return ResponseEntity.status(404).body(null);
        return ResponseEntity.status(200).body(output);

    }

    @PostMapping("/api/auth/user")
    public ResponseEntity addUser(@RequestBody User user){

        if(!user.checkData()) return ResponseEntity.status(400).body(Map.of());

        if(userService.getAllUsers().size() == 0){
            user.setRole("ROLE_ADMINISTRATOR");
            user.setAccountNonLocked(true);

        }
        else {
            user.setRole("ROLE_MERCHANT");
            user.setAccountNonLocked(false);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userService.addUser(user);

        if (user != null) {
            return ResponseEntity.status(201).body(Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "username", user.getUsername(),
                    "role", user.getRole().substring(5)
            ));
        }
        else{
            return ResponseEntity.status(409).body(Map.of());
        }

    }

    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity deleteUser(@PathVariable String username){
        if(userService.deleteUser(username)){
            return ResponseEntity.status(200).body(Map.of(
                    "username", username,
                    "status","Deleted successfully!"
            ));
        }
        else return ResponseEntity.status(404).body(Map.of());
    }



    @GetMapping("/api/auth/list")
    public ResponseEntity getList(){

        return ResponseEntity.status(200).body(userService.getAllUsers());
    }

    @PutMapping("/api/auth/role")
    public ResponseEntity changeRole(@RequestBody UserRole userRole){
        User findUser = userService.findUser(userRole.getUsername());
        if(findUser == null) return ResponseEntity.status(404).body(Map.of());
        if(!userRole.getRole().equals("SUPPORT") && !userRole.getRole().equals("MERCHANT") ){
            return ResponseEntity.status(400).body(Map.of());
        }

        if(findUser.getRole().substring(5).equals(userRole.getRole())){
            return ResponseEntity.status(409).body(Map.of());
        }
        findUser.setRole("ROLE_"+userRole.getRole());

        User user = userRepository.save(findUser);
        if(user != null){
            return ResponseEntity.status(200).body(Map.of(
                    "id",user.getId(),
                    "name", user.getName(),
                    "username", user.getUsername(),
                    "role", user.getRole().substring(5)
            ));
        }
        else return ResponseEntity.status(404).body(Map.of());
    }

    @PutMapping("/api/auth/access")
    public ResponseEntity changeLock(@RequestBody UserLock userLock){
        User user = userService.findUser(userLock.getUsername());
        if(user != null){
            if(user.getRole().equals("ROLE_ADMINISTRATOR")){
                return ResponseEntity.status(400).body(Map.of());
            }
            else if(userLock.getOperation().equals("LOCK")){
                user.setAccountNonLocked(false);
                userRepository.save(user);
                return ResponseEntity.status(200).body(Map.of("status", "User " +user.getUsername() + " locked!"));
            }
            else if(userLock.getOperation().equals("UNLOCK")){
                user.setAccountNonLocked(true);
                userRepository.save(user);
                return ResponseEntity.status(200).body(Map.of("status", "User " +user.getUsername() + " unlocked!"));
            }
        }
        return ResponseEntity.status(404).body(Map.of());
    }


    @GetMapping("/clear")
    public void postTest(){
        transactionService.clear();
    }




}
