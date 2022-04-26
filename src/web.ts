import { WebPlugin } from '@capacitor/core';

import type { LoveAlarmBLEPlugin } from './definitions';

export class LoveAlarmBLEWeb extends WebPlugin implements LoveAlarmBLEPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
