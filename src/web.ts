import { WebPlugin } from '@capacitor/core';

import type {
  ReadOptions,
  LoveAlarmBlePlugin,
  ReadResult,
  BluetoothStatus
} from './definitions';

export class LoveAlarmBleWeb extends WebPlugin implements LoveAlarmBlePlugin {

  async initialize(): Promise<void> {
    return Promise.resolve();
  }

  async isEnable(): Promise<BluetoothStatus> {
    return Promise.resolve({enable: false});
  }

  async enable(): Promise<void> {
    return Promise.resolve();
  }

  async startScan(): Promise<void> {
    return Promise.resolve();
  }

  async stopScan(): Promise<void> {
    return Promise.resolve();
  }

  async read(options: ReadOptions): Promise<ReadResult> {
    return Promise.resolve({
      address: options.address,
      name: '',
      profile: ''
    });
  }

  async startAdvertise(): Promise<void> {
    return Promise.resolve();
  }

  async stopAdvertise(): Promise<void> {
    return Promise.resolve();
  }

  async matches(): Promise<void> {
    return Promise.resolve();
  }

}
