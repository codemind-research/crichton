import React, { useState } from "react";
import { PluginTestInfo, SettingType } from "../../../util/TypeDef";

const SelectComp = (props: any) => {
  const pluginList: Array<PluginTestInfo> = props.pluginList || [];

  const [fileIndexs, setFileIndexs] = useState<{ [key: string]: number }>({});
  const [optionFileNames, setOptionFileNames] = useState<Array<string>>([]);

  const addFileIndex = (keyName: string): void => {
    if (fileIndexs[keyName] !== undefined) return;

    const newFileIndex = fileIndexs;
    newFileIndex[keyName] = Object.keys(fileIndexs).length;
    setFileIndexs(newFileIndex);
  };
  const addOptionFileName = (keyName: string, fileName: string): void => {
    const newOptionFileNames = [...optionFileNames];
    newOptionFileNames[fileIndexs[keyName]] = fileName;
    setOptionFileNames(newOptionFileNames);
  };

  const handleFileChange = async (
    keyName: string,
    setting: SettingType,
    e: React.ChangeEvent<HTMLInputElement>
  ): Promise<void> => {
    const file = e.target.files?.[0];
    const fileName = file === undefined ? "" : file.name;
    setting.data = file;

    await addFileIndex(keyName);
    await addOptionFileName(keyName, fileName);
  };

  const handleSelectFileClick = (id: string): void => {
    const fileInput = document.getElementById(id) as HTMLInputElement | null;
    if (fileInput) fileInput.click();
  };

  const getTypedInput = (keyName: string, setting: SettingType): JSX.Element => {
    const type = setting.type.toLowerCase();
    switch (type) {
      case "file":
        const index = fileIndexs[keyName];
        const id = `${keyName}_${setting.name}`;
        return (
          <div className="input_with_button">
            <input type="file" id={id} onChange={(e) => handleFileChange(keyName, setting, e)} />
            <span>{optionFileNames[index]}</span>
            <button type="submit" onClick={() => handleSelectFileClick(id)}>
              ...
            </button>
          </div>
        );
      case "number":
        return <input type="number" defaultValue={0} onChange={(e) => (setting.data = e.target.value)} />;
      case "string":
        return <input type="text" placeholder="Input here" onChange={(e) => (setting.data = e.target.value)} />;
      default:
        return <span>Invalid type specified</span>;
    }
  };

  const getPluginOptions = (plugin: PluginTestInfo, index: number): JSX.Element | undefined => {
    if (Object.keys(plugin.settings).length === 0) return;

    const options = plugin.settings.map((setting) => {
      const settingName = setting.name;
      const keyName = `${plugin.name}-${settingName}`;
      return (
        <div className="option" key={keyName}>
          <label className="optionName" htmlFor="quantity">
            · {settingName}:{" "}
          </label>
          {getTypedInput(keyName, setting)}
        </div>
      );
    });
    return <React.Fragment>{options}</React.Fragment>;
  };

  return (
    <div className="select_test_type_component">
      <ul>
        {pluginList.map((plugin, index) => (
          <div className="checkbox_option" key={plugin.name}>
            <input
              type="checkbox"
              id={plugin.name}
              value={plugin.selected.toString()}
              onChange={(e) => (plugin.selected = e.target.checked)}
            />
            <label htmlFor={plugin.name}>{plugin.name}</label>
            {getPluginOptions(plugin, index)}
          </div>
        ))}
      </ul>
    </div>
  );
};

export default SelectComp;
