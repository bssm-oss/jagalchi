import { describe, expect, it } from 'vitest';

import { mapToStompPermissions, mapToStompRole } from './jwt';

describe('jwt STOMP mapping', () => {
  it.each([
    ['STUDENT', 'USER', 'READ,WRITE'],
    ['TEACHER', 'ADMIN', 'ALL'],
    ['ADMIN', 'ADMIN', 'ALL'],
    ['USER', 'USER', 'READ,WRITE'],
    ['GUEST', 'GUEST', 'READ'],
    [null, 'GUEST', 'READ'],
  ] as const)('maps %s to %s with %s permission', (role, expectedRole, expectedPermissions) => {
    expect(mapToStompRole(role)).toBe(expectedRole);
    expect(mapToStompPermissions(role)).toBe(expectedPermissions);
  });
});
