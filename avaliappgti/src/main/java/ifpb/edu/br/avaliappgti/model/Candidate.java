package ifpb.edu.br.avaliappgti.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "cpf", nullable = false, unique = true, length = 20)
    private String cpf;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "social_name")
    private String socialName;

    @Column(name = "sex", length = 10)
    private String sex;

    @Column(name = "registration_", length = 50) // Note: PostgreSQL allows trailing underscore
    private String registration;

    @Column(name = "registration_state", length = 50)
    private String registrationState;

    @Column(name = "registration_place")
    private String registrationPlace;

    @Column(name = "address")
    private String address;

    @Column(name = "address_number", length = 50)
    private String addressNumber;

    @Column(name = "address_complement")
    private String addressComplement;

    @Column(name = "address_neighborhood")
    private String addressNeighborhood;

    @Column(name = "address_city")
    private String addressCity;

    @Column(name = "address_state", length = 50)
    private String addressState;

    @Column(name = "address_zipcode", length = 20)
    private String addressZipcode;

    @Column(name = "cell_phone", length = 20)
    private String cellPhone;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "other_email")
    private String otherEmail;

    @Column(name = "education_level")
    private String educationLevel;

    @Column(name = "graduation_course")
    private String graduationCourse;

    @Column(name = "graduation_year", length = 4)
    private String graduationYear; // Storing as String if it can be non-numeric or needs leading zeros

    @Column(name = "graduation_institution")
    private String graduationInstitution;

    @Column(name = "specialization_course")
    private String specializationCourse;

    @Column(name = "specialization_year", length = 4)
    private String specializationYear; // Storing as String

    @Column(name = "specialization_institution")
    private String specializationInstitution;

    @Column(name = "lattes_link")
    private String lattesLink;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetch is generally good for performance
    @JoinColumn(name = "quota_id") // This links to the 'id' field in the 'quotas' table
    private Quota quota;
}
