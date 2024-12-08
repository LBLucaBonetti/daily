import type { QVueGlobals } from 'quasar';

export function notifyPosition($q: QVueGlobals) {
  return $q.screen.lt.md ? 'bottom' : 'top-right';
}
