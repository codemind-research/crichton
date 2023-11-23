import { useState } from "react";
import Select from "./SelectComp";
import { Status, TestTypes } from "../../../util/TypeDef";
import ResultReport from "../../../util/Report/ResultReport";
import "./Test.scss";

const TestComp = (props: any) => {
  const token = window.localStorage.getItem("accessToken");
  const status = props.status;
  const projectPath = window.localStorage.getItem("projectPath");

  const [testType, setTestType] = useState<TestTypes>({
    whitebox: { selected: false, isTesting: false, result: undefined },
    injection: { selected: false, isTesting: false, result: undefined },
  });
  const [whiteBoxSettingFile, setWhiteBoxSettingFile] = useState<Blob>();
  const [injectionDuration, setInjectionDuration] = useState<number>(60);

  const canRunning: boolean =
    (status === Status.Created || status === Status.Tested) &&
    (testType.whitebox.selected || testType.injection.selected);
  const canReporting: boolean = status === Status.Tested;
  const uploadMarginStyle: Object = {
    marginLeft: "80px",
  };
  const handleTestTypeChange = (type: string, checked: boolean): void => {
    setTestType((prevTestTypes) => ({
      ...prevTestTypes,
      [type]: { ...prevTestTypes[type], selected: checked },
    }));
    console.log(testType);
  };
  const handleSettingFileSelect = async (settingFile: File): Promise<void> => {
    const reader = new FileReader();

    reader.onload = () => {
      const blobData = new Blob([reader.result as ArrayBuffer]);
      setWhiteBoxSettingFile(blobData);
    };

    await reader.readAsArrayBuffer(settingFile);
  };
  const handleDurationChange = (duration: number) => {
    setInjectionDuration(duration);
  };

  const handleTestRunClick = async (): Promise<void> => {
    props.setStatus(Status.Testing);
    if (testType.whitebox.selected) await runWhiteBoxTest();
    if (testType.injection.selected) await runInjectionTest();
    props.setStatus(Status.Tested);
  };

  const runWhiteBoxTest = async (): Promise<void> => {
    const formdata = new FormData();

    const jsonData = JSON.stringify({ sourcePath: projectPath });
    const jsonBlob = new Blob([jsonData], { type: "application/json" });
    formdata.append("data", jsonBlob);

    if (whiteBoxSettingFile != undefined) formdata.append("file", whiteBoxSettingFile);

    props.setStatus(Status.Testing);
    const response = await props.api.runWhiteboxTest(formdata, token);
    console.log(response);
  };

  const runInjectionTest = async (): Promise<void> => {
    const formdata = new FormData();
    const data = {
      sourcePath: projectPath,
      testDuration: injectionDuration,
    };

    const jsonData = JSON.stringify(data);
    const jsonBlob = new Blob([jsonData], { type: "application/json" });
    formdata.append("data", jsonBlob);

    const response = await props.api.runInjectionTest(formdata, token);
    console.log(response);
  };

  const handleDownloadClick = async (): Promise<void> => {
    const data = {
      sourcePath: projectPath,
    };
    const response = await props.api.getReportData(data, token);
    showCrichtonHtmlReport(response.result);
  };

  const showCrichtonHtmlReport = (reportData: object): void => {
    const data = {
      reportData: reportData,
      testType: testType,
    };
    const htmlCode: string = ResultReport(data);
    const newTab: Window | null = window.open();
    if (newTab != null) newTab.document.body.innerHTML = htmlCode;
  };

  return (
    <div className="running_test_component">
      <Select
        testType={testType}
        duration={injectionDuration}
        testTypeChange={handleTestTypeChange}
        settingFileSelect={handleSettingFileSelect}
        durationChange={handleDurationChange}
      />
      <hr />
      <div className="test_option_button">
        <button onClick={handleTestRunClick} disabled={!canRunning}>
          Run
        </button>
        <button onClick={handleDownloadClick} style={uploadMarginStyle} disabled={!canReporting}>
          Download Report
        </button>
      </div>
    </div>
  );
};

export default TestComp;
