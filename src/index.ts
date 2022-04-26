import { registerPlugin } from '@capacitor/core';

import type { LoveAlarmBLEPlugin } from './definitions';

const LoveAlarmBLE = registerPlugin<LoveAlarmBLEPlugin>('LoveAlarmBLE', {
  web: () => import('./web').then(m => new m.LoveAlarmBLEWeb()),
});

export * from './definitions';
export { LoveAlarmBLE };
