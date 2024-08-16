package rize.os.access.manager.user.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class UserNotFoundException extends IllegalArgumentException
{
    public UserNotFoundException(String id)
    {
        super("User not found with id: " + id);
        log.warn(getMessage());
    }
}
