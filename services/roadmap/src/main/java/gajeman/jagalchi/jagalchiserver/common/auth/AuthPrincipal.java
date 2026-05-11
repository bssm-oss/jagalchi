package gajeman.jagalchi.jagalchiserver.common.auth;

import java.util.Set;

public record AuthPrincipal(Long userId, Set<AuthPermission> permissions, Long roadmapId) {

    public static final String ATTRIBUTE_NAME = "jagalchiAuthPrincipal";

    public boolean canRead() {
        return permissions != null && (permissions.contains(AuthPermission.READ) || permissions.contains(AuthPermission.EDIT));
    }

    public boolean canEdit() {
        return permissions != null && permissions.contains(AuthPermission.EDIT);
    }
}

