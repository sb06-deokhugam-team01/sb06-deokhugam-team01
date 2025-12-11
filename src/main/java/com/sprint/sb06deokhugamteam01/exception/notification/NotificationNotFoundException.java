package com.sprint.sb06deokhugamteam01.exception.notification;

import com.sprint.sb06deokhugamteam01.exception.ErrorCode;
import com.sprint.sb06deokhugamteam01.exception.RootException;
import java.util.Map;

public class NotificationNotFoundException extends RootException {

    public NotificationNotFoundException(Map<String, Object> details) {
        super(ErrorCode.NOTIFICATION_NOT_FOUND, details);
    }

}
