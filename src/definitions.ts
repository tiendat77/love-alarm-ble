export interface LoveAlarmBLEPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
