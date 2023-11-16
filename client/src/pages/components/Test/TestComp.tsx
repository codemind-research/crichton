import { useState } from "react";
import Select from "./SelectComp";
import { Status } from "../../../util/Constants";
import "./Test.scss";

const TestComp = (props: any) => {
  const API: { list: any; token: string; refresh: string } = props.api;
  const canRunning: boolean = props.status === Status.Created || props.status === Status.Tested;
  const canReporting: boolean = props.status === Status.Tested;
  const uploadMarginStyle: Object = {
    marginLeft: "80px",
  };
  const [testType, setTestType] = useState<{ whitebox: string; injection: string }>({
    whitebox: "false",
    injection: "false",
  });
  const [injectionDuration, setInjectionDuration] = useState<number>(60);

  const handleTestTypeChange = (id: string, checked: string) => {
    setTestType((prevState) => ({
      ...prevState,
      [id]: checked,
    }));
  };
  const handleDurationChange = (duration: number) => {
    setInjectionDuration(duration);
  };

  const handleTestRunClick = async () => {
    const data = {
      project: props.projectPath,
      whitebox: testType.whitebox,
      injection: testType.injection,
      duration: injectionDuration,
    };
    props.setStatus(Status.Testing);
    const response = await API.list.runTest(data, API.token);
    props.setStatus(Status.Tested);
    console.log(response); // 끝났을 때 알림
  };

  const handleDownloadClick = async () => {
    const data = { project: props.projectPath };
    const response = await API.list.getReportData(data, API.token);
    console.log(response); // 레포트 정보
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
        <button onClick={handleTestRunClick} disabled={canRunning}>
          Run
        </button>
        <button onClick={handleDownloadClick} style={uploadMarginStyle} disabled={canReporting}>
          Download Report
        </button>
      </div>
    </div>
  );
};

export default TestComp;
