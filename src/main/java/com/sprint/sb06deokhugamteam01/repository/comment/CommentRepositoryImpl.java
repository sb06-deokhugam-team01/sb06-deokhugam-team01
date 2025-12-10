package com.sprint.sb06deokhugamteam01.repository.comment;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.sb06deokhugamteam01.domain.Comment;
import com.sprint.sb06deokhugamteam01.domain.QComment;
import com.sprint.sb06deokhugamteam01.domain.QUser;
import com.sprint.sb06deokhugamteam01.domain.QReview;
import com.sprint.sb06deokhugamteam01.dto.comment.request.CommentSearchCondition;
import com.sprint.sb06deokhugamteam01.dto.comment.response.CommentSliceResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QComment comment = QComment.comment;
    private final QReview review = QReview.review;
    private final QUser user = QUser.user;


    @Override
    public CommentSliceResult sliceComments(CommentSearchCondition condition) {

        OrderSpecifier<?> direction = (condition.direction() == Sort.Direction.DESC)?
                comment.createdAt.desc() : comment.createdAt.asc();

        BooleanBuilder baseBuilder = new BooleanBuilder();
        baseBuilder.and(comment.review.id.eq(condition.reviewId()));

        Long totalElements = Optional.ofNullable(
                        queryFactory
                                .select(comment.count())
                                .from(comment)
                                .where(baseBuilder)
                                .fetchOne())
                .orElse(0L);

        if (condition.after() != null || condition.cursor() != null) {
            baseBuilder.and(cursorPagination(condition));
        }

        List<Comment> comments = queryFactory
                .selectFrom(comment)
                .innerJoin(comment.review, review).fetchJoin()
                .innerJoin(comment.user, user).fetchJoin()
                .where(baseBuilder)
                .orderBy(direction, comment.id.asc())
                .limit(condition.limit() + 1)
                .fetch();

        boolean hasNext = false;
        if(comments.size() > condition.limit()) {
            hasNext = true;
            comments.remove(condition.limit());
        }

        return new CommentSliceResult(comments, hasNext, totalElements);
    }

    private BooleanBuilder cursorPagination(CommentSearchCondition condition) {
        BooleanBuilder cursorBuilder = new BooleanBuilder();
        if (condition.direction() == Sort.Direction.DESC) {
            cursorBuilder.and(comment.createdAt.lt(condition.after()));

            BooleanBuilder descBuilder = new BooleanBuilder();
            descBuilder.and(comment.createdAt.eq(condition.after()));
            descBuilder.and(comment.id.gt(condition.cursor()));

            cursorBuilder.or(descBuilder);
        } else {
            cursorBuilder.and(comment.createdAt.gt(condition.after()));

            BooleanBuilder ascBuilder = new BooleanBuilder();
            ascBuilder.and(comment.createdAt.eq(condition.after()));
            ascBuilder.and(comment.id.gt(condition.cursor()));

            cursorBuilder.or(ascBuilder);
        }
        return cursorBuilder;
    }
}
