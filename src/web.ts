import { WebPlugin } from '@capacitor/core';

import type {
  InitOptions,
  ReadOptions,
  LoveAlarmBlePlugin,
  ReadResult
} from './definitions';

export class LoveAlarmBleWeb extends WebPlugin implements LoveAlarmBlePlugin {

  async initialize(options: InitOptions): Promise<void> {
    console.log('INITIALIZE: ' + options.advertising);
    return Promise.resolve();
  }

  async scan(): Promise<void> {
    console.log('SCAN');
    return Promise.resolve();
  }

  async stopScan(): Promise<void> {
    console.log('STOP SCAN');
    return Promise.resolve();
  }

  async read(options: ReadOptions): Promise<ReadResult> {
    console.log(`READ ${options.address}`);
    return Promise.resolve({
      address: options.address,
      name: '',
      profile: ''
    });
  }

  async advertise(): Promise<void> {
    console.log('ADVERTISE');
    return Promise.resolve();
  }

  async stopAdvertise(): Promise<void> {
    console.log('STOP ADVERTISE');
    return Promise.resolve();
  }

}
