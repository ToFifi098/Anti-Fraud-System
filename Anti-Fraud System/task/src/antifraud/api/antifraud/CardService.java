package antifraud.api.antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {
    StolenCardRepository cardRepository;

    @Autowired
    public CardService(StolenCardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public boolean isValid(Card card){
        String IIN = "400000";
        if(!card.getNumber().substring(0,6).equals(IIN)) return false;
        char[] cardNumbers = card.getNumber().toCharArray();

        if(card.getNumber().toCharArray().length != 16) return false;

        int[] code = new int[15];
        for (int i = 0; i < 15; i++){
            code[i] = cardNumbers[i] - '0';
        }
        int checkDigit = cardNumbers[15] - '0';

        int sum = 0;
        int parity = 1;
        for(int i = 1; i <= code.length; i++){
            if(i % 2 != parity){
                sum += code[i-1];
            }
            else if((cardNumbers[i-1] - '0') > 4){
                sum += (2 * code[i-1]) - 9;
            }
            else
                sum += (2 * code[i-1]);
        }
        return (10 - (sum % 10)) == checkDigit;
    }

    Card addCard(Card card){
        return cardRepository.save(card);
    }

    public Card findByNumber(String num){
        for(Card number: cardRepository.findAll()){
            if(number.getNumber().equals(num)){
                return number;
            }
        }
        return null;
    }

    void deleteCard(Card card){
        card = findByNumber(card.getNumber());
        cardRepository.delete(card);
    }

    List<Card> getAllCards(){
        return cardRepository.findAll();
    }


}
