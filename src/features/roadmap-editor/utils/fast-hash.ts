/**
 * FNV-1a hash algorithm for fast change detection
 * Faster than JSON.stringify for large objects
 * Good enough for detecting changes (not cryptographically secure)
 */

const FNV_OFFSET = 2166136261;
const FNV_PRIME = 16777619;

function hashString(str: string): number {
  let hash = FNV_OFFSET;
  for (let i = 0; i < str.length; i++) {
    hash ^= str.charCodeAt(i);
    hash = Math.imul(hash, FNV_PRIME);
  }
  return hash >>> 0; // Convert to unsigned 32-bit integer
}

/**
 * Fast hash for arrays of objects
 * Uses JSON.stringify but only for individual values, then combines hashes
 * Much faster than JSON.stringify for the entire array
 */
export function fastArrayHash<T extends Record<string, unknown>>(
  arr: T[],
  keys: (keyof T)[],
): string {
  if (!arr.length) return '0';

  let combined = arr.length; // Include array length in hash

  for (const item of arr) {
    for (const key of keys) {
      const value = item[key];
      // Hash the value - use JSON.stringify for complex objects
      const str = typeof value === 'object' ? JSON.stringify(value) : String(value);
      combined ^= hashString(str);
    }
  }

  return combined.toString(36); // Base36 for shorter strings
}

/**
 * Fast hash for nodes - hashes relevant properties for change detection
 */
export function hashNodes(nodes: { id: string; position: unknown; data: unknown }[]): string {
  return fastArrayHash(nodes, ['id', 'position', 'data']);
}

/**
 * Fast hash for edges - hashes relevant properties for change detection
 */
export function hashEdges(edges: { id: string; source: string; target: string }[]): string {
  return fastArrayHash(edges, ['id', 'source', 'target']);
}
