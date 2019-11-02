package pl.oczadly.money.api.client.entity;

import pl.oczadly.money.api.account.entity.Account;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "client")
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;

    private String surname;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Account> accounts;

    public Client() {
        accounts = new HashSet<>();
    }

    public void addAccount(Account account) {
        account.setClient(this);
        accounts.add(account);
    }

    public void removeAccount(Account account) {
        account.setClient(null);
        accounts.remove(account);
    }

    @Override
    public boolean equals(Object objectToCompare) {
        if (this == objectToCompare) {
            return true;
        }

        if (objectToCompare == null || getClass() != objectToCompare.getClass()) {
            return false;
        }
        Client client = (Client) objectToCompare;

        return Objects.equals(id, client.id) &&
                Objects.equals(firstName, client.firstName) &&
                Objects.equals(surname, client.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, surname);
    }

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

    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }
}
