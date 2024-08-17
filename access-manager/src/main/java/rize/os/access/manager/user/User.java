package rize.os.access.manager.user;

import com.vaadin.hilla.Nullable;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User
{
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> organizationIds;

    @Builder.Default
    private Boolean emailVerified = false;

    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    private Boolean isOnline = false;

    @Nullable
    private LocalDateTime onlineSince;
}
