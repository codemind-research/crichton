import { useState, useEffect } from "react";
import Select from "./SelectComp";
import { Status } from "../../../util/Constants";
import ResultReport from "../../../util/Report/ResultReport";
import "./Test.scss";

const TestComp = (props: any) => {
  const token = window.sessionStorage.getItem("accessToken");
  const status = props.status;
  const projectPath = window.sessionStorage.getItem("projectPath");
  const [testType, setTestType] = useState<{ whitebox: boolean; injection: boolean }>({
    whitebox: false,
    injection: false,
  });
  const [injectionDuration, setInjectionDuration] = useState<number>(60);

  const canRunning: boolean =
    (status === Status.Created || status === Status.Tested) && (testType.whitebox || testType.injection);
  const canReporting: boolean = status === Status.Tested;
  const uploadMarginStyle: Object = {
    marginLeft: "80px",
  };
  const handleTestTypeChange = (id: string, checked: boolean) => {
    setTestType((prevState) => ({
      ...prevState,
      [id]: checked,
    }));
  };
  const handleDurationChange = (duration: number) => {
    setInjectionDuration(duration);
  };

  const handleTestRunClick = async (): Promise<void> => {
    props.setStatus(Status.Testing);
    if (testType.whitebox) await runWhiteBoxTest();
    if (testType.injection) await runInjectionTest();
    props.setStatus(Status.Tested);
  };

  const runWhiteBoxTest = async (): Promise<void> => {
    props.setStatus(Status.Testing);
    const data = {
      sourcePath: projectPath,
    };
    const response = await props.api.runWhiteboxTest(data, token);
    console.log(response);
  };

  const runInjectionTest = async (): Promise<void> => {
    const data = {
      sourcePath: projectPath,
      testDuration: injectionDuration,
    };
    const response = await props.api.runWhiteBoxTest(data, token);
    console.log(response);
  };

  const handleDownloadClick = async (): Promise<void> => {
    const data = {
      sourcePath: projectPath,
    };
    const response = await props.api.getReportData(data, token);

    const htmlCode: string = ResultReport(response.result);
    const newTab: Window | null = window.open();
    if (newTab != null) newTab.document.body.innerHTML = htmlCode;
  };

  return (
    <div className="running_test_component">
      <Select
        testType={testType}
        duration={injectionDuration}
        testTypeChange={handleTestTypeChange}
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
