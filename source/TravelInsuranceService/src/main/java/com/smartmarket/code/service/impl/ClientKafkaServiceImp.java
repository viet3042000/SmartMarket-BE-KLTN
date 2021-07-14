package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.ClientRepository;
import com.smartmarket.code.model.Client;
import com.smartmarket.code.service.ClientKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class ClientKafkaServiceImp implements ClientKafkaService {

    @Autowired
    private ClientRepository clientRepository;

    public Client createConsumerClientKafka(Map<String, Object> keyPairs) {
        Client client = new Client();

        for (String k : keyPairs.keySet()) {
            if (k.equals("client_id")) {
//                client.setClientIdSync(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("client_id_code")) {
                client.setClientIdCode( (String) keyPairs.get(k));
            }
            if (k.equals("secret")) {
                client.setSecret( (String) keyPairs.get(k));
            }
            if (k.equals("is_active")) {
                client.setIsActive(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("consumer_id")) {
                client.setConsumerId((String) keyPairs.get(k));
            }
            if (k.equals("ip_access")) {
                client.setIpAccess((String) keyPairs.get(k));
            }
            if (k.equals("client_id_name")) {
                client.setClientIdName((String) keyPairs.get(k));
            }
        }
        return clientRepository.save(client);
    }

    public int updateConsumerClientKafka(String clientIdName, String clientIdCode, String secret ,
                                         Long isActive,String consumerId,String ipAccess) {
        return clientRepository.updateConsumerClientKafka(clientIdName,clientIdCode,secret, isActive,consumerId,ipAccess);
    }

    public int deleteConsumerClientKafka(String clientIdName) {
        return clientRepository.deleteConsumerClientKafka(clientIdName);
    }

    public int truncateConsumerClientKafka() {
        return clientRepository.truncateConsumerClientKafka();
    }

}

