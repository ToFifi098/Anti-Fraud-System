package antifraud.api.antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IpService {
    IpRepository ipRepository;

    @Autowired
    public IpService(IpRepository ipRepository) {
        this.ipRepository = ipRepository;
    }

    public boolean isValid(Ip ip){
        String[] bits = ip.getIp().split("\\.");
        if(bits.length == 4){
            for(String value: bits){
                if(Integer.parseInt(value) < 0 || Integer.parseInt(value) > 255){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    public Ip findByIp(String ipOrg){
        for(Ip ip: ipRepository.findAll()){
            if(ip.getIp().equals(ipOrg)){
                return ip;
            }
        }
        return null;
    }

    public Ip addIp(Ip ip){
        return ipRepository.save(ip);
    }

    void deleteIp(Ip ip){
        ip = findByIp(ip.getIp());
        System.out.println(ip.getIp());
        ipRepository.delete(ip);
    }

    public List<Ip> getAllIps(){
        return ipRepository.findAll();
    }

    void clear(){
        ipRepository.deleteAll();
    }
}
