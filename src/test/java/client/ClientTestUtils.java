package client;

import client.entity.Client;

public class ClientTestUtils {

    private ClientTestUtils() {
    }


    public static Client createClientEntity(String firstName, String surname) {
        Client client = new Client();
        client.setFirstName(firstName);
        client.setSurname(surname);
        return client;
    }
}
