package test.project.telega.data.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import test.project.telega.bot.scenaries.states.UserState;

@Entity
public class User {
    @Id
    private Long chatId;

    @Enumerated(EnumType.STRING)
    private UserState state;
}
