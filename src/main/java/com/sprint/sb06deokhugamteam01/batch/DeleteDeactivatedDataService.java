package com.sprint.sb06deokhugamteam01.batch;


import com.sprint.sb06deokhugamteam01.repository.BookRepository;
import com.sprint.sb06deokhugamteam01.repository.CommentRepository;
import com.sprint.sb06deokhugamteam01.repository.notification.NotificationRepository;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewRepository;
import com.sprint.sb06deokhugamteam01.repository.user.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteDeactivatedDataService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void deleteDeactivatedUsers() {
        userRepository.deleteByIsActiveFalse();
    }

    @Transactional
    public void deleteDeactivatedBooks() {
        bookRepository.deleteAllByIsActiveFalse();
    }

    @Transactional
    public void deleteDeactivatedReviews() {
        reviewRepository.deleteAllByIsActiveFalse();
    }

    @Transactional
    public void deleteDeactivatedNotifications() {
        notificationRepository.deleteByConfirmedIsTrueAndUpdatedAtBefore(LocalDateTime.now().minusDays(7));
    }

    @Transactional
    public void deleteDeactivatedComments() {
        commentRepository.deleteAllByIsActiveFalse();
    }


}
