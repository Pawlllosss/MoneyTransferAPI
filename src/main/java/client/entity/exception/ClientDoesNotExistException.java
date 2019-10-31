package client.entity.exception;

public class ClientDoesNotExistException extends RuntimeException {

    public ClientDoesNotExistException(Long id) {
        super("Client with the following id does not exists: " + id);
    }
}
