import type { PluginListenerHandle } from '@capacitor/core';
import type { InitOptions, ReadOptions, ReadResult, ScanResult } from './definitions';
import { LoveAlarmBle } from './plugin';

export interface LoveAlarmBleInterface {
  initialize(options: InitOptions): Promise<void>;
  advertise(): Promise<void>;
  stopAdvertise(): Promise<void>;
  scan(callback: (result: ScanResult) => void): Promise<void>;
  stopScan(): Promise<void>;
  read(options: ReadOptions): Promise<ReadResult>;
}

class LoveAlarmBleClass implements LoveAlarmBleInterface {

  private scanListener: PluginListenerHandle | null = null;

  async initialize(options: InitOptions): Promise<void> {
    await LoveAlarmBle.initialize(options);
  }

  async scan(callback: (result: ScanResult) => void) {
    await this.scanListener?.remove();

    this.scanListener = await LoveAlarmBle.addListener(
      'onScanResult',
      (resultInternal: ScanResult) => {
        callback(resultInternal);
      }
    );

    await LoveAlarmBle.scan(callback);
  }

  async stopScan(): Promise<void> {
    await LoveAlarmBle.stopScan();
  }

  async read(options: ReadOptions): Promise<ReadResult> {
    return await LoveAlarmBle.read(options);
  }

  async advertise(): Promise<void> {
    await LoveAlarmBle.advertise();
  }

  async stopAdvertise(): Promise<void> {
    await LoveAlarmBle.stopAdvertise();
  }

}

export const BleClient = new LoveAlarmBleClass();