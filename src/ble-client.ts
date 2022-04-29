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

  /**
   * Initialize Bluetooth Low Energy (BLE). If it fails, BLE might be unavailable on this device.
   * On **Android** it will ask for the location permission.
   */
  async initialize(options: InitOptions): Promise<void> {
    await LoveAlarmBle.initialize(options);
  }

  /**
   * Scan for nearby BLE devices.The callback will be invoked on each device that is found.
   * @param callback
   */
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

  /**
   * Stop scanning for BLE devices.
   */
  async stopScan(): Promise<void> {
    await LoveAlarmBle.stopScan();
  }

  /**
   * Read profile ID from characteristic.
   * @param options
   * @returns ReadResult
   */
  async read(options: ReadOptions): Promise<ReadResult> {
    return await LoveAlarmBle.read(options);
  }

  /**
   * Advertising Profile ID to nearby BLE devices.
   */
  async advertise(): Promise<void> {
    await LoveAlarmBle.advertise();
  }

  /**
   * Stop advertising Profile ID to nearby BLE devices.
   */
  async stopAdvertise(): Promise<void> {
    await LoveAlarmBle.stopAdvertise();
  }

}

export const BleClient = new LoveAlarmBleClass();