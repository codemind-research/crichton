import { useState, useEffect } from "react";
import Test from "./components/Test/TestComp";
import Log from "./components/LogComp";
import { Status, PluginTestInfo } from "../util/TypeDef";
import "./Page.scss";

export default function MainPage() {
  const apiList = require("../api/APIList").default;
  const aesUtil = require("../util/AES").default;

  const [pluginInfoList, setPluginInfoList] = useState<Array<PluginTestInfo>>();
  const [projectStatus, setProjectStatus] = useState<Status>(Status.NotStarted);

  useEffect(() => {
    created();
  }, []);
  useEffect(() => {
    window.localStorage.setItem("projectStatus", projectStatus.valueOf().toString());
  }, [projectStatus]);

  const created = async (): Promise<void> => {
    await getToken();
    await getPluginList();
  };

  const getToken = async (): Promise<void> => {
    await aesUtil.generateCryptoKey();
    const response = await apiList.getToken();
    if (response.successful) {
      window.localStorage.setItem("accessToken", response.result.accessToken);
      window.localStorage.setItem("refreshToken", response.result.refreshToken);
    } else {
      alert("Server is not running");
    }
  };

  const getPluginList = async (): Promise<void> => {
    const response = await apiList.getPluginList(window.localStorage.getItem("accessToken"));
    if (response.successful) {
      // const pluginList: Array<{
      //   plugin: string;
      //   setting: { [key: string]: string };
      // }> = [
      //   { plugin: "coyote", setting: { projectSetting: "File" } },
      //   { plugin: "injection", setting: { file1: "File", file2: "File", file3: "File", file4: "File" } },
      //   { plugin: "whatever", setting: { option1: "Number", option2: "String" } },
      //   { plugin: "짜잔", setting: {} },
      //   { plugin: "짜자장", setting: { wrong: "test" } },
      // ];
      const pluginList: Array<{
        plugin: string;
        setting: { [key: string]: string };
      }> = response.result.pluginList;

      const pluginTestInfo: Array<PluginTestInfo> = pluginList.map((a) => {
        const settings = Object.keys(a.setting).map((settingName) => ({
          name: settingName,
          type: a.setting[settingName],
          data: undefined,
        }));
        return {
          name: a.plugin,
          selected: false,
          settings: settings,
          isTesting: false,
          isSuccess: undefined,
        };
      });
      setPluginInfoList(pluginTestInfo);
    } else {
      alert("Failed to retrieve the list of plugins.");
    }
  };

  const handleStatusChange = (status: Status) => {
    if (status) setProjectStatus(status);
  };

  return (
    <div>
      <header className="main_header">
        <h1>Crichton</h1>
      </header>
      <div className="main_content">
        <Test api={apiList} status={projectStatus} setStatus={handleStatusChange} pluginList={pluginInfoList} />
        <Log api={apiList} status={projectStatus} />
      </div>
    </div>
  );
}
