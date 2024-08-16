package rize.os.access.manager.usersession;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserSession
{
    private String id;
    private String userId;
    private String ipAddress;
    private LocalDateTime startedAt;
    private LocalDateTime lastAccessAt;
    private List<String> clients;
}
