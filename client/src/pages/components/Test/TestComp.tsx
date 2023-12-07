import { useState, useEffect } from "react";
import Upload from "./UploadComp";
import Select from "./SelectComp";
import ResultReport from "../../../util/Report/ResultReport";
import { Status, PluginTestInfo } from "../../../util/TypeDef";
import "./Test.scss";

const TestComp = (props: any) => {
  const token: string | null = window.localStorage.getItem("accessToken");
  const pluginList: Array<PluginTestInfo> = props.pluginList || [];
  const status: Status = props.status;

  const [isUploadDone, setIsUploadDone] = useState<boolean>(false);
  const [fileList, setFileList] = useState<Array<File>>([]);
  const [projectPath, setProjectPath] = useState<string>("");

  const canRunning: boolean = (status > Status.Created && status !== Status.Testing) || fileList.length > 0;
  const canReporting: boolean = status === Status.Tested;

  const uploadMarginStyle: Object = {
    marginLeft: "80px",
  };

  useEffect(() => {
    runTest();
  }, [isUploadDone]);

  const handleTargetDirSelect = (list: Array<File>) => {
    setFileList(list);
    setIsUploadDone(false);
  };

  const makeZipFile = async (fileList: File[]): Promise<any> => {
    const zip = require("jszip")();

    for (let file = 0; file < fileList.length; file++) {
      zip.file(fileList[file].name, fileList[file]);
    }

    const zipped = await zip.generateAsync({
      type: "blob",
    });
    return zipped;
  };

  const handleTestRunClick = async (): Promise<void> => {
    if (!isUploadDone) {
      const zipFile = await makeZipFile(fileList);
      const formdata = new FormData();
      formdata.append("file", zipFile, "crichton_project_temp.zip");

      const response = await props.api.uploadFile(formdata, token);
      if (response.successful) {
        setProjectPath(response.result.unzipPath);
        props.setStatus(Status.Created);
        setIsUploadDone(true);
      } else {
        alert("Directory upload failed");
      }
    } else if (projectPath !== "") {
      runTest();
    }
  };

  const runTest = async (): Promise<void> => {
    const testPluginList = pluginList.filter((plugin) => plugin.selected);
    if (testPluginList.length === 0) return;

    props.setStatus(Status.Testing);
    await runPluginTest(testPluginList);
    props.setStatus(Status.Tested);
  };

  const runPluginTest = async (testPluginList: Array<PluginTestInfo>): Promise<void> => {
    for (const plugin of testPluginList) {
      const formdata = new FormData();

      const settingFiles: Array<File> = [];
      const pluginSettings: { [key: string]: string } = {};
      plugin.settings.forEach((setting) => {
        if (setting.type.toLowerCase() === "file" && setting.data) settingFiles.push(setting.data);

        const value =
          setting.type.toLowerCase() === "file"
            ? setting.data === undefined
              ? ""
              : setting.name
            : setting.data.toString();
        pluginSettings[setting.name] = value;
      });

      if (settingFiles.length > 0) {
        const zipFile = await makeZipFile(settingFiles);
        formdata.append("file", zipFile, "setting.zip");
      }

      const jsonData = JSON.stringify({
        plugin: plugin.name,
        sourcePath: projectPath,
        pluginSettings: pluginSettings,
      });
      const jsonBlob = new Blob([jsonData], { type: "application/json" });
      formdata.append("data", jsonBlob);

      plugin.isTesting = true;
      const response = await props.api.runPluginTest(formdata, token);
      plugin.isTesting = false;
      plugin.isSuccess = response.successful && response.result.testResult;
    }
  };

  const handleDownloadClick = async (): Promise<void> => {
    const response = await props.api.getReportData(token);
    if (!response.successful) {
      alert("Failed to generate the result report");
      return;
    }
    const htmlCode: string = ResultReport({ reportData: response.result.data });
    console.log(htmlCode);
    const newTab: Window | null = window.open();
    if (newTab != null) newTab.document.body.innerHTML = htmlCode;
  };

  return (
    <div className="running_test_component">
      <Upload fileList={fileList} setFileList={handleTargetDirSelect} isTesting={status === Status.Testing} />
      <div className="test_plugin_component">
        <Select pluginList={pluginList} />
      </div>
      <div className="test_option_button">
        <hr />
        <button onClick={handleTestRunClick} disabled={!canRunning}>
          Run
        </button>
        <button onClick={handleDownloadClick} style={uploadMarginStyle}>
          Download Report
        </button>
      </div>
    </div>
  );
};

export default TestComp;
