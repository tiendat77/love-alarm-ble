import { registerPlugin } from '@capacitor/core';

import type { LoveAlarmBlePlugin } from './definitions';

export const LoveAlarmBle = registerPlugin<LoveAlarmBlePlugin>(
  'LoveAlarmBle',
  {
    web: () => import('./web').then(m => new m.LoveAlarmBleWeb()),
  },
);
