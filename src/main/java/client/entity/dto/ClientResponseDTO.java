package client.entity.dto;

import account.entity.dto.AccountResponseDTO;

import java.util.Set;

public class ClientResponseDTO {

    private Long id;
    private String firstName;
    private String surname;
    private Set<AccountResponseDTO> accounts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Set<AccountResponseDTO> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<AccountResponseDTO> accounts) {
        this.accounts = accounts;
    }
}
