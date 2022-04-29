import { PluginListenerHandle } from '@capacitor/core';

export interface LoveAlarmBlePlugin {
  initialize(options: InitOptions): Promise<void>;
  advertise(): Promise<void>;
  stopAdvertise(): Promise<void>;
  scan(callback: (result: ScanResult) => void): Promise<void>;
  stopScan(): Promise<void>;
  read(options: ReadOptions): Promise<ReadResult>;
  addListener(eventName: string, listenerFunc: (event: any) => void): PluginListenerHandle;
  addListener(eventName: 'onScanResult', listenerFunc: (result: any) => void): PluginListenerHandle;
}

export interface InitOptions {
  advertising: string;
}

export interface ReadOptions {
  address: string;
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
