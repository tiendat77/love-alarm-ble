import type { PluginListenerHandle } from '@capacitor/core';
import { LoveAlarmBle } from './plugin';
import type {
  BluetoothStatus,
  InitOptions,
  MatchingOptions,
  ReadOptions,
  ReadResult,
  ScanResult,
  WatchResult
} from './definitions';

export interface LoveAlarmBleInterface {
  initialize(options: InitOptions): Promise<void>;
  isEnable(): Promise<BluetoothStatus>;
  enable(): Promise<void>;
  startAdvertise(): Promise<void>;
  stopAdvertise(): Promise<void>;
  startScan(callback: (result: ScanResult) => void): Promise<void>;
  stopScan(): Promise<void>;
  read(options: ReadOptions): Promise<ReadResult>;
  matches(options: MatchingOptions): Promise<void>;
}

class LoveAlarmBleClass implements LoveAlarmBleInterface {

  private scanListener: PluginListenerHandle | null = null;
  private watchListener: PluginListenerHandle | null = null;

  /**
   * Initialize Bluetooth Low Energy (BLE). If it fails, BLE might be unavailable on this device.
   * On **Android** it will ask for the location permission.
   */
  async initialize(options: InitOptions): Promise<void> {
    await LoveAlarmBle.initialize(options);
  }

  /**
   * Is Bluetooth is on in device
  */
  async isEnable(): Promise<BluetoothStatus> {
    return await LoveAlarmBle.isEnable();
  }

  /**
   * Enable Bluetooth
  */
  async enable(): Promise<void> {
    return await LoveAlarmBle.enable();
  }

  /**
   * Scan for nearby BLE devices.The callback will be invoked on each device that is found.
   * @param callback
   */
  async startScan(callback: (result: ScanResult) => void) {
    await this.scanListener?.remove();

    this.scanListener = await LoveAlarmBle.addListener(
      'onScanResult',
      (resultInternal: ScanResult) => {
        callback(resultInternal);
      }
    );

    await LoveAlarmBle.startScan(callback);
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
  async startAdvertise(): Promise<void> {
    await LoveAlarmBle.startAdvertise();
  }

  /**
   * Stop advertising Profile ID to nearby BLE devices.
   */
  async stopAdvertise(): Promise<void> {
    await LoveAlarmBle.stopAdvertise();
  }

  async matches(options: MatchingOptions): Promise<void> {
    await LoveAlarmBle.matches(options);
  }

  /**
   * Watch for Gatt Service changes.
   * Changes will be emitted if nearby device "ring".
   */
  async watch(callback: (result: WatchResult) => void) {
    await this.watchListener?.remove();

    this.watchListener = await LoveAlarmBle.addListener(
      'onWatchResult',
      (result: WatchResult) => {
        callback(result);
      }
    );
  }

}

export const BleClient = new LoveAlarmBleClass();