import { useState } from "react";
import Select from "./SelectComp";
import { Status, TestTypeInfo } from "../../../util/TypeDef";
import ResultReport from "../../../util/Report/ResultReport";
import "./Test.scss";

const TestComp = (props: any) => {
  const token = window.localStorage.getItem("accessToken");
  const status = props.status;
  const projectPath = window.localStorage.getItem("projectPath");

  const [testTypes, setTestTypes] = useState<{ whitebox: TestTypeInfo; injection: TestTypeInfo }>({
    whitebox: { name: "whitebox", selected: false, isTesting: undefined, isSuccess: undefined },
    injection: { name: "injection", selected: false, isTesting: undefined, isSuccess: undefined },
  });
  const [whiteBoxSettingFile, setWhiteBoxSettingFile] = useState<Blob>();
  const [injectionDuration, setInjectionDuration] = useState<number>(60);

  const canRunning: boolean =
    (status === Status.Created || status === Status.Tested) &&
    (testTypes.whitebox.selected || testTypes.injection.selected);
  const canReporting: boolean = status === Status.Tested;

  const uploadMarginStyle: Object = {
    marginLeft: "80px",
  };

  const getTestType = (typeName: string): TestTypeInfo | undefined => {
    switch (typeName) {
      case testTypes.whitebox.name:
        return testTypes.whitebox;
      case testTypes.injection.name:
        return testTypes.injection;
      default:
        return undefined;
    }
  };

  const handleTypeSelectedChange = (typeName: string, checked: boolean): void => {
    setTestTypes((prevTypes) => ({
      ...prevTypes,
      [typeName]: { ...getTestType(typeName), selected: checked },
    }));
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
    if (testTypes.whitebox.selected) await runWhiteBoxTest();
    if (testTypes.injection.selected) await runInjectionTest();
    props.setStatus(Status.Tested);
  };

  const handleTypeStatusChange = (
    testType: TestTypeInfo,
    isTesting: boolean | undefined,
    isSuccess: boolean | undefined
  ): void => {
    setTestTypes((prevTypes) => ({
      ...prevTypes,
      [testType.name]: { ...testType, isTesting: isTesting, isSuccess: isSuccess },
    }));
  };

  const updateTestResultToType = (type: TestTypeInfo, result: string): void => {
    console.log(result);
    let isSuccess;
    if (result === "SUCCESS") isSuccess = true;
    else isSuccess = false;
    handleTypeStatusChange(type, false, isSuccess);
  };

  const runWhiteBoxTest = async (): Promise<void> => {
    const formdata = new FormData();

    const jsonData = JSON.stringify({ sourcePath: projectPath });
    const jsonBlob = new Blob([jsonData], { type: "application/json" });
    formdata.append("data", jsonBlob);

    if (whiteBoxSettingFile != undefined) formdata.append("file", whiteBoxSettingFile);

    handleTypeStatusChange(testTypes.whitebox, true, undefined);
    const response = await props.api.runWhiteboxTest(formdata, token);
    updateTestResultToType(testTypes.whitebox, response.result.testResult);
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
    updateTestResultToType(testTypes.injection, response.result.testResult);
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
      testType: testTypes,
    };
    const htmlCode: string = ResultReport(data);
    const newTab: Window | null = window.open();
    if (newTab != null) newTab.document.body.innerHTML = htmlCode;
  };

  return (
    <div className="running_test_component">
      <Select
        testType={testTypes}
        duration={injectionDuration}
        testTypeChange={handleTypeSelectedChange}
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
