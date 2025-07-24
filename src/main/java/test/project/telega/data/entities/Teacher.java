package test.project.telega.data.entities;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Teacher {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String surname;

    private String lastName;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name="subject_id")
    private Subject subject;

    @OneToMany(mappedBy = "teacher", fetch =  FetchType.EAGER)
    List<Feedback> feedbackList;

    @Override
    public String toString() {
        return surname +  " " + firstName +  " " + lastName;
    }
}
