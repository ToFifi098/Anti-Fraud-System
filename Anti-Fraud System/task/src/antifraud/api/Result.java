package antifraud.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
@NoArgsConstructor
public class Result {
    private Transaction.status result;
    private ArrayList<String> info = new ArrayList<>();

    public void addInfo(String info){
        this.info.add(info);
    }
    public String getInfo(){
        if(info.size() == 0) return "none";
        info.sort(String::compareTo);
        return String.join(", ", info);
    }

    @Override
    public String toString() {
        return "{" +
                "result" + result +
                "info" + getInfo();
    }
}
