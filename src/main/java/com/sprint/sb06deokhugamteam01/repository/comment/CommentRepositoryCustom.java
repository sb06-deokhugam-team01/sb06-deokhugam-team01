package com.sprint.sb06deokhugamteam01.repository.comment;

import com.sprint.sb06deokhugamteam01.dto.comment.request.CommentSearchCondition;
import com.sprint.sb06deokhugamteam01.dto.comment.response.CommentSliceResult;

public interface CommentRepositoryCustom {
    CommentSliceResult sliceComments(CommentSearchCondition condition);
}
