import test from 'node:test';
import assert from 'node:assert/strict';
import { buildResponse, findSchemeMatches, normalizeText } from '../src/sakhiData.js';

test('normalizes extra whitespace and punctuation', () => {
  assert.equal(normalizeText('  LPG,   connection! '), 'lpg connection');
});

test('matches LPG queries to PM Ujjwala', () => {
  const results = findSchemeMatches('I need help with a gas cylinder');
  assert.equal(results[0]?.id, 'ujjwala');
});

test('matches education queries to scholarship portal', () => {
  const results = findSchemeMatches('college scholarship help');
  assert.equal(results[0]?.id, 'scholarship');
});

test('returns a safe fallback when no scheme matches', () => {
  const result = buildResponse('I need general support', 'English');
  assert.match(result, /official scheme website/i);
});
