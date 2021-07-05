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
            if (k.equals("client_id_sync")) {
                client.setClientIdSync((Number) keyPairs.get(k));
            }
            if (k.equals("client_id_code")) {
                client.setClientIdCode( (String) keyPairs.get(k));
            }
            if (k.equals("secret")) {
                client.setSecret( (String) keyPairs.get(k));
            }
            if (k.equals("is_active")) {
                client.setIsActive((Long) keyPairs.get(k));
            }
            if (k.equals("consumer_id")) {
                client.setConsumerId((String) keyPairs.get(k));
            }
            if (k.equals("ip_access")) {
                client.setIpAccess((String) keyPairs.get(k));
            }

        }
        return clientRepository.save(client);
    }

    public int updateConsumerClientKafka(String clientIdSync,String clientIdCode, String secret ,
                                         Long isActive,String ipAccess) {
        return clientRepository.updateConsumerClientKafka(clientIdSync,clientIdCode,secret, isActive,ipAccess);
    }

    public int deleteConsumerClientKafka(Number clientIdSync) {
        return clientRepository.deleteConsumerClientKafka(clientIdSync);
    }

    public int truncateConsumerClientKafka() {
        return clientRepository.truncateConsumerClientKafka();
    }

}

