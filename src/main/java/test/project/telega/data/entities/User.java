package test.project.telega.data.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
public class User {
    @Id
    private Long chatId;
}
