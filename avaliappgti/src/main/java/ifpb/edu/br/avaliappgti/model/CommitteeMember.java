package ifpb.edu.br.avaliappgti.model;


import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "committee_members")
public class CommitteeMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "cpf", nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(name = "if_registration", nullable = false, unique = true, length = 14)
    private String ifRegistration;

    @Column(name = "password", nullable = false)
    private String password; // Consider storing hashed passwords, not plain text

    // Constructors
    public CommitteeMember(String name, String email, String cpf, String ifRegistration, String password) {
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.ifRegistration = ifRegistration;
        this.password = password;
    }
}
