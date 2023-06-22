package antifraud.api.antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("/test/")
public class AntifraudController {
    @Autowired
    IpService ipService;
    @Autowired
    CardService cardService;

    final String url = "/api/antifraud/";

    @PostMapping(url + "suspicious-ip")
    public ResponseEntity addIp(@RequestBody Ip ip){
        if(!ipService.isValid(ip)){
            return ResponseEntity.status(400).body(Map.of());
        }
        Ip checkIp = ipService.findByIp(ip.getIp());
        if(checkIp != null) return ResponseEntity.status(409).body(Map.of());
        ip = ipService.addIp(ip);
        return ResponseEntity.status(200).body(ip);
    }

    @DeleteMapping(url + "suspicious-ip/{ipString}")
    public ResponseEntity removeIp(@PathVariable String ipString){
        Ip ip = new Ip(ipString);
        if(!ipService.isValid(ip)){
            return ResponseEntity.status(400).body(Map.of());
        }
        Ip checkIp = ipService.findByIp(ip.getIp());
        if(checkIp == null) return ResponseEntity.status(404).body(Map.of());
        ipService.deleteIp(checkIp);
        return ResponseEntity.status(200).body(Map.of("status", "IP "+checkIp.getIp()+" successfully removed!"));
    }

    @GetMapping(url + "suspicious-ip")
    public ResponseEntity getAllIp(){
        return ResponseEntity.status(200).body(ipService.getAllIps());
    }

    @PostMapping(url + "stolencard")
    public ResponseEntity addStolenCard(@RequestBody Card card){
        if(!cardService.isValid(card)){
            return ResponseEntity.status(400).body(Map.of());
        }
        Card checkCard = cardService.findByNumber(card.getNumber());
        if(checkCard != null) return ResponseEntity.status(409).body(Map.of());
        card = cardService.addCard(card);
        return ResponseEntity.status(200).body(card);
    }

    @DeleteMapping(url + "stolencard/{cardNumber}")
    public ResponseEntity removeCard(@PathVariable String cardNumber){
        Card card = new Card(cardNumber);
        if(!cardService.isValid(card)){
            return ResponseEntity.status(400).body(Map.of());
        }
        Card checkCard = cardService.findByNumber(card.getNumber());
        if(checkCard == null) return ResponseEntity.status(404).body(Map.of());
        cardService.deleteCard(card);
        return ResponseEntity.status(200).body(Map.of("status", "Card "+card.getNumber()+" successfully removed!"));
    }

    @GetMapping(url + "stolencard")
    public ResponseEntity getAllCards(){
        return ResponseEntity.status(200).body(cardService.getAllCards());
    }
}
