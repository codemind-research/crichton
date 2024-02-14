export enum Status {
  NotStarted,
  Created,
  Testing,
  Tested,
}

export interface SettingType {
  name: string;
  type: string;
  data: any;
}

export interface PluginTestInfo {
  name: string;
  selected: boolean;
  settings: Array<SettingType>;
  isTesting: boolean;
  isSuccess: boolean | undefined;
}

export interface PluginResult {
  pluginName: string;
  info: {
    [key: string]: {
      [key: string]: any;
    };
  };
}
