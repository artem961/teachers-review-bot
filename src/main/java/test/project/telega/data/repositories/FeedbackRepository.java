package test.project.telega.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import test.project.telega.data.entities.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback,Long> {
}
