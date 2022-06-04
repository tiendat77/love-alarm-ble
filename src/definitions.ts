import { PluginListenerHandle } from '@capacitor/core';

export interface LoveAlarmBlePlugin {
  initialize(options: InitOptions): Promise<void>;
  isEnable(): Promise<BluetoothStatus>;
  enable(): Promise<void>;
  startAdvertise(): Promise<void>;
  stopAdvertise(): Promise<void>;
  startScan(callback: (result: ScanResult) => void): Promise<void>;
  stopScan(): Promise<void>;
  read(options: ReadOptions): Promise<ReadResult>;
  matches(options: MatchingOptions): Promise<void>;
  addListener(eventName: string, listenerFunc: (event: any) => void): PluginListenerHandle;
  addListener(eventName: 'onScanResult', listenerFunc: (result: any) => void): PluginListenerHandle;
}

export interface InitOptions {
  advertising: string;
}

export interface ReadOptions {
  address: string;
}

export interface MatchingOptions {
  profiles: string[];
}

export interface ScanResult {
  address: any;
  name?: string;
}

export interface ReadResult {
  address: string;
  name?: string;
  profile?: string;
}

export interface BluetoothStatus {
  enable: boolean;
}