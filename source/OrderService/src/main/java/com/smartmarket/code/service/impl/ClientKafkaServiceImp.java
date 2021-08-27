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
            if (k.equals("id")) {
                client.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("client_id")) {
                client.setClientId( (String) keyPairs.get(k));
            }
            if (k.equals("client_secret")) {
                client.setSecret( (String) keyPairs.get(k));
            }
            if (k.equals("consumer_id")) {
                client.setConsumerId((String) keyPairs.get(k));
            }
        }
        return clientRepository.save(client);
    }

    public int updateConsumerClientKafka(Long id,String clientId,String secret,String consumerId) {
        return clientRepository.updateClientKafka(id,clientId,secret,consumerId);
    }

    public int deleteConsumerClientKafka(String clientId) {
        return clientRepository.deleteClientKafka(clientId);
    }

    public int truncateConsumerClientKafka() {
        return clientRepository.truncateClientKafka();
    }

}

