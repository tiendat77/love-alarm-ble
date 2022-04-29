# love-alarm-ble

🔌 Bluetooth Low Energy (BLE) Plugin for Love Alarm app

## Install

```bash
npm install love-alarm-ble
npx cap sync
```

## API

<docgen-index>

* [`initialize(...)`](#initialize)
* [`advertise()`](#advertise)
* [`stopAdvertise()`](#stopadvertise)
* [`scan(...)`](#scan)
* [`stopScan()`](#stopscan)
* [`read(...)`](#read)
* [`addListener(string, ...)`](#addlistenerstring)
* [`addListener('onScanResult', ...)`](#addlisteneronscanresult)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### initialize(...)

```typescript
initialize(options: InitOptions) => Promise<void>
```

| Param         | Type                                                |
| ------------- | --------------------------------------------------- |
| **`options`** | <code><a href="#initoptions">InitOptions</a></code> |

--------------------


### advertise()

```typescript
advertise() => Promise<void>
```

--------------------


### stopAdvertise()

```typescript
stopAdvertise() => Promise<void>
```

--------------------


### scan(...)

```typescript
scan(callback: (result: ScanResult) => void) => Promise<void>
```

| Param          | Type                                                                   |
| -------------- | ---------------------------------------------------------------------- |
| **`callback`** | <code>(result: <a href="#scanresult">ScanResult</a>) =&gt; void</code> |

--------------------


### stopScan()

```typescript
stopScan() => Promise<void>
```

--------------------


### read(...)

```typescript
read(options: ReadOptions) => Promise<ReadResult>
```

| Param         | Type                                                |
| ------------- | --------------------------------------------------- |
| **`options`** | <code><a href="#readoptions">ReadOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#readresult">ReadResult</a>&gt;</code>

--------------------


### addListener(string, ...)

```typescript
addListener(eventName: string, listenerFunc: (event: any) => void) => PluginListenerHandle
```

| Param              | Type                                 |
| ------------------ | ------------------------------------ |
| **`eventName`**    | <code>string</code>                  |
| **`listenerFunc`** | <code>(event: any) =&gt; void</code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onScanResult', ...)

```typescript
addListener(eventName: 'onScanResult', listenerFunc: (result: any) => void) => PluginListenerHandle
```

| Param              | Type                                  |
| ------------------ | ------------------------------------- |
| **`eventName`**    | <code>'onScanResult'</code>           |
| **`listenerFunc`** | <code>(result: any) =&gt; void</code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### Interfaces


#### InitOptions

| Prop              | Type                |
| ----------------- | ------------------- |
| **`advertising`** | <code>string</code> |


#### ScanResult

| Prop          | Type                |
| ------------- | ------------------- |
| **`address`** | <code>any</code>    |
| **`name`**    | <code>string</code> |


#### ReadResult

| Prop          | Type                |
| ------------- | ------------------- |
| **`address`** | <code>string</code> |
| **`name`**    | <code>string</code> |
| **`profile`** | <code>string</code> |


#### ReadOptions

| Prop          | Type                |
| ------------- | ------------------- |
| **`address`** | <code>string</code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |

</docgen-api>
